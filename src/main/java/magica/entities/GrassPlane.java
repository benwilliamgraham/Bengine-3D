package magica.entities;

import org.joml.AABBf;
import org.joml.Planef;
import org.joml.Vector3f;

import bengine.Scene;
import bengine.entities.Entity;
import bengine.physics.Body;
import bengine.physics.Collider;
import bengine.rendering.Material;

public class GrassPlane extends Entity {
	
	private Body body;
	
	public GrassPlane() {
		super();
		this.body = new Body(
				new Collider(
						new AABBf(
								new Vector3f(-10f, -0.05f, -10f),
								new Vector3f( 10f,  0.05f,  10f)
				)));
	}
	
	@Override
	public void onCreated(Scene scene) {
		super.onCreated(scene);
		
		this.model = scene.getAssets().getAsset("planeModel");
		
		this.material = new Material(scene.getAssets().getAsset("defaultShader"));
		this.material.texture = scene.getAssets().getAsset("grassTexture");
		
		this.model.bindMaterial(this.material);
		
		this.transform.scale = new Vector3f(10.0f, 0.1f, 10.0f);
		
		scene.getWorld().addBody(body);
	}
	
	@Override
	public void onUpdate(float delta) {
		// TODO Auto-generated method stub
		
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
	public void onDeregistered() {
		
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}
}
