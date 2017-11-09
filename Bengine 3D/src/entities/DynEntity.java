package entities;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import world.World;

public abstract class DynEntity extends Entity{

	public TexturedModel model;
	public String key = "TEMP";
	
	public DynEntity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
		super(position, rotation, scale);
		this.model = model;
	}
	
	public abstract boolean update(World world, String key);
	
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
}
