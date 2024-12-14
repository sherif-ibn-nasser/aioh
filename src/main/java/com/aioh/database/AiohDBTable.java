package com.aioh.database;

import java.util.ArrayList;

/**
 * Represent an SoA of a typical table
 */
public record AiohDBTable(
        ArrayList<StringBuilder> columnsNames,
        ArrayList<DataType> columnsTypes,
        ArrayList<ArrayList<StringBuilder>> columnsCells
) {

    public int size() {
        return columnsNames.size();
    }
}
