package bengine.entities;

import org.joml.Vector3f;

import bengine.Scene;
import bengine.assets.CubeMap;
import bengine.assets.Shader;
import bengine.rendering.Mesh;
import bengine.rendering.Vertex;

public class Skybox {
	
	protected Shader shader;
	protected CubeMap texture;
	
	protected Mesh skyboxMesh;
	
	private Scene scene;
	
	public Skybox(CubeMap texture, Shader shader, Mesh skyboxMesh) {
		this.texture = texture;
		this.shader = shader;
		this.skyboxMesh = skyboxMesh;
	}
	
	public Shader getShader() {
		return shader;
	}
	
	public CubeMap getTexture() {
		return texture;
	}
	
	public Mesh getMesh() {
		return skyboxMesh;
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
}
