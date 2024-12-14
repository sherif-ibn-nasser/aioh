package com.aioh.database;

public enum DataType {
    INT,
    FLOAT,
    DOUBLE,
    BOOL,
    CHAR,
    VARCHAR;

    public static final StringBuilder FALSE_STRING = new StringBuilder("FALSE");
    public static final StringBuilder TRUE_STRING = new StringBuilder("TRUE");

    private int size;

    @Override
    public String toString() {
        return switch (this) {
            case INT -> "INT";
            case FLOAT -> "FLOAT";
            case DOUBLE -> "DOUBLE";
            case BOOL -> "BOOL";
            case CHAR -> "CHAR";
            case VARCHAR -> "VARCHAR(" + size + ")";
        };
    }

    public StringBuilder getDefaultCellValue() {
        return switch (this) {
            case INT -> new StringBuilder("0");
            case FLOAT, DOUBLE -> new StringBuilder("0.0");
            case BOOL -> FALSE_STRING;
            case CHAR, VARCHAR -> new StringBuilder();
        };
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}