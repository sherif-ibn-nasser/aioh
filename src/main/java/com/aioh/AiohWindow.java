package com.aioh;

import com.aioh.graphics.AiohRenderer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import static com.aioh.graphics.AiohRenderer.mainProgram;
import static com.aioh.graphics.AiohRenderer.textSelectionProgram;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class AiohWindow {

    public interface EventsHandler {
        void onTextInput(char[] newChars);

        void onKeyPressed(int keyCode);

        void onModKeysPressed(int mods, int keyCode);
    }

    public static int width, height;

    private String title;
    private long window;
    private boolean resize;

    public AiohWindow(String title, int width, int height, EventsHandler handler) {
        this.title = title;
        AiohWindow.width = width;
        AiohWindow.height = height;

        init(handler);

        int major = glfwGetWindowAttrib(window, GLFW_CONTEXT_VERSION_MAJOR);
        int minor = glfwGetWindowAttrib(window, GLFW_CONTEXT_VERSION_MINOR);
        System.out.println("OpenGL version: " + major + "." + minor);
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

    private void init(EventsHandler handler) {
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


        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            AiohWindow.width = width;
            AiohWindow.height = height;
            this.resize = true;
            glViewport(0, 0, width, height);
            mainProgram.use();
            AiohRenderer.updateMVPMatrix(mainProgram, width, height);
            textSelectionProgram.use();
            AiohRenderer.updateMVPMatrix(textSelectionProgram, width, height);
        });

        glfwSetKeyCallback(window, (window, key, scanCode, action, mods) -> {

            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
                return;
            }

            if (action == GLFW_PRESS) {
                if (mods != 0)
                    handler.onModKeysPressed(mods, key);
                else
                    handler.onKeyPressed(key);
            }

        });

        glfwSetCharCallback(window, (window, codePoint) -> {
            var newChars = Character.toChars(codePoint);
            handler.onTextInput(newChars);
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
