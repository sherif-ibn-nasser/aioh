package com.aioh;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

public class Main {
    public static void main(String[] args) {
        GLProfile.initSingleton();
        final GLProfile profile = GLProfile.get(GLProfile.GL4);
        final GLCapabilities capabilities = new GLCapabilities(profile);
        final GLWindow window = GLWindow.create(capabilities);
        window.setSize(640, 360);
        window.setResizable(true);
        window.setTitle("Aoih");
        window.addGLEventListener(new AoihEventListener());

        FPSAnimator animator = new FPSAnimator(window, 60);
        animator.start();

        window.setVisible(true);
    }
}
