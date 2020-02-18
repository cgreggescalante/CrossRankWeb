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

import com.crossrank.backend.HttpRequester;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Race implements Serializable {
    @Getter private String meetName;
    @Getter private String division;
    @Getter private String sex;
    @Getter private List<Result> results;
    private int meetId;
    @Getter private int meetDate;
    @Getter private long id;

    public Race(Result result, long id) {
        meetName = result.getMeetName();
        division = result.getDivisionName();
        sex = result.getGenderName();
        meetId = Integer.parseInt(result.getMeetId());
        this.id = id;

        try {
            String content = HttpRequester.Get("https://mn.milesplit.com/api/v1/meets/" + meetId);
            JSONObject response = (JSONObject) new JSONParser().parse(content);
            JSONObject meetData = (JSONObject) response.get("data");

            String date = (String) meetData.get("dateStart");
            meetDate = Integer.parseInt(date.replace("-", ""));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        results = new ArrayList<>();
    }

    public void addResult(Result newResult) {
        results.add(newResult);
    }
}
