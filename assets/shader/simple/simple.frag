#version 400 core

in vec4 pass_position;
in vec4 pass_normal;
in vec3 pass_texCoord;
in vec4 pass_weights;

uniform mat4 viewMatrix;
uniform mat4 transformMatrix;

uniform vec3 baseColor;

out vec4 out_Color;

void main(void){
	vec4 light_position = normalize(vec4(0.0, 8.0, 5.0, 1.0));

	float lightAmbient = 0.5;

	float diffuseCoefficient = clamp((dot(pass_normal, normalize(light_position - pass_position))), 0.0, 1.0);

	vec3 color = vec3(1.0, 1.0, 1.0) * pass_weights.x;

	out_Color = vec4(color, 1.0);
}
