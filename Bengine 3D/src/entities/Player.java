package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import renderEngine.DisplayManager;
import world.World;

public class Player{
	
	//define player constants
	private static final Vector3f DIMENSIONS = new Vector3f(0.98f, 1.98f, 0.98f);
	private static final float RUN_SPEED = 20;
	private static final float STRAFE_SPEED = 17;
	private static final float TURN_SPEED = 0.8f;
	private static final float JUMP_POWER = 25;
	
	//movement variables
	private boolean grounded;

	public Vector3f position, rotation, velocity;
	public Camera camera;
	
	public Player(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
		this.velocity = new Vector3f(0, 0, 0);
		this.grounded = false;
		camera = new Camera();
		
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		Mouse.setGrabbed(true);
	}

	public void update(World world){
		int mouseXChange = Display.getWidth() / 2 - Mouse.getX();
		int mouseYChange = Display.getHeight() / 2 - Mouse.getY();
		
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		Mouse.setGrabbed(true);
		
		rotation.y += mouseXChange * TURN_SPEED / DisplayManager.FPS;
		rotation.x += mouseYChange * TURN_SPEED / DisplayManager.FPS;
		rotation.x = (float) Math.min(Math.max(rotation.x, -Math.PI / 2.5), Math.PI / 2.5);
		
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		
		float forwardInput = 0;
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
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
		
		if(!checkCollision(world, new Vector3f(xChange / DisplayManager.FPS, 0, 0), DIMENSIONS)){
			position.x += xChange / DisplayManager.FPS;
		}
		if(!checkCollision(world, new Vector3f(0, 0, zChange / DisplayManager.FPS), DIMENSIONS)){
			position.z += zChange / DisplayManager.FPS;
		}
		
		if(!checkCollision(world, new Vector3f(0, velocity.y / DisplayManager.FPS, 0), DIMENSIONS)){
			position.y += velocity.y / DisplayManager.FPS;
			grounded = false;
		}else{
			velocity.y = 0;
			grounded = true;
		}
		
		camera.position.x = (this.position.x + camera.position.x * 2f) / 3f;
		camera.position.y = (this.position.y + 1f + camera.position.y * 2f) / 3f;
		camera.position.z = (this.position.z + camera.position.z * 2f) / 3f;
				
		camera.yaw = ((float) (Math.PI - rotation.y) + camera.yaw * 3f) / 4f;
		camera.pitch = (rotation.x + camera.pitch * 3f) / 4f;
	}
	
	
	public boolean checkCollision(World world, Vector3f change, Vector3f dimensions){
		Vector3f checkPos = Vector3f.add(position, change, null);
		
		for(float x = checkPos.x - (dimensions.x / 2f); x <= checkPos.x + (dimensions.x / 2f); x++){
			for(float y = checkPos.y - (dimensions.y / 2f); y <= checkPos.y + (dimensions.y / 2f); y++){
				for(float z = checkPos.z - (dimensions.z / 2f); z <= checkPos.z + (dimensions.z / 2f); z++){
					if(world.checkSolid((int) (x + 0.5f), (int) (y + 0.5f), (int) (z + 2f))){
						return true;
					}
				}
			}
		}
		return false;
	}
}
