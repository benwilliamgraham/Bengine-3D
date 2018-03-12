#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec3 texCoords;

void main(void) {
	gl_Position = vec4(position, 1.0);
}
