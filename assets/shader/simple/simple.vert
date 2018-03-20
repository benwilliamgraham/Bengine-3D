#version 400 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec3 texCoord;
layout(location = 3) in vec4 weights;
layout(location = 4) in ivec4 joints;

uniform mat4 viewMatrix;
uniform mat4 transformMatrix;
uniform mat4 boneTransforms[50];

out vec4 pass_position;
out vec4 pass_normal;
out vec3 pass_texCoord;
out vec4 pass_weights;

mat4 getMat(int id, float weight) {
	if (id != -1) {
		return weight * boneTransforms[uint(id)];
	}

	return mat4(1.0);
}

void main(void) {

	mat4 animationMatrix =
			getMat(joints.x, weights.x);

	pass_position = transformMatrix * animationMatrix * vec4(position, 1.0);
	pass_normal = transformMatrix * vec4(normal, 0.0);
	pass_texCoord = texCoord;
	pass_weights = weights;

	gl_Position = viewMatrix * pass_position;
}
