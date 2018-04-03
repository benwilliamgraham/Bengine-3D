#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec3 texCoord;
layout(location = 3) in vec4 weights;
layout(location = 4) in ivec4 joints;

uniform mat4 viewMatrix;
uniform mat4 transformMatrix;

void main(void) {
	gl_Position = viewMatrix * transformMatrix * vec4(position, 1.0);
}
