package com.aioh;

import com.aioh.graphics.AiohRenderer;
import com.aioh.graphics.AiohShader;
import com.aioh.text.AiohFreeGlyphAtlas;
import glm_.vec2.Vec2;
import glm_.vec4.Vec4;
import org.lwjgl.glfw.GLFW;

public class AiohEditor {
    private AiohFreeGlyphAtlas atlas;
    private AiohRenderer renderer;
    private StringBuilder text = new StringBuilder(1024);

    public void init() {
        var face = AiohUtils.initFace();
        atlas = new AiohFreeGlyphAtlas(face);
        renderer = new AiohRenderer();
    }

    public void render(float w, float h) {
        this.renderer.getResolution().setX(w);
        this.renderer.getResolution().setY(h);
        this.renderer.setTime((float) GLFW.glfwGetTime());
        var max_line_len = renderText();
        updateCamera(w, max_line_len);
    }

    private float renderText() {
        float max_line_len = 0.0f;

        renderer.setCurrentShader(AiohShader.TEXT);

        var pos = new Vec2(0, 0);
        var color = new Vec4(1);

        for (int i = 0; i < text.length(); i++) {
            var glyph_index = text.charAt(i);

            // TODO: support for glyphs outside of ASCII range
            if (glyph_index >= AiohFreeGlyphAtlas.GLYPH_METRICS_CAPACITY) {
                glyph_index = '?';
            }

            renderer.renderLineOfText(atlas, String.valueOf(glyph_index), pos, color);

            var metric = atlas.getMetrics()[glyph_index];
            pos.setX(pos.getX() + metric.ax);

            if (max_line_len < pos.getX()) max_line_len = pos.getX();

        }
        renderer.flush();

        return max_line_len;
    }

    private void updateCamera(float w, float max_line_len) {

        Vec2 cursor_pos = new Vec2();
        if (max_line_len > 1000.0f) {
            max_line_len = 1000.0f;
        }

        float target_scale = w / 3 / (max_line_len * 0.75f); // TODO: division by 0

        Vec2 target = cursor_pos;
        float offset = 0.0f;

        if (target_scale > 3.0f) {
            target_scale = 3.0f;
        } else {
            offset = cursor_pos.getX() - w / 3 / renderer.getCameraScale();
            if (offset < 0.0f) offset = 0.0f;
            target = new Vec2(w / 3 / renderer.getCameraScale() + offset, cursor_pos.getY());
        }

        renderer.setCameraVel(
                target.minus(renderer.getCameraPos()).times(new Vec2(2))
        );

        renderer.setCameraScaleVel(
                (target_scale - renderer.getCameraScale()) * 2
        );

        renderer.setCameraPos(renderer.getCameraPos().plus(
                renderer.getCameraVel().times(new Vec2(AiohRenderer.DELTA_TIME))
        ));

        renderer.setCameraScale(renderer.getCameraScale() + renderer.getCameraScaleVel() * AiohRenderer.DELTA_TIME);

    }

    public StringBuilder getText() {
        return text;
    }

}
