#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normals;
layout(location = 2) in vec3 texCoords;

out vec3 pass_textureCoords;

uniform mat4 viewmodelMatrix;

void main(void){
	pass_textureCoords = texCoords;
	gl_Position = viewmodelMatrix * vec4(position, 1.0);
}
