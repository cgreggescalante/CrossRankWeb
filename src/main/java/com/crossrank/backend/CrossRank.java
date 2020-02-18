/*
 * Copyright 2020 Conor Gregg Escalante
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.crossrank.backend;

import com.crossrank.LoadingBar;
import com.crossrank.backend.datatypes.*;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

public class CrossRank implements Serializable {
    @Getter
    private final List<Person> runners;
    private final List<Race> races;
    private final Map<Integer, List<Integer>> meetData;

    private MeetIndex meetIndex;

    @Getter
    private Map<String, Long> runnerDirectory;

    @Getter
    private Map<Double, String> sortedRankingsBoys;
    @Getter
    private Map<Double, String> sortedRankingsGirls;

    private long runnerIdCounter;
    private long raceIdCounter;

    CrossRank() {
        runners = new ArrayList<>();
        races = new ArrayList<>();

        runnerDirectory = new TreeMap<>();

        meetData = new TreeMap<>();
    }


    /**
     * If there exists a saved MeetIndex, loads that, otherwise generates
     * a new MeetIndex
     */
    private void BuildIndex() {
        meetIndex = CrossRankSerializer.LoadMeetIndex();

        if (meetIndex == null) {
            System.out.println("NO MEET INDEX SAVED");

            meetIndex = new MeetIndex();
            meetIndex.CompileSeason(2019);

            CrossRankSerializer.SaveMeetIndex(meetIndex);
        } else {
            System.out.println("EXISTING MEET INDEX LOADED");
        }
    }


    /**
     * Loads the results for all meets in the meetIndex.
     * Sorts the list into chronological order.
     */
    private void GetResults() {
        System.out.println("LOADING RESULTS");

        meetIndex = CrossRankSerializer.LoadMeetIndex();



        if (meetIndex != null) {
            meetData.putAll(meetIndex.meets);

            LoadingBar loadingBar = new LoadingBar(50, meetData.size());

            for (Map.Entry<Integer, List<Integer>> entry : meetData.entrySet()) {
                List<Race> newRaces = Fetcher.GetRaces(entry.getKey(), entry.getValue(), raceIdCounter);
                races.addAll(newRaces);
                raceIdCounter += newRaces.size();

                loadingBar.updateProgress();
            }

            loadingBar.end();

            races.sort(Comparator.comparing(Race::getMeetDate));
        } else {
            System.out.println("No meet index saved.");
        }
    }


    /**
     * Scores each saved meet.
     */
    private void ScoreMeets() {
        System.out.println("SCORING MEETS");

        LoadingBar loadingBar = new LoadingBar(50, races.size());

        for (Race race : races) {
            ScoreMeet(race);
            loadingBar.updateProgress();
        }

        loadingBar.end();
    }


    /**
     * Performs the ELO Rating algorithm on a race's results.
     * Updates each individuals rating.
     * @param race - a Race object to be scored.
     */
    private void ScoreMeet(Race race) {

        List<Person> raceParticipants = new ArrayList<>();
        for (Result result : race.getResults()) {
            Person runner = getPerson(result);

            if (runner == null) {
                runner = new Person(result, runnerIdCounter);
                runnerDirectory.put(runner.getFullName(), runner.getId());
                runnerIdCounter++;
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

                newRankings(other, current);
            }

            for (int j = i+1; j < raceParticipants.size(); j++) {
                Person other = raceParticipants.get(j);
                newRankings(current, other);
            }
        }

        for (Person participant : raceParticipants) {
            double currentRating = participant.getRanking();
            int lastResult = participant.getResults().size() - 1;
            participant.getResults().get(lastResult).setRating(currentRating);
        }


        runnerDirectory = new TreeMap<>(runnerDirectory);
    }


    /**
     * Returns the probability that the winner would win based on
     * the difference in the two ratings.
     * @param winner - the winner's rating
     * @param loser - the loser's rating.
     * @return A value between 0 and 1 representing the probanility that the
     * winner would have won.
     */
    private static double Probability(double winner, double loser) {
        return 1.0f * 1.0f / (1 + 1.0f * (float)(Math.pow(10, 1.0f * (winner - loser) / 400)));
    }


    /**
     * Updates the two Person's ratings based on the difference in initial rating.
     * @param winner A Person object for the individual who placed higher
     *               in the race.
     * @param loser A Person object for the lower placing individual.
     */
    private static void newRankings(Person winner, Person loser) {
        double Pb = Probability(winner.getRanking(), loser.getRanking());
        double Pa = 1 - Pb;

        float winnerK = 100f / winner.getRaces();
        float loserK = 100f / loser.getRaces();

        double Ra = winner.getRanking() + winnerK * (1 - Pa);
        double Rb = loser.getRanking() + loserK * (-Pb);

        winner.setRanking(Ra);
        loser.setRanking(Rb);
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
                sortedRankingsBoys.put(runner.getRanking(), runner.getFullName());
            } else {
                sortedRankingsGirls.put(runner.getRanking(), runner.getFullName());
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
        Map<Double, String> results = CrossRankSerializer.LoadSortedRankings(sex);

        List<Double> ratings = new ArrayList<>(results.keySet());

        Collections.reverse(ratings);

        Map<Double, String> product = new TreeMap<>();

        int endIndex = page * pageLength;

        if (endIndex > ratings.size()) {
            endIndex = ratings.size();
        }
        for (int i = pageLength * page - pageLength; i < endIndex; i++) {
            product.put(ratings.get(i), results.get(ratings.get(i)));
        }

        return product;
    }

    private Person getPerson(Result result) {
        for (Person p : runners) {
            if (p.getFullName().equals(result.getFullName()) && p.getGenderName().equalsIgnoreCase(result.getGenderName())) {
                return p;
            }
        }
        return null;
    }

    public static Person GetPerson(String name) {

        Map<String, Long> runnerDirectory = CrossRankSerializer.LoadRunnerDirectory();

        try {
            if (runnerDirectory != null) {
                long id = runnerDirectory.get(name);
                return CrossRankSerializer.LoadRunner(id);
            }
        } catch (NullPointerException e) {
            return new Person();
        }

        return new Person();
    }

    public static void main(String[] args) {
        CrossRank crossRank = CrossRankSerializer.LoadRankings();
        crossRank.BuildIndex();
        crossRank.GetResults();
        crossRank.ScoreMeets();
        crossRank.CreateSortedRankings();
        System.out.println("SAVING RESULTS");
        CrossRankSerializer.SaveRankings(crossRank);
        CrossRankSerializer.SaveSortedRankings(crossRank);
        CrossRankSerializer.SaveRunners(crossRank.runners);
        CrossRankSerializer.SaveRunnerDirectory(crossRank.runnerDirectory);
        CrossRankSerializer.SaveRaces(crossRank.races);
    }


}
