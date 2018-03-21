#version 400 core

in vec4 pass_position;
in vec4 pass_normal;
in vec3 pass_texCoord;
in vec4 pass_weights;

uniform mat4 viewMatrix;
uniform mat4 transformMatrix;
uniform mat4 normalMatrix;

uniform vec3 baseColor;

out vec4 out_Color;

struct Light {
	vec3 direction;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

struct Material {
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float shininess;
};

void main(void){

	vec4 normal = normalize(normalMatrix * pass_normal);
	vec4 lightDirection = normalize(vec4(1.0, 1.0, 1.0, 0.0));

	float lambertian = max(dot(lightDirection, normal), 0.0);
	float specular = 0.0;

	if (lambertian > 0) {
		vec4 viewDir = normalize(-(transformMatrix * pass_position));

		vec4 reflectDir = normalize(reflect(lightDirection, normal));

		specular = pow(dot(viewDir, reflectDir), 4.0);
	}

	out_Color = vec4(lambertian * vec3(1, 1, 1) + specular * vec3(1, 1, 1), 1.0);
}
