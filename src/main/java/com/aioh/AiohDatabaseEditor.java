package com.aioh;

public class AiohDatabaseEditor extends AiohEditor {

    public static final float CELL_H_PADDING = 56;
    public static final float CELL_V_PADDING = 56;

    @Override
    public void onInit() {
        super.onInit();
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
    }

    @Override
    public void onDrawColorProgram() {
        renderer.drawTextureRegion(
                -5 - cursorCol * FONT_SIZE / 2f + cameraCursorDiff.getX() - CELL_H_PADDING,
                -0.5f * fontHeight,
                5 - cursorCol * FONT_SIZE / 2f + cameraCursorDiff.getX() - CELL_H_PADDING,
                0.5f * fontHeight,
                0, 0, 0, 0,
                WHITE_COLOR
        );
    }
}