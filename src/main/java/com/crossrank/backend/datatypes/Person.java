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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Person class stores basic identification data for a runner, as well as a List of
 * all the runner's Results.
 */
@Getter
public class Person implements Serializable {
    private final String firstName;
    private final String lastName;
    private final String fullName;
    private final String genderName;

    @Setter
    private double ranking;

    private int raceCount;
    private final int id;

    private final List<Result> results;

    /**
     * @param result a Result object which is the first
     */
    public Person(Result result) {
        id = Integer.parseInt(result.getAthleteId());
        firstName = result.getFirstName();
        lastName = result.getLastName();
        fullName = result.getFullName();
        genderName = result.getGenderName();

        results = new ArrayList<>();
        results.add(result);

        ranking = 1000;
        raceCount = 0;
    }

    public Person() {
        this.id = -1;
        results = null;

        firstName = null;
        lastName = null;
        fullName = "No athlete by that name was found.";
        genderName = null;

        ranking = -1;
        raceCount = -1;
    }

    /**
     * Increments the value of raceCount, used in the ELO Rating algorithm
     */
    public void addRace() {
        raceCount++;
    }

    /**
     * JSONIgnore annotation means that this method's value is not serialized with
     * the rest of the class' fields.
     * @return a double value of the runner's most recent race time in seconds
     */
    @JsonIgnore
    public double getRecentMark() {
        return results.get(results.size() - 1).getMarkDouble();
    }

    /**
     * @param result A Result object to be added to the runner's Results List
     */
    public void addResult(Result result) {
        results.add(result);
    }
}
