package bengine.rendering;

import org.joml.Vector3f;

public class Material {
	
	public Shader shader;
	public Texture texture;
	public Vector3f baseColor = new Vector3f(1.0f);
	
	public Material(Shader shader) {
		this.shader = shader;
	}
}
