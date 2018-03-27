package magica.entities;

import bengine.Scene;
import bengine.entities.Entity;

public class CubeEntity extends Entity {

	public CubeEntity() {
		super();
	}
	
	@Override
	public void onCreated(Scene s) {
		super.onCreated(s);
		
		this.model = s.getAssets().getAsset("cubeModel");
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
	public void onDeregistered() {
		
	}
	
	@Override
	public int getType() {
		return generateTypeId();
	}
	
}
