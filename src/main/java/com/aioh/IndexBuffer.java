package com.aioh;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

import java.nio.Buffer;

import static com.aioh.Main.gl;
import static com.aioh.Main.glCall;

public class IndexBuffer {
    private int[] rendererId;
    private int count;

    public IndexBuffer(Buffer data, int count) {
        rendererId = new int[1];
        this.count = count;
        glCall(() -> gl.glGenBuffers(1, rendererId, 0));
        glCall(() -> gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, rendererId[0]));
        glCall(() -> gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, count * Integer.BYTES, data, GL4.GL_STATIC_DRAW));
    }

    public int getCount() {
        return count;
    }

    public void bind() {
        glCall(() -> gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, rendererId[0]));
    }

    public void unbind() {
        glCall(() -> gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0));
    }
}