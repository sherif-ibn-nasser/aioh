## Texts

### Text Rendering

Aioh uses the FreeType library for rendering scalable fonts. The rendered text is then integrated into the graphics
pipeline using texture mapping.

#### Steps for Text Rendering:

1. **Load Fonts**: Fonts are loaded using the FreeType library.
2. **Generate Glyphs**: Individual characters are rasterized into textures.
3. **Render Text**: The glyph textures are drawn onto the screen.

```java
FT_Library ft;
if(

FT_Init_FreeType(&ft)){
        System.err.

println("Could not initialize FreeType library");
}

FT_Face face;
if(

FT_New_Face(ft, "path/to/font.ttf",0,&face)){
        System.err.

println("Failed to load font");
}

FT_Set_Pixel_Sizes(face, 0,48);
```
