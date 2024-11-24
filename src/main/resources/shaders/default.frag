#version 150 core

uniform float time;

in vec2 pos;
in vec4 vertexColor;
in vec2 textureCoord;

out vec4 fragColor;

uniform sampler2D texImage;

float w = 1;
#define PI 3.1415926538

void main() {
    vec4 textureColor = texture(texImage, textureCoord);
    fragColor =
    (
    vec4(
    1+pos.x+sin(w*time+pos.x),
    1+pos.y+sin(w*time + PI /3 + pos.y),
    1+pos.x+pos.y+sin(2*w*time - PI/2 +pos.x+pos.y),
    1
    )
    ) * textureColor;
}
