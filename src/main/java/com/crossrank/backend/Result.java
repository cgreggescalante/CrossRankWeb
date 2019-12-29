package com.crossrank.backend;

import java.io.Serializable;

public class Result implements Serializable {
    private String name;
    private String raceName;
    private String genderName;
    private int place;

    public Result(String data) {
        try {
            String[] dataArray = data.substring(1, data.length() - 1).split("\",\"");

            name = dataArray[3].substring(12) + " " + dataArray[4].substring(11);
            raceName = dataArray[6].substring(15) + " " + dataArray[7].substring(13);
            genderName = dataArray[7].substring(13);
            place = Integer.parseInt(dataArray[2].substring(8));
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println(data);
        }
    }

    public String getName() {
        return name;
    }

    public String getRaceName() {
        return raceName;
    }

    public String getGenderName() {
        return genderName;
    }

}
