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

        if (path.getParent() != null)
            this.lines.add(UP_DIR);

        for (final File fileEntry : path.listFiles()) {
            var name = new StringBuilder(fileEntry.getName());

            if (fileEntry.isDirectory())
                name.append('/');

            lines.add(name);
        }

        setLines(this.lines);
    }

    public void displayAllInLastPath() {
        displayAllInPath(lastPath);
    }

    public void goUp() {
        var up = currentPath.getParentFile();
        if (up == null)
            return;
        currentPath = up;
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
