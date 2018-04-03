package magica.states;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

import bengine.Game;
import bengine.Scene;
import bengine.State;
import bengine.assets.AssetManager;
import bengine.entities.Entity;
import bengine.input.Keyboard;
import bengine.input.Mouse;
import bengine.rendering.Material;
import bengine.rendering.renderers.Renderer;
import bengine.rendering.renderers.SceneRenderer;
import bengine.rendering.renderers.steps.ShadowEntityStep;
import magica.entities.CubeEntity;
import magica.entities.GrassPlane;
import magica.entities.SphereEntity;

public class LightingTestState implements State {

	private AssetManager assets;
	
	private SceneRenderer renderer;
	
	private Material defaultMaterial;
	
	private Scene scene;
	
	private float freecamSpeed = 3.0f;
	
	private float mouseSensitivity = 2.5f;
	
	private boolean grabMouse = false;
	
	private Game game;
	
	public LightingTestState(AssetManager assets) {
		this.assets = assets;
	}
	
	@Override
	public void onCreated(Game game) {
		this.defaultMaterial = new Material(assets.getAsset("defaultShader"));
		
		this.renderer = new SceneRenderer(defaultMaterial);
		this.renderer.getEntityRenderer().addStep(new ShadowEntityStep(assets.getAsset("shadowShader")));
		
		this.scene = new Scene(assets);
		
		Entity cube = new CubeEntity();
		cube.transform.position.z += 4;
		cube.transform.position.y += 1;
		
		Entity plane = new GrassPlane();
		
		this.scene.addEntity(cube);
		this.scene.addEntity(plane);
		
		this.game = game;
	}

	@Override
	public void onUpdate(float delta) {
		scene.update(delta);
		
		if (Keyboard.isKeyDown(GLFW_KEY_W)) {
			scene.getCamera().transform.move(scene.getCamera().transform.forwards().mul( freecamSpeed * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_S)) {
			scene.getCamera().transform.move(scene.getCamera().transform.forwards().mul(-freecamSpeed * delta));
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_D)) {
			scene.getCamera().transform.move(scene.getCamera().transform.right().mul( freecamSpeed * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_A)) {
			scene.getCamera().transform.move(scene.getCamera().transform.right().mul(-freecamSpeed * delta));
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_SPACE)) {
			scene.getCamera().transform.move(scene.getCamera().transform.up().mul( freecamSpeed * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
			scene.getCamera().transform.move(scene.getCamera().transform.up().mul(-freecamSpeed * delta));
		}
		
		if (grabMouse) {
			scene.getCamera().transform.rotation.rotateY(Mouse.getDX() * -mouseSensitivity * delta);
			scene.getCamera().transform.rotation.rotateLocalX(Mouse.getDY() * -mouseSensitivity * delta);
		}
		
		if (Keyboard.isKeyJustPressed(GLFW_KEY_O)) {
			scene.getCamera().transform.position = new Vector3f(scene.getSun().position);
			scene.getCamera().transform.lookAt(new Vector3f(0, 0, 0));
		}
		
		if (Keyboard.isKeyJustPressed(GLFW_KEY_P)) {
			grabMouse = !grabMouse;
			
			if (grabMouse) {
				Mouse.lockCursor();
			} else {
				Mouse.unlockCursor();
			}
		}
		
		if (Keyboard.isKeyJustPressed(GLFW_KEY_ESCAPE)) {
			this.game.destroy();
		}
		
		
	}

	@Override
	public void onDraw() {
		renderer.clear();
		
		renderer.render(scene);
	}

	@Override
	public void onDestroyed() {
		
	}

	@Override
	public Renderer getRenderer() {
		return renderer;
	}

}
