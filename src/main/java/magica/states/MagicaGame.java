package magica.states;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

import bengine.Game;
import bengine.Scene;
import bengine.State;
import bengine.assets.AssetManager;
import bengine.assets.Model;
import bengine.entities.Entity;
import bengine.entities.Skybox;
import bengine.input.Keyboard;
import bengine.input.Mouse;
import bengine.rendering.Material;
import bengine.rendering.renderers.Renderer;
import bengine.rendering.renderers.SceneRenderer;
import bengine.rendering.renderers.steps.ShadowEntityStep;
import magica.entities.CubeEntity;
import magica.entities.GrassPlane;
import magica.entities.Player;
import magica.entities.SphereEntity;

public class MagicaGame implements State {

	private AssetManager assets;
	
	private SceneRenderer renderer;
	
	private Material defaultMaterial;
	
	private Scene scene;
	
	private float freecamSpeed = 3.0f;
	
	private float mouseSensitivity = 2.5f;
	
	private boolean grabMouse = false;
	
	private Game game;
	
	public MagicaGame(AssetManager assets) {
		this.assets = assets;
	}
	
	@Override
	public void onCreated(Game game) {
		this.defaultMaterial = new Material(assets.getAsset("defaultShader"));
		
		this.renderer = new SceneRenderer(defaultMaterial);
		
		this.scene = new Scene(assets);
		
		Skybox sky = new Skybox(
			assets.getAsset("skybox"),
			assets.getAsset("skyboxShader"),
			((Model) assets.getAsset("cubeModel")).getMeshes()[0]
		);
		
		Player player = new Player();
		
		Entity plane = new GrassPlane();
		
		Entity cube = new CubeEntity();
		
		this.scene.setSky(sky);
		this.scene.addEntity(plane);
		this.scene.addEntity(player);
		this.scene.addEntity(cube);
		
		this.game = game;
	}

	@Override
	public void onUpdate(float delta) {
		scene.update(delta);
		
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
