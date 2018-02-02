package entities;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import networking.sync.SyncedObject;
import toolBox.Calc;
import world.World;


public abstract class Entity extends SyncedObject {
	
	private static final float collisionSkin = 0.05f; 
	
	public World world;
	
	public Vector3f position = new Vector3f();
	public  Vector3f rotation, scale, velocity;
	
	public TexturedModel model;
	
	public boolean visible = true;
	public Vector3f dimensions;
	public boolean collidable;
	
	@SyncedField("health")
	public float health = 0;
	
	protected int collisionSteps = 3;
	
	public Entity(TexturedModel model, Vector3f dimensions, Vector3f position) {
		this.position = position;
		this.rotation = new Vector3f(0, 0, 0);
		this.scale = new Vector3f(1, 1, 1);
		this.velocity = new Vector3f(0, 0, 0);
		this.model = model;
		this.dimensions = dimensions;
		this.collidable = true;
		this.visibility.allowAll = true;
	}
	
	public abstract void onCreated();
	
	public abstract void onUpdate(float delta);
	
	public abstract void onDestroyed();
	
	public boolean checkCollision(World world, Vector3f change){
		Vector3f checkPos = Vector3f.add(position, change, null);
		
		for(float x = checkPos.x - (dimensions.x / 2f); x <= checkPos.x + (dimensions.x / 2f); x += 1f){
			for(float y = checkPos.y - (dimensions.y / 2f); y <= checkPos.y + (dimensions.y / 2f); y += 1f){
				for(float z = checkPos.z - (dimensions.z / 2f); z <= checkPos.z + (dimensions.z / 2f); z += 1f){
					if(world.checkSolid((int) (x + 0.5f), (int) (y + 0.5f), (int) (z + 0.5f))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean checkCollisionParticle(World world, Vector3f change){
		float magnitude = Calc.calculateMagnitude(change);
		
		float step = 0.9f;
		
		for(float dist = 0; dist <= magnitude; dist += step){
			
			float distRatio = dist / magnitude;
			
			if(world.checkSolid(Vector3f.add(position, Vector3f.add(new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(
					change.x * distRatio,
					change.y * distRatio,
					change.z * distRatio), null), null))){
				return true;
			}
		}
		
		return world.checkSolid(Vector3f.add(position, Vector3f.add(new Vector3f(0.5f, 0.5f, 0.5f), change, null), null));
	}
	
	public Entity getIntersection(World world, Vector3f change){
		
		//calculate center positions
		float xCenter = position.x + change.x / 2f;
		float yCenter = position.y + change.y / 2f;
		float zCenter = position.z + change.z / 2f;
		
		for (Entity entity : world.entities.values()) {
			//make sure collisions are possible
			if(!entity.collidable) continue;
			float xDist = Math.abs(entity.position.x - xCenter) - entity.dimensions.x / 2f;
			float yDist = Math.abs(entity.position.y - yCenter) - entity.dimensions.y / 2f;
			float zDist = Math.abs(entity.position.z - zCenter) - entity.dimensions.z / 2f;
			
			if(xDist <= 0 && yDist <= 0 && zDist <=0){
				return entity;
			}
		}
		
		return null;
	}
	
	public void kill() {
		this.world.destroyEntity(getInstanceID());
	}
	
	float randBetween(float min, float max){
		return (float) Math.random() * (max - min) + min;
	}
}
