package magica.states;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

import bengine.Game;
import bengine.Scene;
import bengine.State;
import bengine.assets.AssetManager;
import bengine.input.Keyboard;
import bengine.input.Mouse;
import bengine.rendering.Material;
import bengine.rendering.Renderer;
import magica.entities.CubeEntity;
import magica.entities.SphereEntity;
import magica.entities.TestEntity;

public class TestState implements State {
	
	protected Game game;
	
	protected Renderer renderer;
	protected Scene scene;
	
	private AssetManager assets;
	
	private Material defaultMaterial;
	
	private boolean isPaused, lockCamera;
	
	public TestState(AssetManager assets) {
		this.assets = assets;
		
		defaultMaterial = new Material(assets.getAsset("defaultShader"));
		
		renderer = new Renderer(defaultMaterial);
	}
	
	@Override
	public void onCreated(Game game) {
		this.game = game;
		this.scene = new Scene(assets);
		
		Mouse.lockCursor();
		
		scene.getCamera().transform.move(new Vector3f(0, 0, 10));
		
		TestEntity robotEntity = new TestEntity();
		robotEntity.transform.position.x += 2;
		
		this.scene.addEntity(robotEntity);
		
		SphereEntity sphere = new SphereEntity();
		sphere.transform.position.x -= 2;
		
		scene.addEntity(sphere);
	}

	@Override
	public void onUpdate(float delta) {
		
		float speed = 2;
		
		if (Keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
			speed = 5;
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_W)) {
			scene.getCamera().transform.move(scene.getCamera().transform.forwards().mul(-speed * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_S)) {
			scene.getCamera().transform.move(scene.getCamera().transform.forwards().mul( speed * delta));
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_A)) {
			scene.getCamera().transform.move(scene.getCamera().transform.right().mul(-speed * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_D)) {
			scene.getCamera().transform.move(scene.getCamera().transform.right().mul( speed * delta));
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_SPACE)) {
			scene.getCamera().transform.move(scene.getCamera().transform.up().mul( speed * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
			scene.getCamera().transform.move(scene.getCamera().transform.up().mul(-speed * delta));
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_C)) {
			Mouse.unlockCursor();
			lockCamera = true;
		} else if (Keyboard.isKeyDown(GLFW_KEY_V)) {
			Mouse.lockCursor();
			lockCamera = false;
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_ESCAPE)) {
			game.destroy();
		}
		
		if (!lockCamera) {
			scene.getCamera().transform.rotation.rotateLocalX(Mouse.getDY() * delta);
			scene.getCamera().transform.rotation.rotateY(Mouse.getDX() * delta);
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
