#version 430 core

#define POSSION_SAMPLES 4

struct Light {
	vec3 position;
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
uniform mat4 lightMatrix;

uniform vec3 cameraPosition;

uniform Light sun;

uniform int doTexture;

uniform int doShadow;

uniform Material material;

uniform sampler2D baseTexture;

uniform sampler2DShadow depthMap;

out vec4 out_Color;

vec2 poissonDisk[16] = vec2[](
   vec2( -0.94201624, -0.39906216 ),
   vec2( 0.94558609, -0.76890725 ),
   vec2( -0.094184101, -0.92938870 ),
   vec2( 0.34495938, 0.29387760 ),
   vec2( -0.91588581, 0.45771432 ),
   vec2( -0.81544232, -0.87912464 ),
   vec2( -0.38277543, 0.27676845 ),
   vec2( 0.97484398, 0.75648379 ),
   vec2( 0.44323325, -0.97511554 ),
   vec2( 0.53742981, -0.47373420 ),
   vec2( -0.26496911, -0.41893023 ),
   vec2( 0.79197514, 0.19090188 ),
   vec2( -0.24188840, 0.99706507 ),
   vec2( -0.81409955, 0.91437590 ),
   vec2( 0.19984126, 0.78641367 ),
   vec2( 0.14383161, -0.14100790 )
);

highp float random(vec3 seed, int i){
	vec4 seed4 = vec4(seed,i);
	highp float dot_product = dot(seed4, vec4(12.9898,78.233,45.164,94.673));
	return fract(sin(dot_product) * 43758.5453);
}

float shadow(vec4 pos, vec4 normal, vec4 lightDir) {
	vec3 projCoords = pos.xyz / pos.w;

	projCoords = projCoords * 0.5 + 0.5;

	if(projCoords.z > 1.0)
		return 1.0;

	float bias = max(0.05 * (1.0 - dot(normal, lightDir)), 0.005);

	float visibility = 1.0;

	for (int i=0;i<4;i++){
			// use either :
			//  - Always the same samples.
			//    Gives a fixed pattern in the shadow, but no noise
			int index = i;
			//  - A random sample, based on the pixel's screen location.
			//    No banding, but the shadow moves with the camera, which looks weird.
			// int index = int(16.0*random(gl_FragCoord.xyy, i))%16;
			//  - A random sample, based on the pixel's position in world space.
			//    The position is rounded to the millimeter to avoid too much aliasing
			//int index = int(16.0*random(floor((pass_position).xyz * 1000.0), i))%16;

			// being fully in the shadow will eat up 4*0.2 = 0.8
			// 0.2 potentially remain, which is quite dark.
			visibility -= 0.2*(1.0-texture(depthMap, vec3(projCoords.xy, (projCoords.z-bias)) ));
	}

	return visibility;
	//return (projCoords.z > texture(depthMap, projCoords.xy).r ? 1.0 : 0.0;
}

void main(void){

	vec3 ambientColor = material.ambient * sun.ambient;

	vec4 normal = normalize(transformMatrix * pass_normal);
	vec4 lightDirection = normalize(vec4(sun.position, 1.0));

	float diffuseFactor = max(dot(lightDirection, normal), 0.0);

	vec3 diffuseColor = diffuseFactor * material.diffuse * sun.diffuse;

	vec4 viewDirection = normalize(vec4(cameraPosition, 1.0) - transformMatrix * pass_position);

	vec4 halfwayDirection = normalize(lightDirection + viewDirection);

	float specularFactor = 0;

	if (diffuseFactor > 0) {
		specularFactor = pow(max(dot(normal, halfwayDirection), 0.0), material.shininess);
	}

	vec3 specularColor = specularFactor * material.specular * sun.specular;


	vec4 textureColor = vec4(1.0, 1.0, 1.0, 1.0);

	if (doTexture == 1) {
		//out_Color = vec4(vec3(texture(depthMap, pass_texCoord.xy)), 1.0);
		//return;

		textureColor = texture(baseTexture, pass_texCoord.xy);
	} else {
		textureColor = vec4(1.0);
	}

	float visibility = shadow(lightMatrix * transformMatrix * pass_position, normal, lightDirection);

	//out_Color = vec4(vec3(1.0 - shading), 1.0);

	out_Color = vec4(textureColor.rgb * (ambientColor + visibility * (diffuseColor + specularColor)), textureColor.a);

	//out_Color = vec4(vec3(1.0) * texture(depthMap, vec2(gl_FragCoord.x / 640, gl_FragCoord.y / 480)).r, 1.0);
}
