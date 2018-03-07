package bengine.entities;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import bengine.Transform;
import bengine.networking.sync.SyncedObject;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Renderer;

public abstract class Entity extends SyncedObject {
	public Transform transform;
	
	public Vector3f scale, velocity;
	
	public Mesh model;
	
	public Material material;
	
	public Vector3f dimensions;
	public boolean collidable;
	
	@SyncedField("health")
	public float health = 0;
	
	public Entity() {
		this.model = null;
		this.dimensions = new Vector3f(1.0f, 1.0f, 1.0f);
		this.transform = new Transform();
		this.scale = new Vector3f(1.0f, 1.0f, 1.0f);
		this.velocity = new Vector3f(0.0f, 0.0f, 0.0f);
	}
	
	public Entity(Mesh model, Vector3f dimensions, Vector3f position) {
		this.model = model;
		this.dimensions = dimensions;
		this.transform = new Transform(position, new Quaternionf());
		this.scale = new Vector3f(1.0f, 1.0f, 1.0f);
		this.velocity = new Vector3f(0.0f, 0.0f, 0.0f);
	}
	
	public abstract void onCreated();
	
	public abstract void onUpdate(float delta);
	
	public void onDraw(Renderer renderer) {
		
		Matrix4f transformMatrix = new Matrix4f()
				.scale(this.scale)
				.rotate(this.transform.rotation)
				.translateLocal(this.transform.position);
		
		renderer.useShader(this.material.shader);
		
		renderer.getShader().push("baseColor", this.material.baseColor);
		
		if (this.material.texture != null) {
			renderer.getShader().pushTexture(this.material.texture);
		}
		
		this.model.render(transformMatrix);
		
	}
	
	public abstract void onDestroyed();
	
	/*public boolean checkCollision(World world, Vector3f change){
		Vector3f checkPos = new Vector3f(position).add(change);
		
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
		float magnitude = change.length();
		
		float step = 0.9f;
		
		for(float dist = 0; dist <= magnitude; dist += step){
			
			float distRatio = dist / magnitude;
			
			
			
			if(world.checkSolid(new Vector3f(position).add(new Vector3f(0.5f)).add(new Vector3f(change).mul(distRatio)))){
				return true;
			}
		}
		
		return world.checkSolid(new Vector3f(position).add(new Vector3f(0.5f)));
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
	*/
}
