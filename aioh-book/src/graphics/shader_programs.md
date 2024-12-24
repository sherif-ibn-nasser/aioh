## Shader programs

A **Shader Program** is an OpenGL object that links multiple shader stages together into a single executable unit on the
GPU. In Aioh, a shader program typically combines a **vertex shader** and a **fragment shader**, allowing for flexible
rendering pipelines. Each shader must be compiled separately before linking them into a program.

```java
int vertexShader = glCreateShader(GL_VERTEX_SHADER);

glShaderSource(vertexShader, vertexShaderCode);

glCompileShader(vertexShader);

int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

glShaderSource(fragmentShader, fragmentShaderCode);

glCompileShader(fragmentShader);

int shaderProgram = glCreateProgram();

glAttachShader(shaderProgram, vertexShader);

glAttachShader(shaderProgram, fragmentShader);

glLinkProgram(shaderProgram);

// Bind the shader program before rendering
//
//glUseProgram(shaderProgram);
//
// Set the uniforms
//glUniform1f(uniformLocation, value);
// 
```

In Aioh, there are two shader programs in the renderer:

src/main/java/com/aioh/graphics/AiohRenderer.java:

```java
/**
 * Determines the currently used program
 */
public static ShaderProgram currentProgram;

/**
 * Used to render texts
 */
public static ShaderProgram mainProgram;

/**
 * Used to render identity colored elements (The cursor, the status bar, text selection, etc.)
 */
public static ShaderProgram colorProgram;
```

### Full shader program

src/main/java/com/aioh/graphics/ShaderProgram.java:

```java
{{#include ../../../src/main/java/com/aioh/graphics/ShaderProgram.java}}
```
