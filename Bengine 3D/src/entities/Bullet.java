package entities;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import renderEngine.DisplayManager;
import toolBox.Assets;
import world.World;

public class Bullet extends DynEntity{
	
	public final float SPEED = 60;

	public Bullet(Vector3f position, float yaw, float pitch) {
		super(Assets.cubert, position, new Vector3f(0, 0, 0), new Vector3f(0.1f, 0.1f, 0.1f));
		float xVel = (float) (SPEED * Math.sin(yaw) * Math.cos(pitch)); 
		float yVel = (float) (SPEED * Math.sin(-pitch)); 
		float zVel = (float) (SPEED * Math.cos(yaw) * Math.cos(pitch)); 
		
		velocity = new Vector3f(xVel, yVel, zVel);
	}

	public boolean update(World world) {
		//movement and detection
		if(!checkCollision(world, new Vector3f(velocity.x / DisplayManager.FPS, velocity.y / DisplayManager.FPS, velocity.z / DisplayManager.FPS))){
			position.x += velocity.x / DisplayManager.FPS;
			position.y += velocity.y / DisplayManager.FPS;
			position.z += velocity.z / DisplayManager.FPS;
			world.client.updatePosition(key, position);
		}else{
			return false;
		}
		
		return true;
	}
	
	public boolean checkCollision(World world, Vector3f change){
		return world.checkSolid(Vector3f.add(position, Vector3f.add(new Vector3f(0.5f, 0.5f, 0.5f), change, null), null));
	}

}
