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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MeetResults implements Serializable {
    @Getter
    private List<Race> races;

    public MeetResults() {
        races = new ArrayList<>();
    }

    // The list of fields to be gathered for each result
    private final static String[] fields = new String[] {
            "state",
            "meetName",
            "firstName",
            "lastName",
            "gender",
            "genderName",
            "divisionName",
            "eventCode",
            "mark",
            "place",
            "meetId",
            "athleteId"
    };

    /**
     * Takes a meetId and the resultIds for each race at the meet, and returns a List of
     * Race objects containing the results for each race. Gathers result by making calls
     * to the MileSplit API
     *
     * @param meetId An Integer identifier for the meet
     * @param resultIds List of Integers which are the identifiers for each race
     * @return A List of Race objects
     */
    public static List<Race> GetRaces(int meetId, List<Integer> resultIds) {

        List<Result> results = new ArrayList<>();

        // Gathers the results from each race
        for (int resultsId : resultIds) {
            StringBuilder url = new StringBuilder();

            // Generating the URL for the API call
            url.append("https://mn.milesplit.com/api/v1/meets/")
                    .append(meetId)
                    .append("/performances?resultsId=")
                    .append(resultsId)
                    .append("&fields=");

            // Adds each of the desired fields to the URL
            for (int i = 0; i < fields.length-1; i++) {
                url.append(fields[i])
                        .append("%2C");
            }

            url.append(fields[fields.length-1]);

            String content = HttpRequester.Get(url.toString());

            try {
                // Parses the JSONObject, extracts the results array
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(content);
                JSONArray jsonArray = (JSONArray) jsonObject.get("data");

                // Creates Result objects from the JSONArray of the results
                for (Object o : jsonArray) {
                    JSONObject obj = (JSONObject) o;

                    // Only adds the Result if it came from a 5000m race ran by a Minnesota runner
                    if (obj.get("state").equals("MN") &&
                        obj.get("eventCode").equals("5000m") &&
                        ((String) obj.get("mark")).length() < 10) {

                        Result result = new Result(obj, resultsId);
                        results.add(result);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        List<Race> races = new ArrayList<>();

        /*
         * Attempts to find the corresponding Race for each Result.
         * If the Race is found, the Result is added to the Race,
         * otherwise, a new Race is created from the data contained in the Result.
         */
        for (Result result : results) {
            boolean added = false;
            for (Race race : races) {
                if (result.getMeetName().equals(race.getMeetName()) &&
                        result.getGenderName().equals(race.getGender())) {
                    race.addResult(result);
                    added = true;
                    break;
                }
            }
            if (!added) {
                Race newRace = new Race(result);
                newRace.addResult(result);
                races.add(newRace);
            }
        }

        return races;
    }
}
