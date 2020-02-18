/*
 * Copyright 2020 Conor Gregg Escalante
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.crossrank.backend;

import com.crossrank.backend.datatypes.MeetIndex;
import com.crossrank.backend.datatypes.Person;
import com.crossrank.backend.datatypes.Race;

import java.io.*;
import java.util.*;

public class CrossRankSerializer {
    public static CrossRank LoadRankings() {
        try {
            FileInputStream file = new FileInputStream("src\\main\\resources\\rankings.txt");
            ObjectInputStream in = new ObjectInputStream(file);

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

    public static Person LoadRunner(long id) {
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

    public static void SaveRunnerDirectory(Map<String, Long> runnerDirectory) {
        try {
            FileOutputStream file = new FileOutputStream("src\\main\\resources\\runnerDirectory.txt");
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(runnerDirectory);
            out.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Long> LoadRunnerDirectory() {
        try {
            FileInputStream file = new FileInputStream("src\\main\\resources\\runnerDirectory.txt");
            ObjectInputStream in = new ObjectInputStream(file);

            @SuppressWarnings("unchecked") Map<String, Long> runnerDirectory = (TreeMap<String, Long>) in.readObject();

            in.close();
            file.close();

            return runnerDirectory;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void SaveMeetIndex(MeetIndex meetIndex) {
        try {
            FileOutputStream file = new FileOutputStream("src\\main\\resources\\meetIndex.txt");
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(meetIndex);
            out.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MeetIndex LoadMeetIndex() {
        try {
            FileInputStream file = new FileInputStream("src\\main\\resources\\meetIndex.txt");
            ObjectInputStream in = new ObjectInputStream(file);

            MeetIndex meetIndex = (MeetIndex) in.readObject();

            in.close();
            file.close();

            return meetIndex;

        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void SaveSortedRankings(CrossRank crossRank) {
        try {
            FileOutputStream file = new FileOutputStream("src\\main\\resources\\sortedRankingsBoys.txt");
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(crossRank.getSortedRankingsBoys());
            out.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream file = new FileOutputStream("src\\main\\resources\\sortedRankingsGirls.txt");
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(crossRank.getSortedRankingsGirls());
            out.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Double, String> LoadSortedRankings(String gender) {
        try {
            FileInputStream file = new FileInputStream("src\\main\\resources\\sortedRankings" + gender + ".txt");
            ObjectInputStream in = new ObjectInputStream(file);

            Map<Double, String> rankings = (Map<Double, String>) in.readObject();

            in.close();
            file.close();

            return rankings;

        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
