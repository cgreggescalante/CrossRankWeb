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

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.io.Serializable;

/**
 * Result class is used to store the data from an individual's performance at a race
 * Implements Serializable for saving to a file
 */
@Getter
public class Result implements Serializable {
    private final String athleteId;
    private final String divisionName;
    private final String eventCode;
    private final String firstName;
    private final String lastName;
    private final String gender;
    private final String genderName;
    private final String place;
    private final String meetName;
    private final String mark;
    private final String state;
    private final String meetId;

    private final int resultsId;

    /*
     * Annotation means that fullName() is ran once, then the value is cached for later use.
     * Prevents repetitive calls.
     */
    @Getter(lazy = true)
    private final String fullName = fullName();

    private double markDouble;
    @Setter
    private double rating;

    /**
     * @param data a JSONObject containing the data of a single race result retrieved from MileSplit
     * @param resultsId an Integer of the MileSplit identifier tied to the race's results
     */
    public Result(JSONObject data, int resultsId) {
        /*
         * Extracts the data from the JSONObject into the Result's fields.
         * The result of the data.get() method is a generic Object which must be cast to a String
         */
        eventCode = (String) data.get("eventCode");
        gender = (String) data.get("gender");
        place = (String) data.get("place");
        firstName = (String) data.get("firstName");
        lastName = (String) data.get("lastName");
        meetName = (String) data.get("meetName");
        divisionName = (String) data.get("divisionName");
        genderName = (String) data.get("genderName");
        mark = (String) data.get("mark");
        state = (String) data.get("state");
        meetId = (String) data.get("meetId");
        athleteId = (String) data.get("athleteId");

        this.resultsId = resultsId;

        /* Attempts to parse the time in format mm:ss to a double of seconds */
        try {
            markDouble = Integer.parseInt(mark.substring(0, 2)) * 60
                    + Double.parseDouble(mark.substring(3));
        } catch (NumberFormatException e) {

            /* Prints the JSONObject data and defaults the markDouble to 0 if parsing fails*/
            System.out.println(data);
            markDouble = 0;
        }

        /* Rating value is initialized at 1300 */
        rating = 1300;
    }

    private String fullName() {
        return firstName + " " + lastName;
    }
}
