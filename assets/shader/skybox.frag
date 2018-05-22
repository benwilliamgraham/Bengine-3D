#version 330
in vec3 pass_texcoords;

out vec4 out_color;

uniform samplerCube skybox;

void main(void) {
	out_color = texture(skybox, pass_texcoords);
}