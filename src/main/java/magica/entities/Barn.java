package magica.entities;

import org.joml.Vector3f;

import bengine.Scene;
import bengine.entities.Entity;
import bengine.rendering.Material;

public class Barn extends Entity {

	public Barn() {
		
	}
	
	@Override
	public void onCreated(Scene s) {
		super.onCreated(s);
		
		this.model = s.getAssets().getAsset("barnModel");
		
		this.material = new Material(s.getAssets().getAsset("defaultShader"));
		this.material.texture = s.getAssets().getAsset("barnTexture");
		
		this.model.bindMaterial(this.material);
		
		this.transform.scale = new Vector3f(20.0f, 20.0f, 20.0f);
	}
	
	@Override
	public void onUpdate(float delta) {
		
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
		return 0;
	}

}
