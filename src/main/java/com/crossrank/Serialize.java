package com.crossrank;

import com.crossrank.backend.datatypes.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serialize {
    public static void Save(Object object) {
        String fileOutputName = null;
        if (object.getClass() == MeetIndex.class) {
            fileOutputName = "test.txt";
        } else if (object.getClass() == MeetResults.class) {
            fileOutputName = "test2.txt";
        } else if (object.getClass() == Rankings.class) {
            fileOutputName = "test3.txt";
        }

        try {
            if (object instanceof Map) {
                System.out.println("map");
                Map map = (Map) object;
                System.out.println(map.keySet().toArray()[0].getClass());
                System.out.println(String.class);
                if (map.keySet().toArray()[0].getClass() == String.class) {
                    fileOutputName = "map1.txt";
                }
            }

            fileOutputName = "map.txt";
        } catch (ClassCastException ignore) {

        }

        try {
            if (((List<Person>) object).get(0).getClass() == Person.class) {
                SavePersons((List<Person>) object);
            } else if (((List<Race>) object).get(0).getClass() == Race.class) {
                SaveRaces((List<Race>) object);
            }
        } catch (ClassCastException ignore) {

        }

        if (fileOutputName != null) {
            try {
                FileOutputStream file = new FileOutputStream(fileOutputName);
                ObjectOutputStream out = new ObjectOutputStream(file);

                out.writeObject(object);
                out.close();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void SavePersons(List<Person> runners) {
        for (Person p : runners) {
            try {
                FileOutputStream file = new FileOutputStream("src\\main\\resources\\runners\\" + p.getId() + ".txt");
                ObjectOutputStream out = new ObjectOutputStream(file);

                out.writeObject(p);
                out.close();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void SaveRaces(List<Race> races) {
        for (Race r : races) {
            try {
                FileOutputStream file = new FileOutputStream("src\\main\\resources\\races\\" + r.getId() + ".txt");
                ObjectOutputStream out = new ObjectOutputStream(file);

                out.writeObject(r);
                out.close();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> rankings = new HashMap<>() {{
            put("cake", 1);
        }};

        Save(rankings);
    }
}
