package com.aioh;

import static com.aioh.graphics.AiohWindow.glCall;
import static org.lwjgl.opengl.GL46.*;

public class AiohRenderer2 {
    public void clear() {
        glCall(() -> glClear(GL_COLOR_BUFFER_BIT));
    }

    public void draw(VertexArray va, IndexBuffer ib, Shader shader) {
        shader.bind();
        va.bind();
        ib.bind();

        glCall(() -> glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0));

        va.unbind();
        ib.unbind();
        shader.unbind();
    }
}
