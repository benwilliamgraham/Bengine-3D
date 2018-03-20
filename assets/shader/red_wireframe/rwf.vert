#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec3 texCoord;
layout(location = 3) in vec4 weights;
layout(location = 4) in ivec4 joints;

uniform mat4 viewMatrix;
uniform mat4 transformMatrix;

out vec4 pass_position;
out vec4 pass_normal;
out vec3 pass_texCoord;

void main(void) {
	pass_position = vec4(position, 1.0);
	pass_normal = vec4(normal, 0.0);
	pass_texCoord = texCoord;

	gl_Position = viewMatrix * pass_position;
}
