package com.aioh;

/**
 * Represents the screen state of the database editor in the Aioh project.
 * The state defines the current mode of the editor, determining how the interface behaves and what actions are available.
 * Each state corresponds to a specific operation or view in the database management process.
 *
 * @author Sherif Nasser
 */
public enum AiohDatabaseEditorState {

    /**
     * Represents the state where the list of databases is displayed.
     * No active editing is taking place in this state.
     */
    DATABASES_DISPLAY,

    /**
     * Represents the state for creating a new database.
     * This state allows the user to input a name and other properties for the new database.
     */
    NEW_DATABASE,

    /**
     * Represents the state where a database is being deleted.
     * The user can select a database to remove from the system.
     */
    DELETE_DATABASE,

    /**
     * Represents the state where the tables within a selected database are displayed.
     * Users can view, modify, or delete tables in this state.
     */
    TABLES_DISPLAY,

    /**
     * Represents the state for naming a new table.
     * The user is prompted to enter the name of the table.
     */
    NEW_TABLE_NAME,

    /**
     * Represents the state where columns for a new table are being defined.
     * The user specifies the number of columns and their properties.
     */
    NEW_TABLE_COLUMNS,

    /**
     * Represents the state where the name of a new column in a table is being defined.
     * The user inputs a name for the column.
     */
    NEW_TABLE_COLUMN_NAME,

    /**
     * Represents the state where the data type for a new column is being defined.
     * The user selects the appropriate data type for the column.
     */
    NEW_TABLE_COLUMN_TYPE,

    /**
     * Represents the state where the size of a `VARCHAR` column is being defined.
     * The user specifies the size of the new `VARCHAR` column.
     */
    NEW_TABLE_VARCHAR_SIZE,

    /**
     * Represents the state where a table is being deleted.
     * The user can choose to remove an existing table from the database.
     */
    DELETE_TABLE,

    /**
     * Represents the state where the columns of a selected table are displayed.
     * Users can modify, rename, or delete columns in this state.
     */
    COLUMNS_DISPLAY,

    /**
     * Represents the state where a column in a table is being renamed.
     * The user can change the name of an existing column.
     */
    COLUMN_RENAME,

    /**
     * Represents the state where the value of a cell in a table is being updated.
     * The user can modify the contents of a specific cell.
     */
    CELL_UPDATE
}
