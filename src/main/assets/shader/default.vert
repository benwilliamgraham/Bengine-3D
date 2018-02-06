#version 400 core

in vec3 position;
in vec3 textureCoords;

out vec3 pass_textureCoords;

uniform mat4 viewmodelMatrix;

void main(void){
	
	gl_Position = viewmodelMatrix * vec4(position, 1.0);
	pass_textureCoords = textureCoords;
}