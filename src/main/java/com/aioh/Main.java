package com.aioh;

import com.aioh.graphics.AiohWindow;

import static org.lwjgl.opengl.GL46.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL46.glClear;

public class Main {

    public static void main(String[] args) throws Exception {

        var editor = new AiohEditor();

        var handler = new AiohWindow.EventsHandler() {
            @Override
            public void onTextInput(char[] newChars) {
                var text = editor.getText();
                text.append(newChars);
                System.out.println("Text buffer: " + text);
            }

            @Override
            public void onBackspacePressed() {
                var text = editor.getText();
                if (text.isEmpty())
                    return;

                text.deleteCharAt(text.length() - 1);

                if (text.isEmpty())
                    return;

                var lastIdx = text.length() - 1;
                if (!Character.isDefined(text.charAt(lastIdx)))
                    text.deleteCharAt(lastIdx);

                System.out.println("Text buffer: " + text);
            }
        };

        var window = new AiohWindow("Aioh", 960, 540, handler);

        editor.init();

        while (!window.shouldClose()) {
            window.update();
            glClear(GL_COLOR_BUFFER_BIT);
            editor.render(window.getWidth(), window.getHeight());
        }

        window.destroy();

    }
}
