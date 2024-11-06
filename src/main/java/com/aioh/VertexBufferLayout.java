package com.aioh;

import java.util.ArrayList;

public class VertexBufferLayout {
    private int stride;
    private ArrayList<VertexBufferElement> elements;

    public VertexBufferLayout() {
        this.stride = 0;
        this.elements = new ArrayList<>();
    }

    public int getStride() {
        return stride;
    }

    public ArrayList<VertexBufferElement> getElements() {
        return elements;
    }

    private void push(VertexBufferElementType type, int count, boolean normalized) {
        var element = new VertexBufferElement(

                type, count, normalized
        );
        elements.add(element);
        stride += count * element.getTypeSize();
    }

    public void pushFloat(int count) {
        push(VertexBufferElementType.FLOAT, count, false);
    }

    public void pushUnsignedInt(int count) {
        push(VertexBufferElementType.UNSIGNED_INT, count, false);
    }

    public void pushUnsignedByte(int count) {
        push(VertexBufferElementType.UNSIGNED_BYTE, count, true);
    }
}
