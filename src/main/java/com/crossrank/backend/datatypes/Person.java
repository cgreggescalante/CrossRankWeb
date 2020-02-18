package com.crossrank.backend.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Person implements Serializable {
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;
    @Getter
    private final String genderName;

    @Getter
    @Setter
    private double ranking;

    @Getter
    private int races;
    @Getter
    private final long id;

    @Getter
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

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @JsonIgnore
    public double getRecentMark() {
        return results.get(results.size() - 1).getMarkDouble();
    }

    public void addResult(Result result) {
        results.add(result);
    }
}
