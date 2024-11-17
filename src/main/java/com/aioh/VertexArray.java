package com.aioh;

import static com.aioh.AiohWindow.glCall;
import static org.lwjgl.opengl.GL46.*;

public class VertexArray {

    private int rendererId;

    public VertexArray() {
        glCall(() -> rendererId = glGenVertexArrays());
        glCall(() -> glBindVertexArray(rendererId));
    }

    public void addBuffer(VertexBuffer vb, VertexBufferLayout vbl) {
        this.bind();
        vb.bind();
        var elements = vbl.getElements();
        var offset = 0;
        for (int i = 0; i < elements.size(); i++) {
            var element = elements.get(i);
            int finalI = i;
            int finalOffset = offset;
            glCall(() -> glEnableVertexAttribArray(finalI));
            glCall(() -> glVertexAttribPointer(finalI, element.count(), element.getGlType(), element.normalized(), vbl.getStride(), finalOffset));
            offset += element.count() * element.getTypeSize();
        }
    }

    public void bind() {
        glCall(() -> glBindVertexArray(rendererId));
    }

    public void unbind() {
        glCall(() -> glBindVertexArray(0));
    }
}
