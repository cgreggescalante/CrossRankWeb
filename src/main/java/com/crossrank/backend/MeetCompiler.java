package com.crossrank.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeetCompiler {

    public static Map<Integer, List<Integer>> CompileSeason(int year) {
        Map<Integer, List<Integer>> resultIDs = new HashMap<>();

        for (int i = 8; i < 13; i++) {
            Map<Integer, List<Integer>> month = CompileMonth(i, year);

            resultIDs.putAll(month);
        }

        return resultIDs;
    }

    public static Map<Integer, List<Integer>> CompileMonth(int month, int year) {
        List<Integer> meetIds = new ArrayList<>();
        int prevLength;
        int page = 1;

        do {
            prevLength = meetIds.size();

            String content = HttpRequester.Get("https://mn.milesplit.com/results?month=" + month + "&year=" + year + "&level=hs&page=" + page);

            meetIds.addAll(GatherData(content));

            page++;
        } while (meetIds.size() != prevLength);

        return GetResultIds(meetIds);
    }

    public static List<Integer> GatherData(String content) {
        String regex = "(?<=meets/)[\\d]+(?=/results)";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        List<Integer> data = new ArrayList<>();

        while (matcher.find()) {
            data.add(Integer.parseInt(matcher.group(0).strip()));
        }

        return data;
    }

    public static Map<Integer, List<Integer>> GetResultIds(List<Integer> meetIds) {
        Map<Integer, List<Integer>> resultIds = new HashMap<>();

        for (int meetId : meetIds) {
            String content = HttpRequester.Get("https://www.milesplit.com/meets/"+meetId+"/results");

            List<Integer> data = new ArrayList<>();

            if (content != null) {
                String regex = "(?<=results/)[\\d]+";
                Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(content);



                while (matcher.find()) {
                    int newData = Integer.parseInt(matcher.group(0).strip());
                    if (!data.contains(newData)) {
                        data.add(newData);
                    }
                }
            }

            resultIds.put(meetId, data);
        }

        return resultIds;
    }
}

