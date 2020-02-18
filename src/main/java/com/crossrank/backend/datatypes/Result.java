package com.crossrank.backend.datatypes;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.io.Serializable;

public class Result implements Serializable {
    @Getter
    private final String eventCode;
    @Getter
    private final String gender;
    @Getter
    private final String place;
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;
    @Getter
    private final String meetName;
    @Getter
    private final String divisionName;
    @Getter
    private final String genderName;
    @Getter
    private final String mark;
    @Getter
    private final String state;
    @Getter
    private final String meetId;

    @Getter(lazy = true)
    private final String fullName = fullName();

    @Getter
    private double markDouble;
    @Getter
    @Setter
    private double rating;

    public Result(JSONObject data) {
        eventCode = (String) data.get("eventCode");
        gender = (String) data.get("gender");
        place = (String) data.get("place");
        firstName = (String) data.get("firstName");
        lastName = (String) data.get("lastName");
        meetName = (String) data.get("meetName");
        divisionName = (String) data.get("divisionName");
        genderName = (String) data.get("genderName");
        mark = (String) data.get("mark");
        state = (String) data.get("state");
        meetId = (String) data.get("meetId");

        try {
            markDouble = Integer.parseInt(mark.substring(0, 2)) * 60
                    + Double.parseDouble(mark.substring(3));
        } catch (NumberFormatException e) {
            System.out.println(data);
            markDouble = 0;
        }

        rating = 1300;
    }

    private String fullName() {
        return firstName + " " + lastName;
    }
}
