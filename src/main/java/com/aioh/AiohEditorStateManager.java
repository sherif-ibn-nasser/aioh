package com.aioh;

import static org.lwjgl.glfw.GLFW.*;

public class AiohEditorStateManager implements AiohWindow.EventsHandler {

    public static boolean isMainMenuDisplayed = false;
    public static AiohEditorState state = AiohEditorState.TEXT_EDITING;
    public static final AiohEditor textEditor = new AiohEditor();
    public static final AiohDatabaseEditor databaseEditor = new AiohDatabaseEditor();
    public static final AiohMainMenu mainMenu = new AiohMainMenu();

    public void init() {
        switch (state) {
            case TEXT_EDITING -> textEditor.init();
            case DATABASE -> databaseEditor.init();
        }
    }

    public void loop() {

        if (isMainMenuDisplayed) {
            mainMenu.loop();
            return;
        }

        switch (state) {
            case TEXT_EDITING -> textEditor.loop();
            case DATABASE -> databaseEditor.loop();
        }
    }


    @Override
    public void onTextInput(char[] newChars) {

        if (isMainMenuDisplayed) {
            mainMenu.onTextInput(newChars);
            return;
        }

        switch (state) {
            case TEXT_EDITING -> textEditor.onTextInput(newChars);
            case DATABASE -> databaseEditor.onTextInput(newChars);
        }
    }

    @Override
    public void onKeyPressed(int keyCode) {

        if (isMainMenuDisplayed) {
            if (keyCode == GLFW_KEY_ESCAPE)
                isMainMenuDisplayed = false;
            else if (keyCode == GLFW_KEY_ENTER) {
                onActionSelectedFromMainMenu();
                isMainMenuDisplayed = false;
            } else
                mainMenu.onKeyPressed(keyCode);
            return;
        }

        switch (state) {
            case TEXT_EDITING -> textEditor.onKeyPressed(keyCode);
            case DATABASE -> databaseEditor.onKeyPressed(keyCode);
        }
    }

    @Override
    public void onModKeysPressed(int mods, int keyCode) {
        if (isMainMenuDisplayed) {
            mainMenu.onModKeysPressed(mods, keyCode);
            return;
        } else if ((mods & GLFW_MOD_CONTROL) != 0 && keyCode == GLFW_KEY_M) {
            mainMenu.cursorLine = 0;
            isMainMenuDisplayed = true;
            return;
        }

        switch (state) {
            case TEXT_EDITING -> textEditor.onModKeysPressed(mods, keyCode);
            case DATABASE -> databaseEditor.onModKeysPressed(mods, keyCode);
        }
    }

    private void onActionSelectedFromMainMenu() {
        if (mainMenu.getSelected() == AiohMainMenu.OPEN_FILE_BUTTON) {
            // TODO
        } else if (mainMenu.getSelected() == AiohMainMenu.OPEN_DATABASE_BUTTON) {
            // TODO
        } else if (mainMenu.getSelected() == AiohMainMenu.OPEN_DATABASE_BUTTON) {
            state = AiohEditorState.DATABASE;
        } else if (mainMenu.getSelected() == AiohMainMenu.EXIT_BUTTON) {
            glfwSetWindowShouldClose(AiohWindow.windowId, true);
        }
    }
}
