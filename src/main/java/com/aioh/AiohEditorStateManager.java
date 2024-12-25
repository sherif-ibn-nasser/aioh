package com.aioh;

import java.io.File;
import java.io.FileWriter;

import static org.lwjgl.glfw.GLFW.*;

public class AiohEditorStateManager implements AiohWindow.EventsHandler {

    public static AiohEditorState state = AiohEditorState.TEXT_EDITING;
    public static AiohEditorState prevState;
    public static final AiohEditor textEditor = new AiohEditor();
    public static final AiohEditor fileNameEditor = new AiohEditor();
    public static final AiohDatabaseEditor databaseEditor = new AiohDatabaseEditor();
    public static final AiohMainMenu mainMenu = new AiohMainMenu();
    public static final AiohFileBrowser fileBrowser = new AiohFileBrowser();

    public void init() {
        switch (state) {
            case TEXT_EDITING -> textEditor.init();
            case MAIN_MENU -> mainMenu.init();
            case FILE_BROWSING, FILE_SAVE_PATH_SELECTION -> fileBrowser.init();
            case FILE_SAVE_FILE_NAME_ENTERING -> fileNameEditor.init();
            case DATABASE -> databaseEditor.init();
        }
    }

    public void loop() {
        switch (state) {
            case TEXT_EDITING -> textEditor.loop();
            case MAIN_MENU -> mainMenu.loop();
            case FILE_BROWSING, FILE_SAVE_PATH_SELECTION -> fileBrowser.loop();
            case FILE_SAVE_FILE_NAME_ENTERING -> fileNameEditor.loop();
            case DATABASE -> databaseEditor.loop();
        }
    }


    @Override
    public void onTextInput(char[] newChars) {
        switch (state) {
            case TEXT_EDITING -> textEditor.onTextInput(newChars);
            case MAIN_MENU -> mainMenu.onTextInput(newChars);
            case FILE_BROWSING, FILE_SAVE_PATH_SELECTION -> fileBrowser.onTextInput(newChars);
            case FILE_SAVE_FILE_NAME_ENTERING -> fileNameEditor.onTextInput(newChars);
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
            case FILE_SAVE_PATH_SELECTION -> {
                if (keyCode == GLFW_KEY_ESCAPE)
                    state = prevState;
                else if (keyCode == GLFW_KEY_ENTER) {
                    var selected = fileBrowser.getSelected();
                    if (selected == AiohFileBrowser.UP_DIR)
                        fileBrowser.goUp();
                    else
                        fileBrowser.enterSelected();
                } else
                    fileBrowser.onKeyPressed(keyCode);
            }
            case FILE_SAVE_FILE_NAME_ENTERING -> {
                if (keyCode == GLFW_KEY_ESCAPE)
                    state = AiohEditorState.FILE_SAVE_PATH_SELECTION;
                else if (keyCode == GLFW_KEY_ENTER) {
                    var name = fileNameEditor.lines.getFirst();
                    if (name.isEmpty()) {
                        // TODO: Show the error
                        return;
                    }
                    var path = fileBrowser.getCurrentPath() + "/" + name;
                    if (new File(path).exists()) {
                        // TODO
                    } else {
                        saveFile(path);
                        state = prevState;
                        textEditor.init(path);
                    }
                } else
                    fileNameEditor.onKeyPressed(keyCode);
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
            case TEXT_EDITING -> {
                if ((mods & GLFW_MOD_CONTROL) != 0 && keyCode == GLFW_KEY_S) {
                    var path = textEditor.getCurrentFile();
                    if (path != null && new File(path).exists())
                        saveFile(path);
                    else {
                        fileBrowser.cursorLine = 0;
                        fileBrowser.displayDirectoriesInLastPath();
                        prevState = state;
                        state = AiohEditorState.FILE_SAVE_PATH_SELECTION;
                    }
                } else
                    textEditor.onModKeysPressed(mods, keyCode);
            }
            case FILE_BROWSING -> fileBrowser.onModKeysPressed(mods, keyCode);
            case FILE_SAVE_PATH_SELECTION -> {
                if ((mods & GLFW_MOD_CONTROL) != 0 && keyCode == GLFW_KEY_S) {
                    fileNameEditor.lines.clear();
                    fileNameEditor.lines.add(new StringBuilder());
                    state = AiohEditorState.FILE_SAVE_FILE_NAME_ENTERING;
                } else
                    fileBrowser.onModKeysPressed(mods, keyCode);
            }
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
            databaseEditor.init();
            state = AiohEditorState.DATABASE;
        } else if (mainMenu.getSelected() == AiohMainMenu.EXIT_BUTTON) {
            glfwSetWindowShouldClose(AiohWindow.windowId, true);
        }
    }

    private void saveFile(String filePath) {
        var lines = textEditor.lines;

        try {
            var writer = new FileWriter(filePath);

            for (int i = 0; i < lines.size(); i++) {
                writer.append(lines.get(i));
                if (i < lines.size() - 1)
                    writer.append('\n');
            }
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
