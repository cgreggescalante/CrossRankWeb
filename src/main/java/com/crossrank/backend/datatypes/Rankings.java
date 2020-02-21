/*
 * Copyright 2020 Conor Gregg Escalante
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.crossrank.backend.datatypes;

import com.crossrank.LoadingBar;
import lombok.Getter;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

public class Rankings implements Serializable {
    @Getter
    private List<Person> runners;

    @Getter
    private Map<String, Integer> runnerDirectory;

    @Getter
    private Map<Double, String> sortedRankingsBoys;
    @Getter
    private Map<Double, String> sortedRankingsGirls;

    public Rankings() {
        runners = new ArrayList<>();

        runnerDirectory = new TreeMap<>();
    }

    public void ScoreRaces(List<Race> races) {
        System.out.println("SCORING MEETS");

        LoadingBar loadingBar = new LoadingBar(50,  races.size());

        for (Race race : races) {
            ScoreRace(race);
            loadingBar.updateProgress();
        }

        loadingBar.end();

        updateDatabaseRatings();
    }

    private void updateDatabaseRatings() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crossrank", "PotatoTax", "PotatoTax1707")) {

                PreparedStatement updateRating = conn.prepareStatement("UPDATE runner SET rating = ? where id = ?");

                for (Person runner : runners) {
                    updateRating.setObject(1, runner.getRating());
                    updateRating.setString(2, Integer.toString(runner.getId()));
                    updateRating.execute();
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs the ELO Rating algorithm on a race's results.
     * Updates each individuals rating.
     * @param race - a Race object to be scored.
     */
    public void ScoreRace(Race race) {

        List<Person> raceParticipants = new ArrayList<>();
        for (Result result : race.getResults()) {
            Person runner = getPerson(result);

            if (runner == null) {
                runner = new Person(result);
                runnerDirectory.put(runner.getFullName(), runner.getId());
                runners.add(runner);
            } else {
                runner.addResult(result);
            }

            raceParticipants.add(runner);
        }

        raceParticipants.sort(Comparator.comparing(Person::getRecentMark));

        for (Person raceParticipant : raceParticipants) {
            raceParticipant.addRace();
        }

        for (int i = 0; i < raceParticipants.size(); i++) {
            Person current = raceParticipants.get(i);

            for (int j = 0; j < i; j++) {
                Person other = raceParticipants.get(j);

                newRatings(other, current);
            }

            for (int j = i+1; j < raceParticipants.size(); j++) {
                Person other = raceParticipants.get(j);
                newRatings(current, other);
            }
        }

        for (Person participant : raceParticipants) {
            double currentRating = participant.getRating();
            int lastResult = participant.getResults().size() - 1;
            participant.getResults().get(lastResult).setRating(currentRating);
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crossrank", "PotatoTax", "PotatoTax1707")) {
                PreparedStatement getRecentResult = conn.prepareStatement("SELECT re.id FROM runner ru INNER JOIN result re ON ru.id = re.runner_id INNER JOIN meet m ON m.id = re.meet_id WHERE ru.id = ? ORDER BY m.date desc LIMIT 1");
                PreparedStatement updateRating = conn.prepareStatement("UPDATE result SET rating = ? WHERE id = ?");

                for (Person raceParticipant : raceParticipants) {
                    getRecentResult.setInt(1, raceParticipant.getId());
                    ResultSet resultSet = getRecentResult.executeQuery();
                    resultSet.next();

                    try {
                        int lastResultId = resultSet.getInt(1);

                        updateRating.setObject(1, raceParticipant.getRating());
                        updateRating.setInt(2, lastResultId);
                        updateRating.execute();
                    } catch (SQLException ignored) {

                    }

                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        runnerDirectory = new TreeMap<>(runnerDirectory);
    }

    /**
     * Updates the two Person's ratings based on the difference in initial rating.
     * @param winner A Person object for the individual who placed higher
     *               in the race.
     * @param loser A Person object for the lower placing individual.
     */
    public static void newRatings(Person winner, Person loser) {
        double Pb = 1.0f * 1.0f / (1 + 1.0f * (float)(Math.pow(10, 1.0f * (winner.getRating() - loser.getRating()) / 400)));
        double Pa = 1 - Pb;

        float winnerK = 100f / winner.getRaces();
        float loserK = 100f / loser.getRaces();

        double Ra = winner.getRating() + winnerK * (1 - Pa);
        double Rb = loser.getRating() + loserK * (-Pb);

        winner.setRating(Ra);
        loser.setRating(Rb);
    }

    /**
     * Places runners into sorted TreeMaps by gender
     * Used for quickly retrieving rankings.
     */
    private void CreateSortedRankings() {
        sortedRankingsBoys = new TreeMap<>();
        sortedRankingsGirls = new TreeMap<>();

        for (Person runner : runners) {
            if (runner.getGenderName().equals("Boys")) {
                sortedRankingsBoys.put(runner.getRating(), runner.getFullName());
            } else {
                sortedRankingsGirls.put(runner.getRating(), runner.getFullName());
            }
        }
    }

    /**
     * Given the section and sex, returns the names and ratings of all runners
     * who fall in the area.
     * @param page Integer denoting the section the rankings will come from.
     * @param pageLength Integer denoting the number of runners on each page.
     * @param sex String denoting the sex to be ranked
     * @return A TreeMap with Double ratings and String names.
     */
    public static Map<Double, String> GetRankings(int page, int pageLength, String sex) {
        int endIndex = page * pageLength;
        int startIndex = pageLength * (page - 1);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crossrank", "PotatoTax", "PotatoTax1707")) {
                Statement getRankings = conn.createStatement();

                ResultSet resultSet = getRankings.executeQuery("SELECT first_name, last_name, rating FROM (SELECT ROW_NUMBER() over (ORDER BY rating DESC, first_name) row_num, first_name, last_name, rating, gender FROM runner WHERE gender = '" + sex + "') t WHERE row_num > " + startIndex + " AND row_num <= " + endIndex);

                Map<Double, String> rankings = new TreeMap<>();

                while (resultSet.next()) {
                    rankings.put(resultSet.getDouble(3), resultSet.getString(1) + " " + resultSet.getString(2));
                }

                return rankings;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Person getPerson(Result result) {
        for (Person p : runners) {
            if (p.getFullName().equals(result.getFullName()) && p.getGenderName().equalsIgnoreCase(result.getGenderName())) {
                return p;
            }
        }
        return null;
    }
}
