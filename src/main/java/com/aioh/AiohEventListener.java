package com.aioh;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.aioh.Main.gl;
import static com.aioh.Main.glCall;
import static glm_.Java.glm;

public class AiohEventListener implements GLEventListener {

    private AiohRenderer renderer;
    private Shader shader;
    private VertexArray va;
    private IndexBuffer ib;
    private com.jogamp.opengl.util.texture.Texture texture;

    @Override
    public void init(GLAutoDrawable drawable) {
        renderer = new AiohRenderer();

        var positions = new float[]{
                -0.5f, -0.5f, 0, 0,
                0.5f, -0.5f, 1, 0,
                0.5f, 0.5f, 1, 1,
                -0.5f, 0.5f, 0, 1,
        };

        var indexes = new int[]{
                0, 1, 2,
                2, 3, 0
        };

        gl = drawable.getGL().getGL4();

        glCall(() -> gl.glEnable(GL4.GL_BLEND));
        glCall(() -> gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA));

        this.va = new VertexArray();

        var vb = new VertexBuffer(FloatBuffer.wrap(positions), positions.length * Float.BYTES);

        var layout = new VertexBufferLayout();
        layout.pushFloat(2);
        layout.pushFloat(2);


        this.va.addBuffer(vb, layout);

        this.ib = new IndexBuffer(IntBuffer.wrap(indexes), indexes.length);

        this.shader = new Shader("./src/main/resources/shader/basic.shader");

        var texture = new Texture("./assets/aioh-logo.png");
        texture.bind();
        this.shader.setUniform1i("u_Texture", texture.getSlot());

        float left = -1.5f;
        float right = 1.5f;
        float bottom = -2.5f;
        float top = 2.5f;
        float near = -1.0f;
        float far = 1.0f;

        var mat = glm.ortho(left, right, bottom, top, near, far);
        this.shader.setUniformMat4f("u_MVP", mat.array);

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

        renderer.draw(va, ib, shader);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }


}
