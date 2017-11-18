package entities;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import renderEngine.DisplayManager;
import toolBox.Assets;
import toolBox.Calc;
import world.World;

public class Bullet extends DynEntity{
	
	public final float SPEED = 64;

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
			world.client.updateVelocity(intersection.key, intersection.velocity);
			world.client.updateHealth(intersection.key, intersection.health);
			return false;
		}else if(!checkCollision(world, new Vector3f(velocity.x / DisplayManager.FPS, velocity.y / DisplayManager.FPS, velocity.z / DisplayManager.FPS))){
			position.x += velocity.x / DisplayManager.FPS;
			position.y += velocity.y / DisplayManager.FPS;
			position.z += velocity.z / DisplayManager.FPS;
			world.client.updatePosition(key, position);
		}else{
			return false;
		}
		
		return true;
	}

	@Override
	public int getEntityId() {
		return 2;
	}
}
