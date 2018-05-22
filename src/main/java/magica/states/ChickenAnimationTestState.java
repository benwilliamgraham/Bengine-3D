package magica.states;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import bengine.Game;
import bengine.Scene;
import bengine.State;
import bengine.assets.AssetManager;
import bengine.input.Keyboard;
import bengine.rendering.Material;
import bengine.rendering.renderers.Renderer;
import bengine.rendering.renderers.SceneRenderer;
import magica.entities.AnimatedChicken;
import magica.entities.Chicken;
import magica.entities.CubeEntity;

public class ChickenAnimationTestState implements State {

	private Game game;
	
	private AssetManager assets;
	
	private SceneRenderer renderer;
	
	private Scene scene;
	
	private Material defaultMaterial;
	
	private float freecamSpeed = 3.0f;
	
	public ChickenAnimationTestState(AssetManager assets) {
		this.assets = assets;
		this.defaultMaterial = new Material(assets.getAsset("defaultShader"));
		this.renderer = new SceneRenderer(defaultMaterial);
	}
	
	@Override
	public void onCreated(Game game) {
		this.scene = new Scene(assets);
		
		//scene.getCamera().transform.position.z = 10;
		
		//scene.addEntity(new CubeEntity());
		
		scene.addEntity(new Chicken());
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
		
	}

	@Override
	public void onDraw() {
		renderer.clear();
		
		renderer.render(scene);
	}

	@Override
	public void onDestroyed() {
		scene.destroy();
	}

	@Override
	public Renderer getRenderer() {
		return renderer;
	}
	
}
