package bengine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.joml.Vector3f;

import bengine.assets.AssetManager;
import bengine.entities.Camera;
import bengine.entities.Entity;

public class Scene {
	private static final Logger LOGGER = Logger.getLogger(Scene.class.getName());
	
	protected Camera camera;
	
	protected Map<Long, Entity> entities;
	protected AssetManager assets;
	
	public Scene(AssetManager assets) {
		this.assets = assets;
		this.entities = new HashMap<Long, Entity>();
		this.camera = new Camera(new Vector3f(0, 0, -2.0f), 120.0f, 150.0f); //Create a camera at the origin.
		this.camera.name = "DefaultCamera";
	}
	
	public void update(float timeStep) {
		
		this.camera.onUpdate(timeStep);
		
		for (Entity e : entities.values()) {
			e.onUpdate(timeStep);
		}
	}
	
	public void addEntity(Entity e) {
		this.entities.put(e.getInstanceID(), e);
		
		e.onCreated(this);
	}
	
	public void destroy() {
		
		for (Entity e : entities.values()) {
			e.onDestroyed();
		}
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public Entity getEntity(long entityId) {
		return entities.get(entityId);
	}
	
	public Collection<Entity> getEntities() {
		return entities.values();
	}
	
	public AssetManager getAssets() {
		return assets;
	}
	
	private Logger getLogger() {
		return LOGGER;
	}
}
