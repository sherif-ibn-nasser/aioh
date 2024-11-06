package com.aioh;

import com.jogamp.opengl.GL4;

public record VertexBufferElement(VertexBufferElementType type, int count, boolean normalized) {

    public int getGlType() {
        return switch (type) {
            case FLOAT -> GL4.GL_FLOAT;
            case UNSIGNED_INT -> GL4.GL_UNSIGNED_INT;
            case UNSIGNED_BYTE -> GL4.GL_UNSIGNED_BYTE;
        };
    }

    public int getTypeSize() {
        return switch (type) {
            case FLOAT -> Float.BYTES;
            case UNSIGNED_INT -> Integer.BYTES;
            case UNSIGNED_BYTE -> Byte.BYTES;
        };
    }
}
