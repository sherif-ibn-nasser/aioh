package com.aioh.graphics;

import com.aioh.AiohUtils;
import glm_.vec2.Vec2;
import org.lwjgl.BufferUtils;

import static com.aioh.graphics.AiohWindow.*;
import static org.lwjgl.opengl.GL46.*;

public class AiohRenderer {
    public static final String ROOT = "/home/sherif/IdeaProjects/aioh/src/main/resources/shaders/";
    public static final String VERTEX_SHADER_PATH = ROOT + "simple.vert";
    public static final String COLOR_FRAG_SHADER_PATH = ROOT + "simple_color.frag";
    public static final String IMAGE_FRAG_SHADER_PATH = ROOT + "simple_image.frag";
    public static final String TEXT_FRAG_SHADER_PATH = ROOT + "simple_text.frag";
    public static final String EPIC_FRAG_SHADER_PATH = ROOT + "simple_epic.frag";

    public static final String UNIFORM_SLOT_TIME = "time";
    public static final String UNIFORM_SLOT_RESOLUTION = "resolution";
    public static final String UNIFORM_SLOT_CAMERA_POS = "camera_pos";
    public static final String UNIFORM_SLOT_CAMERA_SCALE = "camera_scale";

    public static final String[] FRAG_SHADERS_PATHS = new String[]{
            COLOR_FRAG_SHADER_PATH, IMAGE_FRAG_SHADER_PATH, TEXT_FRAG_SHADER_PATH, EPIC_FRAG_SHADER_PATH
    };
    public static final String[] UNIFORM_SLOTS_NAMES = new String[]{
            UNIFORM_SLOT_TIME, UNIFORM_SLOT_RESOLUTION, UNIFORM_SLOT_CAMERA_POS, UNIFORM_SLOT_CAMERA_SCALE
    };

    private int vao, vbo;
    private AiohShader currentShader;
    private float time, cameraScale = 0.3f, cameraScaleVel;
    private Vec2 resolution = new Vec2(), cameraPos = new Vec2(), cameraVel = new Vec2();
    private AiohVertices vertices = new AiohVertices();
    private int[] programs, uniformsSlots = new int[4];

    public AiohRenderer() {
        initVertices();
        this.programs = loadShaders();
    }

    private void initVertices() {
        glCall(() -> vao = glGenVertexArrays());
        glCall(() -> glBindVertexArray(vao));
        glCall(() -> vbo = glGenBuffers());
        glCall(() -> glBindBuffer(GL_ARRAY_BUFFER, vbo));
        var buffer = BufferUtils.createFloatBuffer(AiohVertices.VERTICES_CAP);
        buffer.put(this.vertices.getBuffer());
        buffer.flip();
        glCall(() -> glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW));

        // position
        glCall(() -> glEnableVertexAttribArray(AiohVertexAttr.POSITION.ordinal()));
        glCall(() -> glVertexAttribPointer(
                AiohVertexAttr.POSITION.ordinal(),
                AiohVertexAttr.POSITION.size,
                GL_FLOAT,
                false,
                AiohVertex.BYTES,
                AiohVertexAttr.POSITION.offset));

        // color
        glCall(() -> glEnableVertexAttribArray(AiohVertexAttr.COLOR.ordinal()));
        glCall(() -> glVertexAttribPointer(
                AiohVertexAttr.COLOR.ordinal(),
                AiohVertexAttr.COLOR.size,
                GL_FLOAT,
                false,
                AiohVertex.BYTES,
                AiohVertexAttr.COLOR.offset));

        // uv
        glCall(() -> glEnableVertexAttribArray(AiohVertexAttr.UV.ordinal()));
        glCall(() -> glVertexAttribPointer(
                AiohVertexAttr.UV.ordinal(),
                AiohVertexAttr.UV.size,
                GL_FLOAT,
                false,
                AiohVertex.BYTES,
                AiohVertexAttr.UV.offset));
    }

    public void reloadShaders() {
        var programs = loadShaders();
        for (int i = 0; i < this.programs.length; i++) {
            glDeleteProgram(this.programs[i]);
        }
        this.programs = programs;
    }

    private int[] loadShaders() {
        var programs = new int[4];
        var vs = compileShader(VERTEX_SHADER_PATH, GL_VERTEX_SHADER);

        for (int i = 0; i < programs.length; i++) {
            var fs = compileShader(FRAG_SHADERS_PATHS[i], GL_FRAGMENT_SHADER);
            var program = programs[i] = glCreateProgram();
            glCall(() -> glAttachShader(program, vs));
            glCall(() -> glAttachShader(program, fs));
            glCall(() -> glLinkProgram(program));

            var result = BufferUtils.createIntBuffer(1);
            glCall(() -> glGetProgramiv(program, GL_COMPILE_STATUS, result));

            if (result.get(0) == GL_FALSE) {
                String msg = glGetProgramInfoLog(program);
                throw new RuntimeException("Failed to link program[" + i + "]\n" + msg);
            }

            glCall(() -> glDeleteShader(fs));
        }

        glCall(() -> glDeleteShader(vs));

        return programs;
    }

    private int compileShader(String filePath, int shaderType) {
        var source = AiohUtils.readFile(filePath);

        var shaderId = glCreateShader(shaderType);
        glCall(() -> glShaderSource(shaderId, source));
        glCall(() -> glCompileShader(shaderId));

        var result = BufferUtils.createIntBuffer(1);
        glCall(() -> glGetShaderiv(shaderId, GL_COMPILE_STATUS, result));

        if (result.get(0) == GL_FALSE) {
            String msg = glGetShaderInfoLog(shaderId);
            throw new RuntimeException("Failed to compile " + ((shaderType == GL_VERTEX_SHADER) ? "vertex" : "fragment") + "\n" + msg);
        }

        return shaderId;
    }

    public void setCurrentShader(AiohShader shader) {
        this.currentShader = shader;
        var program = this.programs[shader.ordinal()];
        glCall(() -> glUseProgram(program));
        for (int i = 0; i < this.uniformsSlots.length; i++) {
            this.uniformsSlots[i] = getUniformLocation(program, UNIFORM_SLOTS_NAMES[i]);
        }
        glCall(() -> glUniform2f(this.uniformsSlots[0], this.resolution.getX(), this.resolution.getY()));
        glCall(() -> glUniform1f(this.uniformsSlots[1], this.time));
        glCall(() -> glUniform2f(this.uniformsSlots[2], this.cameraPos.getX(), this.cameraPos.getY()));
        glCall(() -> glUniform1f(this.uniformsSlots[3], this.cameraScale));
    }

    public int getUniformLocation(int program, String name) {
// TODO: load uniforms from cache

//        var locationFromCache = this.uniformsLocations.get(name);
//
//        if (locationFromCache != null) {
//            return locationFromCache;
//        }
//
        glClearErrors();
        var location = glGetUniformLocation(program, name);
        glCheckErrors();
        if (location == -1) {
            System.err.println("[OpenGL Warning]: uniform '" + name + "' doesn't exist");
        }
//        this.uniformsLocations.put(name, location);
        return location;
    }

    public void sync() {
        var buffer = BufferUtils.createFloatBuffer(AiohVertices.VERTICES_CAP);
        buffer.put(this.vertices.getBuffer());
        buffer.flip();
        glCall(() -> glBufferSubData(GL_ARRAY_BUFFER, 0, buffer));
    }

    public void draw() {
        glCall(() -> glDrawArrays(GL_TRIANGLES, 0, this.vertices.getCount()));
    }

    public void flush() {
        sync();
        draw();
        this.vertices.clear();
    }
}
