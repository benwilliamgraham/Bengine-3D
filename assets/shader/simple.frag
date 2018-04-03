#version 430 core

uniform vec3 fragColor;

out vec4 out_Color;

void main(void){
	out_Color = vec4(fragColor, 1.0);
}
