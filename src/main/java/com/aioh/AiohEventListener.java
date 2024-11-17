package com.aioh;

import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public class AiohEventListener {

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
    private IndexBuffer ibCube;
    private Texture texture;

    private float dir = 1;
    private Mat4 proj = glm.ortho(LEFT, RIGHT, BOTTOM, TOP, -1, 1);
    private Mat4 view = glm.translate(new Mat4(), new Vec3(0, 0, 0));
    private Mat4 model = glm.translate(new Mat4(), new Vec3(0, 0, 0));

    public void init() {
        renderer = new AiohRenderer();

        var positions = new float[]{
                -100, -50, 0, 0,
                100, -50, 1, 0,
                100, 50, 1, 1,
                -100, 50, 0, 1,
        };

        var indexes = new int[]{
                0, 1, 2,
                2, 3, 0
        };

        var positionsCube = new float[]{
                0, 0, 0,
                0, 0, 100,
                0, 100, 0,
                0, 100, 100,
                100, 0, 0,
                100, 0, 100,
                100, 100, 0,
                100, 100, 100,
        };

        var indexesCube = new int[]{
                0, 2, 6,
                0, 4, 6,

                0, 1, 3,
                0, 2, 3,

                0, 1, 5,
                0, 4, 5,

                7, 3, 2,
                7, 6, 2,

                7, 5, 1,
                7, 3, 1,

                7, 6, 4,
                7, 5, 4,
        };

        this.va = new VertexArray();

        var vb = new VertexBuffer(positions);

        var layout = new VertexBufferLayout();
        layout.pushFloat(2);
        layout.pushFloat(2);

        this.va.addBuffer(vb, layout);

        this.ib = new IndexBuffer(indexes);

//        var vbCube = new VertexBuffer(FloatBuffer.wrap(positionsCube), positionsCube.length * Float.BYTES);
//        var layoutCube = new VertexBufferLayout();
//        layoutCube.pushFloat(3);
//        this.va.addBuffer(vbCube, layoutCube);
//        this.ibCube = new IndexBuffer(IntBuffer.wrap(indexesCube), indexesCube.length);

        this.shader = new Shader("./src/main/resources/shader/basic.shader");
        texture = new Texture("./assets/tt.png");
        texture.bind();
        shader.setUniform1i("u_Texture", texture.getSlot());
//        shader.setUniform4f("u_Color", 1, 0, 0, 0);
        shader.unbind();

    }


    public void display() {
        renderer.clear();

        if (model.getD0() > RIGHT - 100) {
            dir = -1;
        } else if (model.getD0() < LEFT + 100) {
            dir = 1;
        }

        model = model.translate(new Vec3(dir, 0, 0));

//        model = model.rotateY(0.01f);
        Mat4 mvp = proj.times(view).times(model);

        shader.bind();
        shader.setUniformMat4f("u_MVP", mvp.array);
        renderer.draw(va, ib, shader);
//        renderer.draw(va, ibCube, shader);
    }


}
