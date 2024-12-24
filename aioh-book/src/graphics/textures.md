## Textures

In OpenGL, textures are used to apply images or patterns onto 3D objects or surfaces. A texture is essentially an image
that can be mapped onto geometry, allowing for detailed surfaces without the need to model every detail. Textures are
loaded into memory and are typically represented by a 2D or 3D array of pixel data. The process of mapping a texture
onto an object is known as texture mapping, and it involves associating texture coordinates with vertices in the 3D
space.

Textures can be used for a wide range of effects, including color mapping, bump mapping, normal mapping, and environment
mapping. OpenGL supports various types of texture filtering, such as nearest-neighbor and bi-linear filtering, to
improve
the visual quality of textures at different distances and angles. Textures can also be manipulated with shaders to
achieve dynamic effects, like animation, lighting, and reflection.

src/main/java/com/aioh/graphics/Texture.java:

```java
{{#include ../../../src/main/java/com/aioh/graphics/Texture.java}}
```