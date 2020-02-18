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

package com.crossrank.backend.serialization;

import com.crossrank.backend.datatypes.Person;

import java.io.*;
import java.util.List;

public class PersonSerializer {
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
