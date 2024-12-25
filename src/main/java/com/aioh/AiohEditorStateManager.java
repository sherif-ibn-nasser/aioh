package com.aioh;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;

public class AiohEditorStateManager implements AiohWindow.EventsHandler {

    public static AiohEditorState state = AiohEditorState.TEXT_EDITING;
    public static AiohEditorState prevState;
    public static final AiohEditor textEditor = new AiohEditor();
    public static final AiohDatabaseEditor databaseEditor = new AiohDatabaseEditor();
    public static final AiohMainMenu mainMenu = new AiohMainMenu();
    public static final AiohFileBrowser fileBrowser = new AiohFileBrowser();

    public void init() {
        switch (state) {
            case TEXT_EDITING -> textEditor.init();
            case MAIN_MENU -> mainMenu.init();
            case FILE_BROWSING -> fileBrowser.init();
            case DATABASE -> databaseEditor.init();
        }
    }

    public void loop() {
        switch (state) {
            case TEXT_EDITING -> textEditor.loop();
            case MAIN_MENU -> mainMenu.loop();
            case FILE_BROWSING -> fileBrowser.loop();
            case DATABASE -> databaseEditor.loop();
        }
    }


    @Override
    public void onTextInput(char[] newChars) {
        switch (state) {
            case TEXT_EDITING -> textEditor.onTextInput(newChars);
            case MAIN_MENU -> mainMenu.onTextInput(newChars);
            case FILE_BROWSING -> fileBrowser.onTextInput(newChars);
            case DATABASE -> databaseEditor.onTextInput(newChars);
        }
    }

    @Override
    public void onKeyPressed(int keyCode) {

        switch (state) {
            case TEXT_EDITING -> textEditor.onKeyPressed(keyCode);
            case MAIN_MENU -> {
                if (keyCode == GLFW_KEY_ESCAPE)
                    state = prevState;
                else if (keyCode == GLFW_KEY_ENTER)
                    onActionSelectedFromMainMenu();
                else
                    mainMenu.onKeyPressed(keyCode);
            }
            case FILE_BROWSING -> {
                if (keyCode == GLFW_KEY_ESCAPE)
                    state = prevState;
                else if (keyCode == GLFW_KEY_ENTER) {
                    var selected = fileBrowser.getSelected();
                    if (selected == AiohFileBrowser.UP_DIR)
                        fileBrowser.goUp();
                    else if (selected.charAt(selected.length() - 1) == '/')
                        fileBrowser.enterSelected();
                    else {
                        fileBrowser.updateLastPath();
                        textEditor.init(new File(fileBrowser.getLastPath(), selected.toString()));
                        state = AiohEditorState.TEXT_EDITING;
                    }
                } else
                    fileBrowser.onKeyPressed(keyCode);
            }
            case DATABASE -> databaseEditor.onKeyPressed(keyCode);
        }
    }

    @Override
    public void onModKeysPressed(int mods, int keyCode) {
        if (state == AiohEditorState.MAIN_MENU) {
            mainMenu.onModKeysPressed(mods, keyCode);
            return;
        } else if ((mods & GLFW_MOD_CONTROL) != 0 && keyCode == GLFW_KEY_M) {
            mainMenu.cursorLine = 0;
            prevState = state;
            state = AiohEditorState.MAIN_MENU;
            return;
        }

        switch (state) {
            case TEXT_EDITING -> textEditor.onModKeysPressed(mods, keyCode);
            case FILE_BROWSING -> fileBrowser.onModKeysPressed(mods, keyCode);
            case DATABASE -> databaseEditor.onModKeysPressed(mods, keyCode);
        }
    }

    private void onActionSelectedFromMainMenu() {
        if (mainMenu.getSelected() == AiohMainMenu.OPEN_FILE_BUTTON) {
            fileBrowser.displayAllInLastPath();
            // Main menu is not preserved here
            if (state != AiohEditorState.MAIN_MENU)
                prevState = state;
            state = AiohEditorState.FILE_BROWSING;
        } else if (mainMenu.getSelected() == AiohMainMenu.OPEN_RECENT_BUTTON) {
            // TODO
        } else if (mainMenu.getSelected() == AiohMainMenu.DATABASE_MODE_BUTTON) {
            state = AiohEditorState.DATABASE;
        } else if (mainMenu.getSelected() == AiohMainMenu.EXIT_BUTTON) {
            glfwSetWindowShouldClose(AiohWindow.windowId, true);
        }
    }
}
