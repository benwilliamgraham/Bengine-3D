#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec3 texCoord;
layout(location = 3) in vec4 weights;
layout(location = 4) in ivec4 joints;

uniform mat4 viewMatrix;
uniform mat4 transformMatrix;
uniform mat4 boneTransforms[50];
uniform mat4 lightMatrix;

out vec4 pass_position;
out vec4 pass_normal;
out vec3 pass_texCoord;
out vec4 pass_weights;

vec4 animate(vec4 pos) {
	return (((boneTransforms[joints.x]) * pos) * weights.x +
			((boneTransforms[joints.y]) * pos) * weights.y +
			((boneTransforms[joints.z]) * pos) * weights.z +
			((boneTransforms[joints.w]) * pos) * weights.w);
}

void main(void) {

	if (joints.x == -1) {
		pass_position = vec4(position, 1.0);
	} else {
		pass_position = animate(vec4(position, 1.0));
	}


	pass_normal = vec4(normal, 0.0);
	pass_texCoord = texCoord;
	pass_weights = weights;

	gl_Position = viewMatrix * transformMatrix * pass_position;
}
