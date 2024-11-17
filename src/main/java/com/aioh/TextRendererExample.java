//package com.aioh;
//
//import com.jogamp.opengl.*;
//import com.jogamp.opengl.awt.GLCanvas;
//import com.jogamp.opengl.util.awt.TextRenderer;
//
//import java.awt.*;
//import java.awt.event.ComponentEvent;
//import java.awt.event.ComponentListener;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//
//public class TextRendererExample implements GLEventListener, KeyListener, ComponentListener {
//
//    private TextRenderer textRenderer;
//    private StringBuilder text = new StringBuilder("Hello, JOGL Text Rendering!");
//    private int canvasWidth, canvasHeight;
//
//    @Override
//    public void init(GLAutoDrawable drawable) {
//        GL2 gl = drawable.getGL().getGL2();
//        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
//
//        // Load a basic font for TextRenderer (e.g., Arial or a default system font)
//        Font font = new Font("Arial", Font.BOLD, 36);
//        textRenderer = new TextRenderer(font);
//    }
//
//    @Override
//    public void display(GLAutoDrawable drawable) {
//
//        // Use TextRenderer to draw the text
//        textRenderer.beginRendering(canvasWidth, canvasHeight);
//        textRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f); // Set text color to white
//        textRenderer.draw(text.toString(), 100, canvasHeight - 100); // Position text at (100, 100 from bottom)
//        textRenderer.endRendering();
//    }
//
//    @Override
//    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
//        canvasWidth = width;
//        canvasHeight = height;
//
//        GL2 gl = drawable.getGL().getGL2();
//        gl.glViewport(0, 0, width, height);
//        gl.glMatrixMode(GL2.GL_PROJECTION);
//        gl.glLoadIdentity();
//        gl.glOrtho(0, width, 0, height, -1, 1);  // Set up orthographic projection
//        gl.glMatrixMode(GL2.GL_MODELVIEW);
//
//        ((GLCanvas) drawable).repaint();
//    }
//
//    @Override
//    public void dispose(GLAutoDrawable drawable) {
//        if (textRenderer != null) {
//            textRenderer.dispose();
//        }
//    }
//
//    @Override
//    public void keyTyped(KeyEvent e) {
//        if (e.getKeyChar() == '\b' && text.length() > 0) {
//            text.deleteCharAt(text.length() - 1);
//        } else {
//            text.append(e.getKeyChar());
//        }
//        ((GLCanvas) e.getSource()).repaint();
//    }
//
//    @Override
//    public void keyPressed(KeyEvent e) {
//        // Do nothing
//    }
//
//    @Override
//    public void keyReleased(KeyEvent e) {
//        // Do nothing
//    }
//
//    @Override
//    public void componentResized(ComponentEvent e) {
//        canvasWidth = e.getComponent().getWidth();
//        canvasHeight = e.getComponent().getHeight();
//        ((GLCanvas) e.getComponent()).repaint();
//    }
//
//    @Override
//    public void componentMoved(ComponentEvent e) {
//        // Do nothing
//    }
//
//    @Override
//    public void componentShown(ComponentEvent e) {
//        // Do nothing
//    }
//
//    @Override
//    public void componentHidden(ComponentEvent e) {
//        // Do nothing
//    }
//
//    public static void main(String[] args) {
//        GLProfile.initSingleton();
//        GLProfile profile = GLProfile.get(GLProfile.GL2);
//        GLCapabilities capabilities = new GLCapabilities(profile);
//        GLCanvas canvas = new GLCanvas(capabilities);
//        TextRendererExample renderer = new TextRendererExample();
//
//        canvas.addGLEventListener(renderer);
//
////        canvas.addKeyListener(renderer);
////        canvas.addComponentListener(renderer);
//
////        JFrame frame = new JFrame("JOGL Text Rendering Example");
////        frame.getContentPane().add(canvas);
////        frame.setSize(800, 600);
////        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////        frame.setVisible(true);
//    }
//}