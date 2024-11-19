package com.aioh;

import com.aioh.graphics.AiohRenderer;
import com.aioh.graphics.Timer;
import glm_.vec2.Vec2;
import glm_.vec4.Vec4;
import org.lwjgl.opengl.GL;

public class AiohEditor {
    public static final int FONT_SIZE = 32;
    public static final int CURSOR_BLINK_THRESHOLD = 500;
    public static final int CURSOR_BLINK_PERIOD = 1000;

    private Timer timer = new Timer();
    private AiohRenderer renderer = new AiohRenderer();
    private StringBuilder text = new StringBuilder("");
    private Vec2 cursorPos = new Vec2();
    private float windowWidth, windowHeight;

    public static boolean isDefaultContext() {
        return GL.getCapabilities().OpenGL32;
    }

    public void init() {
        renderer.init();
    }

    public void loop(float windowWidth, float windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        renderer.getFont().drawText(renderer, text, windowWidth / 2, windowHeight / 2 - cursorPos.getY() * renderer.getFont().getFontHeight());
        drawCursor();
    }

    private void drawCursor() {
        var t = (timer.getTime() - timer.getLastLoopTime()) * 1000;

        if (t > CURSOR_BLINK_THRESHOLD)
            renderer.getFont().drawText(
                    renderer,
                    "|",
                    cursorPos.getX() * FONT_SIZE / 2 - (float) FONT_SIZE / 4 + windowWidth / 2,
                    windowHeight / 2,
                    new Vec4((float) 0x4C / 256, (float) 0xAF / 256, (float) 0x50 / 256, 1)
            );

        if (t > CURSOR_BLINK_PERIOD)
            timer.getDelta();

    }

    public StringBuilder getText() {
        return text;
    }

    public Vec2 getCursorPos() {
        return cursorPos;
    }
}
