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
		if(world.player.position.x < 0){
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
		
		
		//gravity
		velocity.y -= 80 / DisplayManager.FPS;
		
		float forwardSpeed = forwardInput * RUN_SPEED;
		float sidewaysSpeed = sidewaysInput * STRAFE_SPEED;
		
		float xChange = (float) (forwardSpeed * Math.sin(rotation.y) + sidewaysSpeed * Math.sin(rotation.y + Math.PI / 2f));
		float zChange = (float) (forwardSpeed * Math.cos(rotation.y) + sidewaysSpeed * Math.cos(rotation.y + Math.PI / 2f));
		
		if(!checkCollision(world, new Vector3f(0, velocity.y / DisplayManager.FPS, 0), DIMENSIONS)){
			position.y += velocity.y / DisplayManager.FPS;
			grounded = false;
		}else{
			velocity.y = 0;
			grounded = true;
		}
		
		if(!checkCollision(world, new Vector3f(xChange / DisplayManager.FPS, 0, 0), DIMENSIONS)){
			position.x += xChange / DisplayManager.FPS;
		}else if(!checkCollision(world, new Vector3f(xChange / DisplayManager.FPS, 1, 0), DIMENSIONS)){
			position.y += 1;
		}
		if(!checkCollision(world, new Vector3f(0, 0, zChange / DisplayManager.FPS), DIMENSIONS)){
			position.z += zChange / DisplayManager.FPS;
		}else if(!checkCollision(world, new Vector3f(0, 1, zChange / DisplayManager.FPS), DIMENSIONS)){
			position.y += 1;
		}
		
		
		world.client.updatePosition(key, position);
		return true;
	}
}