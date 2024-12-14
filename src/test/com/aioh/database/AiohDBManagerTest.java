package com.aioh.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiohDBManagerTest {

    public static final String TEST_DB = "aioh_test";

    @DisplayName("Test retrieving all databases on the local machine")
    @Test
    void testGetAllDatabases() {
        var dbs = AiohDBManager.getAvailableDatabases();
        for (var db : dbs) {
            System.out.println(db.getName());
        }
        assertThat(dbs).anyMatch((db) -> db.getName().equals(TEST_DB));
    }
}