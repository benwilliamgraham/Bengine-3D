package magica.entities;

import org.joml.Vector3f;

import bengine.entities.Entity;
import bengine.rendering.Mesh;
import magica.Assets;

public class TestEntity extends Entity {
	/*public TestEntity() {
		super(Assets.squareMesh, new Vector3f(0,0,0), new Vector3f(0,0,0));
		this.material = Assets.testMaterial;
	
	}*/

	public TestEntity() {
		super(Assets.squareMesh, new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -2.0f));
	}

	@Override
	public void onCreated() {
		
	}

	@Override
	public void onUpdate(float delta) {
		
		
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
