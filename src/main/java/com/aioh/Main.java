package com.aioh;

import static org.lwjgl.opengl.GL46.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL46.glClear;

public class Main {

    public static void main(String[] args) throws Exception {

        var editor = new AiohEditor();

        var window = new AiohWindow("Aioh", 960, 540, editor);

        editor.init();

        while (!window.shouldClose()) {
            glClear(GL_COLOR_BUFFER_BIT);
            editor.loop();
            window.update();
        }

        window.destroy();

    }
}
