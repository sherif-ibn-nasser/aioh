package com.aioh;

import java.io.File;

public class AiohFileBrowser extends AiohButtonsSelector {
    private File lastPath = new File(System.getProperty("user.dir"));
    private File currentPath = lastPath;
    public static final StringBuilder UP_DIR = new StringBuilder("..");


    @Override
    public void init() {
        super.init();
        lines.add(UP_DIR);
    }

    private void displayAllInPath(File path) {

        this.lines.clear();
        this.lines.add(UP_DIR);

        for (final File fileEntry : path.listFiles()) {
            var name = fileEntry.getName();

            if (fileEntry.isDirectory()) {
                lines.add(new StringBuilder(name).append('/'));
            } else {
                lines.add(new StringBuilder(name));
            }
        }

        setLines(this.lines);
    }

    public void displayAllInLastPath() {
        displayAllInPath(lastPath);
    }

    public void goUp() {
        currentPath = currentPath.getParentFile();
        displayAllInPath(currentPath);
    }

    public void enterSelected() {
        currentPath = new File(currentPath, getSelected().toString());
        displayAllInPath(currentPath);
    }

    public void updateLastPath() {
        lastPath = currentPath;
    }

    public File getLastPath() {
        return lastPath;
    }
}
