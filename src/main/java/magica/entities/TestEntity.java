package magica.entities;

import org.joml.Vector3f;

import bengine.Scene;
import bengine.animation.Animation;
import bengine.entities.Entity;
import bengine.rendering.Mesh;
import bengine.rendering.Renderer;

public class TestEntity extends Entity {

	private Animation activeAnimation;
	
	public TestEntity() {
		
	}

	@Override
	public void onCreated(Scene scene) {
		super.onCreated(scene);
		
		this.model = scene.getAssets().getAsset("robotModel");
	}

	@Override
	public void onUpdate(float delta) {
		if (activeAnimation != null) {
			activeAnimation.update(delta);
		}
		
		
	}

	@Override
	public void onDestroyed() {}

	@Override
	public void onRegistered() {}

	@Override
	public void onObjectUpdate() {}

	@Override
	public int getType() {
		return generateTypeId();
	}
	
	@Override
	public Animation getActiveAnimation() {
		return activeAnimation;
	}
}
