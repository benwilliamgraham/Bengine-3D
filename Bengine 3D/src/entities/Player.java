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
	private float verticalMovement = 0.0f;
	
	private Vector3f targetPos;
	
	public Camera camera;
	
	public Player() {
		super(Assets.cubert, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 2.5f, 1), new Vector3f(1, 2.5f, 1), true);
	
		this.grounded = false;
		this.supported = false;
		this.usingItem = false;
		this.mouseActive = true;
		this.health = 15;
		this.isNetworked = true;
	}
	
	public Player(Vector3f position) {
		super(Assets.cubert, position, new Vector3f(0, 0, 0), new Vector3f(1, 2.5f, 1), new Vector3f(1, 2.5f, 1), true);
		this.grounded = false;
		this.supported = false;
		this.usingItem = false;
		this.mouseActive = true;
		this.health = 15;
		this.isNetworked = true;
		camera = new Camera();
		
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		Mouse.setGrabbed(true);
	}

	public boolean onUpdate(float delta){
		if (!isRemote) {
			int mouseXChange = Display.getWidth() / 2 - Mouse.getX();
			int mouseYChange = Display.getHeight() / 2 - Mouse.getY();
			
			
			if(Keyboard.isKeyDown(Keyboard.KEY_P)){
				mouseActive = !mouseActive;
			}
			
			if(mouseActive){
				yaw += mouseXChange * TURN_SPEED * delta;
				rotation.y = yaw;
				pitch += mouseYChange * TURN_SPEED * delta;
				pitch = (float) Math.min(Math.max(pitch, -Math.PI / 2.5), Math.PI / 2.5);
				
				Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
				Mouse.setGrabbed(true);
			}else{
				Mouse.setGrabbed(false);
			}
			
			//shooting
			/*if(Mouse.isButtonDown(0)){
				if(usingItem == false){
					usingItem = true;
					for(int n = 0; n < 10; n++){
						
						Bullet b = new Bullet(new Vector3f(position.x, position.y + 1.1f, position.z), 
								yaw + randBetween(-0.05f, 0.05f), pitch + randBetween(-0.05f, 0.05f));
						
						world.addDynEntity(b);
					}
				}
			}else{
				usingItem = false;
			}*/
			
			//gravity
			verticalMovement = Math.max(verticalMovement - World.GRAVITY * delta, -World.GRAVITY);
			
			if (checkCollision(world, new Vector3f(0, -0.5f, 0))) {
				verticalMovement = 0.0f;
				grounded = true;
			} else {
				grounded = false;
			}
			
			Vector3f movementVelocity = new Vector3f();
			
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && grounded){
				verticalMovement = JUMP_POWER;
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				movementVelocity.x += (float) (STRAFE_SPEED * Math.cos(yaw));
				movementVelocity.z += (float) -(STRAFE_SPEED * Math.sin(yaw));
			} 
			
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				movementVelocity.x += (float) -(STRAFE_SPEED * Math.cos(yaw));
				movementVelocity.z += (float) (STRAFE_SPEED * Math.sin(yaw));
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				movementVelocity.z += (float) (RUN_SPEED * Math.cos(yaw));
				movementVelocity.x += (float) (RUN_SPEED * Math.sin(yaw));
			} 
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				movementVelocity.z += (float) -(RUN_SPEED * Math.cos(yaw));
				movementVelocity.x += (float) -(RUN_SPEED * Math.sin(yaw));
			}
			
			movementVelocity.y = verticalMovement;
			
			Vector3f.add(velocity, movementVelocity, velocity);
			
			updateMovement(delta);
			
			velocity = new Vector3f();
			
			visible = false;
			camera.position.x = (this.position.x + camera.position.x * 2f) / 3f;
			camera.position.y = (this.position.y + 1.1f + camera.position.y * 2f) / 3f;
			camera.position.z = (this.position.z + camera.position.z * 2f) / 3f;
			
			camera.yaw = ((float) (Math.PI - yaw) + camera.yaw * 1f) / 2f;
			camera.pitch = (pitch + camera.pitch * 1f) / 2f;
			
			if(health <= 0) {
				world.camera = world.spectatorCamera;
				return false;
			}
			return true;
		} else { 
			if (this.targetPos != null && !this.targetPos.equals(this.position)) {
				this.position = Calc.lerp(this.position, this.targetPos, delta * UDPClient.TICKRATE);
			}
		}
		
		return true;
	}
	
	@Override
	public void onNetworkUpdate(UpdateEntityPacket u) {
		//System.out.println("Tick");
		if (u.pos != null) {
			this.position = u.pos;
		}
		
		
		//this.targetPos = u.pos;
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