package magica.states;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import bengine.Game;
import bengine.Scene;
import bengine.State;
import bengine.assets.AssetManager;
import bengine.assets.Model;
import bengine.entities.Skybox;
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
		
		scene.getCamera().transform.position.z = -1.3f;
		scene.getCamera().transform.position.y = 0.7f;
		
		Skybox sky = new Skybox(
				assets.getAsset("skybox"),
				assets.getAsset("skyboxShader"),
				((Model) assets.getAsset("cubeModel")).getMeshes()[0]
			);
		
		
		scene.setSky(sky);
		
		scene.addEntity(new AnimatedChicken());
		//scene.addEntity(new AnimatedChicken());
	}

	@Override
	public void onUpdate(float delta) {
		scene.update(delta);
		
		if (Keyboard.isKeyDown(GLFW_KEY_ESCAPE)) {
			Game.getCurrent().destroy();
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
