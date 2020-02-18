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
