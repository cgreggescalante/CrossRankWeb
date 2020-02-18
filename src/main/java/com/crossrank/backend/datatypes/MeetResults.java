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

    public static List<Race> GetRaces(int meetId, List<Integer> resultIds, long raceIdCounter) {

        List<Result> results = new ArrayList<>();

        for (int resultsId : resultIds) {
            StringBuilder url = new StringBuilder();

            url.append("https://mn.milesplit.com/api/v1/meets/")
                    .append(meetId)
                    .append("/performances?resultsId=")
                    .append(resultsId)
                    .append("&fields=");

            for (int i = 0; i < fields.length-1; i++) {
                url.append(fields[i])
                        .append("%2C");
            }

            url.append(fields[fields.length-1]);

            String content = HttpRequester.Get(url.toString());

            try {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(content);
                JSONArray jsonArray = (JSONArray) jsonObject.get("data");

                for (Object o : jsonArray) {
                    JSONObject obj = (JSONObject) o;
                    if (obj.get("state").equals("MN") && obj.get("eventCode").equals("5000m") && ((String) obj.get("mark")).length() < 10) {
                        Result result = new Result(obj);
                        results.add(result);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        List<Race> races = new ArrayList<>();

        for (Result result : results) {
            boolean added = false;
            for (Race race : races) {
                if (result.getMeetName().equals(race.getMeetName()) &&
                        result.getGenderName().equals(race.getSex())) {
                    race.addResult(result);
                    added = true;
                    break;
                }
            }
            if (!added) {
                Race newRace = new Race(result, raceIdCounter);
                raceIdCounter++;
                newRace.addResult(result);
                races.add(newRace);
            }
        }

        return races;
    }
}
