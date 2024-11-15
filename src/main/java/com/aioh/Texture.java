package com.aioh;

import com.jogamp.opengl.GL4;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.aioh.Main.gl;
import static com.aioh.Main.glCall;

public class Texture {
    private int[] rendererId;
    private String filePath;
    private byte[] localBuffer;
    private int width, height, BPP, slot = 0;

    public Texture(String filePath) {
        this.rendererId = new int[1];
        glCall(() -> gl.glGenTextures(1, this.rendererId, 0));
        this.filePath = filePath;

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

        glCall(() -> gl.glBindTexture(GL4.GL_TEXTURE_2D, this.rendererId[0]));
        glCall(() -> gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR));
        glCall(() -> gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR));
        glCall(() -> gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE));
        glCall(() -> gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE));

        glCall(() -> gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA8, width, height, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, ByteBuffer.wrap(rgba)));
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
        glCall(() -> gl.glActiveTexture(GL4.GL_TEXTURE0 + slot));
        glCall(() -> gl.glBindTexture(GL4.GL_TEXTURE_2D, this.rendererId[0]));
    }

    public void unbind() {
        glCall(() -> gl.glBindTexture(GL4.GL_TEXTURE_2D, 0));
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
