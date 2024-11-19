package com.aioh;

import com.aioh.graphics.AiohRenderer;
import com.aioh.graphics.Timer;
import glm_.vec4.Vec4;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class AiohEditor implements AiohWindow.EventsHandler {
    public static final int FONT_SIZE = 32;
    public static final int CURSOR_BLINK_THRESHOLD = 500;
    public static final int CURSOR_BLINK_PERIOD = 1000;

    private Timer timer = new Timer();
    private AiohRenderer renderer = new AiohRenderer();
    private StringBuilder text = new StringBuilder("");
    private float cursorLine = 0, cursorCol = 0, windowWidth, windowHeight;

    public static boolean isDefaultContext() {
        return GL.getCapabilities().OpenGL32;
    }

    public void init() {
        renderer.init();
    }

    public void loop(float windowWidth, float windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        renderer.getFont().drawText(
                renderer,
                text,
                windowWidth / 2,
                windowHeight / 2 + cursorLine * renderer.getFont().getFontHeight()
        );
        drawCursor();
    }

    private void drawCursor() {
        var t = (timer.getTime() - timer.getLastLoopTime()) * 1000;

        if (t > CURSOR_BLINK_THRESHOLD)
            renderer.getFont().drawText(
                    renderer,
                    "|",
                    cursorCol * FONT_SIZE / 2 - (float) FONT_SIZE / 4 + windowWidth / 2,
                    windowHeight / 2,
                    new Vec4((float) 0x4C / 256, (float) 0xAF / 256, (float) 0x50 / 256, 1)
            );

        if (t > CURSOR_BLINK_PERIOD)
            timer.getDelta();

    }

    @Override
    public void onTextInput(char[] newChars) {
        text.append(newChars);
        System.out.println("Text buffer: " + text);

        if (newChars[0] == '\n') {
            cursorCol = 0;
            cursorLine += 1;
        } else
            cursorCol += 1;
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

        if (text.isEmpty())
            return;

        var lastIdx = text.length() - 1;
        var lastChar = text.charAt(lastIdx);
        text.deleteCharAt(lastIdx);

        if (lastChar == '\n') {
            var lineLen = 0;
            for (int i = text.length() - 1; i >= 0; i--) {
                if (text.charAt(i) == '\n')
                    break;
                lineLen += 1;
            }
            cursorCol = lineLen;
            cursorLine -= 1;
        } else
            cursorCol -= 1;

        if (text.isEmpty())
            return;

        lastIdx = text.length() - 1;
        if (!Character.isDefined(text.charAt(lastIdx)))
            text.deleteCharAt(lastIdx);

        System.out.println("Text buffer: " + text);
    }

    private void onUpArrowPressed() {

    }

    private void onDownArrowPressed() {

    }

    private void onLeftArrowPressed() {

    }

    private void onRightArrowPressed() {

    }
}
