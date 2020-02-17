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

    private int races;
    private final long id;

    private final List<Result> results;

    public Person(Result result, long id) {
        this.id = id;
        results = new ArrayList<>();
        results.add(result);

        firstName = result.getFirstName();
        lastName = result.getLastName();
        genderName = result.getGenderName();

        ranking = 1000;
        races = 0;
    }

    public Person() {
        this.id = -1;
        results = null;

        firstName = "No athlete by that name was found.";
        lastName = null;
        genderName = null;

        ranking = -1;
        races = -1;
    }

    public void addRace() {
        races++;
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

    public void setRanking(double ranking) {
        this.ranking = ranking;
    }

    public void addResult(Result result) {
        results.add(result);
    }
}
