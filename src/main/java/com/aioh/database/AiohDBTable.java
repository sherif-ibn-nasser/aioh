package com.aioh.database;

import java.util.ArrayList;

/**
 * Represent an SoA of a typical table
 */
public record AiohDBTable(
        ArrayList<String> columnsNames,
        ArrayList<DataType> columnsTypes,
        ArrayList<ArrayList<StringBuilder>> columnsCells
) {

    public int columnsSize() {
        return columnsNames.size();
    }

    public int rowsSize() {
        return columnsCells.getFirst().size();
    }
}
