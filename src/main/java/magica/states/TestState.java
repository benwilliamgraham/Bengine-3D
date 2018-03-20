package magica.states;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

import bengine.Game;
import bengine.Scene;
import bengine.State;
import bengine.assets.AssetManager;
import bengine.input.Keyboard;
import bengine.rendering.Material;
import bengine.rendering.Renderer;
import magica.entities.CubeEntity;
import magica.entities.TestEntity;

public class TestState implements State {
	
	protected Game game;
	
	protected Renderer renderer;
	protected Scene scene;
	
	private AssetManager assets;
	
	private Material defaultMaterial;
	
	public TestState(AssetManager assets) {
		this.assets = assets;
		
		defaultMaterial = new Material(assets.getAsset("simpleShader"));
		
		renderer = new Renderer(defaultMaterial);
	}
	
	@Override
	public void onCreated(Game game) {
		this.game = game;
		this.scene = new Scene(assets);
		
		scene.getCamera().transform.move(new Vector3f(0, 0, 10));
		
		TestEntity.debugMaterial = new Material(assets.getAsset("wireframeShader"));
		
		TestEntity robotEntity = new TestEntity();
		
		this.scene.addEntity(robotEntity);
	}

	@Override
	public void onUpdate(float delta) {
		
		if (Keyboard.isKeyDown(GLFW_KEY_W)) {
			scene.getCamera().transform.move(scene.getCamera().transform.forwards().mul(-2.0f * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_S)) {
			scene.getCamera().transform.move(scene.getCamera().transform.forwards().mul(2.0f * delta));
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_A)) {
			scene.getCamera().transform.move(scene.getCamera().transform.right().mul(-2.0f * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_D)) {
			scene.getCamera().transform.move(scene.getCamera().transform.right().mul(2.0f * delta));
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_RIGHT)) {
			scene.getCamera().transform.rotation.rotateAxis((float) Math.PI / 4 * delta, new Vector3f(0.0f, 1.0f, 0.0f));
		} else if (Keyboard.isKeyDown(GLFW_KEY_LEFT)) {
			scene.getCamera().transform.rotation.rotateAxis((float) -Math.PI / 4 * delta, new Vector3f(0.0f, 1.0f, 0.0f));
		}
		
		
		scene.update(delta);
	}

	@Override
	public void onDraw() {
		renderer.clear(scene.getCamera().clearColor);
		
		renderer.render(scene);
	}

	@Override
	public void onDestroyed() {
		assets.destroy();
	}

	
	public Renderer getRenderer() {
		return renderer;
	}
}
