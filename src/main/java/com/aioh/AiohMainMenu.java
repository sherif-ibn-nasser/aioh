package com.aioh;

import java.util.ArrayList;

public class AiohMainMenu extends AiohButtonsSelector {

    public static final StringBuilder OPEN_FILE_BUTTON = new StringBuilder("Open file");
    public static final StringBuilder OPEN_RECENT_BUTTON = new StringBuilder("Open recent file");
    public static final StringBuilder DATABASE_MODE_BUTTON = new StringBuilder("Database mode");
    public static final StringBuilder EXIT_BUTTON = new StringBuilder("Exit");

    public static final ArrayList<StringBuilder> MAIN_MENU_ACTIONS_BUTTONS = new ArrayList<>(2);

    static {
        MAIN_MENU_ACTIONS_BUTTONS.add(OPEN_FILE_BUTTON);
        MAIN_MENU_ACTIONS_BUTTONS.add(OPEN_RECENT_BUTTON);
        MAIN_MENU_ACTIONS_BUTTONS.add(DATABASE_MODE_BUTTON);
        MAIN_MENU_ACTIONS_BUTTONS.add(EXIT_BUTTON);
    }

    @Override
    public void init() {
        super.init();
        setLines(MAIN_MENU_ACTIONS_BUTTONS);
        title = """
                Select an action (Enter). Use up and down arrows to switch
                Cancel (Esc)""";
        titlePosY = 2 * renderer.getDebugFont().getFontHeight();
    }
}
