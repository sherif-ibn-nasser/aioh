package com.aioh.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstraction layer for DBMS
 *
 * @author Sherif Nasser
 */
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

    public static AiohDB connectToDBByName(CharSequence dbName) {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(AiohDBManager.URL + dbName);
        } catch (SQLException e) {
            System.err.println("Cannot connect to \"" + dbName + "\" database.");
        }

        return new AiohDB(dbName.toString(), connection);
    }

    public static AiohDB createDatabase(CharSequence dbName) {
        Connection connection = null;
        try {
            // Connect to the MariaDB server
            connection = DriverManager.getConnection(AiohDBManager.URL);
            Statement statement = connection.createStatement();

            // Execute the SQL command to create the database
            String createDBSQL = "CREATE DATABASE `" + dbName + "`";
            statement.executeUpdate(createDBSQL);

            // Connect to the newly created database
            return connectToDBByName(dbName);
        } catch (SQLException e) {
            System.err.println("Error while creating database \"" + dbName + "\": " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing the connection: " + e.getMessage());
            }
        }
        return null;
    }


    public static boolean deleteDatabase(CharSequence dbName) {
        Connection connection = null;
        Statement statement = null;

        try {
            // Connect to the MariaDB server
            connection = DriverManager.getConnection(URL);
            statement = connection.createStatement();

            // Execute the SQL to drop the database
            statement.executeUpdate("DROP DATABASE `" + dbName + "`");
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to delete database \"" + dbName + "\".\nCause: " + e.getMessage());
            return false;
        } finally {
            // Close resources
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Failed to close resources.\nCause: " + e.getMessage());
            }
        }
    }
}
