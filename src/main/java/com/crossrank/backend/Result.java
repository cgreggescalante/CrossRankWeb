package com.crossrank.backend;

import java.io.Serializable;
import java.util.Arrays;

public class Result implements Serializable {
    private String name;
    private String raceName;
    private String genderName;
    private int place;

    public Result(String data) {
        try {
            data = data.replaceAll("\"", "");
            String[] dataArray = data.split(",");

            System.out.println(Arrays.toString(dataArray));
            if (dataArray[0].substring(10).equals("5000m")) {
                place = Integer.parseInt(dataArray[2].substring(6));
                name = dataArray[3].substring(10) + " " + dataArray[4].substring(9);
                raceName = dataArray[5].substring(9) + " " + dataArray[6].substring(13) + " " + dataArray[7].substring(11);
                genderName = dataArray[7].substring(11);
                genderName = genderName.replaceAll("\"", "");


                if (!genderName.equalsIgnoreCase("boys") && !genderName.equalsIgnoreCase("girls")) {
                    System.out.println(genderName + " " + Arrays.toString(dataArray) + dataArray[7]);
                }
            }

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
