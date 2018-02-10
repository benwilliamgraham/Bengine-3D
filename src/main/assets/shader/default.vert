#version 400 core

layout(location = 0) in vec3 position;
//in vec3 textureCoords;

out vec3 pass_textureCoords;

uniform mat4 viewmodelMatrix;

void main(void){
	gl_Position = vec4(position, 1.0);
}