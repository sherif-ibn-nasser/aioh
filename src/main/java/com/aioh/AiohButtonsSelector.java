package com.aioh;

import java.util.ArrayList;
import java.util.Comparator;

import static com.aioh.AiohDatabaseEditor.CELL_H_PADDING;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

public class AiohButtonsSelector extends AiohEditor {

    private int maxLen;

    public void setLines(ArrayList<StringBuilder> lines) {
        this.lines = lines;
        maxLen = lines.stream().max(Comparator.comparingInt(a -> a.length())).get().length() / 2;
    }

    @Override
    public void onInit() {
        lines.clear();
        lines.add(new StringBuilder("."));
        lines.add(new StringBuilder(".."));
        lines.add(new StringBuilder("Hello"));
        lines.add(new StringBuilder("Hello Guys  ksjhg "));
        lines.add(new StringBuilder("Hello"));
        setLines(lines);
    }

    @Override
    protected void updateCameraPos() {

        var centerLen = maxLen;

        if (centerLen * FONT_SIZE * cameraScale > 0.97 * AiohWindow.width) {
            centerLen = (int) (0.97 * AiohWindow.width / FONT_SIZE / cameraScale);
        }

        cursorCol = centerLen;

        super.updateCameraPos();
    }

    @Override
    protected void onDrawColorProgram() {
        renderer.drawSolidRect(
                -cameraPos.getX() - 0.5f * FONT_SIZE - CELL_H_PADDING,
                -cameraPos.getY() - (cursorLine - 0.5f) * fontHeight,
                -cameraPos.getX() + 0.5f * ((lines.get(cursorLine).length() - 1) * FONT_SIZE) + CELL_H_PADDING,
                -cameraPos.getY() - (cursorLine + 0.5f) * fontHeight,
                TEXT_SELECTION_COLOR
        );
    }

    @Override
    public void onTextInput(char[] newChars) {

    }

    @Override
    public void onKeyPressed(int keyCode) {
        switch (keyCode) {
            case GLFW_KEY_UP -> {
                if (cursorLine > 0)
                    cursorLine--;
            }
            case GLFW_KEY_DOWN -> {
                if (cursorLine < lines.size() - 1)
                    cursorLine++;
            }
        }
    }

    @Override
    public void onModKeysPressed(int mods, int keyCode) {

    }
}