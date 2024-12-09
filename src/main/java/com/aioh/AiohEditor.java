package com.aioh;

import com.aioh.graphics.AiohRenderer;
import com.aioh.graphics.Timer;
import glm_.vec2.Vec2;
import glm_.vec4.Vec4;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.Comparator;

import static com.aioh.graphics.AiohRenderer.colorProgram;
import static com.aioh.graphics.AiohRenderer.mainProgram;
import static org.lwjgl.glfw.GLFW.*;

public class AiohEditor implements AiohWindow.EventsHandler {
    public static final Vec4 CURSOR_COLOR = new Vec4((float) 0x4C / 256, (float) 0xAF / 256, (float) 0x50 / 256, 1);
    public static final Vec4 TEXT_SELECTION_COLOR = new Vec4(0.5f);
    public static final Vec4 WHITE_COLOR = new Vec4(1);
    public static final int FONT_SIZE = 128;
    public static final int CURSOR_BLINK_THRESHOLD = 500;
    public static final int CURSOR_BLINK_PERIOD = 1000;
    public static final int LINE_INITIAL_CAP = 512;
    public static final int CAMERA_VELOCITY = 5;
    public static final int FPS = 60;
    public static final int CHARS_COUNT_CAMERA_SCALE_THRESHOLD = 50;
    public static final int LINES_COUNT_CAMERA_SCALE_THRESHOLD = 10;

    private Timer timer = new Timer();
    private AiohRenderer renderer = new AiohRenderer();
    private ArrayList<StringBuilder> lines = new ArrayList<>(32);
    private int
            cursorLine = 0,
            cursorCol = 0,
            maxCursorCol = 0,
            selectionStartLine = 0,
            selectionStartCol = 0,
            selectionEndLine = 0,
            selectionEndCol = 0;
    private boolean selectRight = false, selectLeft = false;
    private Vec2 cameraPos = new Vec2(), cursorPos = new Vec2(), cameraCursorDiff = new Vec2();
    private float cameraScale = 1, fontHeight;

    public static boolean isDefaultContext() {
        return GL.getCapabilities().OpenGL32;
    }

    public void init() {
        lines.add(new StringBuilder(LINE_INITIAL_CAP));
        renderer.init();
        fontHeight = renderer.getFont().getFontHeight();
    }

    public void loop() {
        updateCameraPos();
        updateCameraScale();

        colorProgram.use();
        colorProgram.setUniform("cameraScale", cameraScale);

        renderer.begin();
        drawSelectedText();
        renderer.end();

        renderer.begin();
        drawCursor();
        renderer.end();

        mainProgram.use();
        mainProgram.setUniform("cameraScale", cameraScale);

        renderer.begin();
        drawText();
        renderer.end();
    }

    private int getMaxLineLen() {
        var line = lines.stream().max(Comparator.comparingInt(a -> a.length()));
        return line.map(stringBuilder -> stringBuilder.length()).orElse(0);
    }

    private int getMaxLineLen(int fromIndex, int toIndex) {
        if (fromIndex >= lines.size())
            return 0;

        if (fromIndex < 0)
            fromIndex = 0;

        if (toIndex >= lines.size())
            toIndex = lines.size() - 1;

        if (toIndex < fromIndex)
            return 0;

        if (toIndex == fromIndex)
            return lines.get(fromIndex).length();

        var line = lines.subList(fromIndex, toIndex).stream().max(Comparator.comparingInt(a -> a.length()));

        return line.map(stringBuilder -> stringBuilder.length()).orElse(0);
    }

    private void updateCameraPos() {

        cursorPos.setX(
                (float) (cursorCol * FONT_SIZE) / 2 - (float) FONT_SIZE / 2
        );
        cursorPos.setY(
                -cursorLine * fontHeight
        );

        cameraCursorDiff = cursorPos.minus(cameraPos);

        cameraPos = cameraPos.plus(
                cameraCursorDiff.times((float) CAMERA_VELOCITY / FPS)
        );

    }

    public void updateCameraScale() {

        var cameraScaleVelocity = getCameraScaleVelocity();

        cameraScale += cameraScaleVelocity;

        if (cameraScale < 0.25f)
            cameraScale = 0.25f;

    }

    private float getCameraScaleVelocity() {

        var maxLineLen = getMaxLineLen();

        if (maxLineLen > CHARS_COUNT_CAMERA_SCALE_THRESHOLD)
            maxLineLen = CHARS_COUNT_CAMERA_SCALE_THRESHOLD;

        var targetCameraScale = 1f - (float) maxLineLen / CHARS_COUNT_CAMERA_SCALE_THRESHOLD;

        return (targetCameraScale - cameraScale) / FPS;
    }

    private void drawText(CharSequence text, float centerX, float centerY) {
        drawText(text, centerX, centerY, WHITE_COLOR);
    }

    private void drawText(CharSequence text, float centerX, float centerY, Vec4 color) {
        renderer.getFont().drawText(renderer, text, centerX - 0.5f * FONT_SIZE, centerY - 0.5f * fontHeight, color);
    }

    private void drawText() {
        // TODO: Optimize and render only visible lines
        var text = new StringBuilder(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            text.append(lines.get(i));
            if (i < lines.size() - 1)
                text.append('\n');
        }

        drawText(text, -cameraPos.getX(), -cameraPos.getY());

    }

    private void drawSelectedText() {

        if (selectionStartLine >= lines.size() || selectionEndLine >= lines.size())
            return;

        var text = new StringBuilder(lines.size());

        if (selectionStartLine == selectionEndLine) {
            text.repeat(" ", selectionEndCol - selectionStartCol);

            drawText(
                    text,
                    -cameraPos.getX() + 0.5f * (selectionStartCol * FONT_SIZE),
                    -cameraPos.getY() - selectionStartLine * fontHeight,
                    TEXT_SELECTION_COLOR
            );

            return;
        }

        var maxLen = getMaxLineLen();

        text.repeat(" ", maxLen - selectionStartCol);

        drawText(
                text,
                -cameraPos.getX() + 0.5f * (selectionStartCol * FONT_SIZE),
                -cameraPos.getY() - selectionStartLine * fontHeight,
                TEXT_SELECTION_COLOR
        );

        text.setLength(0);

        for (int i = selectionStartLine + 1; i < lines.size() && i < selectionEndLine; i++) {

            text.repeat(" ", maxLen);
            text.append('\n');

            drawText(
                    text,
                    -cameraPos.getX(),
                    -cameraPos.getY() - i * fontHeight,
                    TEXT_SELECTION_COLOR
            );

            text.setLength(0);
        }

        if (selectionEndLine < lines.size()) {
            text.repeat(" ", selectionEndCol);
            drawText(
                    text,
                    -cameraPos.getX(),
                    -cameraPos.getY() - selectionEndLine * fontHeight,
                    TEXT_SELECTION_COLOR
            );
        }

    }

    private void drawCursor() {
        var t = (timer.getTime() - timer.getLastLoopTime()) * 1000;

        if (t % CURSOR_BLINK_PERIOD < CURSOR_BLINK_THRESHOLD) {
            renderer.drawTextureRegion(
                    -5 + cameraCursorDiff.getX(),
                    -0.5f * fontHeight + cameraCursorDiff.getY(),
                    5 + cameraCursorDiff.getX(),
                    0.5f * fontHeight + cameraCursorDiff.getY(),
                    0, 0, 0, 0,
                    CURSOR_COLOR
            );
        }

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

    private boolean isEmptySelection() {
        return selectionStartLine == selectionEndLine && selectionStartCol == selectionEndCol;
    }

    private int getMinCursorCol() {
        return Math.min(getCurrentLine().length(), maxCursorCol);
    }

    private int getMinCursorCol(int index) {
        return Math.min(lines.get(index).length(), maxCursorCol);
    }

    @Override
    public void onTextInput(char[] newChars) {

        if (newChars[0] == '\n') {
            if (cursorCol == getCurrentLine().length() && cursorCol != 0)
                lines.add(cursorLine + 1, new StringBuilder(LINE_INITIAL_CAP));

            else {
                var prevLine = getCurrentLine();
                var newLine = prevLine.substring(cursorCol);
                prevLine.setLength(cursorCol);
                lines.add(cursorLine + 1, new StringBuilder(newLine));
            }

            cursorCol = 0;
            cursorLine++;
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
            case GLFW_KEY_RIGHT -> onRightArrowPressed();
            case GLFW_KEY_LEFT -> onLeftArrowPressed();
        }
    }

    /**
     * @param mods    an integer represents mod key they are pressed.
     *                <a href="https://www.glfw.org/docs/3.3/group__mods.html#ga6ed94871c3208eefd85713fa929d45aa">See also</a>
     * @param keyCode the key pressed with the mod key
     */
    @Override
    public void onModKeysPressed(int mods, int keyCode) {
        if ((mods & GLFW_MOD_SHIFT) != 0) {
            switch (keyCode) {
                case GLFW_KEY_UP -> selectToUp();
                case GLFW_KEY_DOWN -> selectToDown();
                case GLFW_KEY_RIGHT -> selectCharToRight();
                case GLFW_KEY_LEFT -> selectCharToLeft();
            }
        }
    }

    private void onBackspacePressed() {

        if (isCursorAtStartOfFile())
            return;

        if (cursorCol == 0) {
            var lineBelow = getCurrentLine();
            lines.remove(cursorLine);
            cursorLine--;
            cursorCol = getCurrentLine().length();
            getCurrentLine().append(lineBelow);
        } else {
            cursorCol--;
            getCurrentLine().deleteCharAt(cursorCol);
        }

        maxCursorCol = cursorCol;

        // TODO: Handle deleting 3 and 4 bytes characters
    }

    private void onUpArrowPressed() {

        if (selectRight || selectLeft) {
            clearTextSelection(selectionStartLine, selectionStartCol);
            return;
        }

        if (isCursorAtStartOfFile())
            return;

        if (cursorLine == 0) {
            cursorCol = 0;
            return;
        }

        cursorLine--;
        cursorCol = getMinCursorCol();
    }

    private void onDownArrowPressed() {

        if (selectRight || selectLeft) {
            clearTextSelection(selectionEndLine, selectionEndCol);
            return;
        }

        if (isCursorAtEndOfFile())
            return;

        if (cursorLine == lines.size() - 1) {
            cursorCol = getCurrentLine().length();
            return;
        }

        cursorLine++;
        cursorCol = getMinCursorCol();
    }

    private void onRightArrowPressed() {

        if (selectRight || selectLeft) {
            clearTextSelection(selectionEndLine, selectionEndCol);
            return;
        }

        if (isCursorAtEndOfFile())
            return;

        if (cursorCol == getCurrentLine().length()) {
            cursorLine++;
            maxCursorCol = cursorCol = 0;
            return;
        }

        maxCursorCol = ++cursorCol;
    }

    private void onLeftArrowPressed() {

        if (selectRight || selectLeft) {
            clearTextSelection(selectionStartLine, selectionStartCol);
            return;
        }

        if (isCursorAtStartOfFile())
            return;

        if (cursorCol == 0) {
            cursorLine--;
            maxCursorCol = cursorCol = getCurrentLine().length();
            return;
        }

        maxCursorCol = --cursorCol;

    }

    private void clearTextSelection(int newLine, int newCol) {
        selectionStartLine = selectionEndLine = cursorLine = newLine;
        selectionStartCol = selectionEndCol = maxCursorCol = cursorCol = newCol;
        selectRight = selectLeft = false;
    }

    private void updateCursorIfEmptyTextSelection() {
        if (isEmptySelection()) {
            selectionStartLine = selectionEndLine = cursorLine;
            selectionStartCol = selectionEndCol = maxCursorCol = cursorCol;
            selectLeft = selectRight = false;
        }
    }

    private void selectToUp() {

        updateCursorIfEmptyTextSelection();

        if (selectRight) {

            if (selectionEndLine > 0) {
                selectionEndLine--;
                selectionEndCol = getMinCursorCol(selectionEndLine);
            }

            cursorLine = selectionEndLine;
            cursorCol = selectionEndCol;

        } else {

            if (selectionStartLine == 0 && selectionStartCol > 0)
                selectionStartCol = 0;
            if (selectionStartLine > 0) {
                selectionStartLine--;
                selectionStartCol = getMinCursorCol(selectionStartLine);
            }

            cursorLine = selectionStartLine;
            cursorCol = selectionStartCol;
            selectLeft = true;
        }
    }

    private void selectToDown() {

        updateCursorIfEmptyTextSelection();

        if (selectLeft) {

            if (selectionStartLine < lines.size() - 1) {
                selectionStartLine++;
                selectionStartCol = getMinCursorCol(selectionStartLine);
            }

            cursorLine = selectionStartLine;
            cursorCol = selectionStartCol;

        } else {
            var endLineLen = lines.get(selectionEndLine).length();

            if (selectionEndLine == lines.size() - 1 && selectionEndCol < endLineLen)
                selectionEndCol = endLineLen;
            else if (selectionEndLine < lines.size() - 1) {
                selectionEndLine++;
                selectionEndCol = getMinCursorCol(selectionEndLine);
            }

            cursorLine = selectionEndLine;
            cursorCol = selectionEndCol;
            selectRight = true;
        }

    }

    private void selectCharToRight() {

        updateCursorIfEmptyTextSelection();

        if (selectLeft) {

            if (selectionStartCol < lines.get(selectionStartLine).length())
                selectionStartCol++;
            else if (selectionStartCol == lines.get(selectionStartLine).length() && selectionStartLine < lines.size() - 1) {
                selectionStartLine++;
                selectionStartCol = 0;
            }

            cursorLine = selectionStartLine;
            maxCursorCol = cursorCol = selectionStartCol;
        } else {

            if (selectionEndCol < lines.get(selectionEndLine).length())
                selectionEndCol++;
            else if (selectionEndCol == lines.get(selectionEndLine).length() && selectionEndLine < lines.size() - 1) {
                selectionEndLine++;
                selectionEndCol = 0;
            }

            cursorLine = selectionEndLine;
            maxCursorCol = cursorCol = selectionEndCol;
            selectRight = true;
        }

    }

    private void selectCharToLeft() {

        updateCursorIfEmptyTextSelection();

        if (selectRight) {

            if (selectionEndCol > 0)
                selectionEndCol--;
            else if (selectionEndCol == 0 && selectionEndLine > 0) {
                selectionEndLine--;
                selectionEndCol = lines.get(selectionEndLine).length();
            }

            cursorLine = selectionEndLine;
            maxCursorCol = cursorCol = selectionEndCol;

        } else {

            if (selectionStartCol > 0)
                selectionStartCol--;
            else if (selectionStartCol == 0 && selectionStartLine > 0) {
                selectionStartLine--;
                selectionStartCol = lines.get(selectionStartLine).length();
            }

            cursorLine = selectionStartLine;
            maxCursorCol = cursorCol = selectionStartCol;
            selectLeft = true;
        }

    }
}