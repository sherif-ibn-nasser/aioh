package com.aioh.text;

import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.aioh.graphics.AiohWindow.glCall;
import static org.lwjgl.opengl.GL46.*;

public class AiohFreeGlyphAtlas {
    public static final int GLYPH_METRICS_CAPACITY = 128;
    public static final int FREE_GLYPH_FONT_SIZE = 64;
    private int atlasWidth = 0, atlasHeight = 0, glyphsTexture;
    private AiohGlyphMetric[] metrics = new AiohGlyphMetric[GLYPH_METRICS_CAPACITY];

    public int getGlyphsTexture() {
        return glyphsTexture;
    }

    public void setGlyphsTexture(int glyphsTexture) {
        this.glyphsTexture = glyphsTexture;
    }

    public int getAtlasHeight() {
        return atlasHeight;
    }

    public void setAtlasHeight(int atlasHeight) {
        this.atlasHeight = atlasHeight;
    }

    public int getAtlasWidth() {
        return atlasWidth;
    }

    public void setAtlasWidth(int atlasWidth) {
        this.atlasWidth = atlasWidth;
    }

    public AiohGlyphMetric[] getMetrics() {
        return metrics;
    }

    public AiohFreeGlyphAtlas(FT_Face face) {

        // TODO: Introduction of SDF font slowed down the start up time
        // We need to investigate what's up with that

        var loadFlags = FreeType.FT_LOAD_RENDER | FreeType.FT_LOAD_TARGET_MODE(FreeType.FT_RENDER_MODE_SDF);

        for (int charCode = 32; charCode < 128; ++charCode) {

            var err = FreeType.FT_Load_Char(face, charCode, loadFlags);

            if (err != FreeType.FT_Err_Ok)
                throw new RuntimeException("[Freetype error 0x" + Long.toHexString(err) + "]: Cannot load char code" + charCode);

            var bitmap = face.glyph().bitmap();
            this.atlasWidth += bitmap.width();
            if (this.atlasHeight < bitmap.rows())
                this.atlasHeight = bitmap.rows();

        }

        glCall(() -> glActiveTexture(GL_TEXTURE0));
        glCall(() -> glyphsTexture = glGenTextures());
        glCall(() -> glBindTexture(GL_TEXTURE_2D, glyphsTexture));
        glCall(() -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR));
        glCall(() -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR));
        glCall(() -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE));
        glCall(() -> glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE));
        glCall(() -> glPixelStorei(GL_UNPACK_ALIGNMENT, 1));
        glCall(() -> glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RED,
                atlasWidth,
                atlasHeight,
                0,
                GL_RED,
                GL_UNSIGNED_BYTE,
                (ByteBuffer) null));

        int x = 0;

        Arrays.fill(this.metrics, new AiohGlyphMetric());

        for (int i = 32; i < 128; ++i) {

            var err = FreeType.FT_Load_Char(face, i, loadFlags);

            if (err != FreeType.FT_Err_Ok)
                throw new RuntimeException("[Freetype error 0x" + Long.toHexString(err) + "]: Cannot load char code" + i);

            err = FreeType.FT_Render_Glyph(face.glyph(), FreeType.FT_RENDER_MODE_NORMAL);

            if (err != FreeType.FT_Err_Ok)
                throw new RuntimeException("[Freetype error 0x" + Long.toHexString(err) + "]: Couldn't render glyph with char code" + i);

            metrics[i].ax = face.glyph().advance().x() >> 6;
            metrics[i].ay = face.glyph().advance().y() >> 6;
            metrics[i].bw = face.glyph().bitmap().width();
            metrics[i].bh = face.glyph().bitmap().rows();
            metrics[i].bl = face.glyph().bitmap_left();
            metrics[i].bt = face.glyph().bitmap_top();
            metrics[i].tx = (float) x / (float) atlasWidth;

            glCall(() -> glPixelStorei(GL_UNPACK_ALIGNMENT, 1));

            var bitmap = face.glyph().bitmap();

            int finalX = x;
            var buffer = bitmap.buffer(
                    bitmap.rows() * bitmap.pitch()
            );

            if (i == 32) {
                x += bitmap.width();
                continue; // Skip the space char
            }
            glCall(() -> glTexSubImage2D(
                    GL_TEXTURE_2D,
                    0,
                    finalX,
                    0,
                    bitmap.width(),
                    bitmap.rows(),
                    GL_RED,
                    GL_UNSIGNED_BYTE,
                    buffer
            ));
            x += bitmap.width();
        }
    }

}
