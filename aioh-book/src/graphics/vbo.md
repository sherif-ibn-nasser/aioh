## The Vertex Buffer

### What is a Vertex Buffer?

A **Vertex Buffer** is a memory structure that stores vertex data for rendering graphics. In Aioh, the vertex buffer is
a critical component of the graphics pipeline, as it manages the data needed to render geometric shapes and objects
efficiently.

### Key Components

- **Vertices**: Points in 3D or 2D space that define the shape of graphical objects.
- **Attributes**: Data associated with vertices, such as position, color, and texture coordinates.

### Implementation in Aioh

Aioh uses OpenGL’s Vertex Buffer Object (VBO) to store vertex data. The VBO is created and managed as follows:

```java
int vbo = glGenBuffers();

glBindBuffer(GL_ARRAY_BUFFER, vbo);

glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
```

- **`glGenBuffers`**: Generates a new buffer object.
- **`glBindBuffer`**: Binds the buffer to the target (e.g., `GL_ARRAY_BUFFER`).
- **`glBufferData`**: Allocates and initializes the buffer’s data.

These core OpenGL functions could be abstracted like this
(See [LWJGL tutorial](https://github.com/SilverTiger/lwjgl3-tutorial)):

src/main/java/com/aioh/graphics/VertexBufferObject.java:

```java
{{#include ../../../src/main/java/com/aioh/graphics/VertexBufferObject.java}}
```