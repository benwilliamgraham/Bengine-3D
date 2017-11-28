package entities;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import toolBox.Calc;
import world.World;

public abstract class DynEntity extends Entity{

	public TexturedModel model;
	public boolean visible = true;
	public Vector3f velocity = new Vector3f(0, 0, 0);
	public Vector3f dimensions;
	public boolean collidable;
	public float health = 0;
	
	public DynEntity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale, Vector3f dimensions, boolean collidable) {
		super(position, rotation, scale);
		this.model = model;
		this.dimensions = dimensions;
		this.collidable = collidable;
	}
	
	public abstract boolean update(World world);
	
	public boolean checkCollision(World world, Vector3f change, Vector3f dimensions){
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
	
	public boolean checkCollision(World world, Vector3f change){
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
		world.lockMap = true;
		ents.putAll(world.dynEntities);
		world.lockMap = false;
		
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
	
	public float randBetween(float min, float max){
		return (float) Math.random() * (max - min) + min;
	}
}
