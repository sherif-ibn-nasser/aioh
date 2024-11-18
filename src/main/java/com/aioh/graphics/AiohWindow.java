package com.aioh.graphics;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class AiohWindow {

    public interface EventsHandler {
        void onTextInput(char[] newChars);

        void onBackspacePressed();
    }

    private String title;
    private int width, height;
    private long window;
    private boolean resize;

    public AiohWindow(String title, int width, int height, EventsHandler handler) {
        this.title = title;
        this.width = width;
        this.height = height;

        init();

        glfwSetKeyCallback(window, (window, key, scanCode, action, mods) -> {

            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
                return;
            }

            if (action != GLFW_PRESS)
                return;

            switch (key) {
                case GLFW_KEY_BACKSPACE -> {
                    handler.onBackspacePressed();
                }
            }

        });

        glfwSetCharCallback(window, (window, codePoint) -> {
            var newChars = Character.toChars(codePoint);
            handler.onTextInput(newChars);
        });
    }

    public static void glCall(Runnable callBack) {
        glClearErrors();
        callBack.run();
        glCheckErrors();
    }

    public static void glClearErrors() {
        while (glGetError() != GL_NO_ERROR) ;
    }

    public static void glCheckErrors() {
        var err = glGetError();
        while (err != GL_NO_ERROR) {
            new RuntimeException("[OpenGL Error] (0x" + Integer.toHexString(err) + " or " + err + ")").printStackTrace(System.err);
            err = glGetError();
        }
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err);

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        boolean maximized = false;

        if (width == 0 || height == 0) {
            width = 100;
            height = 100;
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            maximized = true;
        }

        window = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

        if (window == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create a GLFW window");

        int major = glfwGetWindowAttrib(window, GLFW_CONTEXT_VERSION_MAJOR);
        int minor = glfwGetWindowAttrib(window, GLFW_CONTEXT_VERSION_MINOR);
        System.out.println("OpenGL version: " + major + "." + minor);

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resize = true;
        });

        if (maximized)
            glfwMaximizeWindow(window);
        else {
            var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);

        GL.createCapabilities();

        glCall(() -> glEnable(GL_BLEND));
        glCall(() -> glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA));
    }

    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void destroy() {
        glfwDestroyWindow(window);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
