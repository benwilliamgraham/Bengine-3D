package magica.entities;

import bengine.Scene;
import bengine.entities.Entity;

public class SphereEntity extends Entity {

	public SphereEntity() {
		super();
	}
	
	@Override
	public void onCreated(Scene s) {
		super.onCreated(s);
		
		this.model = s.getAssets().getAsset("sphereModel");
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
