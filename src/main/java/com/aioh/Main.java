package com.aioh;

import java.io.FileInputStream;

import static java.awt.Font.PLAIN;
import static java.awt.Font.TRUETYPE_FONT;
import static org.lwjgl.opengl.GL11.glClearColor;
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
        var editorStateManager = new AiohEditorStateManager();

        var window = new AiohWindow("Aioh", 1280, 720, editorStateManager);

        if (args.length == 0)
            AiohEditorStateManager.textEditor.init();
        else
            AiohEditorStateManager.textEditor.init(args[0]);

        AiohEditorStateManager.mainMenu.init();
        AiohEditorStateManager.fileBrowser.init();
        AiohEditorStateManager.fileNameEditor.init();
        AiohEditorStateManager.databaseEditor.init();

        AiohEditorStateManager.fileNameEditor.title = """
                Enter file name
                Save (Enter)
                Back (ESC)""";
        AiohEditorStateManager.fileNameEditor.titlePosY = 2 * AiohEditorStateManager.fileNameEditor.renderer.getDebugFont().getFontHeight();

        while (!window.shouldClose()) {
            glClear(GL_COLOR_BUFFER_BIT);
            glClearColor(0x2a / 256f, 0x2a / 256f, 0x2a / 256f, 1);
            editorStateManager.loop();
            window.update();
        }

        window.destroy();

    }
}
