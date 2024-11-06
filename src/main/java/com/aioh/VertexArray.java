package com.aioh;

import static com.aioh.Main.gl;
import static com.aioh.Main.glCall;

public class VertexArray {

    private int[] rendererId;

    public VertexArray() {
        this.rendererId = new int[1];
        glCall(() -> gl.glGenVertexArrays(1, rendererId, 0));
        glCall(() -> gl.glBindVertexArray(rendererId[0]));
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
            glCall(() -> gl.glEnableVertexAttribArray(finalI));
            glCall(() -> gl.glVertexAttribPointer(finalI, element.count(), element.getGlType(), element.normalized(), vbl.getStride(), finalOffset));
            offset += element.count() * element.getTypeSize();
        }
    }

    public void bind() {
        glCall(() -> gl.glBindVertexArray(rendererId[0]));
    }

    public void unbind() {
        glCall(() -> gl.glBindVertexArray(0));
    }
}
