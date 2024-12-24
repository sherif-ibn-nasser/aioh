## Text Rendering

In OpenGL, texts are not native objects; instead, we treat text as textures. Text is rendered by generating a texture
from the font, and then mapping this texture onto a 2D surface or geometry. This allows the text to be manipulated like
any other texture, enabling effects such as scaling, rotation, and filtering.

#### Steps for Text Rendering:

1. **Load Fonts**: Fonts are loaded using `java.io.FileInputStream` and created using `java.awt.Font`:

   src/main/java/com/aioh/Main.java:

    ```java
    var fontFile = new FileInputStream(AiohUtils.FONTS_PATH + "/iosevka-regular.ttf");
    var font = java.awt.Font.createFont(TRUETYPE_FONT, fontFile).deriveFont(PLAIN, AiohEditor.FONT_SIZE);
   ```
2. **Generate Glyphs**: Individual characters are rasterized into textures and cached using
   `com.aioh.graphics.text.Font`.
3. **Render Text**: The glyph textures are drawn onto the screen.

src/main/java/com/aioh/graphics/text/Glyph.java:

```java
{{#include ../../../src/main/java/com/aioh/graphics/text/Glyph.java}}

```

src/main/java/com/aioh/graphics/text/Font.java:

```java
{{#include ../../../src/main/java/com/aioh/graphics/text/Font.java}}
```