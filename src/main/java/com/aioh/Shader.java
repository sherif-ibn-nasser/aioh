package com.aioh;

import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import static com.aioh.graphics.AiohWindow.*;
import static org.lwjgl.opengl.GL46.*;

public class Shader {
    private HashMap<String, Integer> uniformsLocations;
    private String filePath;
    private int rendererId;

    public Shader(String filePath) {
        this.uniformsLocations = new HashMap();
        this.filePath = filePath;
        var shaders = readShaders();
        this.rendererId = createShader(shaders.vertexShader, shaders.fragmentShader);
        glCall(() -> glUseProgram(rendererId));
    }

    public void bind() {
        glCall(() -> glUseProgram(rendererId));
    }

    public void unbind() {
        glCall(() -> glUseProgram(0));
    }

    public int getUniformLocation(String name) {

        var locationFromCache = this.uniformsLocations.get(name);

        if (locationFromCache != null) {
            return locationFromCache;
        }

        glClearErrors();
        var location = glGetUniformLocation(rendererId, name);
        glCheckErrors();
        if (location == -1) {
            System.err.println("[OpenGL Warning]: uniform '" + name + "' doesn't exist");
        }
        this.uniformsLocations.put(name, location);
        return location;
    }

    public void setUniform4f(String name, float v0, float v1, float v2, float v3) {
        glCall(() -> glUniform4f(getUniformLocation(name), v0, v1, v2, v3));
    }

    public void setUniform1i(String name, int value) {
        glCall(() -> glUniform1i(getUniformLocation(name), value));
    }

    public void setUniformMat4f(String name, float[] mat) {
        glCall(() -> glUniformMatrix4fv(getUniformLocation(name), false, mat));
    }

    public int createShader(StringBuilder vertexShader, StringBuilder fragmentShader) {
        var program = glCreateProgram();

        var vs = compileShader(GL_VERTEX_SHADER, vertexShader);
        var fs = compileShader(GL_FRAGMENT_SHADER, fragmentShader);

        glCall(() -> glAttachShader(program, vs));
        glCall(() -> glAttachShader(program, fs));

        glCall(() -> glLinkProgram(program));
        glCall(() -> glValidateProgram(program));

        glCall(() -> glDeleteShader(vs));
        glCall(() -> glDeleteShader(fs));

        return program;
    }

    public int compileShader(int type, StringBuilder source) {
        var id = glCreateShader(type);
        glCall(() -> glShaderSource(id, source));
        glCall(() -> glCompileShader(id));

        var result = BufferUtils.createIntBuffer(1);
        glCall(() -> glGetShaderiv(id, GL_COMPILE_STATUS, result));

        if (result.get(0) == GL_FALSE) {
            String msg = glGetShaderInfoLog(id);
            System.out.println("Failed to compile " + ((type == GL_VERTEX_SHADER) ? "vertex" : "fragment"));
            System.out.println(msg);
        }

        return id;
    }


    public record ShadersSource(StringBuilder vertexShader, StringBuilder fragmentShader) {
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

        return new ShadersSource(ss[0], ss[1]);
    }

}
