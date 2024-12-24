### Unifroms

Uniforms are global variables in shaders that remain constant during the execution of a single draw call. They are used
to pass data from the CPU to the GPU (the shaders). Unlike attributes or varying variables, uniforms
are read-only and consistent for all processed vertices or fragments during a render pass.

In GLSL, uniforms are declared as follows:

```glsl
uniform mat4 mvp;
uniform float cameraScale;
uniform float time;
```

To set uniforms from CPU to th GPU, we first locate the uniform variable in the shader program and then assign it a
value:

1. Locate the Uniform Use glGetUniformLocation to get the location of the uniform variable in the shader program.

    ```java
    int uniformLocation = glGetUniformLocation(shaderProgram, "model");
    ```

2. Set the Uniform Value Depending on the type of the uniform, use the corresponding OpenGL function:

    * For matrices:
   ```java
   glUniformMatrix4fv(uniformLocation, false, matrixBuffer);
   ```

   Here, matrixBuffer contains the matrix data.

    * For vectors:
   ```java
   glUniform3f(uniformLocation, x, y, z);
   ```
    * For single values:

   ```java
   glUniform1f(uniformLocation, value);
   ```