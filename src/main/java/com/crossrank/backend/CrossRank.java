package com.crossrank.backend;

import com.crossrank.LoadingBar;
import com.crossrank.backend.datatypes.*;

import java.io.Serializable;
import java.util.*;

public class CrossRank implements Serializable {
    private final List<Person> runners;
    private final List<Race> races;
    private final Map<Integer, List<Integer>> meetData;

    private MeetIndex meetIndex;

    private Map<String, Long> runnerDirectory;

    private long runnerIdCounter;
    private long raceIdCounter;

    CrossRank() {
        runners = new ArrayList<>();
        races = new ArrayList<>();

        runnerDirectory = new TreeMap<>();

        meetData = new HashMap<>();

        meetIndex = new MeetIndex();
    }


    /**
     * Generates the MeetIndex, a list of the meetIds and their respective
     * resultIds.
     */
    private void BuildIndex() {
        meetIndex.meets.putAll(MeetCompiler.CompileSeason(2019));

        CrossRankSerializer.SaveMeetIndex(meetIndex);
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

    public static Rankings GetRankings(int page, int pageLength, String sex) {
        CrossRank crossRank = CrossRankSerializer.LoadRankings();

        List<Person> sorted = new ArrayList<>();
        Rankings rankings = new Rankings();

        crossRank.runners.sort(Comparator.comparing(Person::getRanking).reversed());

        for (Person p : crossRank.runners) {
            if (p.getGenderName().equalsIgnoreCase(sex)) {
                sorted.add(p);
            }
        }

        for (int i = pageLength * page - pageLength; i < pageLength * page; i++) {
            rankings.addRunner(sorted.get(i));
        }

        return rankings;
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

    @SuppressWarnings("unused")
    public List<Person> getRunners() {
        return runners;
    }

    @SuppressWarnings("unused")
    public Map<String, Long> getRunnerDirectory() {
        return runnerDirectory;
    }

    public static void main(String[] args) {
        CrossRank crossRank = CrossRankSerializer.LoadRankings();
        crossRank.GetResults();
        crossRank.ScoreMeets();
        System.out.println("SAVING RESULTS");
        CrossRankSerializer.SaveRankings(crossRank);
        CrossRankSerializer.SaveRunners(crossRank.runners);
        CrossRankSerializer.SaveRunnerDirectory(crossRank.runnerDirectory);
        CrossRankSerializer.SaveRaces(crossRank.races);
    }
}
