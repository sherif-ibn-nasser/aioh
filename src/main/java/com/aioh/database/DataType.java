package com.aioh.database;

import static com.aioh.AiohDatabaseEditor.FALSE_STRING;

public enum DataType {
    INT,
    FLOAT,
    BOOL,
    STRING;

    @Override
    public String toString() {
        return switch (this) {
            case INT -> "Int";
            case FLOAT -> "Float";
            case BOOL -> "Bool";
            case STRING -> "String";
        };
    }

    public StringBuilder getDefaultString() {
        return switch (this) {
            case INT -> new StringBuilder("0");
            case FLOAT -> new StringBuilder("0.0");
            case BOOL -> FALSE_STRING;
            case STRING -> new StringBuilder();
        };
    }
}