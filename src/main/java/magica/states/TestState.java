package magica.states;

import static org.lwjgl.glfw.GLFW.*;

import java.net.SocketAddress;

import org.joml.Vector3f;

import bengine.Game;
import bengine.Scene;
import bengine.State;
import bengine.assets.AssetManager;
import bengine.entities.Entity;
import bengine.input.Keyboard;
import bengine.input.Mouse;
import bengine.networking.Client;
import bengine.networking.sync.SyncedObject;
import bengine.rendering.Material;
import bengine.rendering.Renderer;
import magica.entities.SphereEntity;
import magica.entities.Barn;
import magica.entities.Chicken;
import magica.entities.GrassPlane;

public class TestState extends Client implements State  {
	
	protected Game game;
	
	protected Renderer renderer;
	protected Scene scene;
	
	private AssetManager assets;
	
	private Material defaultMaterial;
	
	private Entity chicken;
	
	public TestState(AssetManager assets, String name, SocketAddress addr) {
		super(name, addr);
		this.assets = assets;
		
		defaultMaterial = new Material(assets.getAsset("defaultShader"));
		
		renderer = new Renderer(defaultMaterial);
	}
	
	@Override
	public void onCreated(Game game) {
		this.game = game;
		this.scene = new Scene(assets);
		
		//Mouse.lockCursor();
		
		scene.getCamera().transform.move(new Vector3f(-75, 50, 50));
		
		this.chicken = new Chicken();
		
		this.chicken.transform.position = new Vector3f((float) Math.random() * 90 - 45, 0.0f,(float)  Math.random() * 90 - 45);
		
		Barn barn = new Barn();
		barn.transform.position = new Vector3f(0.0f, 60f, -175.0f);
		
		GrassPlane grass = new GrassPlane();
		
		
		
		this.scene.addEntity(barn);
		this.scene.addEntity(grass);
		
		this.open();
	}

	@Override
	public void onUpdate(float delta) {
		if (Keyboard.isKeyDown(GLFW_KEY_ESCAPE)) {
			game.destroy();
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
		//objectManager.destroyObject(this.chicken);
		this.close();
		assets.destroy();
	}

	
	public Renderer getRenderer() {
		return renderer;
	}

	@Override
	public void onConnected() {
		System.out.println("Connected to server.");
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
		
	}
}
