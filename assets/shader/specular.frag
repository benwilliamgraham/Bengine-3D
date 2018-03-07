#version 400 core

in vec3 pass_textureCoords;
in vec3 pass_normal;
in vec3 pass_position;

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform mat4 viewmodelMatrix;

void main(void){

	vec3 baseColor = vec3(1.0, 0.0, 0.0);

	vec3 lightPosition = vec3(0.0, 0.0, 0.0);

	vec4 lightDirection = normalize(vec4(lightPosition, 1.0) - vec4(pass_position, 1.0) * viewmodelMatrix);

	float lightIntensity = max(dot(vec4(pass_normal, 0.0), lightDirection), 0);

	out_Color = vec4(baseColor * lightIntensity, 1.0);
}
