package entities;

import javax.sound.midi.ControllerEventListener;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.input.ControllerAdapter;
import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import renderEngine.DisplayManager;
import toolBox.Assets;
import world.World;

public class NPC extends DynEntity{
	
	//define player constants
	private static final Vector3f DIMENSIONS = new Vector3f(1f, 2f, 1f);
	private static final float RUN_SPEED = 20;
	private static final float STRAFE_SPEED = 17;
	private static final float JUMP_POWER = 25;
	
	//movement variables
	private boolean grounded;
	
	public Vector3f velocity;
	
	public NPC(Vector3f position) {
		super(Assets.cubert, position, new Vector3f(0, 0, 0), new Vector3f(1, 2f, 1));
		this.velocity = new Vector3f(0, 0, 0);
		this.grounded = false;
	}

	public boolean update(World world){
		
		float forwardInput = 0;
		if(world.player.position.){
			forwardInput = 1;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			forwardInput = -1;
		}
		
		float sidewaysInput = 0;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			sidewaysInput = 1;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			sidewaysInput = -1;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && grounded){
			velocity.y += JUMP_POWER;
		}
		
		//shooting
		if(Mouse.isButtonDown(0)){
			if(usingItem == false){
				usingItem = true;
				for(int n = 0; n < 10; n++){
					world.createDynEntity(new Bullet(new Vector3f(position.x, position.y + 0.6f, position.z), 
							yaw + randBetween(-0.05f, 0.05f), pitch + randBetween(-0.05f, 0.05f)));
				}
			}
		}else{
			usingItem = false;
		}
		
		//gravity
		velocity.y -= 80 / DisplayManager.FPS;
		
		float forwardSpeed = forwardInput * RUN_SPEED;
		float sidewaysSpeed = sidewaysInput * STRAFE_SPEED;
		
		float xChange = (float) (forwardSpeed * Math.sin(yaw) + sidewaysSpeed * Math.sin(yaw + Math.PI / 2f));
		float zChange = (float) (forwardSpeed * Math.cos(yaw) + sidewaysSpeed * Math.cos(yaw + Math.PI / 2f));
		
		if(!checkCollision(world, new Vector3f(0, velocity.y / DisplayManager.FPS, 0), DIMENSIONS)){
			position.y += velocity.y / DisplayManager.FPS;
			grounded = false;
		}else{
			velocity.y = 0;
			grounded = true;
		}
		
		supported = false;
		if(!checkCollision(world, new Vector3f(xChange / DisplayManager.FPS, 0, 0), DIMENSIONS)){
			position.x += xChange / DisplayManager.FPS;
		}else if(!checkCollision(world, new Vector3f(xChange / DisplayManager.FPS, 1, 0), DIMENSIONS)){
			position.y += 1;
		}else{
			supported = true;
		}
		if(!checkCollision(world, new Vector3f(0, 0, zChange / DisplayManager.FPS), DIMENSIONS)){
			position.z += zChange / DisplayManager.FPS;
		}else if(!checkCollision(world, new Vector3f(0, 1, zChange / DisplayManager.FPS), DIMENSIONS)){
			position.y += 1;
		}else{
			supported = true;
		}
		
		/*third person
		float camDist = 2f;
		camera.position.x = (this.position.x - camDist * (float) Math.sin(yaw) + camera.position.x * 2f) / 3f;
		camera.position.y = (this.position.y + 1f + camera.position.y * 2f) / 3f;
		camera.position.z = (this.position.z - camDist * (float) Math.cos(yaw) + camera.position.z * 2f) / 3f;
		
		camera.yaw = ((float) (Math.PI - yaw) + camera.yaw * 3f) / 4f;
		camera.pitch = (pitch + camera.pitch * 3f) / 4f;
		*/
		
		visible = false;
		camera.position.x = (this.position.x + camera.position.x * 2f) / 3f;
		camera.position.y = (this.position.y + 0.9f + camera.position.y * 2f) / 3f;
		camera.position.z = (this.position.z + camera.position.z * 2f) / 3f;
		
		camera.yaw = ((float) (Math.PI - yaw) + camera.yaw * 3f) / 4f;
		camera.pitch = (pitch + camera.pitch * 3f) / 4f;
		
		
		world.client.updatePosition(key, position);
		return true;
	}
}