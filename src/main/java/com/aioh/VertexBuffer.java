package com.aioh;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

import java.nio.Buffer;

import static com.aioh.Main.gl;
import static com.aioh.Main.glCall;

public class VertexBuffer {
    private int[] rendererId;

    public VertexBuffer(Buffer data, long size) {
        this.rendererId = new int[1];
        glCall(() -> gl.glGenBuffers(1, rendererId, 0));
        glCall(() -> gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, rendererId[0]));
        glCall(() -> gl.glBufferData(GL.GL_ARRAY_BUFFER, size, data, GL4.GL_STATIC_DRAW));
    }

    public void bind() {
        glCall(() -> gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, rendererId[0]));
    }

    public void unbind() {
        glCall(() -> gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0));
    }
}