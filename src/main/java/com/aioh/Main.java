package com.aioh;

import org.lwjgl.PointerBuffer;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

public class Main {
    public static final int FREE_GLYPH_FONT_SIZE = 64;

    public static void main(String[] args) throws Exception {

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

        err = FreeType.FT_Set_Pixel_Sizes(face, 0, FREE_GLYPH_FONT_SIZE);

        if (err != FreeType.FT_Err_Ok)
            throw new RuntimeException("[Freetype error 0x" + Long.toHexString(err) + "]: Cannot set pixel size for font face `" + fontPath + "`");

        var loadFlags = FreeType.FT_LOAD_RENDER | FreeType.FT_LOAD_TARGET_MODE(FreeType.FT_RENDER_MODE_SDF);

        var charCode = 65;

        err = FreeType.nFT_Load_Char(face.address(), charCode, loadFlags);

        if (err != FreeType.FT_Err_Ok)
            throw new RuntimeException("[Freetype error 0x" + Long.toHexString(err) + "]: Cannot load char code" + charCode + " in font face `" + fontPath + "`");

//        if (face == null)
//            throw new Exception("Error creating face from file '" + fontPath + "'.");


//        if (face.setPixelSizes(0, FREE_GLYPH_FONT_SIZE))
////            throw new Exception("Error changing size.");

        var window = new AiohWindow("Aioh", 960, 540);
        window.init();


        var listener = new AiohEventListener();
        listener.init();

        while (!window.shouldClose()) {
            window.update();
            listener.display();
        }

        window.destroy();


    }
}
