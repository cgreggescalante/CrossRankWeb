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

public class Result implements Serializable {
    @Getter
    private final String eventCode;
    @Getter
    private final String gender;
    @Getter
    private final String place;
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;
    @Getter
    private final String meetName;
    @Getter
    private final String divisionName;
    @Getter
    private final String genderName;
    @Getter
    private final String mark;
    @Getter
    private final String state;
    @Getter
    private final String meetId;
    @Getter
    private final String athleteId;

    @Getter
    private final int resultsId;

    @Getter(lazy = true)
    private final String fullName = fullName();

    @Getter
    private double markDouble;
    @Getter
    @Setter
    private double rating;

    public Result(JSONObject data, int resultsId) {
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

        try {
            markDouble = Integer.parseInt(mark.substring(0, 2)) * 60
                    + Double.parseDouble(mark.substring(3));
        } catch (NumberFormatException e) {
            System.out.println(data);
            markDouble = 0;
        }

        rating = 1300;
    }

    private String fullName() {
        return firstName + " " + lastName;
    }
}
