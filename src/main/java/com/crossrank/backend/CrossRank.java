package com.crossrank.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrossRank implements Serializable {
    private List<Person> runners;
    private List<Race> races;
    private List<String> scoredMeets;

    private long runnerIdCounter;
    private long raceIdCounter;

    CrossRank() {
        runners = new ArrayList<>();
        scoredMeets = new ArrayList<>();
        races = new ArrayList<>();
    }

    private void ScanMeets() {
        Map<Integer, List<Integer>> meetData = MeetCompiler.CompileMonth();

        for (Map.Entry<Integer, List<Integer>> entry : meetData.entrySet()) {
            int meetId = entry.getKey();

            for (int resultsId : entry.getValue()) {
                ScoreMeet(meetId, resultsId);
            }
        }
    }

    private void ScoreMeet(int meetId, int resultsId) {
        if (scoredMeets.contains(meetId + " " + resultsId)) {
            System.out.println("Meet : " + meetId + " " + resultsId + " has already been scored");
            return;
        }

        scoredMeets.add(meetId + " " + resultsId);

        List<Race> newRaces = Fetcher.GetRaces(meetId, resultsId, raceIdCounter);
        raceIdCounter += newRaces.size();

        for (Race race : newRaces) {
            races.add(race);
            List<Person> raceParticipants = new ArrayList<>();
            for (Result result : race.getResults()) {
                Person runner = getPerson(result);

                if (runner == null) {
                    runner = new Person(result, runnerIdCounter);
                    runnerIdCounter++;
                    runners.add(runner);
                    System.out.println("New runner added!");
                }

                raceParticipants.add(runner);
            }

            for (int i = 0; i < raceParticipants.size(); i++) {
                Person participant = raceParticipants.get(i);
                participant.addRaces(raceParticipants.size()-1);

                double opponentRating = 0;
                for (int j = 0; j < i; j++) {
                    opponentRating += raceParticipants.get(j).getRanking();
                }
                for (int j = i+1; j < raceParticipants.size(); j++) {
                    opponentRating += raceParticipants.get(j).getRanking();
                }

                participant.addOpponentRatings(opponentRating);
                participant.addWinLoss(raceParticipants.size() - i - 1);

                participant.updateRanking();
            }

            for (Person p : raceParticipants) {
                p.finalizeRanking();
            }
        }
    }

    public void PrintRunners() {
        for (Person p : runners) {
            System.out.println(p);
        }
    }

    private Person getPerson(Result result) {
        for (Person p : runners) {
            if (p.getName().equals(result.getName()) && p.getGenderName().equalsIgnoreCase(result.getGenderName())) {
                return p;
            }
        }
        return null;
    }

    public List<Person> getRunners() {
        return runners;
    }

    public static void main(String[] args) {
        CrossRank crossRank = CrossRankSerializer.LoadRankings();
        crossRank.ScanMeets();
        CrossRankSerializer.SaveRankings(crossRank);
        CrossRankSerializer.SaveRunners(crossRank.runners);
        CrossRankSerializer.SaveRaces(crossRank.races);
    }
}
