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

@Getter
public class Person implements Serializable {
    private final String firstName;
    private final String lastName;
    private final String genderName;

    @Setter
    private double rating;

    private int races;
    private final int id;

    private final List<Result> results;

    public Person(Result result) {
        id = Integer.parseInt(result.getAthleteId());
        results = new ArrayList<>();
        results.add(result);

        firstName = result.getFirstName();
        lastName = result.getLastName();
        genderName = result.getGenderName();

        rating = 1000;
        races = 0;
    }

    public Person() {
        this.id = -1;
        results = null;

        firstName = "No athlete by that name was found.";
        lastName = null;
        genderName = null;

        rating = -1;
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
