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

    public static List<Race> GetRaces(int meetId, int resultsId, long raceIdCounter) {
        String url = "https://mn.milesplit.com/api/v1/meets/" + meetId + "/performances?resultsId=" + resultsId + "&fields=meetName%2CfirstName%2ClastName%2Cgender%2CgenderName%2CdivisionName%2CeventCode%2Cmark%2Cplace&teamScores=true&m=GET";

        String content = HttpRequester.Get(url);
        System.out.println(content);

        List<Result> results = new ArrayList<>();

        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(content);
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");

            for (Object o : jsonArray) {
                Result result = new Result((JSONObject) o);
                results.add(result);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Race> races = new ArrayList<>();

        for (Result result : results) {
            boolean added = false;
            for (Race race : races) {
                if (result.getMeetName().equals(race.getRaceName())) {
                    race.addResult(result);
                    added = true;
                    break;
                }
            }
            if (!added) {
                Race newRace = new Race(result.getMeetName(), raceIdCounter);
                raceIdCounter++;
                newRace.addResult(result);
                races.add(newRace);
            }
        }

        return races;
    }

    public static void main(String[] args) {
        Fetcher.GetRaces(362828, 681528, 0);
    }
}