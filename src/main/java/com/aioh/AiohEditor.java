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
        renderText();
    }


    private void renderText() {

        renderer.setCurrentShader(AiohShader.TEXT);
        var pos = new Vec2();
        var color = new Vec4(1, 0, 0, 0);

        for (int i = 0; i < text.length(); i++) {
            var glyph_index = text.charAt(i);

            // TODO: support for glyphs outside of ASCII range
            if (glyph_index >= AiohFreeGlyphAtlas.GLYPH_METRICS_CAPACITY) {
                glyph_index = '?';
            }

            renderer.renderLineOfText(atlas, String.valueOf(glyph_index), pos, color);

            var metric = atlas.getMetrics()[glyph_index];
            pos.setX(pos.getX() + metric.ax);

        }
        renderer.flush();
    }

    public StringBuilder getText() {
        return text;
    }

}
