package com.crossrank.backend;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;

public class Result implements Serializable {
    private String eventCode;
    private String gender;
    private String place;
    private String firstName;
    private String lastName;
    private String meetName;
    private String divisionName;
    private String genderName;
    private String mark;

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
    }

    public String getEventCode() {
        return eventCode;
    }

    public String getGender() {
        return gender;
    }

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

    public String getMark() {
        return mark;
    }
}
