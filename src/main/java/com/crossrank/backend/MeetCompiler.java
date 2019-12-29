package com.crossrank.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeetCompiler {

    public static Map<Integer, List<Integer>> CompileMonth() {
        String content = HttpRequester.Get("https://mn.milesplit.com/results?month=11&year=&level=hs");

        List<Integer> meetIds = GatherData(content);

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
        Map<Integer, List<Integer>> resultIds = new HashMap<Integer, List<Integer>>();

        for (int meetId : meetIds) {
            String content = HttpRequester.Get("https://www.milesplit.com/meets/"+meetId+"/results");

            String regex = "(?<=results/)[\\d]+";
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(content);

            List<Integer> data = new ArrayList<>();

            while (matcher.find()) {
                int newData = Integer.parseInt(matcher.group(0).strip());
                if (!data.contains(newData)) {
                    data.add(newData);
                }
            }

            resultIds.put(meetId, data);
        }

        return resultIds;
    }

    public static void main(String[] args) {
        MeetCompiler.CompileMonth();
    }
}

