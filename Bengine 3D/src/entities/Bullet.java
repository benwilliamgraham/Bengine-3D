package entities;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import renderEngine.DisplayManager;
import toolBox.Assets;
import toolBox.Calc;
import world.World;

public class Bullet extends DynEntity{
	
	public static final int type = 2;
	
	public final float SPEED = 128;

	public Bullet(Vector3f position, float yaw, float pitch) {
		super(Assets.cubert, position, new Vector3f(0, 0, 0), new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(0, 0, 0), false);
		float xVel = (float) (SPEED * Math.sin(yaw) * Math.cos(pitch)); 
		float yVel = (float) (SPEED * Math.sin(-pitch)); 
		float zVel = (float) (SPEED * Math.cos(yaw) * Math.cos(pitch)); 
		
		//set velocity
		velocity = new Vector3f(xVel, yVel, zVel);
		
		//move away from player
		position.x += 1.5f * velocity.x / DisplayManager.FPS;
		position.y += 1.5f * velocity.y / DisplayManager.FPS;
		position.z += 1.5f * velocity.z / DisplayManager.FPS;
	}

	public boolean update(World world) {
		//movement and detection
		DynEntity intersection = getIntersection(world, new Vector3f(velocity.x / DisplayManager.FPS, velocity.y / DisplayManager.FPS, velocity.z / DisplayManager.FPS));
		if(intersection != null){
			intersection.health -= 1;
			intersection.velocity = velocity;
			//world.client.updateVelocity(intersection.key, intersection.velocity);
			//world.client.updateHealth(intersection.key, intersection.health);
			return false;
		}else{
			float magnitude = Calc.calculateMagnitude(velocity) / DisplayManager.FPS;
			
			float step = 0.9f;
			
			for(float dist = 0; dist <= magnitude; dist += step){
				
				float distRatio = dist / magnitude;
				
				
				Vector3f checkPos = Vector3f.add(position, Vector3f.add(new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(
						velocity.x * distRatio / DisplayManager.FPS,
						velocity.y * distRatio / DisplayManager.FPS,
						velocity.z * distRatio / DisplayManager.FPS), null), null);
				if(world.checkSolid(checkPos)){
					world.destroyVoxel((int) checkPos.x, (int) checkPos.y, (int) checkPos.z);
					return false;
				}
			}
		}
		
		position.x += velocity.x / DisplayManager.FPS;
		position.y += velocity.y / DisplayManager.FPS;
		position.z += velocity.z / DisplayManager.FPS;
		
		return true;
	}

	@Override
	public int getEntityType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public boolean onUpdate(float delta) {
		// TODO Auto-generated method stub
		return false;
	}
}
