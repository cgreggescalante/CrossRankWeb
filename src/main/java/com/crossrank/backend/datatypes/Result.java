package com.crossrank.backend.datatypes;

import org.json.simple.JSONObject;

import java.io.Serializable;

public class Result implements Serializable {
    private final String eventCode;
    private final String gender;
    private final String place;
    private final String firstName;
    private final String lastName;
    private final String meetName;
    private final String divisionName;
    private final String genderName;
    private final String mark;
    private final String state;

    private final double markDouble;

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

        markDouble = Integer.parseInt(mark.substring(0, 2)) * 60
                + Double.parseDouble(mark.substring(3));
    }

    @SuppressWarnings("unused")
    public String getEventCode() {
        return eventCode;
    }

    @SuppressWarnings("unused")
    public String getGender() {
        return gender;
    }

    @SuppressWarnings("unused")
    public String getPlace() {
        return place;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getMeetName() {
        return meetName;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public String getGenderName() {
        return genderName;
    }

    @SuppressWarnings("unused")
    public String getMark() {
        return mark;
    }

    public String getState() {
        return state;
    }

    public double getMarkDouble() {
        return markDouble;
    }
}
