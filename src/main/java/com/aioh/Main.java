package com.aioh;

public class Main {
    public static final int FREE_GLYPH_FONT_SIZE = 64;

    public static void main(String[] args) throws Exception {

        var window = new AiohWindow("Aioh", 960, 540);
        window.init();

        var listener = new AiohEventListener();
        listener.init();

        while (!window.shouldClose()) {
            window.update();
            listener.display();
        }

        window.destroy();

//        var library = PointerBuffer.allocateDirect(8);
//
//        FreeType.FT_Init_FreeType(library);
////
////        if (library == null)
////            throw new Exception("Error initializing FreeType.");
////
////        var fontPath = "./src/resources/fonts/Roboto-Regular.ttf";
////        FT_Face face = FT_Face.create();
////        if (face == null)
////            throw new Exception("Error creating face from file '" + fontPath + "'.");
////
////        if (face.setPixelSizes(0, FREE_GLYPH_FONT_SIZE))
////            throw new Exception("Error changing size.");
////        face.loadChar('a', 0);
////
////        face.getGlyphSlot().getBitmap().getRows();
//
//        GLProfile.initSingleton();
//        final GLProfile profile = GLProfile.get(GLProfile.GL4);
//        final GLCapabilities capabilities = new GLCapabilities(profile);
//
//        win = GLWindow.create(capabilities);
//        win.setSize((int) MAX_WIDTH, (int) MAX_HEIGHT);
//        win.setResizable(true);
//        win.setTitle("Aioh");
//
//        win.addGLEventListener(new AiohEventListener());
//
//        FPSAnimator animator = new FPSAnimator(win, 120);
//        animator.start();
//        win.setVisible(true);
    }
}
