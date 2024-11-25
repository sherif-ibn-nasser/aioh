package com.aioh;

import com.aioh.graphics.AiohRenderer;
import com.aioh.graphics.Timer;
import glm_.vec2.Vec2;
import glm_.vec4.Vec4;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.Comparator;

import static com.aioh.graphics.AiohRenderer.mainProgram;
import static com.aioh.graphics.AiohRenderer.textSelectionProgram;
import static org.lwjgl.glfw.GLFW.*;

public class AiohEditor implements AiohWindow.EventsHandler {
    public static final Vec4 GREEN_COLOR = new Vec4((float) 0x4C / 256, (float) 0xAF / 256, (float) 0x50 / 256, 1);
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
    private Vec2 cameraPos = new Vec2(), cursorPos = new Vec2(), cameraCursorDiff = new Vec2();
    private float cameraScale = 1;

    public static boolean isDefaultContext() {
        return GL.getCapabilities().OpenGL32;
    }

    public void init() {
        lines.add(new StringBuilder(LINE_INITIAL_CAP));
        renderer.init();
    }

    public void loop() {
        updateCameraPos();
        updateCameraScale();

        renderer.begin();
        textSelectionProgram.use();
        textSelectionProgram.setUniform("cameraScale", cameraScale);
//        renderer.drawTextureRegion(-5, (float) -renderer.getFont().getFontHeight() / 2, 5, (float) renderer.getFont().getFontHeight() / 2, 0, 0, 0, 0, new Vec4(1));
        drawSelectedText();
        renderer.end();

        renderer.begin();
        mainProgram.use();
        mainProgram.setUniform("cameraScale", cameraScale);
        drawText();
        drawCursor();
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
                (float) (cursorCol * FONT_SIZE) / 2 - (float) FONT_SIZE / 4
        );
        cursorPos.setY(
                -cursorLine * renderer.getFont().getFontHeight()
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
                -cameraPos.getX(),
                -cameraPos.getY()
        );

    }

    private void drawSelectedText() {
        selectionStartCol = 3;
        selectionEndCol = 5;
        selectionStartLine = 0;
        selectionEndLine = 3;

        if (selectionStartLine >= lines.size() || selectionEndLine >= lines.size())
            return;

        var text = new StringBuilder(lines.size());

        if (selectionStartLine == selectionEndLine) {
            text.repeat(" ", selectionEndCol - selectionStartCol);
            renderer.getFont().drawText(
                    renderer,
                    text,
                    -cameraPos.getX() + (float) (selectionStartCol * FONT_SIZE) / 2,
                    -cameraPos.getY() - selectionStartLine * renderer.getFont().getFontHeight()
            );
            return;
        }

        var maxLen = getMaxLineLen();

        text.repeat(" ", maxLen - selectionStartCol);

        renderer.getFont().drawText(
                renderer,
                text,
                -cameraPos.getX() + (float) (selectionStartCol * FONT_SIZE) / 2,
                -cameraPos.getY() - selectionStartLine * renderer.getFont().getFontHeight()
        );

        text.setLength(0);

        for (int i = selectionStartLine + 1; i < lines.size() && i < selectionEndLine; i++) {

            text.repeat(" ", maxLen);
            text.append('\n');

            renderer.getFont().drawText(
                    renderer,
                    text,
                    -cameraPos.getX(),
                    -cameraPos.getY() - i * renderer.getFont().getFontHeight()
            );

            text.setLength(0);
        }

        if (selectionEndLine < lines.size()) {
            text.repeat(" ", selectionEndCol);

            renderer.getFont().drawText(
                    renderer,
                    text,
                    -cameraPos.getX(),
                    -cameraPos.getY() - selectionEndLine * renderer.getFont().getFontHeight()
            );
        }

    }

    private void drawCursor() {
        var t = (timer.getTime() - timer.getLastLoopTime()) * 1000;

        if (t % CURSOR_BLINK_PERIOD < CURSOR_BLINK_THRESHOLD)
            renderer.getFont().drawText(
                    renderer,
                    "|",
                    cameraCursorDiff.getX(),
                    cameraCursorDiff.getY(),
                    GREEN_COLOR
            );

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
                lines.add(cursorLine + 1, new StringBuilder(LINE_INITIAL_CAP));

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

    /**
     * @param mods    an integer represents mod key they are pressed.
     *                <a href="https://www.glfw.org/docs/3.3/group__mods.html#ga6ed94871c3208eefd85713fa929d45aa">See also</a>
     * @param keyCode the key pressed with the mod key
     */
    @Override
    public void onModKeysPressed(int mods, int keyCode) {
        // TODO
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
