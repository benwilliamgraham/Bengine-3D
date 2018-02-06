package entities;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import toolBox.Assets;
import toolBox.Calc;

public class Bullet extends Entity {
	
	public static final int OBJECT_TYPE = generateTypeId();
	
	public final float SPEED = 128;

	@SyncedField("position")
	public Vector3f networkedPosition = new Vector3f();
	
	public Bullet() {
		super(Assets.cubert, new Vector3f(0.1f, 0.1f, 0.1f), new Vector3f(0, 0, 0));
		this.scale = new Vector3f(0.1f, 0.1f, 0.1f);
	}
	
	public Bullet(Vector3f position, float yaw, float pitch) {
		super(Assets.cubert, new Vector3f(0.1f, 0.1f, 0.1f), position);
		float xVel = (float) (SPEED * Math.sin(yaw) * Math.cos(pitch)); 
		float yVel = (float) (SPEED * Math.sin(-pitch)); 
		float zVel = (float) (SPEED * Math.cos(yaw) * Math.cos(pitch)); 
		
		rotation.x = pitch;
		rotation.y = yaw;
		
		//set velocity
		velocity = new Vector3f(xVel, yVel, zVel);
		
		//move away from player
		position.x += 1.5f * velocity.x / DisplayManager.FPS;
		position.y += 1.5f * velocity.y / DisplayManager.FPS;
		position.z += 1.5f * velocity.z / DisplayManager.FPS;
	}

	@Override
	public void onUpdate(float delta) {
		if (isLocalAuthority()) {
			//movement and detection
			Entity intersection = getIntersection(world, new Vector3f(velocity.x / DisplayManager.FPS, velocity.y / DisplayManager.FPS, velocity.z / DisplayManager.FPS));
			if(intersection != null){
				intersection.health -= 1;
				//world.networkClient.updateEntity(intersection);
				//intersection.velocity = velocity;
				//world.client.updateVelocity(intersection.key, intersection.velocity);
				//world.client.updateHealth(intersection.key, intersection.health);
				this.kill();
			}else{
				float magnitude = Calc.calculateMagnitude(velocity) / DisplayManager.FPS;
				
				float step = 0.9f;
				
				for(float dist = 0; dist <= magnitude; dist += step){
					
					float distRatio = dist / magnitude;
					
					
					Vector3f checkPos = Vector3f.add(position, Vector3f.add(new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(
							velocity.x * distRatio / DisplayManager.FPS,
							velocity.y * distRatio / DisplayManager.FPS,
							velocity.z * distRatio / DisplayManager.FPS), null), null);
					if(world.checkSolid(checkPos)){
						world.destroyVoxel((int) checkPos.x, (int) checkPos.y, (int) checkPos.z);
						RPC("destroyTerrain", RPC.ALL_REMOTES, (int) checkPos.x, (int) checkPos.y, (int) checkPos.z);
						this.kill();
					}
				}
			}
			
			position.x += velocity.x / DisplayManager.FPS;
			position.y += velocity.y / DisplayManager.FPS;
			position.z += velocity.z / DisplayManager.FPS;
			
			networkedPosition = new Vector3f(position);
		} else {
			position = new Vector3f(networkedPosition);
		}
		
	}
	
	@RPC("destroyTerrain")
	public void destroyTerrain(int x, int y, int z) {
		System.out.println("Remote procedure called");
		world.destroyVoxel(x, y, z);
	}

	@Override
	public void onCreated() {
		
	}

	@Override
	public void onDestroyed() {
		
	}

	@Override
	public void onRegistered() {
		
	}

	@Override
	public void onObjectUpdate() {
		
	}

	@Override
	public int getType() {
		return OBJECT_TYPE;
	}

}
