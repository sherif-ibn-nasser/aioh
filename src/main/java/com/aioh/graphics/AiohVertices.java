package com.aioh.graphics;

import glm_.vec2.Vec2;
import glm_.vec4.Vec4;

public class AiohVertices {
    public static final int VERTICES_CAP = 640 * 1024;
    private float[] buffer = new float[VERTICES_CAP];
    private int bufferSize = 0;
    private int count = 0;

    public AiohVertexIndex add(AiohVertex vertex) {
        vertex.writeToBuffer(buffer, bufferSize);
        bufferSize += AiohVertex.BYTES;
        var index = count;
        count += 1;
        return new AiohVertexIndex(index);
    }

    public void addTriangle(AiohVertex v0, AiohVertex v1, AiohVertex v2) {
        add(v0);
        add(v1);
        add(v2);
    }

    // 2-3
    // |\|
    // 0-1
    public void addQuad(AiohVertex v0, AiohVertex v1, AiohVertex v2, AiohVertex v3) {
        addTriangle(v0, v1, v2);
        addTriangle(v1, v2, v3);
    }

    public void addImageRect(Vec2 p, Vec2 s, Vec2 uvp, Vec2 uvs, Vec4 c) {
        addQuad(
                new AiohVertex(p, c, uvp),
                new AiohVertex(p.plus(new Vec2(s.getX(), 0)), c, uvp.plus(new Vec2(uvs.getX(), 0))),
                new AiohVertex(p.plus(new Vec2(0, s.getY())), c, uvp.plus(new Vec2(0, uvs.getY()))),
                new AiohVertex(p.plus(s), c, uvp.plus(uvs))
        );
    }

    public void addSolidRect(Vec2 p, Vec2 s, Vec4 c) {
        var uv = new Vec2(0);
        addQuad(
                new AiohVertex(p, c, uv),
                new AiohVertex(p.plus(new Vec2(s.getX(), 0)), c, uv),
                new AiohVertex(p.plus(new Vec2(0, s.getY())), c, uv),
                new AiohVertex(p.plus(s), c, uv)
        );
    }

    public AiohVertex get(AiohVertexIndex index) {
        return AiohVertex.readFromBuffer(buffer, index.index());
    }

    public void clear() {
        bufferSize = count = 0;
    }

    public float[] getBuffer() {
        return buffer;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public int getCount() {
        return count;
    }
}
