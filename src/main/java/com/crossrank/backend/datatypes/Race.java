package com.crossrank.backend.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Race implements Serializable {
    private String meetName;
    private String division;
    private String sex;
    private List<Result> results;
    private final long id;

    public Race(Result result, long id) {
        meetName = result.getMeetName();
        division = result.getDivisionName();
        sex = result.getGenderName();
        this.id = id;

        results = new ArrayList<>();
    }

    public void addResult(Result newResult) {
        results.add(newResult);
    }

    public String getMeetName() {
        return meetName;
    }

    public String getDivision() {
        return division;
    }

    public String getSex() {
        return sex;
    }

    public List<Result> getResults() {
        return results;
    }

    public long getId() {
        return id;
    }
}
