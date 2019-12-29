package com.crossrank.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fetcher {

    public static List<Race> GetRaces(int meetId, int resultsId, long raceIdCounter) {
        final String regex = "(?<!:)(?<=\\{)[\"\\w\\s:,.\\\\/\\-]{50,}(?=})";
        String content;

        String url = "https://mn.milesplit.com/api/v1/meets/" + meetId + "/performances?resultsId=" + resultsId + "&fields=meetName%2CfirstName%2ClastName%2Cgender%2CgenderName%2CdivisionName%2CeventCode%2Cmark%2Cplace&teamScores=true&m=GET";

        content = HttpRequester.Get(url);
        System.out.println(content);

        List<Result> results = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            results.add(new Result(matcher.group(0)));
        }

        List<Race> races = new ArrayList<>();

        for (Result result : results) {
            boolean added = false;
            for (Race race : races) {
                if (result.getRaceName().equals(race.getRaceName())) {
                    race.addResult(result);
                    added = true;
                    break;
                }
            }
            if (!added) {
                Race newRace = new Race(result.getRaceName(), raceIdCounter);
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