package com.company;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Database {

    List<String> TableList;
    private Connection connection;

    Database(String db_name) {
        Connection conn = null;
        String url = "jdbc:sqlite:./db/" + db_name;
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                System.out.println("Connection to SQLite has been established.");
                connection = conn;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        connection = conn;
        this.setTableList();
    }


    private void setTableList() {
        List<String> tableList = new ArrayList<String>();
        try (Statement stmt = this.connection.createStatement()) {
            DatabaseMetaData md = this.connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                tableList.add(rs.getString(3));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        this.TableList = tableList;
    }

    private List<String> getColumnList(String TableName) {
        List<String> ColumnList = new ArrayList<>();
        try {
            DatabaseMetaData md = this.connection.getMetaData();
            ResultSet rs = md.getColumns(null, null, TableName, null);
            while (rs.next()) {
                ColumnList.add(rs.getString(4));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ColumnList;
    }

    public void generateCreateTable(Writer writer) {
        String query = "SELECT sql FROM sqlite_master WHERE name=?";
        for (String Table :
                this.TableList
        ) {
            try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
                stmt.setString(1, Table);
                ResultSet rs = stmt.executeQuery();
                writer.writeCreateTable(rs);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public void generateInsertRecord(Writer writer) {
        for (String TableName :
                this.TableList
        ) {
            List<String> ColumnList = this.getColumnList(TableName);
            try (PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM " + TableName)) {
                ResultSet rs = stmt.executeQuery();
                writer.writeInsertRecord(rs, TableName, ColumnList);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }


}
