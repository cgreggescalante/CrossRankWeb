package com.crossrank.backend.datatypes;

import com.crossrank.LoadingBar;
import com.crossrank.backend.HttpRequester;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeetIndex implements Serializable {
    public Map<Integer, List<Integer>> meets;

    public MeetIndex() {
        meets = new HashMap<>();
    }

    /**
     * Gathers meets for each month in a given year
     * @param year Integer denoting which year to gather meets from.
     */
    public void CompileSeason(int year) {

        LoadingBar loadingBar = new LoadingBar(50, 5);

        for (int i = 8; i < 13; i++) {
            Map<Integer, List<Integer>> month = CompileMonth(i, year);

            loadingBar.updateProgress();
            meets.putAll(month);
        }

        loadingBar.end();

    }

    /**
     * Gathers meets for a given month.
     * @param month Integer denoting the month to gather meets from.
     * @param year Integer denoting the year to gather meets from.
     * @return A HashMap containing Integer meetIds as keys and a List of Integer
     * resultIds as values.
     */
    public Map<Integer, List<Integer>> CompileMonth(int month, int year) {
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

    /**
     * Locates all the meetIds on the MileSplit meet results page.
     * @param content A string containing the HTML for the page.
     * @return A list of Integer meetIds.
     */
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

    /**
     * Gathers the resultIds for the given meetIds.
     * @param meetIds A List of Integer meetIds to gather resultIds for.
     * @return A HashMap containing Integer meetIds as keys and a List of Integer
     * resultIds as values.
     */
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
