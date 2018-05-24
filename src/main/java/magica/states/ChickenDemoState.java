package magica.states;

import static org.lwjgl.glfw.GLFW.*;

import java.net.SocketAddress;

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
import bengine.networking.Client;
import bengine.networking.sync.SyncedObject;
import bengine.rendering.Material;
import bengine.rendering.renderers.Renderer;
import bengine.rendering.renderers.SceneRenderer;
import magica.entities.SphereEntity;
import magica.entities.Barn;
import magica.entities.Chicken;
import magica.entities.GrassPlane;

public class ChickenDemoState extends Client implements State  {
	
	protected Game game;
	
	protected SceneRenderer renderer;
	protected Scene scene;
	
	private AssetManager assets;
	
	private Material defaultMaterial;
	
	private Entity chicken;
	
	private boolean grabMouse = true;
	
	private float freecamSpeed = 3.0f;
	
	private float mouseSensitivity = 2.5f;
	
	public ChickenDemoState(AssetManager assets, String name, SocketAddress addr) {
		super(name, addr);
		this.assets = assets;
		
		defaultMaterial = new Material(assets.getAsset("defaultShader"));
		
		renderer = new SceneRenderer(defaultMaterial);
	}
	
	@Override
	public void onCreated(Game game) {
		this.game = game;
		this.scene = new Scene(assets);
		
		//Mouse.lockCursor();
		
		scene.getCamera().transform.move(new Vector3f(5, 5, 5));
		
		this.chicken = new Chicken();
		
		this.chicken.transform.position = new Vector3f((float) Math.random() * 10 - 5, 0.0f,(float)  Math.random() * 8 - 4);
		
		Barn barn = new Barn();
		barn.transform.position = new Vector3f(0f, 3f, -10f);
		
		GrassPlane grass = new GrassPlane();
		
		this.scene.addEntity(barn);
		this.scene.addEntity(grass);
		
		Skybox sky = new Skybox(
				assets.getAsset("skybox"),
				assets.getAsset("skyboxShader"),
				((Model) assets.getAsset("cubeModel")).getMeshes()[0]
			);
		
		
		scene.setSky(sky);
		
		this.open();
	}

	@Override
	public void onUpdate(float delta) {
		scene.update(delta);
		
		/*if (Keyboard.isKeyDown(GLFW_KEY_W)) {
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
			scene.getCamera().transform.rotation.rotateY(-Mouse.getDX() * mouseSensitivity * delta);
			scene.getCamera().transform.rotation.rotateLocalX(-Mouse.getDY() * mouseSensitivity * delta);
		}
		
		if (Keyboard.isKeyJustPressed(GLFW_KEY_P)) {
			grabMouse = !grabMouse;
			
			if (grabMouse) {
				Mouse.lockCursor();
			} else {
				Mouse.unlockCursor();
			}
		}*/
		
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
		objectManager.deregisterObject(this.chicken, getConnection());
		this.close();
		assets.destroy();
	}

	
	public Renderer getRenderer() {
		return renderer;
	}

	@Override
	public void onConnected() {
		System.out.println("Connected to server as: " + this.name);
		objectManager.registerObject(chicken, getConnection());
	}

	@Override
	public void onNewObject(SyncedObject obj) {
		if (obj instanceof Entity) {
			scene.addEntity((Entity) obj);
		}
	}

	@Override
	public void onDisconnected() {
		this.game.destroy();
	}
}
