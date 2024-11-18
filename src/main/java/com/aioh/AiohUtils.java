package com.aioh;

import com.aioh.text.AiohFreeGlyphAtlas;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AiohUtils {
    public static FT_Face initFace() {

        var libraryPtr = PointerBuffer.allocateDirect(1);
        var err = FreeType.FT_Init_FreeType(libraryPtr);

        if (err != FreeType.FT_Err_Ok)
            throw new RuntimeException("[Freetype error 0x" + Long.toHexString(err) + "]: Cannot initialize FT_Library");

        var library = libraryPtr.get();

        var fontPath = "/home/sherif/IdeaProjects/aioh/src/main/resources/fonts/iosevka-regular.ttf";
        var facePtr = PointerBuffer.allocateDirect(1);
        err = FreeType.FT_New_Face(library, fontPath, 0, facePtr);

        if (err != FreeType.FT_Err_Ok)
            throw new RuntimeException("[Freetype error 0x" + Long.toHexString(err) + "]: Cannot read font face `" + fontPath + "`");

        var face = FT_Face.createSafe(facePtr.get());

        err = FreeType.FT_Set_Pixel_Sizes(face, 0, AiohFreeGlyphAtlas.FREE_GLYPH_FONT_SIZE);

        if (err != FreeType.FT_Err_Ok)
            throw new RuntimeException("[Freetype error 0x" + Long.toHexString(err) + "]: Cannot set pixel size for font face `" + fontPath + "`");

        return face;
    }

    public static String readFile(String filePath) {
        var content = new StringBuilder();
        try {
            var scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                content.append(line + "\n");
            }
            return content.toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
