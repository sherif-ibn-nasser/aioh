package com.aioh.graphics;

public enum AiohVertexAttr {
    POSITION(0, 2),
    COLOR(2 * Float.BYTES, 4),
    UV(6 * Float.BYTES, 2);

    int offset, size;

    AiohVertexAttr(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }
}