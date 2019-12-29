package com.crossrank.backend;

import java.io.*;

public class PersonSerializer {
    public static CrossRank LoadRankings() {
        final File folder = new File("src\\main\\java\\com\\crossrank\\backend\\runners");

        if (folder.listFiles() != null) {
            System.out.println("has files");
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isFile()) {
                    System.out.println(fileEntry.getName());
                }
            }
        } else {
            System.out.println("no files");
        }

        return null;
    }

    public static void SaveRankings(CrossRank toSave) {
        try {
            FileOutputStream file = new FileOutputStream("rankings.txt");

            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(toSave);
            out.close();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PersonSerializer.LoadRankings();
    }
}