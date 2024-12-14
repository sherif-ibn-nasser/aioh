package com.aioh.database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AiohDBManager {

    public static final String URL = "jdbc:mariadb://localhost:3306/";

    public static List<AiohDB> getAvailableDatabases(String username, String password) {
        var databasesArrayList = new ArrayList<AiohDB>();
        try {
            var databasesRS = DriverManager.getConnection(URL, username, password).getMetaData().getCatalogs();
            while (databasesRS.next()) {
                databasesArrayList.add(new AiohDB(databasesRS.getString("TABLE_CAT")));
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve databases on this machine. Try again later.");
        }
        return databasesArrayList;
    }

    public static List<AiohDB> getAvailableDatabases() {
        var databasesArrayList = new ArrayList<AiohDB>();
        try {
            var databasesRS = DriverManager.getConnection(URL).getMetaData().getCatalogs();
            while (databasesRS.next()) {
                databasesArrayList.add(new AiohDB(databasesRS.getString("TABLE_CAT")));
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve databases on this machine. Try again later.");
        }
        return databasesArrayList;
    }
}
