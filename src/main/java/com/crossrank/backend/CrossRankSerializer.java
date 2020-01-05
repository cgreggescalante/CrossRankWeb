package com.crossrank.backend;

import com.crossrank.backend.datatypes.Person;
import com.crossrank.backend.datatypes.Race;

import java.io.*;
import java.util.List;

public class CrossRankSerializer {
    public static CrossRank LoadRankings() {
        try {
            FileInputStream file = new FileInputStream("src\\main\\resources\\rankings.txt");
            ObjectInputStream in = new ObjectInputStream(file);

            System.out.println("loading rankings");

            CrossRank crossRank = (CrossRank) in.readObject();

            in.close();
            file.close();

            return crossRank;

        } catch(IOException | ClassNotFoundException ignored) { }
        return new CrossRank();
    }

    public static void SaveRankings(CrossRank toSave) {
        try {
            FileOutputStream file = new FileOutputStream("src\\main\\resources\\rankings.txt");

            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(toSave);
            out.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void SaveRunners(List<Person> runners) {
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

    public static Person LoadRunner(int id) {
        try {
            FileInputStream file = new FileInputStream("src\\main\\resources\\runners\\" + id + ".txt");
            ObjectInputStream in = new ObjectInputStream(file);

            Person runner = (Person) in.readObject();

            in.close();
            file.close();

            return runner;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
