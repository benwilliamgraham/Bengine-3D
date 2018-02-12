#version 400 core

in vec3 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D modelTexture;

void main(void){

	vec4 textureColor = texture(modelTexture, pass_textureCoords.xy);

	out_Color = textureColor;
}
