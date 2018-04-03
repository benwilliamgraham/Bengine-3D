package bengine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import bengine.animation.Animation;
import bengine.assets.Shader;
import bengine.assets.Texture;
import bengine.entities.Camera;
import bengine.entities.Light;

public class Material {
	
	public Texture texture;
	public Vector3f ambientColor, diffuseColor, specularColor;
	public float shininess = 5.0f;
	
	protected Shader shader;
	
	public Material(Shader shader) {
		this.shader = shader;
		this.ambientColor = new Vector3f(0.2f, 0.2f, 0.2f);
		this.diffuseColor = new Vector3f(0.5f, 0.5f, 0.5f);
		this.specularColor = new Vector3f(1.0f, 1.0f, 0.7f);
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
	
	public void sun(Light sun) {
		shader.push("sun.position", sun.position);
		shader.push("sun.ambient", sun.ambient);
		shader.push("sun.diffuse", sun.diffuse);
		shader.push("sun.specular", sun.specular);
	}
	
	public void camera(Camera c, Matrix4f transformMatrix) {
		shader.push("viewMatrix", c.generateView());
		shader.push("transformMatrix", transformMatrix);
		shader.push("cameraPosition", c.transform.position);
		
		Matrix4f normalMatrix = new Matrix4f(transformMatrix);
		
		normalMatrix
			.invert()
			.transpose();
		
		shader.push("normalMatrix", normalMatrix);
		
	}
	
	public void bind() {
		shader.bind();
		
		if (texture != null) {
			texture.bind();
			
			shader.push("baseTexture", 0);
			shader.push("doTexture", 1);
		} else {
			shader.push("doTexture", 0);
		}
		
		//Pass this material's values to the uniform in the shader.
		shader.push("material.ambient", this.ambientColor);
		shader.push("material.diffuse", this.diffuseColor);
		shader.push("material.specular", this.specularColor);
		shader.push("material.shininess", this.shininess);
	}
	
	public void unbind() {
		shader.unbind();
	}
	
	public Shader getShader() {
		return shader;
	}
}