package com.aioh;

import com.aioh.database.DataType;
import com.aioh.graphics.AiohRenderer;
import glm_.vec4.Vec4;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;

import static org.lwjgl.glfw.GLFW.*;

public class AiohDatabaseEditor extends AiohEditor {
    public static final Vec4 CELL_COLOR1 = new Vec4(0.4f);
    public static final Vec4 CELL_COLOR2 = new Vec4(0.5f);
    public static final float CELL_H_PADDING = FONT_SIZE / 2f;
    public static final float CELL_V_PADDING = FONT_SIZE / 2f;
    public static final int CELL_CHARS_THRESHOLD = 16;
    public static final int CELL_SPACING = 10;
    public static final StringBuilder FALSE_STRING = new StringBuilder("FALSE");
    public static final StringBuilder TRUE_STRING = new StringBuilder("TRUE");

    private final ArrayList<ArrayList<StringBuilder>> columns = new ArrayList<>(8);
    private final ArrayList<String> columnsNames = new ArrayList<>(8);
    private final ArrayList<DataType> columnsTypes = new ArrayList<>(8);
    private final ArrayList<Float> columnsWidths = new ArrayList<>(8);
    private int databaseCol = 0, databaseRow = 0;
    private float currentColWidth = 0, maxColWidth;
    private boolean textEditing = false, columnRenaming = false;
    private DataType currentCellType;
    private Connection connection;

    @Override
    public void onInit() {
        super.onInit();
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
        lines.clear();
        lines.add(new StringBuilder());
        var col0 = new ArrayList<StringBuilder>();
        col0.add(new StringBuilder("0"));
        col0.add(new StringBuilder("1"));
        col0.add(new StringBuilder("2"));
        col0.add(new StringBuilder("3"));

        var col1 = new ArrayList<StringBuilder>();
        col1.add(new StringBuilder("Mahmoud"));
        col1.add(new StringBuilder("Mohammed"));
        col1.add(new StringBuilder("Mustafa"));
        col1.add(new StringBuilder("Sherif"));

        var col2 = new ArrayList<StringBuilder>();
        col2.add(new StringBuilder("Mahmoud"));
        col2.add(new StringBuilder("Ahmed"));
        col2.add(new StringBuilder("Mohammed"));
        col2.add(new StringBuilder("Nasser"));

        var col3 = new ArrayList<StringBuilder>();
        col3.add(new StringBuilder("21"));
        col3.add(new StringBuilder("23"));
        col3.add(new StringBuilder("24"));
        col3.add(new StringBuilder("21"));

        var col4 = new ArrayList<StringBuilder>();
        col4.add(new StringBuilder("100000.0"));
        col4.add(new StringBuilder("10000.0"));
        col4.add(new StringBuilder("20000.0"));
        col4.add(new StringBuilder("30000.0"));

        var col5 = new ArrayList<StringBuilder>();
        col5.add(new StringBuilder(FALSE_STRING));
        col5.add(new StringBuilder(TRUE_STRING));
        col5.add(new StringBuilder(TRUE_STRING));
        col5.add(new StringBuilder(FALSE_STRING));

        columns.add(col0);
        columns.add(col1);
        columns.add(col2);
        columns.add(col3);
        columns.add(col4);
        columns.add(col5);

        columnsNames.add("Id");
        columnsNames.add("First Name");
        columnsNames.add("Last Name");
        columnsNames.add("Age");
        columnsNames.add("Salary");
        columnsNames.add("Married");

        columnsTypes.add(DataType.INT);
        columnsTypes.add(DataType.STRING);
        columnsTypes.add(DataType.STRING);
        columnsTypes.add(DataType.INT);
        columnsTypes.add(DataType.FLOAT);
        columnsTypes.add(DataType.BOOL);

        currentCellType = columnsTypes.getFirst();

        connectToDatabase();
    }

    private void connectToDatabase() {
        // Database credentials
        String url = "jdbc:mariadb://localhost:3306/aioh_test"; // Replace with your database name

        // Connection object
        connection = null;

        try {
            // Establishing the connection
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to the MariaDB database successfully!");
            loadDatabase();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the MariaDB database.");
            e.printStackTrace();
        } finally {
            // Closing the connection
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Connection closed.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void loadDatabase() throws SQLException {

        // Get DatabaseMetaData
        DatabaseMetaData metaData = connection.getMetaData();

        // Get list of tables
        ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

        System.out.println("Tables in the database:");

        // Loop through each table
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            System.out.println("Table: " + tableName);

            // Get columns for each table
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            System.out.println("Columns for table: " + tableName);

            // Loop through each column
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME"); // Get the data type of the column
                int columnSize = columns.getInt("COLUMN_SIZE"); // Get the size of the column

                // Check if the column is VARCHAR and print size if applicable
                if (columnType.equalsIgnoreCase("VARCHAR")) {
                    System.out.println("  Column: " + columnName + " | Type: " + columnType + " | Size: " + columnSize);
                } else {
                    System.out.println("  Column: " + columnName + " | Type: " + columnType);
                }
            }
            System.out.println(); // Empty line between tables
        }
    }

    private ArrayList<StringBuilder> getCurrentCol() {
        return columns.get(databaseCol);
    }

    private StringBuilder getCurrentCell() {
        return getCurrentCol().get(databaseRow);
    }

    private void enableTextEditing(CharSequence initialText) {
        lines.clear();
        lines.add(new StringBuilder());
        cursorCol = 0;
        cursorLine = 0;
        for (int i = 0; i < initialText.length(); i++) {
            super.onTextInput(new char[]{initialText.charAt(i)});
        }
        enableTextEditing();
    }

    private void enableTextEditing() {
        textEditing = true;
        renderer.getFont().setFontSpacing(0);
        this.fontSpacing = 0;
        this.fontHeight = renderer.getFont().getFontHeight();
    }

    private void disableTextEditing() {
        textEditing = false;
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
        this.fontSpacing = CELL_V_PADDING;
        this.fontHeight = renderer.getFont().getFontHeight() + fontSpacing;
    }

    @Override
    protected void onStartRendering() {
        columnsWidths.clear();
        maxColWidth = 0;
        // Compare first line lengths of each cell until certain threshold
        // FIXME: Maybe this is slow?
        for (var column : columns) {
            var len = column.stream()
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

        if (textEditing) {
            super.updateCameraPos();
            return;
        }

        var centerX = 0.0f;

        for (int i = 0; i < columns.size(); i++) {
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

        if (textEditing)
            return super.getCameraScaleVelocity();

        var normalizedWidthRatio = currentColWidth / maxColWidth;

        var targetCameraScale = Math.max(1f - normalizedWidthRatio, 0.45f);

        return (targetCameraScale - cameraScale) / FPS;
    }


    @Override
    public void onDrawColorProgram() {
        if (textEditing) {
            super.onDrawColorProgram();
            return;
        }
        drawColumnsBackgrounds();
        drawBorderAroundCurrentCell();
    }

    @Override
    protected void onDrawMainProgram() {
        if (textEditing) {
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

        for (int i = 0; i < columns.size(); i++) {
            var column = columns.get(i);
            var end = start + columnsWidths.get(i);

            for (int j = 0; j < column.size(); j++) {
                var color = (j % 2 == 0) ? CELL_COLOR1 : CELL_COLOR2;
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

        for (int i = 0; i < columns.size(); i++) {
            var columnWidth = columnsWidths.get(i);
            drawColumnText(i, columnStart + columnWidth / 2);
            columnStart += columnWidth + CELL_SPACING;
        }
    }

    private void drawColumnText(int columnIdx, float columnCenter) {
        // TODO: Optimize and render only visible cells
        var column = columns.get(columnIdx);
        var text = new StringBuilder(column.size());
        for (int i = 0; i < column.size(); i++) {
            var cell = column.get(i);

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
                    -cameraPos.getY() - (i) * fontHeight - 0.5f * fontHeight,
                    WHITE_COLOR
            );

            text.setLength(0);
        }

    }

    private void drawStatusBar() {
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
                "Cell No.: " + (databaseRow + 1) +
                        ", Column: \"" +
                        columnsNames.get(databaseCol) +
                        "\" (" + columnsTypes.get(databaseCol) + ")",
                -AiohWindow.width / 2f + 10,
                -AiohWindow.height / 2f,
                WHITE_COLOR
        );
    }

    @Override
    public void onTextInput(char[] newChars) {
        if (!textEditing)
            return;

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
            case STRING -> {
            }
            default -> {
                return;
            }
        }

        super.onTextInput(newChars);
    }

    @Override
    public void onKeyPressed(int keyCode) {
        if (textEditing) {
            if (keyCode == GLFW_KEY_ESCAPE)
                disableTextEditing();
            else
                super.onKeyPressed(keyCode);
            return;
        }
        switch (keyCode) {
            case GLFW_KEY_BACKSPACE -> onBackspacePressed();
            case GLFW_KEY_DELETE -> onDeletePressed();
            case GLFW_KEY_ENTER -> onEnterPressed();
            case GLFW_KEY_UP -> onUpArrowPressed();
            case GLFW_KEY_DOWN -> onDownArrowPressed();
            case GLFW_KEY_RIGHT -> onRightArrowPressed();
            case GLFW_KEY_LEFT -> onLeftArrowPressed();
        }
    }

    private void onBackspacePressed() {
        if (columnsTypes.get(databaseCol) == DataType.BOOL)
            return;
        getCurrentCell().setLength(0);
    }

    private void onDeletePressed() {
        if (columns.getFirst().size() == 1)
            return;

        for (var column : columns) {
            column.remove(databaseRow);
        }

        if (databaseRow >= columns.getFirst().size())
            databaseRow = columns.getFirst().size() - 1;
    }

    private void onEnterPressed() {
        currentCellType = columnsTypes.get(databaseCol);
        var cell = getCurrentCell();

        if (currentCellType == DataType.BOOL) {
            columns.get(databaseCol).set(
                    databaseRow,
                    (cell == TRUE_STRING) ? FALSE_STRING : TRUE_STRING
            );
            return;
        }

        enableTextEditing(cell);
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
        if (databaseCol < columns.size() - 1)
            databaseCol++;
    }

    private void onLeftArrowPressed() {
        if (databaseCol > 0)
            databaseCol--;
    }

    @Override
    public void onModKeysPressed(int mods, int keyCode) {
        if (textEditing) {
            if ((mods & GLFW_MOD_CONTROL) != 0) {
                switch (keyCode) {
                    case GLFW_KEY_S -> updateCurrentCell();
                }
                return;
            }
            super.onModKeysPressed(mods, keyCode);
            return;
        }

        if ((mods & GLFW_MOD_CONTROL) != 0) {
            switch (keyCode) {
                case GLFW_KEY_UP -> addRowAbove();
                case GLFW_KEY_DOWN -> addRowBelow();
                case GLFW_KEY_BACKSPACE -> onDeletePressed();
                case GLFW_KEY_R -> renameCurrentColumn();
                case GLFW_KEY_D -> duplicateCurrentRow();
            }
        }
    }

    private void updateCurrentCell() {
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
            case STRING -> {
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

        if (columnRenaming) {
            columnsNames.set(databaseCol, cell.toString());
            columnRenaming = false;
        } else {
            columns.get(databaseCol).set(databaseRow, cell);
        }
    }

    private void renameCurrentColumn() {
        enableTextEditing(columnsNames.get(databaseCol));
        currentCellType = DataType.STRING;
        columnRenaming = true;
    }

    private void addRowAbove() {
        for (int i = 0; i < columns.size(); i++) {
            var column = columns.get(i);
            column.add(databaseRow, columnsTypes.get(i).getDefaultString());
        }
    }

    private void addRowBelow() {
        for (int i = 0; i < columns.size(); i++) {
            var column = columns.get(i);
            column.add(databaseRow + 1, columnsTypes.get(i).getDefaultString());
        }
        databaseRow++;
    }

    private void duplicateCurrentRow() {
        for (var column : columns) {
            column.add(databaseRow + 1, new StringBuilder(column.get(databaseRow)));
        }
        databaseRow++;
    }

}