package com.crossrank.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Person implements Serializable {
    private String name;
    private String genderName;

    private double ranking;
    private double tempRanking;
    private double opponentRankings;

    private int races;
    private int winLoss;
    private final long id;

    private List<Result> results;

    public Person(Result result, long id) {
        this.id = id;
        results = new ArrayList<>();
        results.add(result);

        name = result.getName();
        genderName = result.getGenderName();

        ranking = 1300;
        tempRanking = 0;
        races = 0;
        winLoss = 0;
        opponentRankings = 0;
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

    public String getName() {
        return name;
    }

    public double getRanking() {
        return ranking;
    }

    public String getGenderName() {
        return genderName;
    }

    @Override
    public String toString() {
        return name + ' ' + (int) ranking;
    }

    public long getId() {
        return id;
    }
}
