package com.aioh;

import com.jogamp.opengl.GL4;

import static com.aioh.Main.gl;
import static com.aioh.Main.glCall;

public class AiohRenderer {
    public void clear() {
        glCall(() -> gl.glClear(GL4.GL_COLOR_BUFFER_BIT));
    }

    public void draw(VertexArray va, IndexBuffer ib, Shader shader) {
        shader.bind();
        va.bind();
        ib.bind();

        glCall(() -> gl.glDrawElements(GL4.GL_TRIANGLES, 6, GL4.GL_UNSIGNED_INT, 0));

        va.unbind();
        ib.unbind();
        shader.unbind();
    }
}
