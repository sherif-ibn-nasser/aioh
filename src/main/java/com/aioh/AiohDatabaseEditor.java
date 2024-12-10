package com.aioh;

import glm_.vec4.Vec4;

import java.util.ArrayList;
import java.util.Comparator;

public class AiohDatabaseEditor extends AiohEditor {
    public static final Vec4 CELL_COLOR1 = new Vec4(0.4f);
    public static final Vec4 CELL_COLOR2 = new Vec4(0.5f);

    public static final float CELL_H_PADDING = 56;
    public static final float CELL_V_PADDING = 56;

    protected ArrayList<StringBuilder> column = new ArrayList<>(32);

    @Override
    public void onInit() {
        super.onInit();
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
        lines.clear();
        lines.add(new StringBuilder());
        column.add(new StringBuilder("Sherif\nNassefghfghr"));
        column.add(new StringBuilder(""));
        column.add(new StringBuilder("Mohammed"));
        column.add(new StringBuilder("Khairy"));
    }

    @Override
    public void onDrawColorProgram() {
        drawColumn("Name", column);
    }

    @Override
    protected void drawText() {

        // TODO: Optimize and render only visible lines
        var text = new StringBuilder(column.size());
        for (int i = 0; i < column.size(); i++) {
            text.append(column.get(i));
            if (i < column.size() - 1)
                text.append('\n');
        }

        drawText(text, -cameraPos.getX(), -cameraPos.getY());
    }

    private void drawColumn(String columnName, ArrayList<StringBuilder> values) {
        var maxCol = values.stream()
                .flatMap((a) -> a.toString().lines())
                .max(Comparator.comparingInt(String::length))
                .orElse("")
                .length();

        var start = 5 - cursorCol * FONT_SIZE / 2f + cameraCursorDiff.getX() - CELL_H_PADDING;
        var end = -5 + (maxCol - cursorCol) * FONT_SIZE / 2f + cameraCursorDiff.getX() + CELL_H_PADDING;

        final int[] line = {0};
        boolean colorSwitch = true;

        var veryBottom = -cameraPos.getY() - (line[0] - 0.5f) * fontHeight;
        var veryTop = 0f;
        for (int i = 0; i < values.size(); i++) {
            var value = values.get(i);
            var linesCount = value.toString().lines().count();
            if (linesCount == 0) linesCount++;

            var bottom = -cameraPos.getY() - (line[0] - 0.5f) * fontHeight;
            line[0] += linesCount;
            var top = -cameraPos.getY() - (line[0] - 0.5f) * fontHeight;
            veryTop = top;

            // Background
            renderer.drawSolidRect(
                    start,
                    bottom,
                    end,
                    top,
                    (colorSwitch) ? CELL_COLOR1 : CELL_COLOR2
            );

            colorSwitch = !colorSwitch;

////             Line
//            renderer.drawSolidRect(
//                    start,
//                    bottom - linesCount * fontHeight,
//                    end,
//                    bottom - linesCount * fontHeight + 10,
//                    WHITE_COLOR
//            );

        }

        // Start border
        renderer.drawSolidRect(
                -5 - cursorCol * FONT_SIZE / 2f + cameraCursorDiff.getX() - CELL_H_PADDING,
                veryBottom,
                start,
                veryTop,
                WHITE_COLOR
        );

        // End border
        renderer.drawSolidRect(
                end,
                veryBottom,
                5 + (maxCol - cursorCol) * FONT_SIZE / 2f + cameraCursorDiff.getX() + CELL_H_PADDING,
                veryTop,
                WHITE_COLOR
        );
    }

    private void highlightLine(int line, int maxCol, Vec4 color) {

        var start = 5 - cursorCol * FONT_SIZE / 2f + cameraCursorDiff.getX() - CELL_H_PADDING;
        var end = -5 + (maxCol - cursorCol) * FONT_SIZE / 2f + cameraCursorDiff.getX() + CELL_H_PADDING;
        var bottom = -cameraPos.getY() - (line - 0.5f) * fontHeight;
        var top = -cameraPos.getY() - (line + 0.5f) * fontHeight;

        renderer.drawSolidRect(
                start,
                bottom,
                end,
                top,
                color
        );


        // Start bar
        renderer.drawSolidRect(
                -5 - cursorCol * FONT_SIZE / 2f + cameraCursorDiff.getX() - CELL_H_PADDING,
                bottom,
                start,
                top,
                WHITE_COLOR
        );

        // Line
        renderer.drawSolidRect(
                start,
                bottom - fontHeight,
                end,
                bottom - fontHeight + 10,
                WHITE_COLOR
        );

        // End bar
        renderer.drawSolidRect(
                end,
                bottom,
                5 + (maxCol - cursorCol) * FONT_SIZE / 2f + cameraCursorDiff.getX() + CELL_H_PADDING,
                top,
                WHITE_COLOR
        );
    }
}