package com.crossrank.backend;

import java.io.*;

public class Serializer {
    public static CrossRank LoadRankings() {
        try {
            // Reading the object from a file
            FileInputStream file = new FileInputStream("rankings.txt");
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            CrossRank crossRank = (CrossRank) in.readObject();

            in.close();
            file.close();

            System.out.println("Object has been dematerialized ");

            return crossRank;

        } catch(IOException | ClassNotFoundException ignored) { }
        System.out.println("No save found, creating new save");
        return new CrossRank();
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
}
