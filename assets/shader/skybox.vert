#version 330
layout(location = 0) in vec3 position;

out vec3 pass_texcoords;

uniform mat4 viewMatrix;

void main(void) {
	pass_texcoords = position;
	gl_Position = viewMatrix * vec4(position, 1.0);
}