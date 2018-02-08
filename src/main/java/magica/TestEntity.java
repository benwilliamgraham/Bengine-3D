package magica;

import org.joml.Vector3f;

import bengine.entities.Entity;
import bengine.rendering.Mesh;

public class TestEntity extends Entity {
	/*public TestEntity() {
		super(Assets.squareMesh, new Vector3f(0,0,0), new Vector3f(0,0,0));
		this.material = Assets.testMaterial;
	
	}*/

	public TestEntity(Mesh model, Vector3f dimensions, Vector3f position) {
		super(model, dimensions, position);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreated() {
		
	}

	@Override
	public void onUpdate(float delta) {
		// TODO Auto-generated method stub
		
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
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}
}
