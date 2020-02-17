package com.crossrank.backend.datatypes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetIndex implements Serializable {
    public Map<Integer, List<Integer>> meets;

    public MeetIndex() {
        meets = new HashMap<>();
    }
}
