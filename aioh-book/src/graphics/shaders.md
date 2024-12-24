## Shaders

Shaders are programs executed on the GPU to control the rendering process. Aioh utilizes two primary types of
shaders:

1. **Vertex Shader**: Transforms vertex data into screen coordinates.
2. **Fragment Shader**: Determines the color of individual pixels.

### Writing Shaders in Aioh

Aioh shaders are written in GLSL (OpenGL Shading Language). Below is an example of a basic vertex shader:

src/main/resources/shaders/default.vert:

```glsl
{{#include ../../../src/main/resources/shaders/default.vert}}
```

And the text fragment shader:

src/main/resources/shaders/default.frag:

```glsl
{{#include ../../../src/main/resources/shaders/default.frag}}
```

Also, there is also an identity color fragment shader, used to render the cursor and the status bar:

src/main/resources/shaders/color.frag:

```glsl
{{#include ../../../src/main/resources/shaders/color.frag}}
```

### Compiling and Linking Shaders

Shaders are compiled and linked as follows:

```java
int vertexShader = glCreateShader(GL_VERTEX_SHADER);

glShaderSource(vertexShader, vertexShaderCode);

glCompileShader(vertexShader);

int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

glShaderSource(fragmentShader, fragmentShaderCode);

glCompileShader(fragmentShader);
```

This could be modeled in more convenient way:

src/main/java/com/aioh/graphics/Shader.java:

```java
{{#include ../../../src/main/java/com/aioh/graphics/Shader.java}}
```