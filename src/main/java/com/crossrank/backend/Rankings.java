package com.crossrank.backend;

import java.util.ArrayList;
import java.util.List;

public class Rankings {
    private List<Person> runners;

    public Rankings() {
        runners = new ArrayList<>();
    }

    public void addRunner(Person newRunner) {
        runners.add(newRunner);
    }

    public List<Person> getRunners() {
        return runners;
    }
}
