package com.crossrank.backend.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Person implements Serializable {
    private final String firstName;
    private final String lastName;
    private final String genderName;

    private double ranking;
    private double tempRanking;
    private double opponentRankings;

    private int races;
    private int winLoss;
    private final long id;

    private final List<Result> results;

    public Person(Result result, long id) {
        this.id = id;
        results = new ArrayList<>();
        results.add(result);

        firstName = result.getFirstName();
        lastName = result.getLastName();
        genderName = result.getGenderName();

        ranking = 1300;
        tempRanking = 0;
        races = 0;
        winLoss = 0;
        opponentRankings = 0;
    }

    public Person() {
        this.id = -1;
        results = null;

        firstName = "No athlete by that name was found.";
        lastName = null;
        genderName = null;

        ranking = -1;
        tempRanking = -1;
        races = -1;
        winLoss = -1;
        opponentRankings = -1;
    }

    public void updateRanking() {
        tempRanking = (opponentRankings + 400 * winLoss) / races;
    }

    public void finalizeRanking() {
        ranking = tempRanking;
    }

    public void addRaces(int i) {
        races += i;
    }

    public void addOpponentRatings(double opponentRating) {
        opponentRankings += opponentRating;
    }

    public void addWinLoss(int i) {
        winLoss += i;
    }

    public void addResults(Result result) {
        results.add(result);
    }

    @SuppressWarnings("unused")
    public String getFirstName() {
        return firstName;
    }

    @SuppressWarnings("unused")
    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getGenderName() {
        return genderName;
    }

    public double getRanking() {
        return ranking;
    }

    @SuppressWarnings("unused")
    public int getRaces() {
        return races;
    }

    @SuppressWarnings("unused")
    public int getWinLoss() {
        return winLoss;
    }

    public long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public List<Result> getResults() {
        return results;
    }

    @JsonIgnore
    public double getRecentMark() {
        return results.get(results.size() - 1).getMarkDouble();
    }
}
