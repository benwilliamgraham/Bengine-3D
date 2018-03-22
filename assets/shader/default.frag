#version 430 core

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

in vec4 pass_position;
in vec4 pass_normal;
in vec3 pass_texCoord;
in vec4 pass_weights;

uniform mat4 viewMatrix;
uniform mat4 transformMatrix;
uniform mat4 normalMatrix;

uniform int doTexture;

uniform Material material;

uniform sampler2D baseTexture;

out vec4 out_Color;

void main(void){

	vec4 normal = normalize(normalMatrix * pass_normal);
	vec4 lightDirection = normalize(vec4(1.0, 1.0, 1.0, 0.0));

	float lambertian = clamp(dot(-lightDirection, normal), 0.0, 1.0);
	float specular = 0.0;

	if (lambertian > 0) {
		vec4 viewDir = normalize(-(transformMatrix * pass_position));

		vec4 reflectDir = normalize(reflect(lightDirection, normal));

		specular = pow(clamp(dot(viewDir, reflectDir), 0.0, 1.0), material.shininess);
	}

	vec4 textureColor = vec4(1.0, 1.0, 1.0, 1.0);

	if (doTexture == 1) {
		textureColor = texture(baseTexture, pass_texCoord.xy);
	} else {
		textureColor = vec4(1.0);
	}

	out_Color = vec4(textureColor.rgb * material.ambient + textureColor.rgb * material.diffuse * lambertian + textureColor.rgb * material.specular * specular, textureColor.a);
}
