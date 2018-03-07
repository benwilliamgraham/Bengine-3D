package magica.entities;

import org.joml.Vector3f;

import bengine.entities.Entity;
import bengine.rendering.Mesh;
import bengine.rendering.Renderer;
import magica.Assets;

public class TestEntity extends Entity {

	public TestEntity() {
		super(Assets.monkeyMesh, new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 0.0f, -2.0f));
	}

	@Override
	public void onCreated() {
		
	}

	@Override
	public void onUpdate(float delta) {
		
		transform.rotate(new Vector3f(0, (float) Math.PI / 4.0f * delta, 0));
		
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
