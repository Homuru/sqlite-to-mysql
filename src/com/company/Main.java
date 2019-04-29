package com.company;

import java.io.*;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        String filename = "non_password_jlpt.sqlite3";
        Writer writer = new Writer(filename);
        Database db = new Database(filename);
        db.generateCreateTable(writer);
        db.generateInsertRecord(writer);
        writer.closeFile();

    }
}
