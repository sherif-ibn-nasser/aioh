package com.aioh;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.aioh.Main.gl;

public class AiohEventListener implements GLEventListener {

    private AiohRenderer renderer;
    private Shader shader;
    private VertexArray va;
    private IndexBuffer ib;

    @Override
    public void init(GLAutoDrawable drawable) {
        renderer = new AiohRenderer();

        var positions = new float[]{
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,
        };

        var indexes = new int[]{
                0, 1, 2,
                2, 3, 0
        };

        gl = drawable.getGL().getGL4();

        this.va = new VertexArray();

        var vb = new VertexBuffer(FloatBuffer.wrap(positions), positions.length * Float.BYTES);

        var layout = new VertexBufferLayout();
        layout.pushFloat(2);

        this.va.addBuffer(vb, layout);

        this.ib = new IndexBuffer(IntBuffer.wrap(indexes), indexes.length);

        this.shader = new Shader("./src/main/resources/shader/basic.shader");

        va.unbind();
        ib.unbind();
        vb.unbind();
        shader.unbind();

    }

    private float red = 0;
    private float green = 0;
    private float blue = 0;

    private int redDir = 1;
    private int greenDir = 0;
    private int blueDir = 0;

    @Override
    public void display(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL4();

        renderer.clear();
        shader.bind();
        shader.setUniform4f("u_Color", this.red, this.green, this.blue, 1);
        renderer.draw(va, ib, shader);

        if (this.red > 1 && redDir != 0) {
            redDir = 0;
            greenDir = 1;
        } else if (this.red < 0 && redDir != 0) {
            redDir = 0;
            greenDir = -1;
        } else if (this.green > 1 && greenDir != 0) {
            greenDir = 0;
            blueDir = 1;
        } else if (this.green < 0 && greenDir != 0) {
            greenDir = 0;
            blueDir = -1;
        } else if (this.blue > 1 && blueDir != 0) {
            blueDir = 0;
            redDir = -1;
        } else if (this.blue < 0 && blueDir != 0) {
            blueDir = 0;
            redDir = 1;
        }

        this.red += this.redDir * 0.01;
        this.green += this.greenDir * 0.01;
        this.blue += this.blueDir * 0.01;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }


}
