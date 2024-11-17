package com.aioh;

import static org.lwjgl.opengl.GL46.*;

public record VertexBufferElement(VertexBufferElementType type, int count, boolean normalized) {

    public int getGlType() {
        return switch (type) {
            case FLOAT -> GL_FLOAT;
            case UNSIGNED_INT -> GL_UNSIGNED_INT;
            case UNSIGNED_BYTE -> GL_UNSIGNED_BYTE;
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
