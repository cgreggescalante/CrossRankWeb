package com.crossrank.backend.datatypes;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Rankings {
    @Getter
    private final List<Person> runners;

    public Rankings() {
        runners = new ArrayList<>();
    }

    public void addRunner(Person newRunner) {
        runners.add(newRunner);
    }
}
