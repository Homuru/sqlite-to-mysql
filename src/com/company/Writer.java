package com.company;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Writer {

    PrintWriter file;

    Writer(String filename) {
        try {
            FileWriter fileWriter = new FileWriter(filename + ".sql");
            file = new PrintWriter(fileWriter);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static PrintWriter createFile(String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(filename + ".sql");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        return printWriter;
    }

    public void writeCreateTable(ResultSet rs) {
        PrintWriter file = this.file;
        try {
            while (rs.next()) {
                String string = rs.getString(1).replaceAll("ON CONFLICT ROLLBACK", "");
                if (string.contains(new StringBuilder("VARCHAR,")))
                    string = string.replaceAll("VARCHAR,", "TEXT,");
                file.print(string);
                file.println(";");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void writeInsertRecord(ResultSet rs, String TableName, List<String> ColumnList) {
        PrintWriter file = this.file;
        try {
            while (rs.next()) {
                int index = 0;
                file.printf("INSERT INTO %s (", TableName);
                for (String ColumnName : ColumnList
                ) {
                    if (index == ColumnList.size() - 1)
                        file.printf("%s) ", ColumnName);
                    else
                        file.printf("%s,", ColumnName);
                    ++index;
                }
                file.print("VALUES (");
                index = 0;
                for (String ColumnName : ColumnList
                ) {
                    if (index == ColumnList.size() - 1) {
                        if (rs.getString(ColumnName) == null)
                            file.printf("%s); %n", rs.getString(ColumnName));
                        else
                            file.printf("\'%s\'); %n", rs.getString(ColumnName));
                    } else {
                        if (rs.getString(ColumnName) == null)
                            file.printf("%s,", rs.getString(ColumnName));
                        else
                            file.printf("\'%s\',", rs.getString(ColumnName));
                    }
                    ++index;
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void closeFile() {
        this.file.close();
    }
}
