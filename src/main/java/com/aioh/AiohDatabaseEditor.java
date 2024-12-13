package com.aioh;

import glm_.vec4.Vec4;

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
    }

    private ArrayList<ArrayList<StringBuilder>> columns = new ArrayList<>(8);
    private ArrayList<String> columnsNames = new ArrayList<>(8);
    private ArrayList<DataType> columnsTypes = new ArrayList<>(8);
    private ArrayList<Float> columnsWidths = new ArrayList<>(8);
    private int databaseCol = 0, databaseRow = 0;
    private float currentColWidth = 0;
    private boolean cellEditing = false;
    private DataType currentCellType;

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

        currentCellType = columnsTypes.get(0);
    }

    private ArrayList<StringBuilder> getCurrentCol() {
        return columns.get(databaseCol);
    }

    @Override
    protected void onStartRendering() {
        columnsWidths.clear();
        // Compare first line lengths of each cell until certain threshold
        // FIXME: Maybe this is slow?
        for (int i = 0; i < columns.size(); i++) {
            var col = columns.get(i);
            var len = col.stream()
                    .max(Comparator.comparingInt(a -> {
                        var idx = a.indexOf("\n");
                        return (idx == -1) ? a.length() : idx;
                    }))
                    .orElse(new StringBuilder())
                    .length();

            len = Math.min(CELL_CHARS_THRESHOLD, len);
            var columnWidth = 0.5f * len * FONT_SIZE + 2 * CELL_H_PADDING;
            columnsWidths.add(columnWidth);
        }
    }

    @Override
    protected void updateCameraPos() {

        if (cellEditing) {
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
    public void onDrawColorProgram() {
        if (cellEditing) {
            super.onDrawColorProgram();
            return;
        }
        drawColumnsBackgrounds();
        drawBorderAroundCurrentCell();
    }

    @Override
    protected void onDrawMainProgram() {
        if (cellEditing) {
            super.onDrawMainProgram();
            return;
        }
        drawColumnsTexts();

//
//        renderer.end();
//        AiohRenderer.colorProgram.use();
//        renderer.begin();
//        renderer.drawSolidRect(
//                -AiohWindow.width / 2f,
//                -AiohWindow.height / 2f,
//                AiohWindow.width / 2f,
//                -AiohWindow.height / 2f + renderer.getDebugFont().getFontHeight(),
//                AIOH_COLOR_DARK
//        );
//        renderer.end();
//        AiohRenderer.mainProgram.use();
//        renderer.begin();
//        renderer.getDebugFont().drawText(
//                renderer,
//                "Cell No.: " + (cursorLine + 1) +
//                        ", Column: \"" +
//                        columnsNames.get(databaseCol) +
//                        "\" (" + columnsTypes.get(databaseCol) + ")",
//                -AiohWindow.width / 2f + 10,
//                -AiohWindow.height / 2f,
//                WHITE_COLOR
//        );
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

    @Override
    public void onTextInput(char[] newChars) {
        if (!cellEditing)
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
        if (cellEditing) {
            if (keyCode == GLFW_KEY_ESCAPE)
                disableCellEditing();
            else
                super.onKeyPressed(keyCode);
            return;
        }
        switch (keyCode) {
            case GLFW_KEY_ENTER -> {

                currentCellType = columnsTypes.get(databaseCol);
                var cell = columns.get(databaseCol).get(databaseRow);

                if (currentCellType == DataType.BOOL) {
                    if (cell == TRUE_STRING) {
                        columns.get(databaseCol).set(databaseRow, FALSE_STRING);
                    } else {
                        columns.get(databaseCol).set(databaseRow, TRUE_STRING);
                    }
                    return;
                }
                // TODO: Show full cell
                lines.clear();
                lines.add(new StringBuilder());
                cursorCol = 0;
                cursorLine = 0;
                for (int i = 0; i < cell.length(); i++) {
                    super.onTextInput(new char[]{cell.charAt(i)});
                }
                cellEditing = true;
                renderer.getFont().setFontSpacing(0);
                this.fontSpacing = 0;
                this.fontHeight = renderer.getFont().getFontHeight();
            }
            case GLFW_KEY_UP -> onUpArrowPressed();
            case GLFW_KEY_DOWN -> onDownArrowPressed();
            case GLFW_KEY_RIGHT -> onRightArrowPressed();
            case GLFW_KEY_LEFT -> onLeftArrowPressed();
        }
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
        if (cellEditing) {
            if ((mods & GLFW_MOD_CONTROL) != 0) {
                switch (keyCode) {
                    case GLFW_KEY_S -> updateCurrentCell();
                }
                return;
            }
            super.onModKeysPressed(mods, keyCode);
            return;
        }
        // TODO
    }

    private void disableCellEditing() {
        cellEditing = false;
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
        this.fontSpacing = CELL_V_PADDING;
        this.fontHeight = renderer.getFont().getFontHeight() + fontSpacing;
    }

    private void updateCurrentCell() {
        disableCellEditing();
        var cell = new StringBuilder(lines.size());
        // FIXME: This might be very slow?
        if (currentCellType == DataType.FLOAT) {
            var num = lines.getFirst();
            if (num.indexOf(".") == -1)
                num.append(".0");
            cell.append(num);
        } else {
            for (int i = 0; i < lines.size() - 1; i++) {
                cell.append(lines.get(i));
                cell.append('\n');
            }
            cell.append(lines.getLast());
        }
        columns.get(databaseCol).set(databaseRow, cell);
    }

}