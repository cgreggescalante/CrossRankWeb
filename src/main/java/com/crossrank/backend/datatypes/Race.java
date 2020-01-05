package com.crossrank.backend.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Race implements Serializable {
    private String raceName;
    private List<Result> results;
    private final long id;

    public Race(String raceName, long id) {
        this.raceName = raceName;
        this.id = id;

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

    public long getId() {
        return id;
    }
}
