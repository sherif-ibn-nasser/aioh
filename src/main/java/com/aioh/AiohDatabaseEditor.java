package com.aioh;

import com.aioh.database.AiohDB;
import com.aioh.database.AiohDBManager;
import com.aioh.database.AiohDBTable;
import com.aioh.database.DataType;
import com.aioh.graphics.AiohRenderer;
import glm_.vec4.Vec4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;

public class AiohDatabaseEditor extends AiohEditor {
    public static final Vec4 COLUMNS_NAMES_ROW_COLOR = new Vec4((float) 0x4C / 256, (float) 0xAF / 256, (float) 0x50 / 256, .5f);
    public static final Vec4 CELL_COLOR1 = new Vec4(0.4f);
    public static final Vec4 CELL_COLOR2 = new Vec4(0.5f);
    public static final float CELL_H_PADDING = FONT_SIZE / 2f;
    public static final float CELL_V_PADDING = FONT_SIZE / 2f;
    public static final int CELL_CHARS_THRESHOLD = 16;
    public static final int CELL_SPACING = 10;

    public enum ColumnInfo {
        NAME,
        DATATYPE,
        PK, AI, NN, UQ, UN, B, ZF, G;

        @Override
        public String toString() {
            return switch (this) {
                case NAME -> "Name";
                case DATATYPE -> "Datatype";
                case PK -> "PK";
                case AI -> "AI";
                case NN -> "NN";
                case UQ -> "UQ";
                case UN -> "UN";
                case B -> "B";
                case ZF -> "ZF";
                case G -> "G";
            };
        }
    }

    public static final AiohDBTable COLUMNS_INFO_TABLE = new AiohDBTable(
            Arrays.stream(ColumnInfo.values())
                    .map(ColumnInfo::toString)
                    .collect(Collectors.toCollection(ArrayList::new)),
            new ArrayList<>(),
            new ArrayList<>()
    );
    private static final ArrayList<StringBuilder> COLUMN_TYPES = new ArrayList<>(DataType.values().length);

    static {
        COLUMNS_INFO_TABLE.columnsNames().forEach((n) -> {
            COLUMNS_INFO_TABLE.columnsCells().add(new ArrayList<>(List.of(new StringBuilder(n))));
        });

        for (int i = 0; i < DataType.values().length; i++) {
            COLUMN_TYPES.add(new StringBuilder(DataType.values()[i].toString()));
        }
    }

    private AiohButtonsSelector buttonsSelector;
    private List<String> dbs;
    private AiohDB db;
    private AiohDBTable dbTable;
    private StringBuilder dbName, dbTableName;
    private ArrayList<Float> columnsWidths;
    private int databaseCol = 0, databaseRow = 0;
    private float currentColWidth = 0, maxColWidth;
    private AiohDatabaseEditorState state;
    private DataType currentCellType;

    @Override
    public void onInit() {
        super.onInit();
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
        lines.clear();
        lines.add(new StringBuilder());
        dbs = AiohDBManager.getAvailableDatabases();
        buttonsSelector = new AiohButtonsSelector();
        buttonsSelector.init();
        goToDatabasesDisplayState();
    }

    private void goToDatabasesDisplayState() {
        buttonsSelector.cursorLine = 0;
        buttonsSelector.setLines(dbs);
        buttonsSelector.title =
                """
                        Select a database (Enter). Use up and down arrows to switch
                        New database (Ctrl+N)
                        Delete database (Delete)"""
        ;
        buttonsSelector.titlePosY = 2 * renderer.getDebugFont().getFontHeight();
        state = AiohDatabaseEditorState.DATABASES_DISPLAY;
    }

    private void goToNewDatabaseState() {
        enableTextEditing("");
        title =
                """
                        Enter database name
                        Create (Enter)
                        Cancel (Esc)""";
        titlePosY = 2 * renderer.getDebugFont().getFontHeight();
        state = AiohDatabaseEditorState.NEW_DATABASE;
    }

    private void goToDeleteDatabaseState() {
        buttonsSelector.cursorLine = 0;
        buttonsSelector.displayNoAndYesButtons();
        buttonsSelector.title = "Are you sure you wanna delete the database `" + dbName + "`?";
        buttonsSelector.titlePosY = 0;
        state = AiohDatabaseEditorState.DELETE_DATABASE;
    }

    private void goToTablesDisplayState() {
        databaseCol = databaseRow = 0;
        buttonsSelector.cursorLine = 0;
        buttonsSelector.setLines(db.getTablesNames());
        buttonsSelector.title =
                """
                        Select a table (Enter). Use up and down arrows to switch
                        New table (Ctrl+N)
                        Delete table (Delete)
                        Back (Esc)""";
        buttonsSelector.titlePosY = 3 * renderer.getDebugFont().getFontHeight();
        state = AiohDatabaseEditorState.TABLES_DISPLAY;
    }

    private void goToNewTableNameState() {
        enableTextEditing(dbTableName);
        title =
                """
                        Enter table name
                        Continue (Enter)
                        Cancel (Esc)""";
        titlePosY = 2 * renderer.getDebugFont().getFontHeight();
        state = AiohDatabaseEditorState.NEW_TABLE_NAME;
    }

    private void goToNewTableColumnNameState() {
        enableTextEditing(getCurrentCell());
        title =
                """
                        Enter column name
                        Save (Enter)
                        Cancel (Esc)""";
        titlePosY = 2 * renderer.getDebugFont().getFontHeight();
        state = AiohDatabaseEditorState.NEW_TABLE_COLUMN_NAME;
    }

    private void goToNewTableColumnTypeState() {
        buttonsSelector.cursorLine = 0;
        buttonsSelector.lines = COLUMN_TYPES;
        buttonsSelector.title =
                """
                        Select column type (Enter). Use up and down arrows to switch
                        Cancel (Esc)""";
        buttonsSelector.titlePosY = renderer.getDebugFont().getFontHeight();
        state = AiohDatabaseEditorState.NEW_TABLE_COLUMN_TYPE;
    }

    private void goToNewTableVarcharSizeState() {
        enableTextEditing("55");
        title =
                """
                        Enter varchar size
                        Save (Enter)
                        Cancel (Esc)""";
        titlePosY = 2 * renderer.getDebugFont().getFontHeight();
        state = AiohDatabaseEditorState.NEW_TABLE_VARCHAR_SIZE;
    }

    private void goToDeleteTableState() {
        buttonsSelector.displayNoAndYesButtons();
        buttonsSelector.title = "Are you sure you wanna delete the table `" + dbTableName + "`?";
        buttonsSelector.titlePosY = 0;
        state = AiohDatabaseEditorState.DELETE_TABLE;
    }

    private void goToColumnRenameState() {
        enableTextEditing(getCurrentColName());
        currentCellType = DataType.VARCHAR;
        title =
                "Edit column name (currently: \"" + getCurrentColName() + "\")\n" +
                        "Save (Ctrl+S)\n" +
                        "Cancel (Esc)";
        titlePosY = 2 * renderer.getDebugFont().getFontHeight();
        state = AiohDatabaseEditorState.COLUMN_RENAME;
    }

    private void goToCellUpdateState() {

        currentCellType = dbTable.columnsTypes().get(databaseCol);
        var cell = getCurrentCell();

        if (currentCellType == DataType.BOOL) {
            dbTable.columnsCells().get(databaseCol).set(
                    databaseRow,
                    (cell == DataType.TRUE_STRING) ? DataType.FALSE_STRING : DataType.TRUE_STRING
            );
            return;
        }

        enableTextEditing(cell);
        title =
                "Update cell (type: " + dbTable.columnsTypes().get(databaseCol) + ")\n" +
                        "Save (Ctrl+S)\n" +
                        "Cancel (Esc)";
        titlePosY = 2 * renderer.getDebugFont().getFontHeight();
        state = AiohDatabaseEditorState.CELL_UPDATE;
    }

    @Override
    public void loop() {
        if (isButtonsSelectorState())
            buttonsSelector.loop();
        else
            super.loop();
    }

    private boolean isButtonsSelectorState() {
        return switch (state) {
            case DATABASES_DISPLAY, DELETE_DATABASE, TABLES_DISPLAY, NEW_TABLE_COLUMN_TYPE, DELETE_TABLE -> true;
            default -> false;
        };
    }

    private boolean isTextEditingState() {
        return switch (state) {
            case NEW_DATABASE, NEW_TABLE_NAME, NEW_TABLE_COLUMN_NAME, NEW_TABLE_VARCHAR_SIZE, CELL_UPDATE,
                 COLUMN_RENAME -> true;
            default -> false;
        };
    }

    private String getCurrentColName() {
        return dbTable.columnsNames().get(databaseCol);
    }

    private ArrayList<StringBuilder> getCurrentCol() {
        return dbTable.columnsCells().get(databaseCol);
    }

    private StringBuilder getCurrentCell() {
        return getCurrentCol().get(databaseRow);
    }

    private void enableTextEditing(CharSequence initialText) {
        lines.clear();
        lines.add(new StringBuilder());
        selectionStartCol = selectionEndCol = selectionStartLine = selectionEndLine = cursorCol = cursorLine = 0;
        for (int i = 0; i < initialText.length(); i++) {
            super.onTextInput(new char[]{initialText.charAt(i)});
        }
        renderer.getFont().setFontSpacing(0);
        this.fontSpacing = 0;
        this.fontHeight = renderer.getFont().getFontHeight();
    }

    private void disableTextEditing() {
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
        this.fontSpacing = CELL_V_PADDING;
        this.fontHeight = renderer.getFont().getFontHeight() + fontSpacing;
    }

    @Override
    protected void onStartRendering() {

        if (state != AiohDatabaseEditorState.COLUMNS_DISPLAY && state != AiohDatabaseEditorState.NEW_TABLE_COLUMNS)
            return;

        columnsWidths.clear();
        maxColWidth = 0;
        // Compare first line lengths of each cell until certain threshold
        // FIXME: Maybe this is slow?
        for (var columnCells : dbTable.columnsCells()) {
            var len = columnCells.stream()
                    .max(Comparator.comparingInt(a -> {
                        var idx = a.indexOf("\n");
                        return (idx == -1) ? a.length() : idx;
                    }))
                    .orElse(new StringBuilder())
                    .length();

            len = Math.min(CELL_CHARS_THRESHOLD, len);
            var columnWidth = 0.5f * len * FONT_SIZE + 2 * CELL_H_PADDING;
            columnsWidths.add(columnWidth);
            if (columnWidth > maxColWidth)
                maxColWidth = columnWidth;
        }
    }

    @Override
    protected void updateCameraPos() {

        if (isTextEditingState()) {
            super.updateCameraPos();
            return;
        }

        var centerX = 0.0f;

        for (int i = 0; i < dbTable.columnsSize(); i++) {
            var columnWidth = columnsWidths.get(i);

            if (i < databaseCol)
                centerX += columnWidth + CELL_SPACING;
            else if (i == databaseCol) {
                centerX += 0.5f * columnWidth;
                break;
            }
        }
        cursorPos.setX(centerX);
        cursorPos.setY(
                -databaseRow * fontHeight
        );

        cameraCursorDiff = cursorPos.minus(cameraPos);

        cameraPos = cameraPos.plus(
                cameraCursorDiff.times((float) CAMERA_VELOCITY / FPS)
        );
    }

    @Override
    protected float getCameraScaleVelocity() {

        if (isTextEditingState())
            return super.getCameraScaleVelocity();

        var normalizedWidthRatio = currentColWidth / maxColWidth;

        var targetCameraScale = Math.max(1f - normalizedWidthRatio, 0.45f);

        return (targetCameraScale - cameraScale) / FPS;
    }


    @Override
    public void onDrawColorProgram() {
        if (isTextEditingState()) {
            super.onDrawColorProgram();
            return;
        }
        drawColumnsBackgrounds();
        drawBorderAroundCurrentCell();
    }

    @Override
    protected void onDrawMainProgram() {
        if (isTextEditingState()) {
            super.onDrawMainProgram();
            return;
        }
        drawColumnsTexts();
        drawStatusBar();
    }

    @Override
    protected void onFinishRendering() {
    }

    private void drawColumnsBackgrounds() {

        var start = -cameraPos.getX();

        for (int i = 0; i < dbTable.columnsSize(); i++) {
            var columnCells = dbTable.columnsCells().get(i);
            var end = start + columnsWidths.get(i);

            for (int j = 0; j < columnCells.size(); j++) {
                var color =
                        (state == AiohDatabaseEditorState.NEW_TABLE_COLUMNS && j == 0) ? COLUMNS_NAMES_ROW_COLOR
                                : (j % 2 == 0) ? CELL_COLOR1
                                : CELL_COLOR2;
                var bottom = -cameraPos.getY() - (j + 0.5f) * fontHeight;
                // Background
                renderer.drawSolidRect(
                        start,
                        bottom,
                        end,
                        bottom + fontHeight,
                        color
                );
            }

            start = end + CELL_SPACING;

        }
    }

    private void drawBorderAroundCurrentCell() {
        if (dbTable.rowsSize() == 0) {
            return;
        }

        var targetColWidth = columnsWidths.get(databaseCol);
        var diff = targetColWidth - currentColWidth;
        currentColWidth += diff * (float) CAMERA_VELOCITY / FPS;
        var start = -currentColWidth / 2;
        var end = -start;

        var bottom = -0.5f * fontHeight;
        var top = 0.5f * fontHeight;

        // Start border
        renderer.drawSolidRect(
                start - CELL_SPACING,
                bottom,
                start,
                top,
                AIOH_COLOR
        );

        // End border
        renderer.drawSolidRect(
                end,
                bottom,
                end + CELL_SPACING,
                top,
                AIOH_COLOR
        );

        // Top border
        renderer.drawSolidRect(
                start,
                top - CELL_SPACING,
                end,
                top,
                AIOH_COLOR
        );

        // Bottom border
        renderer.drawSolidRect(
                start,
                bottom,
                end,
                bottom + CELL_SPACING,
                AIOH_COLOR
        );
    }

    private void drawColumnsTexts() {
        var columnStart = 0.0f;

        for (int i = 0; i < dbTable.columnsSize(); i++) {
            var columnWidth = columnsWidths.get(i);
            drawColumnText(i, columnStart + columnWidth / 2);
            columnStart += columnWidth + CELL_SPACING;
        }
    }

    private void drawColumnText(int columnIdx, float columnCenter) {
        // TODO: Optimize and render only visible cells
        var columnCells = dbTable.columnsCells().get(columnIdx);
        var text = new StringBuilder(columnCells.size());
        for (int i = 0; i < columnCells.size(); i++) {
            var cell = columnCells.get(i);

            for (int j = 0; j < cell.length() && j < CELL_CHARS_THRESHOLD; j++) {
                var ch = cell.charAt(j);
                if (ch == '\n') {
                    for (int k = j - 1; k >= CELL_CHARS_THRESHOLD - 3; k--) {
                        text.deleteCharAt(k);
                    }
                    text.append("...");
                    break;
                }
                text.append(ch);
            }

            renderer.getFont().drawText(
                    renderer,
                    text,
                    -cameraPos.getX() + columnCenter - text.length() * FONT_SIZE / 4f,
                    -cameraPos.getY() - i * fontHeight - 0.5f * fontHeight,
                    WHITE_COLOR
            );

            text.setLength(0);
        }

    }

    private void drawStatusBar() {

        if (state != AiohDatabaseEditorState.COLUMNS_DISPLAY)
            return;

        renderer.end();

        AiohRenderer.colorProgram.use();
        AiohRenderer.colorProgram.setUniform("cameraScale", 1.0f);

        renderer.begin();
        renderer.drawSolidRect(
                -AiohWindow.width / 2f,
                -AiohWindow.height / 2f,
                AiohWindow.width / 2f,
                -AiohWindow.height / 2f + renderer.getDebugFont().getFontHeight(),
                AIOH_COLOR_DARK
        );
        renderer.end();

        AiohRenderer.mainProgram.use();
        AiohRenderer.mainProgram.setUniform("cameraScale", 1.0f);

        renderer.begin();
        renderer.getDebugFont().drawText(
                renderer,
                "Row No.: " + (databaseRow + 1) +
                        ", Column: \"" +
                        getCurrentColName() +
                        "\" (" + dbTable.columnsTypes().get(databaseCol) + ")",
                -AiohWindow.width / 2f + 10,
                -AiohWindow.height / 2f,
                WHITE_COLOR
        );
    }

    @Override
    public void onTextInput(char[] newChars) {
        switch (state) {
            case DATABASES_DISPLAY, TABLES_DISPLAY -> buttonsSelector.onTextInput(newChars);
            case NEW_DATABASE, NEW_TABLE_NAME, NEW_TABLE_COLUMN_NAME -> super.onTextInput(newChars);
            case NEW_TABLE_VARCHAR_SIZE -> {
                if (Character.isDigit(newChars[0]))
                    super.onTextInput(newChars);
            }
            case COLUMNS_DISPLAY -> {
            }
            case COLUMN_RENAME, CELL_UPDATE -> {
                var ch = newChars[0];
                switch (currentCellType) {
                    case INT -> {
                        if (ch == '-' && cursorCol > 0 || ch != '-' && !Character.isDigit(ch))
                            return;
                    }
                    case FLOAT -> {
                        if (ch == '.' && lines.getFirst().indexOf(".") != -1 || ch != '.' && !Character.isDigit(ch))
                            return;
                    }
                    case VARCHAR -> {
                    }
                    default -> {
                        return;
                    }
                }

                super.onTextInput(newChars);
            }
        }

    }

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

    private void handleDatabasesDisplayOnKeyPressed(int keyCode) {
        switch (keyCode) {
            case GLFW_KEY_ENTER -> {
                dbName = buttonsSelector.getSelected();
                db = AiohDBManager.connectToDBByName(dbName);
                goToTablesDisplayState();
            }
            case GLFW_KEY_DELETE -> {
                dbName = buttonsSelector.getSelected();
                goToDeleteDatabaseState();
            }
            default -> buttonsSelector.onKeyPressed(keyCode);
        }
    }

    private void handleNewDatabaseOnKeyPressed(int keyCode) {
        if (keyCode == GLFW_KEY_ESCAPE) {
            disableTextEditing();
            goToDatabasesDisplayState();
        } else if (keyCode == GLFW_KEY_ENTER) {
            var dbName = lines.getFirst().toString();
            if (dbName.isEmpty() || dbs.contains(dbName)) {
                // TODO: Display the error
                return;
            }
            disableTextEditing();
            db = AiohDBManager.createDatabase(dbName);
            dbs = AiohDBManager.getAvailableDatabases();
            buttonsSelector.setLines(dbs);
            goToDatabasesDisplayState();
        } else
            super.onKeyPressed(keyCode);
    }

    private void handleDeleteDatabaseOnKeyPressed(int keyCode) {
        if (keyCode == GLFW_KEY_ESCAPE) {
            goToDatabasesDisplayState();
        } else if (keyCode == GLFW_KEY_ENTER) {
            if (buttonsSelector.getSelected() == AiohButtonsSelector.YES_BUTTON) {
                AiohDBManager.deleteDatabase(dbName);
                dbs = AiohDBManager.getAvailableDatabases();
                buttonsSelector.setLines(dbs);
            }
            goToDatabasesDisplayState();
        } else
            buttonsSelector.onKeyPressed(keyCode);
    }

    private void handleTablesDisplayOnKeyPressed(int keyCode) {
        switch (keyCode) {
            case GLFW_KEY_ESCAPE -> {
                db.disconnect();
                goToDatabasesDisplayState();
            }
            case GLFW_KEY_ENTER -> {
                dbTableName = buttonsSelector.getSelected();
                dbTable = db.getTableByName(dbTableName);
                currentCellType = dbTable.columnsTypes().getFirst();
                columnsWidths = new ArrayList<>(dbTable.columnsSize());
                state = AiohDatabaseEditorState.COLUMNS_DISPLAY;
            }
            case GLFW_KEY_DELETE -> {
                dbTableName = buttonsSelector.getSelected();
                goToDeleteTableState();
            }
            default -> buttonsSelector.onKeyPressed(keyCode);
        }
    }

    private void handleNewTableNameOnKeyPressed(int keyCode) {
        if (keyCode == GLFW_KEY_ESCAPE) {
            disableTextEditing();
            goToTablesDisplayState();
        } else if (keyCode == GLFW_KEY_ENTER) {
            dbTableName = lines.getFirst();
            var dbTableNameStr = dbTableName.toString();
            if (dbTableName.isEmpty() || buttonsSelector.lines.stream().anyMatch(dbTableNameStr::contentEquals)) {
                // TODO: Display the error
                return;
            }
            disableTextEditing();
            if (columnsWidths == null) {
                columnsWidths = new ArrayList<>(COLUMNS_INFO_TABLE.columnsNames().size());
            }
            dbTable = COLUMNS_INFO_TABLE;
            dbTable.columnsCells().forEach((columnCells) -> {
                var n = columnCells.getFirst();
                columnCells.clear();
                columnCells.add(n);
            });
            databaseRow = 1;
            addRowAboveColumnInfo();
            state = AiohDatabaseEditorState.NEW_TABLE_COLUMNS;
        } else
            super.onKeyPressed(keyCode);
    }

    private void handleNewTableColumnsOnKeyPressed(int keyCode) {
        switch (keyCode) {
            case GLFW_KEY_ESCAPE -> goToNewTableNameState();
            case GLFW_KEY_DELETE -> onDeletePressedColumnInfo();
            case GLFW_KEY_ENTER -> {
                if (databaseCol == 0)
                    goToNewTableColumnNameState();
                else if (databaseCol == 1)
                    goToNewTableColumnTypeState();
                else {
                    var cell = getCurrentCell();
                    getCurrentCol().set(
                            databaseRow,
                            (cell == DataType.TRUE_STRING) ? DataType.FALSE_STRING : DataType.TRUE_STRING
                    );
                }
            }
            case GLFW_KEY_UP -> {
                if (databaseRow > 1)
                    databaseRow--;
            }
            case GLFW_KEY_DOWN -> onDownArrowPressed();
            case GLFW_KEY_RIGHT -> onRightArrowPressed();
            case GLFW_KEY_LEFT -> onLeftArrowPressed();
        }
    }

    private void handleNewTableColumnNameOnKeyPressed(int keyCode) {
        if (keyCode == GLFW_KEY_ESCAPE) {
            disableTextEditing();
            state = AiohDatabaseEditorState.NEW_TABLE_COLUMNS;
        } else if (keyCode == GLFW_KEY_ENTER) {
            var createdColumnsNames = COLUMNS_INFO_TABLE.columnsCells().getFirst();


            var createdColumnName = lines.getFirst();
            if (createdColumnName.isEmpty()) {
                // TODO: Display the error
                return;
            } else {

                var oldTitle = createdColumnsNames.get(0);
                var oldColName = createdColumnsNames.get(databaseRow);
                // Set the column name and the title row cell to empty to not conflict with the new value
                createdColumnsNames.set(0, new StringBuilder());
                createdColumnsNames.set(databaseRow, new StringBuilder());
                var createdColumnNameStr = createdColumnName.toString();
                if (
                        createdColumnsNames.stream().anyMatch(createdColumnNameStr::contentEquals)
                ) {
                    // TODO: Display the error
                    // Restore the column name value
                    createdColumnsNames.set(0, oldColName);
                    return;
                }

                // Restore the title row cell
                createdColumnsNames.set(0, oldTitle);
            }

            createdColumnsNames.set(databaseRow, createdColumnName);
            disableTextEditing();
            state = AiohDatabaseEditorState.NEW_TABLE_COLUMNS;
        } else
            super.onKeyPressed(keyCode);
    }


    private void handleNewTableColumnTypeOnKeyPressed(int keyCode) {
        switch (keyCode) {
            case GLFW_KEY_ESCAPE -> state = AiohDatabaseEditorState.NEW_TABLE_COLUMNS;
            case GLFW_KEY_ENTER -> {
                if (buttonsSelector.cursorLine == DataType.VARCHAR.ordinal())
                    goToNewTableVarcharSizeState();
                else {
                    var selectedType = buttonsSelector.getSelected();
                    COLUMNS_INFO_TABLE.columnsCells().get(1).set(databaseRow, selectedType);
                    state = AiohDatabaseEditorState.NEW_TABLE_COLUMNS;
                }
            }
            default -> buttonsSelector.onKeyPressed(keyCode);
        }
    }

    private void handleNewTableVarcharSizeOnKeyPressed(int keyCode) {
        switch (keyCode) {
            case GLFW_KEY_ESCAPE -> {
                disableTextEditing();
                state = AiohDatabaseEditorState.NEW_TABLE_COLUMN_TYPE;
            }
            case GLFW_KEY_ENTER -> {
                var numStr = lines.getFirst().toString();
                if (numStr.isEmpty())
                    return;
                var type = DataType.VARCHAR;
                type.setSize(Integer.parseInt(numStr));
                COLUMNS_INFO_TABLE.columnsCells().get(1).set(databaseRow, new StringBuilder(type.toString()));
                disableTextEditing();
                state = AiohDatabaseEditorState.NEW_TABLE_COLUMNS;
            }
            default -> super.onKeyPressed(keyCode);
        }
    }

    private void handleDeleteTableOnKeyPressed(int keyCode) {
        if (keyCode == GLFW_KEY_ESCAPE) {
            goToTablesDisplayState();
        } else if (keyCode == GLFW_KEY_ENTER) {
            if (buttonsSelector.getSelected() == AiohButtonsSelector.YES_BUTTON)
                db.deleteTableByName(dbTableName);
            goToTablesDisplayState();
        } else
            buttonsSelector.onKeyPressed(keyCode);
    }

    private void handleColumnsDisplayOnKeyPressed(int keyCode) {
        switch (keyCode) {
            case GLFW_KEY_ESCAPE -> goToTablesDisplayState();
            case GLFW_KEY_BACKSPACE -> onBackspacePressed();
            case GLFW_KEY_DELETE -> onDeletePressed();
            case GLFW_KEY_ENTER -> goToCellUpdateState();
            case GLFW_KEY_UP -> onUpArrowPressed();
            case GLFW_KEY_DOWN -> onDownArrowPressed();
            case GLFW_KEY_RIGHT -> onRightArrowPressed();
            case GLFW_KEY_LEFT -> onLeftArrowPressed();
        }
    }

    private void handleColumnRenameOnKeyPressed(int keyCode) {
        if (keyCode == GLFW_KEY_ESCAPE) {
            disableTextEditing();
            state = AiohDatabaseEditorState.COLUMNS_DISPLAY;
        } else
            super.onKeyPressed(keyCode);
    }

    private void handleCellUpdateOnKeyPressed(int keyCode) {
        if (keyCode == GLFW_KEY_ESCAPE) {
            disableTextEditing();
            state = AiohDatabaseEditorState.COLUMNS_DISPLAY;
        } else
            super.onKeyPressed(keyCode);
    }

    private void onBackspacePressed() {
        if (dbTable.columnsTypes().get(databaseCol) == DataType.BOOL)
            return;
        getCurrentCell().setLength(0);
    }

    private void onDeletePressed() {
        if (dbTable.rowsSize() == 1)
            return;

        for (var columnCells : dbTable.columnsCells()) {
            columnCells.remove(databaseRow);
        }

        if (databaseRow >= dbTable.rowsSize())
            databaseRow = dbTable.rowsSize() - 1;
    }

    private void onUpArrowPressed() {
        if (databaseRow > 0)
            databaseRow--;
    }

    private void onDownArrowPressed() {
        if (databaseRow < getCurrentCol().size() - 1)
            databaseRow++;
    }

    private void onRightArrowPressed() {
        if (databaseCol < dbTable.columnsSize() - 1)
            databaseCol++;
    }

    private void onLeftArrowPressed() {
        if (databaseCol > 0)
            databaseCol--;
    }

    @Override
    public void onModKeysPressed(int mods, int keyCode) {
        switch (state) {
            case DATABASES_DISPLAY -> {
                if ((mods & GLFW_MOD_CONTROL) != 0) {
                    switch (keyCode) {
                        case GLFW_KEY_N -> goToNewDatabaseState();
                    }
                }
            }
            case NEW_DATABASE -> {
            }
            case TABLES_DISPLAY -> {
                if ((mods & GLFW_MOD_CONTROL) != 0) {
                    switch (keyCode) {
                        case GLFW_KEY_N -> {
                            dbTableName = new StringBuilder();
                            goToNewTableNameState();
                        }
                    }
                }
            }
            case NEW_TABLE_NAME -> {
            }
            case NEW_TABLE_COLUMNS -> {
                if ((mods & GLFW_MOD_CONTROL) != 0) {
                    switch (keyCode) {
                        case GLFW_KEY_UP -> addRowAboveColumnInfo();
                        case GLFW_KEY_DOWN -> addRowBelowColumnInfo();
                        case GLFW_KEY_BACKSPACE -> onDeletePressed();
                        case GLFW_KEY_S -> createTable();
                    }
                }
            }
            case COLUMNS_DISPLAY -> {
                if ((mods & GLFW_MOD_CONTROL) != 0) {
                    switch (keyCode) {
                        case GLFW_KEY_UP -> addRowAbove();
                        case GLFW_KEY_DOWN -> addRowBelow();
                        case GLFW_KEY_BACKSPACE -> onDeletePressed();
                        case GLFW_KEY_R -> goToColumnRenameState();
                        case GLFW_KEY_D -> duplicateCurrentRow();
                        case GLFW_KEY_S -> saveTable();
                    }
                }
            }
            case COLUMN_RENAME, CELL_UPDATE -> {
                if ((mods & GLFW_MOD_CONTROL) != 0) {
                    switch (keyCode) {
                        case GLFW_KEY_S -> saveInputText();
                    }
                    return;
                }
                super.onModKeysPressed(mods, keyCode);
            }
        }

    }

    private void saveInputText() {
        disableTextEditing();
        var cell = new StringBuilder(lines.size());
        switch (currentCellType) {
            case INT -> {
                var numStr = lines.getFirst().toString();
                if (numStr.isEmpty())
                    cell.append(0);
                else
                    cell.append(Integer.parseInt(numStr));
            }
            case FLOAT -> {
                var numStr = lines.getFirst().toString();
                if (numStr.isEmpty())
                    cell.append(0.0f);
                else
                    cell.append(Float.parseFloat(numStr));
            }
            case VARCHAR -> {
                // FIXME: This might be very slow?
                for (int i = 0; i < lines.size() - 1; i++) {
                    cell.append(lines.get(i));
                    cell.append('\n');
                }
                cell.append(lines.getLast());
            }
            case BOOL -> {
                return;
            }
        }

        if (state == AiohDatabaseEditorState.COLUMN_RENAME) {
            dbTable.columnsNames().set(databaseCol, cell.toString());
        } else {
            dbTable.columnsCells().get(databaseCol).set(databaseRow, cell);
        }
        state = AiohDatabaseEditorState.COLUMNS_DISPLAY;
    }

    private void addRowAbove() {
        for (int i = 0; i < dbTable.columnsSize(); i++) {
            var columnCells = dbTable.columnsCells().get(i);
            columnCells.add(databaseRow, dbTable.columnsTypes().get(i).getDefaultCellValue());
        }
    }

    private void addRowBelow() {
        if (dbTable.rowsSize() > 0)
            databaseRow++;
        addRowAbove();
    }

    private void duplicateCurrentRow() {
        for (var columnCells : dbTable.columnsCells()) {
            columnCells.add(databaseRow + 1, new StringBuilder(columnCells.get(databaseRow)));
        }
        databaseRow++;
    }

    private void saveTable() {
        db.updateTable(dbTableName, dbTable);
    }

    private void onDeletePressedColumnInfo() {
        if (dbTable.rowsSize() == 2)
            return;

        for (var columnCells : dbTable.columnsCells()) {
            columnCells.remove(databaseRow);
        }

        if (databaseRow >= dbTable.rowsSize())
            databaseRow = dbTable.rowsSize() - 1;
    }

    private void addRowAboveColumnInfo() {
        var createdColumnsNames = COLUMNS_INFO_TABLE.columnsCells().getFirst();
        var baseName = "New Column";
        var columnName = baseName;
        int index = 1;
        while (createdColumnsNames.stream().anyMatch(columnName::contentEquals)) {
            columnName = baseName + "(" + index + ")";
            index++;
        }

        dbTable.columnsCells().getFirst().add(databaseRow, new StringBuilder(columnName));
        dbTable.columnsCells().get(1).add(databaseRow, new StringBuilder(DataType.INT.toString()));
        for (int i = 2; i < dbTable.columnsSize(); i++) {
            var columnCells = dbTable.columnsCells().get(i);
            columnCells.add(databaseRow, DataType.FALSE_STRING);
        }
    }

    private void addRowBelowColumnInfo() {
        databaseRow++;
        addRowAboveColumnInfo();
    }

    private void createTable() {
        db.createTable(dbTableName, COLUMNS_INFO_TABLE);
        db.fetchTablesNames();
        goToTablesDisplayState();
    }

}