/*
 * Copyright 2020 Conor Gregg Escalante
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.crossrank.backend.datatypes;

import com.crossrank.LoadingBar;
import com.crossrank.backend.HttpRequester;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeetIndex implements Serializable {
    public Map<Integer, List<Integer>> meets;

    public MeetIndex() {
        meets = new TreeMap<>();
    }

    /**
     * Gathers meets for each month in a given year
     * @param year Integer denoting which year to gather meets from.
     */
    public void CompileSeason(int year) {
        System.out.println("COMPILING MEETS FROM YEAR : " + year);

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
