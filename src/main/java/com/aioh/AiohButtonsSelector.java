package com.aioh;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.aioh.AiohDatabaseEditor.CELL_H_PADDING;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

/**
 * Select a button from a multiple
 *
 * @author Sherif Nasser
 */
public class AiohButtonsSelector extends AiohEditor {

    public static final StringBuilder NO_BUTTON = new StringBuilder("No");
    public static final StringBuilder YES_BUTTON = new StringBuilder("Yes");
    private static final ArrayList<StringBuilder> NO_YES_BUTTONS = new ArrayList<>(2);

    static {
        NO_YES_BUTTONS.add(NO_BUTTON);
        NO_YES_BUTTONS.add(YES_BUTTON);
    }

    private int maxLen;
    private float titlePosX = super.titlePosX = -CELL_H_PADDING;

    public void setLines(List<String> lines) {
        setLines(
                lines.stream().map(StringBuilder::new)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    public void setLines(ArrayList<StringBuilder> lines) {
        cursorLine = 0;
        this.lines = lines;
        maxLen = lines.stream().max(Comparator.comparingInt(a -> a.length())).orElse(new StringBuilder()).length() / 2;
    }

    public void displayNoAndYesButtons() {
        setLines(NO_YES_BUTTONS);
    }

    public StringBuilder getSelected() {
        return lines.get(cursorLine);
    }

    @Override
    public void onInit() {
        lines.clear();
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
        if (lines.isEmpty())
            return;
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
                else if (cursorLine == 0)
                    cursorLine = lines.size() - 1;
            }
            case GLFW_KEY_DOWN -> {
                if (cursorLine < lines.size() - 1)
                    cursorLine++;
                else if (cursorLine == lines.size() - 1)
                    cursorLine = 0;
            }
        }
    }

    @Override
    public void onModKeysPressed(int mods, int keyCode) {

    }
}