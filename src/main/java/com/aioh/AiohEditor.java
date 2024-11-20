package com.aioh;

import com.aioh.graphics.AiohRenderer;
import com.aioh.graphics.Timer;
import glm_.vec4.Vec4;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class AiohEditor implements AiohWindow.EventsHandler {
    public static final int FONT_SIZE = 32;
    public static final int CURSOR_BLINK_THRESHOLD = 500;
    public static final int CURSOR_BLINK_PERIOD = 1000;
    public static final int LINE_INITIAL_CAP = 512;

    private Timer timer = new Timer();
    private AiohRenderer renderer = new AiohRenderer();
    private ArrayList<StringBuilder> lines = new ArrayList<>(32);
    private int cursorLine = 0, cursorCol = 0, maxCursorCol = 0;

    public static boolean isDefaultContext() {
        return GL.getCapabilities().OpenGL32;
    }

    public void init() {
        lines.add(new StringBuilder(LINE_INITIAL_CAP));
        renderer.init();
    }

    public void loop() {
        renderer.begin();
        drawText();
        drawCursor();
        renderer.end();
    }

    private void drawText() {
        // TODO: Optimize and render only visible lines
        var text = new StringBuilder(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            text.append(lines.get(i));
            if (i < lines.size() - 1)
                text.append('\n');
        }

        renderer.getFont().drawText(
                renderer,
                text,
                0,
                cursorLine * renderer.getFont().getFontHeight()
        );
    }

    private void drawCursor() {
        var t = (timer.getTime() - timer.getLastLoopTime()) * 1000;

        if (t > CURSOR_BLINK_THRESHOLD)
            renderer.getFont().drawText(
                    renderer,
                    "|",
                    (float) (cursorCol * FONT_SIZE) / 2 - (float) FONT_SIZE / 4,
                    0,
                    new Vec4((float) 0x4C / 256, (float) 0xAF / 256, (float) 0x50 / 256, 1)
            );

        if (t > CURSOR_BLINK_PERIOD)
            timer.getDelta();

    }

    private StringBuilder getCurrentLine() {
        return lines.get(cursorLine);
    }

    private char getCurrentChar() {
        var line = getCurrentLine();
        if (line.isEmpty()) {
            if (cursorLine > 0)
                return '\n';
            return '\0';
        }
        return getCurrentLine().charAt(cursorCol - 1);
    }

    private boolean isCursorAtStartOfFile() {
        return cursorLine == 0 && cursorCol == 0;
    }

    private boolean isCursorAtEndOfFile() {
        return cursorLine == lines.size() - 1 && cursorCol == getCurrentLine().length();
    }

    @Override
    public void onTextInput(char[] newChars) {

        if (newChars[0] == '\n') {
            if (cursorCol == getCurrentLine().length() && cursorCol != 0)
                lines.add(new StringBuilder(LINE_INITIAL_CAP));

            else {
                var prevLine = getCurrentLine();
                var newLine = prevLine.substring(cursorCol);
                prevLine.setLength(cursorCol);
                lines.add(cursorLine + 1, new StringBuilder(newLine));
            }

            cursorCol = 0;
            cursorLine += 1;
        } else {
            getCurrentLine().insert(cursorCol, newChars);
            cursorCol += newChars.length;
        }

        maxCursorCol = cursorCol;
    }

    @Override
    public void onKeyPressed(int keyCode) {
        switch (keyCode) {
            case GLFW_KEY_ENTER -> onTextInput(new char[]{'\n'});
            case GLFW_KEY_BACKSPACE -> onBackspacePressed();
            case GLFW_KEY_UP -> onUpArrowPressed();
            case GLFW_KEY_DOWN -> onDownArrowPressed();
            case GLFW_KEY_LEFT -> onLeftArrowPressed();
            case GLFW_KEY_RIGHT -> onRightArrowPressed();
        }
    }

    private void onBackspacePressed() {

        if (isCursorAtStartOfFile())
            return;

        if (cursorCol == 0) {
            var lineBelow = getCurrentLine();
            lines.remove(cursorLine);
            cursorLine -= 1;
            cursorCol = getCurrentLine().length();
            getCurrentLine().append(lineBelow);
        } else {
            cursorCol -= 1;
            getCurrentLine().deleteCharAt(cursorCol);
        }

        maxCursorCol = cursorCol;

        // TODO: Handle deleting 3 and 4 bytes characters
    }

    private void onUpArrowPressed() {

        if (isCursorAtStartOfFile())
            return;

        if (cursorLine == 0) {
            cursorCol = 0;
            return;
        }

        cursorLine -= 1;
        var currentLen = getCurrentLine().length();

        cursorCol = Math.min(currentLen, maxCursorCol);
    }

    private void onDownArrowPressed() {

        if (isCursorAtEndOfFile())
            return;

        if (cursorLine == lines.size() - 1) {
            cursorCol = getCurrentLine().length();
            return;
        }

        cursorLine += 1;
        var currentLen = getCurrentLine().length();

        cursorCol = Math.min(currentLen, maxCursorCol);
    }

    private void onLeftArrowPressed() {

        if (isCursorAtStartOfFile())
            return;

        if (cursorCol == 0) {
            cursorLine -= 1;
            maxCursorCol = cursorCol = getCurrentLine().length();
            return;
        }

        maxCursorCol = cursorCol -= 1;

    }

    private void onRightArrowPressed() {

        if (isCursorAtEndOfFile())
            return;

        if (cursorCol == getCurrentLine().length()) {
            cursorLine += 1;
            maxCursorCol = cursorCol = 0;
            return;
        }

        maxCursorCol = cursorCol += 1;
    }
}
