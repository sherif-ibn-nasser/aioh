package com.aioh;

import org.lwjgl.BufferUtils;

import static com.aioh.AiohWindow.glCall;
import static org.lwjgl.opengl.GL46.*;


public class VertexBuffer {
    private int rendererId;

    public VertexBuffer(float[] data) {
        glCall(() -> rendererId = glGenBuffers());
        glCall(() -> glBindBuffer(GL_ARRAY_BUFFER, rendererId));
        var buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        glCall(() -> glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW));
    }

    public void bind() {
        glCall(() -> glBindBuffer(GL_ARRAY_BUFFER, rendererId));
    }

    public void unbind() {
        glCall(() -> glBindBuffer(GL_ARRAY_BUFFER, 0));
    }
}