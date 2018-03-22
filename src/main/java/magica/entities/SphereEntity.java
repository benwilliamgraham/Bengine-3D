package magica.entities;

import org.joml.Vector3f;

import bengine.Scene;
import bengine.entities.Entity;
import bengine.rendering.Material;

public class SphereEntity extends Entity {

	public SphereEntity() {
		super();
	}
	
	@Override
	public void onCreated(Scene s) {
		super.onCreated(s);
		
		this.model = s.getAssets().getAsset("sphereModel");
		
		this.material = new Material(s.getAssets().getAsset("defaultShader"));
		this.material.ambientColor = new Vector3f(1.0f, 0.0f, 0.0f);
		
		this.model.bindMaterial(this.material);
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
		return generateTypeId();
	}
	
}
