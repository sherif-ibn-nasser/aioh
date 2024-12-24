## Graphics Pipeline

### Overview

The Graphics Pipeline describes the rendering stages in Aioh, from processing vertices to displaying pixels on the
screen.

### Stages

1. **Input Assembly**: Collect vertex data.
2. **Vertex Processing**: Transform vertices using the Vertex Shader.
3. **Rasterization**: Convert primitives into fragments.
4. **Fragment Processing**: Determine the final color of each pixel.
5. **Output Merging**: Display the final image.

Each stage is optimized for performance and designed to support Aioh’s editing features.

See also this OpenGL [tutorial](https://en.wikibooks.org/wiki/OpenGL_Programming/Modern_OpenGL_Introduction).