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

package com.crossrank.backend;

import com.crossrank.backend.datatypes.Race;
import com.crossrank.backend.datatypes.Result;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class Fetcher {

    public static List<Race> GetRaces(int meetId, List<Integer> resultIds, long raceIdCounter) {

        List<Result> results = new ArrayList<>();

        for (int resultsId : resultIds) {
            @SuppressWarnings("SpellCheckingInspection") String url = "https://mn.milesplit.com/api/v1/meets/" + meetId + "/performances?resultsId=" + resultsId + "&fields=state%2CmeetName%2CfirstName%2ClastName%2Cgender%2CgenderName%2CdivisionName%2CeventCode%2Cmark%2Cplace%2CmeetId&teamScores=true&m=GET";

            String content = HttpRequester.Get(url);

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

    public static void main(String[] args) {
        List<Integer> resultIds = new ArrayList<>(){{
            add(681528);
        }};
        Fetcher.GetRaces(362828, resultIds, 0);
    }
}