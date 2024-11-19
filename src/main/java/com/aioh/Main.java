package com.aioh;

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

                var pos = editor.getCursorPos();

                if (newChars[0] == '\n') {
                    pos.setX(0);
                    pos.setY(pos.getY() - 1);
                } else
                    pos.setX(pos.getX() + 1);
            }

            @Override
            public void onBackspacePressed() {
                var text = editor.getText();

                if (text.isEmpty())
                    return;

                var lastIdx = text.length() - 1;
                var lastChar = text.charAt(lastIdx);
                text.deleteCharAt(lastIdx);

                var pos = editor.getCursorPos();
                if (lastChar == '\n') {
                    var lineLen = 0;
                    for (int i = text.length() - 1; i >= 0; i--) {
                        if (text.charAt(i) == '\n')
                            break;
                        lineLen += 1;
                    }
                    pos.setX(lineLen);
                    pos.setY(pos.getY() + 1);
                } else
                    pos.setX(pos.getX() - 1);

                if (text.isEmpty())
                    return;

                lastIdx = text.length() - 1;
                if (!Character.isDefined(text.charAt(lastIdx)))
                    text.deleteCharAt(lastIdx);

                System.out.println("Text buffer: " + text);
            }

            @Override
            public void onUpArrowPressed() {
//                var pos = editor.getCursorPos();
//                var newY = pos.getY() + 1;
//                if (newY >= 0)
//                    pos.setY(newY);
            }

            @Override
            public void onDownArrowPressed() {
//                var pos = editor.getCursorPos();
//                var newY = pos.getY() - 1;
//                if (-newY < editor.getText().toString().lines().count())
//                    pos.setY(newY);
            }

            @Override
            public void onLeftArrowPressed() {
//                var pos = editor.getCursorPos();
//                var newX = pos.getX() - 1;
//                if (newX >= 0)
//                    pos.setX(newX);
            }

            @Override
            public void onRightArrowPressed() {
//                var pos = editor.getCursorPos();
//                var newX = pos.getX() + 1;
//                if (newX >= 0)
//                    pos.setX(newX);
            }


        };

        var window = new AiohWindow("Aioh", 960, 540, handler);

        editor.init();

        while (!window.shouldClose()) {
            glClear(GL_COLOR_BUFFER_BIT);
            editor.loop(window.getWidth(), window.getHeight());
            window.update();
        }

        window.destroy();

    }
}
