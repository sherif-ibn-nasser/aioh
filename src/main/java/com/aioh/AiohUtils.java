package com.aioh;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AiohUtils {

    public static final String RESOURCES_PATH = "src/main/resources";
    public static final String SHADERS_PATH = RESOURCES_PATH + "/shaders";
    public static final String FONTS_PATH = RESOURCES_PATH + "/fonts";

    public static String readFile(String filePath) {
        var content = new StringBuilder();
        try {
            var scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                content.append(line + "\n");
            }
            return content.toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
