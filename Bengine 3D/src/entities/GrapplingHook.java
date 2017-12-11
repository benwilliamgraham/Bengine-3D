package entities;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import renderEngine.DisplayManager;
import toolBox.Assets;
import toolBox.Calc;
import world.World;

public class GrapplingHook extends DynEntity{
	
	public final float SPEED = 64;
	
	public DynEntity boss;
	public Vector3f endPosition;

	public GrapplingHook(DynEntity boss, float yaw, float pitch) {
		super(Assets.cubert, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0, 0, 0), false);
		this.boss = boss;
		
		float xVel = (float) (SPEED * Math.sin(yaw) * Math.cos(pitch));
		float yVel = (float) (SPEED * Math.sin(-pitch));
		float zVel = (float) (SPEED * Math.cos(yaw) * Math.cos(pitch)); 
		
		//set velocity
		velocity = new Vector3f(xVel, yVel, zVel);
		
		endPosition = new Vector3f(boss.position);
	}

	public boolean update(World world) {
		Vector3f toBoss = new Vector3f(boss.position.x - endPosition.x, boss.position.y - endPosition.y, boss.position.z - endPosition.z);
		
		
		scale.z = Calc.calculateMagnitude(toBoss);
		rotation.y = (float) Math.atan(toBoss.x / toBoss.z);
		float base = (float) Math.sqrt(toBoss.x * toBoss.x + toBoss.z + toBoss.z);
		rotation.x = (float) (16 * Math.sin(rotation.y));
		rotation.z = (float) (16 * Math.cos(rotation.y));
		
		position.x = (boss.position.x + endPosition.x) / 2f;
		position.y = (boss.position.y + endPosition.y) / 2f;
		position.z = (boss.position.z + endPosition.z) / 2f;
		
		return true;
	}

	@Override
	public boolean onUpdate(float delta) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getEntityType() {
		// TODO Auto-generated method stub
		return 0;
	}
}
