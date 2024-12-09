package com.aioh;

import glm_.vec4.Vec4;

public class AiohDatabaseEditor extends AiohEditor {
    public static final Vec4 TEXT_SELECTION_COLOR1 = new Vec4(0.4f);
    public static final Vec4 TEXT_SELECTION_COLOR2 = new Vec4(0.5f);

    public static final float CELL_H_PADDING = 56;
    public static final float CELL_V_PADDING = 56;

    @Override
    public void onInit() {
        super.onInit();
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
    }

    @Override
    public void onDrawColorProgram() {
        var maxCol = getMaxLineLen();

        highlightLine(0, maxCol, TEXT_SELECTION_COLOR1);
        highlightLine(1, maxCol, TEXT_SELECTION_COLOR2);
        highlightLine(2, maxCol, TEXT_SELECTION_COLOR1);
    }

    private void drawColumn(String columnName, String[] values) {

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