package com.aioh;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.aioh.Main.gl;
import static com.aioh.Main.glCall;
import static glm_.Java.glm;

public class AiohEventListener implements GLEventListener {

    public static final float MAX_WIDTH = 960;
    public static final float MAX_HEIGHT = 540;
    public static final float LEFT = -MAX_WIDTH / 2;
    public static final float RIGHT = MAX_WIDTH / 2;
    public static final float BOTTOM = -MAX_HEIGHT / 2;
    public static final float TOP = MAX_HEIGHT / 2;
    private AiohRenderer renderer;
    private Shader shader;
    private VertexArray va;
    private IndexBuffer ib;
    private Texture texture;

    private Mat4 proj = glm.ortho(LEFT, RIGHT, BOTTOM, TOP, -1, 1);
    private Mat4 view = glm.translate(new Mat4(), new Vec3(0, 0, 0));
    private Mat4 model = glm.translate(new Mat4(), new Vec3(0, 0, 0));

    @Override
    public void init(GLAutoDrawable drawable) {
        renderer = new AiohRenderer();

        var positions = new float[]{
                -50, -50, 0, 0,
                50, -50, 1, 0,
                50, 50, 1, 1,
                -50, 50, 0, 1,
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

        texture = new Texture("./assets/aioh-logo.png");
        texture.bind();
        this.shader.setUniform1i("u_Texture", texture.getSlot());

        va.unbind();
        ib.unbind();
        vb.unbind();
        shader.unbind();

    }


    private float dir = 1;

    @Override
    public void display(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL4();
        renderer.clear();

        if (model.getD0() > RIGHT - 50) {
            dir = -1;
        } else if (model.getD0() < LEFT + 50) {
            dir = 1;
        }

        model = model.translate(new Vec3(dir, 0, 0));

        shader.bind();

        Mat4 mvp = proj.times(view).times(model);
        shader.setUniformMat4f("u_MVP", mvp.array);

        renderer.draw(va, ib, shader);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }


}
