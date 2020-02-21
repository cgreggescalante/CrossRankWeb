/*
 * Copyright 2020 Conor Gregg Escalante
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.crossrank.backend.datatypes;

import com.crossrank.backend.HttpRequester;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeetResults implements Serializable {
    @Getter
    private List<Race> races;

    public MeetResults() {
        races = new ArrayList<>();
    }

    private final static String[] fields = new String[] {
            "state",
            "meetName",
            "firstName",
            "lastName",
            "gender",
            "genderName",
            "divisionName",
            "eventCode",
            "mark",
            "place",
            "meetId",
            "athleteId"
    };

    public static List<Race> GetRaces(int meetId, List<Integer> resultIds, long raceIdCounter) {

        List<Result> results = new ArrayList<>();

        for (int resultsId : resultIds) {
            StringBuilder url = new StringBuilder();

            url.append("https://mn.milesplit.com/api/v1/meets/")
                    .append(meetId)
                    .append("/performances?resultsId=")
                    .append(resultsId)
                    .append("&fields=");

            for (int i = 0; i < fields.length-1; i++) {
                url.append(fields[i])
                        .append("%2C");
            }

            url.append(fields[fields.length-1]);

            String content = HttpRequester.Get(url.toString());

            try {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(content);
                JSONArray jsonArray = (JSONArray) jsonObject.get("data");

                for (Object o : jsonArray) {
                    JSONObject obj = (JSONObject) o;
                    if (obj.get("state").equals("MN") && obj.get("eventCode").equals("5000m") && ((String) obj.get("mark")).length() < 10) {
                        Result result = new Result(obj, resultsId);
                        results.add(result);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        List<Race> races = new ArrayList<>();

        for (Result result : results) {
            boolean added = false;
            for (Race race : races) {
                if (result.getMeetName().equals(race.getMeetName()) &&
                        result.getGenderName().equals(race.getGender())) {
                    race.addResult(result);
                    added = true;
                    break;
                }
            }
            if (!added) {
                Race newRace = new Race(result);
                raceIdCounter++;
                newRace.addResult(result);
                races.add(newRace);
            }
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/crossrank", "PotatoTax", "PotatoTax1707")) {

                Statement statement = conn.createStatement();

                PreparedStatement insertRunner = conn.prepareStatement("INSERT INTO runner(id, first_name, last_name, gender, rating) VALUES (?, ?, ?, ?, ?)");
                PreparedStatement insertMeet = conn.prepareStatement("INSERT INTO meet (milesplit_meet_id, milesplit_result_id, name, gender, date) VALUES (?, ?, ?, ?, ?)");
                PreparedStatement insertResult = conn.prepareStatement("INSERT INTO result (meet_id, runner_id, place, mark) VALUES (?, ?, ?, ?)");

                for (Race race : races) {
                    insertMeet.setString(1, Integer.toString(race.getMeetId()));
                    insertMeet.setString(2, Long.toString(race.getResultId()));
                    insertMeet.setString(3, race.getMeetName());
                    insertMeet.setString(4, race.getGender());
                    insertMeet.setString(5, race.getMeetDateString());
                    insertMeet.execute();

                    ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
                    resultSet.next();
                    long id = resultSet.getLong("last_insert_id()");

                    for (Result result : race.getResults()) {
                        ResultSet resultSet1 = statement.executeQuery("SELECT * from runner WHERE id = " + result.getAthleteId());

                        if (!resultSet1.next()) { // if runner does not exist, add to table
                            insertRunner.setString(1, result.getAthleteId());
                            insertRunner.setString(2, result.getFirstName());
                            insertRunner.setString(3, result.getLastName());
                            insertRunner.setString(4, result.getGenderName());
                            insertRunner.setObject(5, result.getRating());
                            insertRunner.execute();
                        }

                        insertResult.setLong(1, id);
                        insertResult.setString(2, result.getAthleteId());
                        insertResult.setString(3, result.getPlace());
                        insertResult.setObject(4, result.getMarkDouble());
                        insertResult.execute();
                    }
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


        return races;
    }
}
