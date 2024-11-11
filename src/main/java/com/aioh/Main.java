package com.aioh;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

import static com.aioh.AiohEventListener.MAX_HEIGHT;
import static com.aioh.AiohEventListener.MAX_WIDTH;

public class Main {

    public static GLWindow win;
    public static GL4 gl;

    public static void glCall(Runnable callBack) {
        glClearErrors();
        callBack.run();
        glCheckErrors();
    }

    public static void glClearErrors() {
        while (gl.glGetError() != GL4.GL_NO_ERROR) ;
    }

    public static void glCheckErrors() {
        var err = gl.glGetError();
        while (err != GL4.GL_NO_ERROR) {
            new RuntimeException("[OpenGL Error] (0x" + Integer.toHexString(err) + " or " + err + ")").printStackTrace(System.err);
            err = gl.glGetError();
        }
    }

    public static void main(String[] args) {
        GLProfile.initSingleton();
        final GLProfile profile = GLProfile.get(GLProfile.GL4);
        final GLCapabilities capabilities = new GLCapabilities(profile);
        win = GLWindow.create(capabilities);
        win.setSize((int) MAX_WIDTH, (int) MAX_HEIGHT);
        win.setResizable(true);
        win.setTitle("Aioh");
        win.addGLEventListener(new AiohEventListener());
        FPSAnimator animator = new FPSAnimator(win, 120);
        animator.start();
        win.setVisible(true);
    }
}
