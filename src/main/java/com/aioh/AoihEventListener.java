package com.aioh;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

public class AoihEventListener implements GLEventListener {

    private int uColor = -1;

    @Override
    public void init(GLAutoDrawable drawable) {

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

        var gl = drawable.getGL().getGL4();

        var posBuffer = new int[1];
        glCall(gl, () -> gl.glGenBuffers(1, posBuffer, 0));
        glCall(gl, () -> gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, posBuffer[0]));
        glCall(gl, () -> gl.glBufferData(GL4.GL_ARRAY_BUFFER, positions.length * Float.BYTES, FloatBuffer.wrap(positions), GL4.GL_STATIC_DRAW));

        glCall(gl, () -> gl.glEnableVertexAttribArray(0));
        glCall(gl, () -> gl.glVertexAttribPointer(0, 2, GL4.GL_FLOAT, false, 2 * Float.BYTES, 0));

        var ibo = new int[1];
        glCall(gl, () -> gl.glGenBuffers(1, ibo, 0));
        glCall(gl, () -> gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ibo[0]));
        glCall(gl, () -> gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, indexes.length * Integer.BYTES, IntBuffer.wrap(indexes), GL4.GL_STATIC_DRAW));

        var shaders = readShaders();
        var shader = createShader(gl, shaders.vertexShader, shaders.fragmentShader);
        glCall(gl, () -> gl.glUseProgram(shader));

        this.uColor = gl.glGetUniformLocation(shader, "u_Color");

    }

    private float increment = 0.05f;
    private float red = 0;

    @Override
    public void display(GLAutoDrawable drawable) {
        if (this.uColor == -1)
            return;
        var gl = drawable.getGL().getGL4();
        glCall(gl, () -> gl.glClear(GL4.GL_COLOR_BUFFER_BIT));
        
        glCall(gl, () -> gl.glUniform4f(this.uColor, this.red, 0.3f, 0.3f, 1));
        glCall(gl, () -> gl.glDrawElements(GL4.GL_TRIANGLES, 6, GL4.GL_UNSIGNED_INT, 0));

        if (this.red > 1)
            this.increment = -0.01f;
        else if (this.red < 0)
            this.increment = 0.01f;

        this.red += this.increment;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    public static int createShader(GL4 gl, String vertexShader, String fragmentShader) {
        var program = gl.glCreateProgram();

        var vs = compileShader(gl, GL4.GL_VERTEX_SHADER, vertexShader);
        var fs = compileShader(gl, GL4.GL_FRAGMENT_SHADER, fragmentShader);

        glCall(gl, () -> gl.glAttachShader(program, vs));
        glCall(gl, () -> gl.glAttachShader(program, fs));

        glCall(gl, () -> gl.glLinkProgram(program));
        glCall(gl, () -> gl.glValidateProgram(program));

        glCall(gl, () -> gl.glDeleteShader(vs));
        glCall(gl, () -> gl.glDeleteShader(fs));

        return program;
    }

    public static int compileShader(GL4 gl, int type, String source) {
        var id = gl.glCreateShader(type);
        glCall(gl, () -> gl.glShaderSource(id, 1, new String[]{source}, null));
        glCall(gl, () -> gl.glCompileShader(id));

        var result = IntBuffer.allocate(1);
        glCall(gl, () -> gl.glGetShaderiv(id, GL4.GL_COMPILE_STATUS, result));

        if (result.get(0) == GL4.GL_FALSE) {

            var len = IntBuffer.allocate(1);
            glCall(gl, () -> gl.glGetShaderiv(id, GL4.GL_INFO_LOG_LENGTH, len));
            var msg = ByteBuffer.allocate(len.get(0));
            glCall(gl, () -> gl.glGetShaderInfoLog(id, len.get(0), len, msg));

            System.out.println("Failed to compile " + ((type == GL4.GL_VERTEX_SHADER) ? "vertex" : "fragment"));
            System.out.println(new String(msg.array()));
        }

        return id;
    }


    public record ShadersSource(String vertexShader, String fragmentShader) {
    }

    public static ShadersSource readShaders() {
        var ss = new StringBuilder[]{new StringBuilder(), new StringBuilder()};
        int idx = -1;
        try {
            var scanner = new Scanner(new File(("./src/main/resources/shader/basic.shader")));
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                if (line.startsWith("#shader")) {
                    if (line.contains("vertex")) {
                        idx = 0;
                    } else if (line.contains("fragment")) {
                        idx = 1;
                    }
                } else if (idx != -1) {
                    ss[idx].append(line + "\n");
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new ShadersSource(ss[0].toString(), ss[1].toString());
    }

    public static void glCall(GL4 gl, Runnable callBack) {
        glClearErrors(gl);
        callBack.run();
        glCheckErrors(gl);
    }

    public static void glClearErrors(GL4 gl) {
        while (gl.glGetError() != GL4.GL_NO_ERROR) ;
    }

    public static void glCheckErrors(GL4 gl) {
        var err = gl.glGetError();
        while (err != GL4.GL_NO_ERROR) {
            new RuntimeException("[OpenGL Error] (0x" + Integer.toHexString(err) + ")").printStackTrace(System.err);
            err = gl.glGetError();
        }
    }
}
