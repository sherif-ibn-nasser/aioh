## The Database Editor

The Database Editor component in the Aioh project provides an interface for creating, managing, and interacting with
structured data. It is designed to streamline the process of handling databases within the text editor, offering
flexibility, efficiency, and customization for users.

### Core Features

- **Database Management**: Organizes and facilitates operations on database records and fields.
- **Data Types Support**: Handles various data types with appropriate validation and formatting.
- **Intuitive UI**: Provides an easy-to-use interface that simplifies navigation and interaction.

### Screen States

The Database Editor uses a state-based architecture, similar to the Model-View-Intent (MVI) pattern in Android. It
adapts its interface and behavior based on different screen states, which represent the various modes or views the user
might interact with. These states include:

The `AiohDatabaseEditorState` enum holds possible screen states in the database editor.
Based on those states, we can handle an intent like key pressing:

src/main/java/com/aioh/AiohDatabaseEditor.java:

[//]: # (@formatter:off)
```java
@Override
public void onKeyPressed(int keyCode) {
    switch (state) {
        case DATABASES_DISPLAY -> handleDatabasesDisplayOnKeyPressed(keyCode);
        case NEW_DATABASE -> handleNewDatabaseOnKeyPressed(keyCode);
        case DELETE_DATABASE -> handleDeleteDatabaseOnKeyPressed(keyCode);
        case TABLES_DISPLAY -> handleTablesDisplayOnKeyPressed(keyCode);
        case NEW_TABLE_NAME -> handleNewTableNameOnKeyPressed(keyCode);
        case NEW_TABLE_COLUMNS -> handleNewTableColumnsOnKeyPressed(keyCode);
        case NEW_TABLE_COLUMN_NAME -> handleNewTableColumnNameOnKeyPressed(keyCode);
        case NEW_TABLE_COLUMN_TYPE -> handleNewTableColumnTypeOnKeyPressed(keyCode);
        case NEW_TABLE_VARCHAR_SIZE -> handleNewTableVarcharSizeOnKeyPressed(keyCode);
        case DELETE_TABLE -> handleDeleteTableOnKeyPressed(keyCode);
        case COLUMNS_DISPLAY -> handleColumnsDisplayOnKeyPressed(keyCode);
        case COLUMN_RENAME -> handleColumnRenameOnKeyPressed(keyCode);
        case CELL_UPDATE -> handleCellUpdateOnKeyPressed(keyCode);
    }
}
```

### Implementation

src/main/java/com/aioh/AiohDatabaseEditorState.java:

```java
{{#include ../../src/main/java/com/aioh/AiohDatabaseEditorState.java}}
```
