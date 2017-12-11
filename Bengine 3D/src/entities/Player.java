package entities;

import javax.sound.midi.ControllerEventListener;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.input.ControllerAdapter;
import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import networking.UDPClient;
import networking.packets.UpdateEntityPacket;
import renderEngine.DisplayManager;
import toolBox.Assets;
import toolBox.Calc;
import world.World;

public class Player extends DynEntity{
	
	public static final int type = 0;
	
	public Camera camera;
	
	//define player constants
	private static final float RUN_SPEED = 20;
	private static final float STRAFE_SPEED = 17;
	private static final float TURN_SPEED = 0.4f;
	private static final float JUMP_POWER = 25;
	
	//movement variables
	private boolean grounded;
	private boolean supported;
	private boolean usingItem;
	private boolean mouseActive;
	private float yaw, pitch;
	
	private Vector3f targetPos;
	
	
	
	public Player() {
		super(Assets.cubert, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 3, 1), new Vector3f(1, 3, 1), true);
		this.grounded = false;
		this.supported = false;
		this.usingItem = false;
		this.mouseActive = true;
		this.health = 15;
		this.isNetworked = true;
	}
	
	public Player(Vector3f position) {
		super(Assets.cubert, position, new Vector3f(0, 0, 0), new Vector3f(1, 3, 1), new Vector3f(1, 3, 1), true);
		this.grounded = false;
		this.supported = false;
		this.usingItem = false;
		this.mouseActive = true;
		this.health = 15;
		this.isNetworked = true;
		camera = new Camera();
	}

	public boolean onUpdate(float delta){
		if (!isRemote) {
			int mouseXChange = Display.getWidth() / 2 - Mouse.getX();
			int mouseYChange = Display.getHeight() / 2 - Mouse.getY();
			
			
			if(Keyboard.isKeyDown(Keyboard.KEY_P) && !Keyboard.isRepeatEvent()){
				mouseActive = !mouseActive;
			}
			
			if(mouseActive){
				yaw += mouseXChange * TURN_SPEED / DisplayManager.FPS;
				rotation.y = yaw;
				pitch += mouseYChange * TURN_SPEED / DisplayManager.FPS;
				pitch = (float) Math.min(Math.max(pitch, -Math.PI / 2.5), Math.PI / 2.5);
				
				Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
				Mouse.setGrabbed(true);
			}else{
				Mouse.setGrabbed(false);
			}
			
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
			
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && grounded || Keyboard.isKeyDown(Keyboard.KEY_Q)){
				velocity.y += JUMP_POWER;
			}
			
			//shooting
			if(Mouse.isButtonDown(0)){
				if(usingItem == false){
					usingItem = true;
					
					for(int n = 0; n < 5; n++){
						Bullet b = new Bullet(new Vector3f(position.x, position.y + 1.1f, position.z), 
								yaw + randBetween(-0.05f, 0.05f), pitch + randBetween(-0.05f, 0.05f));
						b.isNetworked = true;
						world.addDynEntity(b);
					}
					
					//world.createDynEntity(new GrapplingHook(this, yaw + randBetween(-0.05f, 0.05f), pitch + randBetween(-0.05f, 0.05f)));
				}
			}else{
				usingItem = false;
			}
			
			//destroy
			if(Keyboard.isKeyDown(Keyboard.KEY_R)){
				int rad = 5;
				for(int x = (int) (position.x - rad); x < position.x + rad; x++){
					for(int y = (int) (position.y - rad); y < position.y + rad; y++){
						for(int z = (int) (position.z - rad); z < position.z + rad; z++){
							world.destroyVoxel(x, y, z);
						}
					}
				}
			}
			
			//gravity
			velocity.y -= World.GRAVITY / DisplayManager.FPS;
			
			float forwardSpeed = forwardInput * RUN_SPEED;
			float sidewaysSpeed = sidewaysInput * STRAFE_SPEED;
			
			float xChange = (float) (forwardSpeed * Math.sin(yaw) + sidewaysSpeed * Math.sin(yaw + Math.PI / 2f)) + velocity.x;
			float zChange = (float) (forwardSpeed * Math.cos(yaw) + sidewaysSpeed * Math.cos(yaw + Math.PI / 2f)) + velocity.z;
			
			if(!checkCollision(world, new Vector3f(0, velocity.y / DisplayManager.FPS, 0))){
				position.y += velocity.y / DisplayManager.FPS;
				grounded = false;
			}else{
				velocity.y *= -0.1f;
				velocity.x *= 0.7f;
				velocity.z *= 0.7f;
				grounded = true;
			}
			
			supported = false;
			if(!checkCollision(world, new Vector3f(xChange / DisplayManager.FPS, 0, 0))){
				position.x += xChange / DisplayManager.FPS;
			}else if(!checkCollision(world, new Vector3f(xChange / DisplayManager.FPS, 1, 0))){
				position.y += 1;
			}else{
				velocity.x *= -0.1f;
				supported = true;
			}
			if(!checkCollision(world, new Vector3f(0, 0, zChange / DisplayManager.FPS))){
				position.z += zChange / DisplayManager.FPS;
			}else if(!checkCollision(world, new Vector3f(0, 1, zChange / DisplayManager.FPS))){
				position.y += 1;
			}else{
				velocity.z *= -0.1f;
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
			camera.position.y = (this.position.y + 1.1f + camera.position.y * 2f) / 3f;
			camera.position.z = (this.position.z + camera.position.z * 2f) / 3f;
			
			camera.yaw = ((float) (Math.PI - yaw) + camera.yaw * 1f) / 2f;
			camera.pitch = (pitch + camera.pitch * 1f) / 2f;
			
			
			
			if(health <= 0){
				world.camera = world.spectatorCamera;
				return false;
			}
		} else {
			if (this.targetPos != null) {
				//this.position = Calc.lerp(this.position, this.targetPos, 1.0f / UDPClient.TICKRATE);
			}
		}
		
		return true;
	}
	@Override
	public void onNetworkUpdate(UpdateEntityPacket u) {
		if (u.pos != null) {
			this.position = u.pos;
			this.rotation = u.rot;
		}
	}

	@Override
	public void onCreate() {
		if (!isRemote) {
			System.out.println("LocalPlayer created");
			this.world.camera = this.camera;
		} else {
			System.out.println("RemotePlayer created");
		}
		
	}
	
	@Override
	public int getEntityType() {
		return type;
	}
}
