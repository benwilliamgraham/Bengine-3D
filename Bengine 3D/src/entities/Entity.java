package entities;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import networking.packets.UpdateEntityPacket;
import world.World;


public abstract class Entity {
	
	public static Map<Integer, Class<? extends Entity>> entities = new HashMap<Integer, Class<? extends Entity>>();
	
	public World world;
	
	public Vector3f position = new Vector3f();
	public  Vector3f rotation, scale, velocity;
	
	public String id;
	
	public String owner;
	
	public boolean isNetworked, isRemote;
	
	public Entity(Vector3f position, Vector3f rotation, Vector3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.velocity = new Vector3f();
		
		this.id = UUID.randomUUID().toString();
	}
	
	public void onCreate() {
		
	}
	
	public void onNetworkUpdate(UpdateEntityPacket p) {
		
	}
	
	public abstract boolean onUpdate(float delta);
	
	public abstract int getEntityType();

	public static void register(Class<? extends Entity> c) {
		try {
			int type = c.getField("type").getInt(null);
			entities.put(type, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
