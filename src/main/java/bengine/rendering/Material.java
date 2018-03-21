package bengine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import bengine.animation.Animation;
import bengine.assets.Shader;
import bengine.assets.Texture;

public class Material {
	
	public Texture texture;
	public Vector3f baseColor = new Vector3f(0.4f);
	
	protected Shader shader;
	
	public Material(Shader shader) {
		this.shader = shader;
	}
	
	public void animate(Animation anim) {
		Matrix4f[] boneTransforms = new Matrix4f[50];
		
		Matrix4f[] oBoneTransforms = anim.GetBoneData();
		
		for (int i = 0; i < 50; i++) {
			if (i < oBoneTransforms.length) {
				boneTransforms[i] = oBoneTransforms[i];
			} else {
				boneTransforms[i] = new Matrix4f().identity();
			}
		}
		
		shader.push("boneTransforms", boneTransforms);
	}
	
	public void camera(Matrix4f viewMatrix, Matrix4f transformMatrix) {
		shader.push("viewMatrix", viewMatrix);
		shader.push("transformMatrix", transformMatrix);
		
		Matrix4f normalMatrix = new Matrix4f(transformMatrix);
		
		normalMatrix
			.invert()
			.transpose();
		
		shader.push("normalMatrix", normalMatrix);
		
	}
	
	public void color(Vector3f color) {
		baseColor = color;
		
		shader.push("baseColor", baseColor);
	}
	
	public void color() {
		if (baseColor != null) {
			color(baseColor);
		}
	}
	
	public void bind() {
		shader.bind();
		
		if (texture != null) {
			shader.push("texture", texture.getTexture());
		}
	}
	
	public void unbind() {
		shader.unbind();
	}
	
	public Shader getShader() {
		return shader;
	}
}