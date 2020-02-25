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
import com.crossrank.backend.serialization.PersonSerializer;
import com.crossrank.backend.serialization.RankingsSerializer;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

/**
 * Rankings class handles the storing of all Person objects
 * and generating the ratings using the ELO algorithms
 */
@Getter
public class Rankings implements Serializable {
    private List<Person> runners;

    private Map<String, Integer> runnerDirectory;

    private Map<Double, String> sortedRankingsBoys;
    private Map<Double, String> sortedRankingsGirls;

    public Rankings() {
        runners = new ArrayList<>();

        runnerDirectory = new TreeMap<>();
    }

    /**
     * Iterates though the List of Races and updates the participant's
     * ratings based on their results, then generates rankings.
     *
     * @param races a List of Race objects in chronological order
     */
    public void ScoreRaces(List<Race> races) {
        System.out.println("SCORING MEETS");

        // a CLI loading bar is used to provide visual indication to the progress being made.
        // Also provides a rough estimate of the remaining time.
        LoadingBar loadingBar = new LoadingBar(50,  races.size());

        for (Race race : races) {
            ScoreRace(race);
            loadingBar.updateProgress();
        }

        loadingBar.end();

        CreateSortedRankings();
    }

    /**
     * Performs the ELO Rating algorithm on a race's results.
     * Updates each individuals rating.
     *
     * @param race - a Race object to be scored.
     */
    public void ScoreRace(Race race) {
        List<Person> raceParticipants = new ArrayList<>();

        for (Result result : race.getResults()) {   // Iterates through the Race's results.
            Person runner = getPerson(result);      // Attempts to find a Person object with the same name as the Result.

            /*
             * If no such Person exists, a new Person is created and added to the runnerDirectory and the runners List.
             */
            if (runner == null) {
                runner = new Person(result);
                runnerDirectory.put(runner.getFullName(), runner.getId());
                runners.add(runner);
            } else {
                // if the Person does exist, the result is added to the Person's results List.
                runner.addResult(result);
            }

            // The Person is added to the List of raceParticipants.
            raceParticipants.add(runner);
        }

        // The raceParticipants List is sorted by race times.
        raceParticipants.sort(Comparator.comparing(Person::getRecentMark));

        // Each Person's raceCount value is increased by 1.
        for (Person raceParticipant : raceParticipants) {
            raceParticipant.addRace();
        }

        /*
         * ELO Rating Algorithm
         */
        for (int i = 0; i < raceParticipants.size(); i++) {     // Iterates through each runner.
            Person current = raceParticipants.get(i);

            /*
             * Iterates though each lower-placing runner
             */
            for (int j = 0; j < i; j++) {
                Person other = raceParticipants.get(j);
                newRankings(other, current);                    // Updates ratings
            }

            /*
             * Iterates though each higher-placing runner
             */
            for (int j = i+1; j < raceParticipants.size(); j++) {
                Person other = raceParticipants.get(j);
                newRankings(current, other);
            }
        }

        for (Person participant : raceParticipants) {
            double currentRating = participant.getRating();
            int lastResult = participant.getResults().size() - 1;
            participant.getResults().get(lastResult).setRating(currentRating);
        }

        runnerDirectory = new TreeMap<>(runnerDirectory);
    }

    /**
     * Updates the two Person's ratings based on the difference in initial rating.
     *
     *
     * @param winner A Person object for the individual who placed higher
     *               in the race.
     * @param loser A Person object for the lower placing individual.
     */
    public static void newRankings(Person winner, Person loser) {
        // Uses the Persons' initial ratings to calculate the probability that each will win
        double probabilityB = 1.0f * 1.0f / (1 + 1.0f * (float)(Math.pow(10, 1.0f * (winner.getRating() - loser.getRating()) / 400)));
        double probabilityA = 1 - probabilityB;

        // Generates K values for each individual based on the number of races they have done
        // The K value is the maximum change in rating that can occur
        float winnerK = 100f / winner.getRaceCount();
        float loserK = 100f / loser.getRaceCount();

        // Calculates the new ratings based on the K values and
        double Ra = winner.getRating() + winnerK * (1 - probabilityA);
        double Rb = loser.getRating() + loserK * (-probabilityB);

        // Updates the ratings of the two Persons
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
     * Given the page number, length, and gender, returns the names and ratings of all runners
     * who fall in the area.
     *
     * @param page Integer denoting the section the rankings will come from.
     * @param pageLength Integer denoting the number of runners on each page.
     * @param gender String denoting the gender to be ranked
     * @return A TreeMap with Double ratings and String names.
     */
    public static Map<Double, String> GetRankings(int page, int pageLength, String gender) {
        // Loads the sorted rankings of the specified gender, creates a List from the rating values
        Map<Double, String> results = RankingsSerializer.LoadSortedRankings(gender);
        List<Double> ratings = new ArrayList<>(results.keySet());

        /*
         * TreeMaps are sorted with the keys in ascending order, but the higher ratings should be at
         * the top of the rankings, so they must be reversed.
         */
        Collections.reverse(ratings);

        Map<Double, String> rankingsPage = new TreeMap<>();

        int endIndex = page * pageLength;
        int startIndex = pageLength * page - pageLength;

        /*
         * To avoid NullPointerExceptions, if the value of the endIndex is greater than the size of the ratings
         * the endIndex is set to the size of the ratings.
         */
        if (endIndex > ratings.size()) {
            endIndex = ratings.size();
        }

        // Adds the names and ratings into the rankingsPage Map
        for (int i = startIndex; i < endIndex; i++) {
            rankingsPage.put(ratings.get(i), results.get(ratings.get(i)));
        }

        return rankingsPage;
    }

    /**
     * Takes a Result object and attempts to find an existing Person object
     * which matches the name and gender of the Result
     *
     * @param result a Result object for which to find the associated Person
     * @return A Person object
     */
    private Person getPerson(Result result) {
        /*
         * Iterates through the runners List
         * Returns the Person with matching name and gender
         */
        for (Person p : runners) {
            if (p.getFullName().equals(result.getFullName()) && p.getGenderName().equalsIgnoreCase(result.getGenderName())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Given a name, loads the runner directory and locates the Person
     * object with the name.
     *
     * @param name The name of the Person to find
     * @return the Person object matching the name provided
     */
    public static Person getPerson(String name) {

        Map<String, Integer> runnerDirectory = RankingsSerializer.LoadRunnerDirectory();

        try {
            if (runnerDirectory != null) {
                int id = runnerDirectory.get(name);
                return PersonSerializer.LoadRunner(id);
            }
        } catch (NullPointerException e) {
            return new Person();
        }

        return new Person();
    }
}
