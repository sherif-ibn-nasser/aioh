package com.aioh;

import com.jogamp.opengl.GL4;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Scanner;

import static com.aioh.Main.*;

public class Shader {
    private HashMap<String, Integer> uniformsLocations;
    private String filePath;
    private int[] rendererId;

    public Shader(String filePath) {
        this.uniformsLocations = new HashMap();
        this.filePath = filePath;
        this.rendererId = new int[1];
        var shaders = readShaders();
        this.rendererId[0] = createShader(shaders.vertexShader, shaders.fragmentShader);
        glCall(() -> gl.glUseProgram(rendererId[0]));
    }

    public void bind() {
        glCall(() -> gl.glUseProgram(rendererId[0]));
    }

    public void unbind() {
        glCall(() -> gl.glUseProgram(0));
    }

    public int getUniformLocation(String name) {

        var locationFromCache = this.uniformsLocations.get(name);

        if (locationFromCache != null) {
            return locationFromCache;
        }

        glClearErrors();
        var location = gl.glGetUniformLocation(rendererId[0], name);
        glCheckErrors();
        if (location == -1) {
            System.err.println("[OpenGL Warning]: uniform '" + name + "' doesn't exist");
        }
        this.uniformsLocations.put(name, location);
        return location;
    }

    public void setUniform4f(String name, float v0, float v1, float v2, float v3) {
        glCall(() -> gl.glUniform4f(getUniformLocation(name), v0, v1, v2, v3));
    }

    public void setUniform1i(String name, int value) {
        glCall(() -> gl.glUniform1i(getUniformLocation(name), value));
    }

    public int createShader(String vertexShader, String fragmentShader) {
        var program = gl.glCreateProgram();

        var vs = compileShader(GL4.GL_VERTEX_SHADER, vertexShader);
        var fs = compileShader(GL4.GL_FRAGMENT_SHADER, fragmentShader);

        glCall(() -> gl.glAttachShader(program, vs));
        glCall(() -> gl.glAttachShader(program, fs));

        glCall(() -> gl.glLinkProgram(program));
        glCall(() -> gl.glValidateProgram(program));

        glCall(() -> gl.glDeleteShader(vs));
        glCall(() -> gl.glDeleteShader(fs));

        return program;
    }

    public int compileShader(int type, String source) {
        var id = gl.glCreateShader(type);
        glCall(() -> gl.glShaderSource(id, 1, new String[]{source}, null));
        glCall(() -> gl.glCompileShader(id));

        var result = IntBuffer.allocate(1);
        glCall(() -> gl.glGetShaderiv(id, GL4.GL_COMPILE_STATUS, result));

        if (result.get(0) == GL4.GL_FALSE) {

            var len = IntBuffer.allocate(1);
            glCall(() -> gl.glGetShaderiv(id, GL4.GL_INFO_LOG_LENGTH, len));
            var msg = ByteBuffer.allocate(len.get(0));
            glCall(() -> gl.glGetShaderInfoLog(id, len.get(0), len, msg));

            System.out.println("Failed to compile " + ((type == GL4.GL_VERTEX_SHADER) ? "vertex" : "fragment"));
            System.out.println(new String(msg.array()));
        }

        return id;
    }


    public record ShadersSource(String vertexShader, String fragmentShader) {
    }

    public ShadersSource readShaders() {
        var ss = new StringBuilder[]{new StringBuilder(), new StringBuilder()};
        int idx = -1;
        try {
            var scanner = new Scanner(new File(this.filePath));
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

}
