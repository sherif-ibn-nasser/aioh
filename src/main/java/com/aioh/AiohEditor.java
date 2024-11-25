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
    public static final int CHARS_COUNT_CAMERA_SCALE_THRESHOLD = 100;
    public static final int LINES_COUNT_CAMERA_SCALE_THRESHOLD = 10;

    private Timer timer = new Timer();
    private AiohRenderer renderer = new AiohRenderer();
    private ArrayList<StringBuilder> lines = new ArrayList<>(32);
    private int
            cursorLine = 0,
            cursorCol = 0,
            maxCursorCol = 0,
            maxLineLen,
            selectionStartLine,
            selectionStartCol,
            selectionLen = 0;
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
        mainProgram.use();
        mainProgram.setUniform("cameraScale", cameraScale);
        drawText();
        drawCursor();
        renderer.end();

        renderer.begin();
        textSelectionProgram.use();
        textSelectionProgram.setUniform("cameraScale", cameraScale);
        drawSelectedText();
        renderer.end();
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

        updateMaxLineLen();

        var len = lines.stream().max(Comparator.comparingInt(a -> a.length())).get().length();

        var cameraScaleVelocity = getCameraScaleVelocity();

        cameraScale += cameraScaleVelocity;

        if (cameraScale < 0.25f)
            cameraScale = 0.25f;

    }


    private void updateMaxLineLen() {
        maxLineLen = 0;

        int i = cursorLine - LINES_COUNT_CAMERA_SCALE_THRESHOLD / 2;

        if (i < 0) i = 0;
        for (; i < lines.size() && i <= cursorLine + LINES_COUNT_CAMERA_SCALE_THRESHOLD / 2; i++) {
            var len = lines.get(i).length();
            if (maxLineLen < len)
                maxLineLen = len;
        }
    }

    private float getCameraScaleVelocity() {

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
        // TODO: Optimize and render only visible lines

        selectionLen = 10;
        var len = 0;
        var text = new StringBuilder(lines.size());

        for (int i = selectionStartLine; i < lines.size() && len < selectionLen; i++) {

            var line = lines.get(i);

            for (int j = 0; j < line.length() && len < selectionLen; j++) {
                text.append(line.charAt(j));
                len++;
            }

            if (i < lines.size() - 1 && len < selectionLen) {
                text.append("          \n");
                len++;
            }
        }

        renderer.getFont().drawText(
                renderer,
                text,
                -cameraPos.getX(),
                -cameraPos.getY()
        );

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
