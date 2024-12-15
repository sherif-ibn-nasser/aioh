package com.aioh.database;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AiohDB {
    private List<String> tablesNames;
    private Connection connection;

    public AiohDB(Connection connection) {
        this.connection = connection;
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (Exception e) {
            System.err.println(
                    "Cannot close this database." +
                            "It might be disconnected already or is no connection established at all."
            );
        }
    }

    public List<String> getTablesNames() {
        if (tablesNames != null)
            return tablesNames;

        tablesNames = new ArrayList<>();

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            // Get list of tables
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            // Loop through each table
            while (tables.next()) {
                tablesNames.add(tables.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tablesNames;
    }

    public AiohDBTable getTableByName(String tableName) {

        ArrayList<String> columnsNames;
        ArrayList<DataType> columnsTypes;
        ArrayList<ArrayList<StringBuilder>> columnsCells;

        try {
            DatabaseMetaData metaData = connection.getMetaData();

            // Get columns for each table
            var columns = metaData.getColumns(null, null, tableName, null);

            columnsNames = new ArrayList<>();
            columnsTypes = new ArrayList<>();
            columnsCells = new ArrayList<>();

            // Loop through each column
            while (columns.next()) {
                var columnName = columns.getString("COLUMN_NAME");
                var columnType = columns.getInt("DATA_TYPE"); // Get the data type of the column
                var columnSize = columns.getInt("COLUMN_SIZE"); // Get the size of the column

                var type = getDataType(columnType, columnSize);

                columnsNames.add(columnName);
                columnsTypes.add(type);
                columnsCells.add(new ArrayList<>());
            }

            String query = "SELECT * FROM " + tableName;
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                for (int i = 0; i < columnsNames.size(); i++) {
                    String columnName = columnsNames.get(i);
                    String value = resultSet.getString(columnName);

                    // Add the cell to the corresponding column
                    columnsCells.get(i).add(value != null ? new StringBuilder(value) : new StringBuilder("NULL"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new AiohDBTable(columnsNames, columnsTypes, columnsCells);
    }

    @NotNull
    private static DataType getDataType(int columnType, int columnSize) {
        var type = switch (columnType) {
            case Types.INTEGER -> DataType.INT;
            case Types.FLOAT -> DataType.FLOAT;
            case Types.DOUBLE -> DataType.DOUBLE;
            case Types.BOOLEAN -> DataType.BOOL;
            case Types.CHAR -> DataType.CHAR;
            case Types.VARCHAR -> DataType.VARCHAR;
            default -> throw new IllegalStateException("Unsupported data type: " + columnType);
        };

        type.setSize(columnSize);
        return type;
    }

//
//    public AiohDBTable getTableByName(String tableName) {
//
//        ArrayList<StringBuilder> columnsNames;
//        ArrayList<DataType> columnsTypes;
//        ArrayList<ArrayList<StringBuilder>> columnsCells;
//
//        try {
//            DatabaseMetaData metaData = connection.getMetaData();
//
//            // Get columns for each table
//            var columns = metaData.getColumns(null, null, tableName, null);
//            var metadata = columns.getMetaData();
//            var count = metadata.getColumnCount();
//
//            columnsNames = new ArrayList<>(count);
//            columnsTypes = new ArrayList<>(count);
//            columnsCells = new ArrayList<>(count);
//
//            System.out.println("Count: " + count);
//
//            for (int i = 0; i < count; i++) {
//                var columnName = metadata.getColumnName(i);
//                var columnType = metadata.getColumnType(i);
//
//                var type = switch (columnType) {
//                    case Types.INTEGER -> DataType.INT;
//                    case Types.FLOAT -> DataType.FLOAT;
//                    case Types.DOUBLE -> DataType.DOUBLE;
//                    case Types.BOOLEAN -> DataType.BOOL;
//                    case Types.CHAR -> DataType.CHAR;
//                    case Types.VARCHAR -> DataType.VARCHAR;
//                    default -> throw new IllegalStateException("Unsupported data type: " + columnType);
//                };
//
//                type.setSize(metadata.getColumnDisplaySize(i));
//
//                columnsNames.add(new StringBuilder(columnName));
//                columnsTypes.add(type);
//                columnsCells.add(new ArrayList<>());
//            }
//
//            String query = "SELECT * FROM " + tableName;
//            PreparedStatement statement = connection.prepareStatement(query);
//            ResultSet resultSet = statement.executeQuery();
//
//            while (resultSet.next()) {
//                for (int i = 0; i < count; i++) {
//                    String columnName = metadata.getColumnName(i);
//                    String value = resultSet.getString(columnName);
//
//                    // Add the cell to the corresponding column
//                    columnsCells.get(i).add(value != null ? new StringBuilder(value) : new StringBuilder("NULL"));
//                }
//            }
//
//            columns.close();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        return new AiohDBTable(columnsNames, columnsTypes, columnsCells);
//    }

}
