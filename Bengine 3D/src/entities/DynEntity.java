package entities;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import toolBox.Calc;
import world.World;

public abstract class DynEntity extends Entity{

	private static final float collisionSkin = 0.05f; 
	
	public TexturedModel model;
	public boolean visible = true;
	public Vector3f dimensions;
	public boolean collidable;
	public float health = 0;
	
	protected int collisionSteps = 3;
	
	public DynEntity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale, Vector3f dimensions, boolean collidable) {
		super(position, rotation, scale);
		this.model = model;
		this.dimensions = dimensions;
		this.collidable = collidable;
	}
	
	public void updateMovement(float delta) {
		
		Vector3f deltaPos = (Vector3f) new Vector3f(velocity).scale(delta);
		
		outer_loop:
			for (float oy = -(dimensions.y / 2.0f); oy <= (dimensions.y / 2.0f); oy += (dimensions.y / collisionSteps)) {
				for (float oz = -(dimensions.z / 2.0f); oz <= (dimensions.z / 2.0f); oz += (dimensions.z / collisionSteps)) {
					if (deltaPos.x > 0) {
						if (world.checkSolid(position.x + deltaPos.x + dimensions.x / 2.0f, position.y + oy, position.z + oz)) {
							System.out.println(deltaPos.x);
							deltaPos.x = world.getClosestVoxelPos(position.x + deltaPos.x + dimensions.x / 2.0f, position.y + oy, position.z + oz).x 
									   - (position.x + (dimensions.x / 2.0f - collisionSkin));
							velocity.x = 0;
							break outer_loop;
						}
					} else if (deltaPos.x < 0) {
						if (world.checkSolid(position.x + deltaPos.x - dimensions.x / 2.0f, position.y + oy, position.z + oz)) {
							deltaPos.x = world.getClosestVoxelPos(position.x + deltaPos.x - dimensions.x / 2.0f, position.y + oy, position.z + oz).x 
									   - (position.x - (dimensions.x / 2.0f + collisionSkin));
							velocity.x = Math.max(0, velocity.x);
							break outer_loop;
						}
					}
				}
			}
		
		outer_loop:
			for (float oy = -(dimensions.y / 2.0f); oy <= (dimensions.y / 2.0f); oy += (dimensions.y / collisionSteps)) {
				for (float ox = -(dimensions.x / 2.0f); ox <= (dimensions.x / 2.0f); ox += (dimensions.x / collisionSteps)) {
					if (deltaPos.z > 0) {
						if (world.checkSolid(position.x + ox, position.y + oy, position.z + deltaPos.z + dimensions.z / 2.0f)) {
							deltaPos.z = world.getClosestVoxelPos(position.x + ox, position.y + oy, position.z + deltaPos.z + dimensions.z / 2.0f).z
									   - (position.z + (dimensions.z / 2.0f + collisionSkin));
							velocity.z = Math.min(0, velocity.z);
							break outer_loop;
						}
					} else if (deltaPos.z < 0) {
						if (world.checkSolid(position.x + ox, position.y + oy, position.z + deltaPos.z - dimensions.z / 2.0f)) {
							deltaPos.z = world.getClosestVoxelPos(position.x + ox, position.y + oy, position.z + deltaPos.z - dimensions.z / 2.0f).z
									   - (position.z - (dimensions.z / 2.0f + collisionSkin));
							velocity.z = Math.max(0, velocity.z);
							break outer_loop;
						}
					}
				}
			}
			
		outer_loop:
			for (float ox = -(dimensions.x / 2.0f); ox <= (dimensions.x / 2.0f); ox += (dimensions.x / collisionSteps)) {
				for (float oz = -(dimensions.z / 2.0f); oz <= (dimensions.z / 2.0f); oz += (dimensions.z / collisionSteps)) {
					if (deltaPos.y > 0) {
						if (world.checkSolid(position.x + ox, position.y + deltaPos.y + dimensions.y / 2.0f, position.z + oz)) {
							deltaPos.y = 0;
							velocity.y = Math.min(0, deltaPos.y);
							break outer_loop;
						}
					} else if (deltaPos.y < 0) {
						if (world.checkSolid(position.x + ox, position.y + deltaPos.y - dimensions.y / 2.0f, position.z + oz)) {
							deltaPos.y = 0;
							velocity.y = Math.max(0, velocity.y);
							break outer_loop;
						}
					}
				}
			}
		
		
		
		//Check along x axis
		/*outer_loop: 
		for (float oy = -(dimensions.y / 2.0f); oy <= (dimensions.y / 2.0f); oy += dimensions.y / collisionSteps) {
			for (float oz = -(dimensions.z / 2.0f); oz <= (dimensions.z / 2.0f); oz += dimensions.z / collisionSteps) {
				if (deltaPos.x > 0) {
					float deltaX = position.x + deltaPos.x + dimensions.x / 2.0f;
					if (world.checkSolid(deltaX, position.y + oy, position.y + oz)) {
						deltaPos.x = (deltaX) - ((int)(deltaX) + 0.1f); //This is setting the delta position equal to the distance from the edge of the player to the edge of the block, minus a collision skin of 0.1  
						velocity.x = 0;
						break outer_loop;
					}
				} else if (deltaPos.x < 0) {
					float deltaX = position.x + deltaPos.x - dimensions.x / 2.0f;
					if (world.checkSolid(deltaX, position.y + oy, position.y + oz)) {
						deltaPos.x = (deltaX) - ((int)(deltaX) + 0.1f); //This is setting the delta position equal to the distance from the edge of the player to the edge of the block, minus a collision skin of 0.1  
						velocity.x = 0;
						break outer_loop;
					}
				}
			}
		}*/
		
		
		Vector3f.add(position, deltaPos, position);
	}
	
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
	
	public DynEntity getIntersection(World world, Vector3f change){
		Map<String, DynEntity> ents = new HashMap<String, DynEntity>();
		ents.putAll(world.entities);
		
		//calculate center positions
		float xCenter = position.x + change.x / 2f;
		float yCenter = position.y + change.y / 2f;
		float zCenter = position.z + change.z / 2f;
		
		for (Map.Entry<String, DynEntity> entity : ents.entrySet()) {
			//make sure collisions are possible
			if(!entity.getValue().collidable) continue;
			float xDist = Math.abs(entity.getValue().position.x - xCenter) - entity.getValue().dimensions.x / 2f;
			float yDist = Math.abs(entity.getValue().position.y - yCenter) - entity.getValue().dimensions.y / 2f ;
			float zDist = Math.abs(entity.getValue().position.z - zCenter) - entity.getValue().dimensions.z / 2f ;
			
			if(xDist <= 0 && yDist <= 0 && zDist <=0){
				return entity.getValue();
			}
		}
		
		return null;
	}
	
	float randBetween(float min, float max){
		return (float) Math.random() * (max - min) + min;
	}
}
