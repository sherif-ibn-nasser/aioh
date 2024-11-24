#version 150 core

in vec2 position;
in vec4 color;
in vec2 texcoord;

out vec4 vertexColor;
out vec2 textureCoord;
out vec2 pos;

//uniform mat4 model;
//uniform mat4 view;
//uniform mat4 projection;
uniform mat4 mvp;
uniform float cameraScale;

void main() {
    vertexColor = color;
    textureCoord = texcoord;
    //    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(cameraScale * position, 0.0, 1.0);
    pos = gl_Position.xy;
}
