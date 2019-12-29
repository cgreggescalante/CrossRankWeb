package com.crossrank.backend;

import java.util.ArrayList;
import java.util.List;

public class Race {
    private String raceName;
    private List<Result> results;

    public Race(String raceName) {
        this.raceName = raceName;

        results = new ArrayList<>();
    }

    public void addResult(Result newResult) {
        results.add(newResult);
    }

    public String getRaceName() {
        return raceName;
    }

    public List<Result> getResults() {
        return results;
    }
}
