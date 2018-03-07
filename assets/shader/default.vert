#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec3 texCoords;

out vec3 pass_textureCoords;
out vec3 pass_normal;
out vec3 pass_position;

uniform mat4 viewmodelMatrix;

void main(void){
	pass_textureCoords = texCoords;
	pass_normal = normal;
	pass_position = position;
	gl_Position = viewmodelMatrix * vec4(position, 1.0);
}
