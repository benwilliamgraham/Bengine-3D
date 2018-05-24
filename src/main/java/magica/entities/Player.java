package magica.entities;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.AABBf;
import org.joml.Intersectionf;
import org.joml.Rayf;
import org.joml.Vector3f;

import bengine.Scene;
import bengine.entities.Camera;
import bengine.entities.Entity;
import bengine.input.Keyboard;
import bengine.input.Mouse;
import bengine.physics.Body;
import bengine.physics.Collider;

public class Player extends Entity {

	public static final int OBJECT_TYPE = generateTypeId();
	
	private static final float collisionSkin = 0.001f;
	
	protected float movementSpeed = 4f;
	
	protected float mouseSensitivty = 2f;
	
	private Body body;
	
	private Camera c;
	
	private float fallingSpeed = 0;
	
	private float terminalVelocity = -35;
	
	private float gravityFactor = 10;
	
	public Player() {
		super();
		this.body = new Body(
				new Collider(
						new AABBf(
								new Vector3f(-0.5f, -1.0f, -0.5f),
								new Vector3f( 0.5f,  1.0f,  0.5f)
				)));
	}
	
	@Override
	public void onCreated(Scene s) {
		super.onCreated(s);
		
		c = s.getCamera();
		
		this.model = s.getAssets().getAsset("cubeModel");
		
		this.transform.scale = new Vector3f(1, 2, 1);
		
		this.transform.position.y += 4;
		body.position.y += 4;
		
		Mouse.lockCursor();
		
		s.getWorld().addBody(body);
	}
	
	@Override
	public void onUpdate(float delta) {
		 Vector3f movement = new Vector3f();
		 
		 if (Keyboard.isKeyDown(GLFW_KEY_W)) {
			movement.add(transform.forwards().mul(movementSpeed * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_S)) {
			movement.add(transform.forwards().mul(-movementSpeed * delta));
		}
			
		if (Keyboard.isKeyDown(GLFW_KEY_D)) {
			 movement.add(transform.right().mul(movementSpeed * delta));
		} else if (Keyboard.isKeyDown(GLFW_KEY_A)) {
			 movement.add(transform.right().mul(-movementSpeed * delta));
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
			movementSpeed = 5;
		} else {
			movementSpeed = 3.5f;
		}
		
		if (body.testRay(new Rayf(0, -1, 0, 0, -1, 0), 0.1f)) { //is grounded.
			fallingSpeed = Math.max(fallingSpeed, 0);
			
			if (Keyboard.isKeyDown(GLFW_KEY_SPACE)) {
				fallingSpeed += 10;
			}
		} else {
			//fallingSpeed = -10;
			fallingSpeed -= gravityFactor * (2.0f - (fallingSpeed / terminalVelocity)) * delta;
		}
		
		movement.y = fallingSpeed * delta;
		
		body.move(movement);
		 
		transform.position = body.position;
		
		transform.rotation.rotateY(-Mouse.getDX() * mouseSensitivty * delta);
		c.transform.rotation.rotateY(-Mouse.getDX() * mouseSensitivty * delta);
		c.transform.rotation.rotateLocalX(-Mouse.getDY() * mouseSensitivty * delta);
		 
		c.transform.position = new Vector3f(this.transform.position).add(0, 2, 0);
	}

	@Override
	public void onDestroyed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegistered() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeregistered() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getType() {
		return OBJECT_TYPE;
	}
	
}
