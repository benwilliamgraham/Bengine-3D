package magica.entities;

import org.joml.Vector3f;

import bengine.Scene;
import bengine.entities.Entity;
import bengine.rendering.Material;

public class GrassPlane extends Entity {
	
	public GrassPlane() {
		
	}
	
	@Override
	public void onCreated(Scene scene) {
		super.onCreated(scene);
		
		this.model = scene.getAssets().getAsset("planeModel");
		
		this.material = new Material(scene.getAssets().getAsset("defaultShader"));
		this.material.texture = scene.getAssets().getAsset("grassTexture");
		
		this.model.bindMaterial(this.material);
		
		this.transform.scale = new Vector3f(10.0f, 1.0f, 10.0f);
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
