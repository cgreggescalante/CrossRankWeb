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

import com.crossrank.backend.HttpRequester;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Race object carries identifying information about a race, as well as
 * results in the form of a List of Result objects.
 */
@Getter
public class Race implements Serializable {
    private final String meetName;
    private final String division;
    private final String gender;

    private int meetDate;

    private final long id;

    private List<Result> results;

    /**
     * @param result A Result object which is the first Result from this race which has been found
     */
    public Race(Result result) {
        meetName = result.getMeetName();
        division = result.getDivisionName();
        gender = result.getGenderName();
        id = result.getResultsId();

        results = new ArrayList<>();

        /*
         * Uses the meetId to retrieve additional information on the meet.
         * Attempts to parse the date of the meet.
         */
        int meetId = Integer.parseInt(result.getMeetId());

        try {
            String content = HttpRequester.Get("https://mn.milesplit.com/api/v1/meets/" + meetId);
            JSONObject response = (JSONObject) new JSONParser().parse(content);
            JSONObject meetData = (JSONObject) response.get("data");

            String date = (String) meetData.get("dateStart");
            meetDate = Integer.parseInt(date.replace("-", ""));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param newResult A Result to be added to the Race's results List
     */
    public void addResult(Result newResult) {
        results.add(newResult);
    }
}
