package com.crossrank.backend.datatypes;

import com.crossrank.backend.HttpRequester;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Race implements Serializable {
    private String meetName;
    private String division;
    private String sex;
    private List<Result> results;
    private int meetId;
    private int meetDate;
    private long id;

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

    public String getMeetName() {
        return meetName;
    }

    @SuppressWarnings("unused")
    public String getDivision() {
        return division;
    }

    public String getSex() {
        return sex;
    }

    public List<Result> getResults() {
        return results;
    }

    public int getMeetDate() {
        return meetDate;
    }

    public long getId() {
        return id;
    }
}
