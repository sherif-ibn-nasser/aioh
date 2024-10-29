package com.aioh;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        final GLCanvas canvas = new GLCanvas(capabilities);
        Jogl example = new Jogl();
        canvas.addGLEventListener(example);
        canvas.setSize(400, 400);

        final JFrame frame = new JFrame("JOGL Example");
        frame.getContentPane().add(canvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);
    }
}

class Jogl implements GLEventListener {

    @Override
    public void init(GLAutoDrawable drawable) {

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }
}