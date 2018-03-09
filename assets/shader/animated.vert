#version 400 core

const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 4;

out vec3 pass_textureCoords;
out vec3 pass_normal;
out vec3 pass_position;

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec3 texCoords;
layout(location = 3) in vec4 jointWeights;
layout(location = 4) in ivec4 jointIDS;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform mat4 viewmodelMatrix;

void main(void){

	vec4 totalPos = vec4(0.0, 0.0, 0.0, 1.0);
	vec4 totalNormal = vec4(0.0, 0.0, 0.0, 0.0);

	for (int i = 0; i < MAX_JOINTS; i++) {
		if (jointIDS[i] != -1) {
		
			mat4 jointTransform = jointTransforms[jointIDS[i]];

			totalPos += jointTransform * vec4(position, 1.0) * jointWeights[i];

			vec4 localNormal = jointTransform * vec4(normal, 0.0);
			totalNormal += localNormal * jointWeights[i];
		}
	}

	pass_normal = normal;
	pass_textureCoords = texCoords;
	pass_position = (viewmodelMatrix * totalPos).xyz;
	gl_Position = viewmodelMatrix * totalPos;
}
