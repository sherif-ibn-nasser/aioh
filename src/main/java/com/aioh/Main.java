package com.aioh;

import java.io.FileInputStream;

import static java.awt.Font.PLAIN;
import static java.awt.Font.TRUETYPE_FONT;
import static org.lwjgl.opengl.GL46.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL46.glClear;

public class Main {

    public static java.awt.Font font;

    public static String vertexShaderSource, defaultFragmentShaderSource, colorFragmentShaderSource;

    public static void main(String[] args) throws Exception {

        var fontFile = new FileInputStream(AiohUtils.FONTS_PATH + "/iosevka-regular.ttf");
        // FIXME: This line is too slow
        font = java.awt.Font.createFont(TRUETYPE_FONT, fontFile).deriveFont(PLAIN, AiohEditor.FONT_SIZE);
        vertexShaderSource = AiohUtils.readFile(AiohUtils.SHADERS_PATH + "/default.vert");
        defaultFragmentShaderSource = AiohUtils.readFile(AiohUtils.SHADERS_PATH + "/default.frag");
        colorFragmentShaderSource = AiohUtils.readFile(AiohUtils.SHADERS_PATH + "/color.frag");
        var editor = new AiohDatabaseEditor();

        var window = new AiohWindow("Aioh", 1280, 720, editor);

        if (args.length == 0)
            editor.init();
        else
            editor.init(args[0]);

        while (!window.shouldClose()) {
            glClear(GL_COLOR_BUFFER_BIT);
            editor.loop();
            window.update();
        }

        window.destroy();

    }
}
