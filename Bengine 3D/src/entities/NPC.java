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
	private static final float RUN_SPEED = 4;
	private static final float STRAFE_SPEED = 17;
	private static final float JUMP_POWER = 25;
	
	//movement variables
	private boolean grounded;
		
	public NPC(Vector3f position) {
		super(Assets.cubert, position, new Vector3f(0, 0, 0), new Vector3f(1, 2.5f, 1), new Vector3f(1, 2.5f, 1), true);
		this.velocity = new Vector3f(0, 0, 0);
		this.grounded = false;
	}

	public boolean update(World world){
		
		float forwardInput = 1;
		
		float sidewaysInput = 0;
		if(world.player.position.x != position.x || world.player.position.z != position.z){
			rotation.y = (float) Math.atan2(world.player.position.x - position.x, world.player.position.z - position.z);
		}
		
		//gravity
		velocity.y -= World.GRAVITY / DisplayManager.FPS;
		
		float forwardSpeed = forwardInput * RUN_SPEED;
		float sidewaysSpeed = sidewaysInput * STRAFE_SPEED;
		
		float xChange = (float) (forwardSpeed * Math.sin(rotation.y) + sidewaysSpeed * Math.sin(rotation.y + Math.PI / 2f)) + velocity.x;
		float zChange = (float) (forwardSpeed * Math.cos(rotation.y) + sidewaysSpeed * Math.cos(rotation.y + Math.PI / 2f)) + velocity.z;
		
		if(!checkCollision(world, new Vector3f(0, velocity.y / DisplayManager.FPS, 0), dimensions)){
			position.y += velocity.y / DisplayManager.FPS;
			grounded = false;
		}else{
			velocity.y *= -0.1f;
			grounded = true;
			//friction
			velocity.x *= 0.95f;
			velocity.z *= 0.95f;
		}
		
		//shooting
		if((int)(Math.random() * 60) == 0){
			for(int n = 0; n < 10; n++){
				world.createDynEntity(new Bullet(new Vector3f(position.x, position.y + 0.6f, position.z), 
						rotation.y + randBetween(-0.05f, 0.05f), 0 + randBetween(-0.05f, 0.05f)));
			}
		}
		
		boolean collide = false;
		if(!checkCollision(world, new Vector3f(xChange / DisplayManager.FPS, 0, 0), dimensions)){
			position.x += xChange / DisplayManager.FPS;
		}else{
			collide = true;
			velocity.x *= -0.1f;
		}
		if(!checkCollision(world, new Vector3f(0, 0, zChange / DisplayManager.FPS), dimensions)){
			position.z += zChange / DisplayManager.FPS;
		}else{
			collide = true;
			velocity.z *= -0.1f;
		}
		
		if(collide && grounded){
			velocity.y = randBetween(JUMP_POWER / 2f, JUMP_POWER);
		}
		
		world.client.updatePosition(key, position);
		return true;
	}
}