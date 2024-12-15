package com.aioh.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AiohDBManager {

    public static final String URL = "jdbc:mariadb://localhost:3306/";

    public static List<String> getAvailableDatabases(String username, String password) {
        var databasesArrayList = new ArrayList<String>();
        try {
            var databasesRS = DriverManager.getConnection(URL, username, password).getMetaData().getCatalogs();
            while (databasesRS.next()) {
                databasesArrayList.add(databasesRS.getString("TABLE_CAT"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve databases on this machine. Try again later.\n" + "Cause: " + e);
        }
        return databasesArrayList;
    }

    public static List<String> getAvailableDatabases() {
        var databasesArrayList = new ArrayList<String>();
        try {
            var databasesRS = DriverManager.getConnection(URL).getMetaData().getCatalogs();
            while (databasesRS.next()) {
                databasesArrayList.add(databasesRS.getString("TABLE_CAT"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve databases on this machine. Try again later.");
        }
        return databasesArrayList;
    }

    public static AiohDB connectToDBByName(String dbName) {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(AiohDBManager.URL + dbName);
        } catch (SQLException e) {
            System.err.println("Cannot connect to \"" + dbName + "\" database.");
        }

        return new AiohDB(connection);
    }
}
