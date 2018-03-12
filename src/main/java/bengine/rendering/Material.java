package bengine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import bengine.animation.Animation;
import bengine.assets.Shader;
import bengine.assets.Texture;

public class Material {
	
	public Texture texture;
	public Vector3f baseColor = new Vector3f(1.0f);
	
	protected Shader shader;
	
	public Material(Shader shader) {
		this.shader = shader;
	}
	
	public void animate(Animation anim) {
		shader.push("jointTransforms", anim.GetBoneData());
	}
	
	public void camera(Matrix4f cameraView) {
		shader.push("viewmodelMatrix", cameraView);
	}
	
	public void bind() {
		shader.bind();
		
		if (baseColor != null) {
			shader.push("baseColor", baseColor);
		}
		
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