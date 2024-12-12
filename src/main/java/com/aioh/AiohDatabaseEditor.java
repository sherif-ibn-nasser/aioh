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

    private ArrayList<ArrayList<StringBuilder>> columns = new ArrayList<>(32);
    private ArrayList<Integer> columnsLens = new ArrayList<>(32);
    private int databaseCol = 0;
    private float currentColWidth = 0;

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
        col1.add(new StringBuilder("d5"));
        col1.add(new StringBuilder("d6"));
        col1.add(new StringBuilder("99"));
        col1.add(new StringBuilder("00"));

        var col2 = new ArrayList<StringBuilder>();
        col2.add(new StringBuilder("aa"));
        col2.add(new StringBuilder("bjjj"));
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
    protected void onStartRendering() {
        columnsLens.clear();
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
            columnsLens.add(len);
        }
    }

    @Override
    protected void updateCameraPos() {

        var centerX = 0.0f;

        for (int i = 0; i < columns.size(); i++) {
            var textWidth = columnsLens.get(i) * FONT_SIZE / 2f;

            if (i < databaseCol)
                centerX += textWidth + 2 * CELL_H_PADDING + CELL_SPACING;
            else if (i == databaseCol) {
                centerX += textWidth / 2f + CELL_H_PADDING;
                break;
            }
        }
        cursorPos.setX(centerX);
        cursorPos.setY(
                -cursorLine * fontHeight
        );

        cameraCursorDiff = cursorPos.minus(cameraPos);

        cameraPos = cameraPos.plus(
                cameraCursorDiff.times((float) CAMERA_VELOCITY / FPS)
        );
    }

    @Override
    public void onDrawColorProgram() {
        drawColumnsBackgrounds();
        drawBorderAroundCurrentCell();
    }

    @Override
    protected void onDrawMainProgram() {
        drawColumnsTexts();
    }

    @Override
    protected void onFinishRendering() {
    }

    private void drawColumnsBackgrounds() {

        var start = -cameraPos.getX();

        for (int i = 0; i < columns.size(); i++) {
            var column = columns.get(i);
            var end = start + columnsLens.get(i) * FONT_SIZE / 2f + 2 * CELL_H_PADDING;

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
        var targetColWidth = columnsLens.get(databaseCol) * FONT_SIZE / 2f + 2 * CELL_H_PADDING;
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
                WHITE_COLOR
        );

        // End border
        renderer.drawSolidRect(
                end,
                bottom,
                end + CELL_SPACING,
                top,
                WHITE_COLOR
        );

        // Top border
        renderer.drawSolidRect(
                start,
                top - CELL_SPACING,
                end,
                top,
                WHITE_COLOR
        );

        // Bottom border
        renderer.drawSolidRect(
                start,
                bottom,
                end,
                bottom + CELL_SPACING,
                WHITE_COLOR
        );
    }

    private void drawColumnsTexts() {
        var cellStart = 0.0f;

        for (int i = 0; i < columns.size(); i++) {
            var columnWidth = columnsLens.get(i) * FONT_SIZE / 2f + 2 * CELL_H_PADDING;
            drawColumnText(i, cellStart + columnWidth / 2);
            cellStart += columnWidth + CELL_SPACING;
        }
    }

    private void drawColumnText(int columnIdx, float columnCenter) {
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