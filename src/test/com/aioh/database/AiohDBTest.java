package com.aioh.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

class AiohDBTest {

    public static final String TEST_TABLE = "test_table";
    private AiohDB db;

    @BeforeEach
    void setup() {
        db = AiohDBManager
                .getAvailableDatabases()
                .stream()
                .filter(db -> Objects.equals(db.getName(), AiohDBManagerTest.TEST_DB))
                .findFirst()
                .get();

        db.connect();
    }

    @AfterEach
    void tearDown() {
        db.disconnect();
    }

    @Test
    void getTablesNames() {
        var tablesNames = db.getTablesNames();

        tablesNames.forEach(System.out::println);
    }

    @Test
    void getTableByName() {
        var table = db.getTableByName(TEST_TABLE);
        for (int i = 0; i < table.size(); i++) {
            System.out.println(
                    "Column: \"" + table.columnsNames().get(i) + "\", type: "
                            + table.columnsTypes().get(i)
            );
            
            for (var cell : table.columnsCells().get(i)) {
                System.out.println("\t" + cell);
            }

        }
    }
}