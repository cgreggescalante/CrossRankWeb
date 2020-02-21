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

import com.crossrank.LoadingBar;
import com.crossrank.backend.HttpRequester;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * The MeetIndex Class provides a way to locate and store the meetIds along
 * with the respective resultIds.
 */

public class MeetIndex implements Serializable {
    private static String meetIdRegex = "(?<=meets/)[\\d]+(?=/results)";
    private static Pattern meetIdPattern = Pattern.compile(meetIdRegex, Pattern.MULTILINE);

    private static String resultIdRegex = "(?<=results/)[\\d]+";
    private static Pattern resultIdPattern = Pattern.compile(resultIdRegex, Pattern.MULTILINE);

    @Getter
    private Map<Integer, List<Integer>> meets;

    public MeetIndex() {
        meets = new TreeMap<>();
    }

    /**
     * Gathers meets for the Cross Coutnry Running season in a given year.
     * The season is defined as being the months August through December.
     * @param year Integer denoting which year to gather meets from.
     */
    public void CompileSeason(int year) {
        System.out.println("COMPILING MEETS FROM YEAR : " + year);

        LoadingBar loadingBar = new LoadingBar(50, 5);

        /* For months August through December */

        Map<Integer, List<Integer>> month;

        for (int i = 8; i < 13; i++) {
            month = CompileMonth(i, year);

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
        String html;

        do {
            prevLength = meetIds.size();

            /* Retrieves the HTML for the results list of the given month and year */
            html = HttpRequester.Get("https://mn.milesplit.com/results?month=" + month + "&year=" + year + "&level=hs&page=" + page);

            /* Extracts all the meetIds from the HTML and adds to the meetIds List */
            meetIds.addAll(GatherMeetIds(html));

            page++;
        } while (meetIds.size() != prevLength); /* Ends once a page is found with no meetIds on it */

        /* Retreives the resultIds for eash of the meetIds */
        return GetResultIds(meetIds);
    }

    /**
     * Extracts all meetIds from the given HTML content.
     * @param html A string containing the HTML for the page.
     * @return A List of Integer meetIds.
     */
    public static List<Integer> GatherMeetIds(String html) {
        Matcher matcher = meetIdPattern.matcher(html);

        List<Integer> meetIds = new ArrayList<>();

        while (matcher.find()) {
            meetIds.add(Integer.parseInt(matcher.group(0).strip()));
        }

        return meetIds;
    }

    /**
     * Gathers the resultIds for the given meetIds.
     * @param meetIds A List of Integer meetIds to gather resultIds for.
     * @return A HashMap containing Integer meetIds as keys and a List of Integer
     * resultIds as values.
     */
    public static Map<Integer, List<Integer>> GetResultIds(List<Integer> meetIds) {
        List<Integer> data;
        String content;
        int newData;

        Map<Integer, List<Integer>> resultIds = new HashMap<>();

        for (int meetId : meetIds) {
            content = HttpRequester.Get("https://www.milesplit.com/meets/"+meetId+"/results");

            data = new ArrayList<>();

            if (content != null) {
                Matcher resultIdMatcher = resultIdPattern.matcher(content);

                while (resultIdMatcher.find()) {
                    newData = Integer.parseInt(resultIdMatcher.group(0).strip());
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
