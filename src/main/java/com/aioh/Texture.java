package com.aioh;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.aioh.graphics.AiohWindow.glCall;
import static org.lwjgl.opengl.GL46.*;

public class Texture {
    private int rendererId;
    private int width, height, BPP, slot = 0;

    public Texture(String filePath) {
        glCall(() -> rendererId = glGenTextures());

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
        this.BPP = bufferedImage.getColorModel().getPixelSize();
        int[] argb = new int[width * height];
        for (int y = 0; y < height; y++) {
            bufferedImage.getRGB(0, height - 1 - y, width, 1, argb, y * width, width);
        }
        byte[] rgba = intARGBtoByteRGBA(argb);

        glCall(() -> glBindTexture(GL_TEXTURE_2D, rendererId));
        glCall(() -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR));
        glCall(() -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR));
        glCall(() -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE));
        glCall(() -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE));
        var buffer = BufferUtils.createByteBuffer(rgba.length);
        buffer.put(rgba);
        buffer.flip();
        glCall(() -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer));
        unbind();

        // We can drop localBuffer
    }

    private static byte[] intARGBtoByteRGBA(int[] argb) {
        byte[] rgba = new byte[argb.length * 4];

        for (int i = 0; i < argb.length; i++) {
            rgba[4 * i] = (byte) ((argb[i] >> 16) & 0xff); // R
            rgba[4 * i + 1] = (byte) ((argb[i] >> 8) & 0xff); // G
            rgba[4 * i + 2] = (byte) ((argb[i]) & 0xff); // B
            rgba[4 * i + 3] = (byte) ((argb[i] >> 24) & 0xff); // A
        }

        return rgba;
    }

    public void bind() {
        this.bind(0);
    }

    public void bind(int slot) {
        this.slot = slot;
        glCall(() -> glActiveTexture(GL_TEXTURE0 + slot));
        glCall(() -> glBindTexture(GL_TEXTURE_2D, rendererId));
    }

    public void unbind() {
        glCall(() -> glBindTexture(GL_TEXTURE_2D, 0));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBPP() {
        return BPP;
    }

    public int getSlot() {
        return slot;
    }
}
