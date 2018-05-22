package magica.entities;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.AABBf;
import org.joml.Vector3f;

import bengine.Scene;
import bengine.entities.Entity;
import bengine.input.Keyboard;
import bengine.physics.Body;
import bengine.physics.Collider;

public class CubeEntity extends Entity {

	private Body body;
	
	public CubeEntity() {
		super();
		this.body = new Body(
				new Collider(new AABBf(
						new Vector3f(-1, -1, -1),
						new Vector3f( 1,  1,  1)
				)));
	}
	
	@Override
	public void onCreated(Scene s) {
		super.onCreated(s);
		
		this.model = s.getAssets().getAsset("cubeModel");
		
		s.getWorld().addBody(body);
	}
	
	@Override
	public void onUpdate(float delta) {
		
		if (Keyboard.isKeyDown(GLFW_KEY_UP)) {
			this.transform.position.z += 4.0f * delta;
		} else if (Keyboard.isKeyDown(GLFW_KEY_DOWN)) {
			this.transform.position.z -= 4.0f * delta;
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_RIGHT)) {
			this.transform.position.x += 4.0f * delta;
		} else if (Keyboard.isKeyDown(GLFW_KEY_LEFT)) {
			this.transform.position.x -= 4.0f * delta;
		}
		
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
		
	}
	
	@Override
	public int getType() {
		return generateTypeId();
	}
	
}
