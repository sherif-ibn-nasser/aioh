package com.aioh.graphics;

import glm_.vec2.Vec2;
import glm_.vec4.Vec4;

public record AiohVertex(
        Vec2 position,
        Vec4 color,
        Vec2 uv
) {
    public static final int BYTES = (2 * Vec2.LENGTH + Vec4.LENGTH) * Float.BYTES;

    public void writeToBuffer(float[] buffer, int at) {
        buffer[at] = position.get_x();
        buffer[at + 1] = position.get_y();

        buffer[at + 2] = color.get_x();
        buffer[at + 3] = color.get_y();
        buffer[at + 4] = color.get_z();
        buffer[at + 5] = color.get_w();

        buffer[at + 6] = uv.get_x();
        buffer[at + 7] = uv.get_y();
    }

    public static AiohVertex readFromBuffer(float[] buffer, int at) {
        var px = buffer[at];
        var py = buffer[at + 1];

        var cx = buffer[at + 2];
        var cy = buffer[at + 3];
        var cz = buffer[at + 4];
        var cw = buffer[at + 5];

        var uvx = buffer[at + 6];
        var uvy = buffer[at + 7];

        return new AiohVertex(
                new Vec2(px, py),
                new Vec4(cx, cy, cz, cw),
                new Vec2(uvx, uvy)
        );
    }
}