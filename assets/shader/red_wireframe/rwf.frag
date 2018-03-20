#version 400 core

in vec4 pass_position;
in vec4 pass_normal;
in vec3 pass_texCoord;

uniform mat4 viewMatrix;
uniform mat4 transformMatrix;

uniform vec3 baseColor;

out vec4 out_Color;

void main(void){


	out_Color = vec4(baseColor, 1.0);
}
