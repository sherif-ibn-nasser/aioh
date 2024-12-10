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

    private ArrayList<ArrayList<StringBuilder>> columns = new ArrayList<>(32);
    private ArrayList<Float> columnsMaxCells = new ArrayList<>(32), columnsCenters = new ArrayList<>(32);
    private float maxCellChars = 0;
    private int databaseCol = 0;

    @Override
    public void onInit() {
        super.onInit();
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
        lines.clear();
        lines.add(new StringBuilder());
        var col0 = new ArrayList<StringBuilder>();
        col0.add(new StringBuilder("000"));
        col0.add(new StringBuilder("02"));
        col0.add(new StringBuilder("Md"));
        col0.add(new StringBuilder("0"));

        var col1 = new ArrayList<StringBuilder>();
        col1.add(new StringBuilder("df5"));
        col1.add(new StringBuilder("df6"));
        col1.add(new StringBuilder("99"));
        col1.add(new StringBuilder("00"));

        var col2 = new ArrayList<StringBuilder>();
        col2.add(new StringBuilder("aa"));
        col2.add(new StringBuilder("b"));
        col2.add(new StringBuilder("c"));
        col2.add(new StringBuilder("d"));

        var col3 = new ArrayList<StringBuilder>();
        col3.add(new StringBuilder("00ffaa"));
        col3.add(new StringBuilder("11"));
        col3.add(new StringBuilder("22"));
        col3.add(new StringBuilder("33"));

        columns.add(col0);
        columns.add(col1);
        columns.add(col2);
        columns.add(col3);
    }

    private ArrayList<StringBuilder> getCurrentCol() {
        return columns.get(databaseCol);
    }

    @Override
    public void onDrawColorProgram() {
        var end = maxCellChars * FONT_SIZE / 2f + CELL_H_PADDING;
        var start = -end;
        drawColumn("Name", databaseCol, start, end);
        drawBorderAroundCurrentCell();
        columnsCenters.set(databaseCol, (start + end) / 2);

        for (int i = databaseCol + 1; i < columns.size(); i++) {
            start = end + 10;
            end = start + getMaxCellChars(i) * FONT_SIZE + 2 * CELL_H_PADDING;
            drawColumn("Age", i, start, end);
            columnsCenters.set(i, (start + end) / 2);
        }

        end = maxCellChars * FONT_SIZE / 2f + CELL_H_PADDING;
        start = -end;

        for (int i = databaseCol - 1; i >= 0; i--) {
            end = start - 10;
            start = end - getMaxCellChars(i) * FONT_SIZE - 2 * CELL_H_PADDING;
            drawColumn("Age", i, start, end);
            columnsCenters.set(i, (start + end) / 2);
        }

    }

    @Override
    protected void onDrawMainProgram() {

        for (int i = 0; i < columns.size(); i++) {
            drawText(i, columnsCenters.get(i));
        }

    }

    @Override
    protected void onStartRendering() {
        columnsMaxCells.clear();
        columnsCenters.clear();
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

            var width = Math.min(CELL_CHARS_THRESHOLD, len) / 2f;
            columnsMaxCells.add(width);
            columnsCenters.add(0.0f);
        }
        maxCellChars = getMaxCellChars(databaseCol);
    }

    private float getMaxCellChars(int column) {
        return columnsMaxCells.get(column);
    }

    @Override
    protected void onFinishRendering() {
    }

    private void drawColumn(String columnName, int columnIdx, float start, float end) {
        var column = columns.get(columnIdx);
        for (int i = 0; i < column.size(); i++) {

            var bottom = -cameraPos.getY() - (i + 0.5f) * fontHeight;
            // Background
            renderer.drawSolidRect(
                    start,
                    bottom,
                    end,
                    bottom + fontHeight,
                    (i % 2 == 0) ? CELL_COLOR1 : CELL_COLOR2
            );

        }

    }

    private void drawBorderAroundCurrentCell() {
        var end = maxCellChars * FONT_SIZE / 2f + CELL_H_PADDING;
        var start = -end;

        var bottom = -0.5f * fontHeight;
        var top = 0.5f * fontHeight;

        // Start border
        renderer.drawSolidRect(
                start - 10,
                bottom,
                start,
                top,
                WHITE_COLOR
        );

        // End border
        renderer.drawSolidRect(
                end,
                bottom,
                end + 10,
                top,
                WHITE_COLOR
        );

        // Top border
        renderer.drawSolidRect(
                start,
                top - 10,
                end,
                top,
                WHITE_COLOR
        );

        // Bottom border
        renderer.drawSolidRect(
                start,
                bottom,
                end,
                bottom + 10,
                WHITE_COLOR
        );
    }


    protected void drawText(int columnIdx, float centerX) {
        // TODO: Optimize and render only visible lines
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

            drawText(text, centerX - cameraPos.getX() - text.length() * FONT_SIZE / 4f, -cameraPos.getY() - i * fontHeight);

            text.setLength(0);
        }

    }

    @Override
    public void onTextInput(char[] newChars) {

    }

    @Override
    public void onKeyPressed(int keyCode) {
        switch (keyCode) {
            case GLFW_KEY_ENTER -> {
                // TODO: Show full cell
            }
            case GLFW_KEY_UP -> onUpArrowPressed();
            case GLFW_KEY_DOWN -> onDownArrowPressed();
            case GLFW_KEY_RIGHT -> onRightArrowPressed();
            case GLFW_KEY_LEFT -> onLeftArrowPressed();
        }
    }

    private void onUpArrowPressed() {
        if (cursorLine > 0)
            cursorLine--;
    }

    private void onDownArrowPressed() {
        if (cursorLine < getCurrentCol().size() - 1)
            cursorLine++;
    }

    private void onRightArrowPressed() {
        if (databaseCol < columns.size() - 1)
            databaseCol++;
    }

    private void onLeftArrowPressed() {
        if (databaseCol > 0)
            databaseCol--;
    }

}