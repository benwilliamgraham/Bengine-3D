package magica.entities;

import javax.swing.JFrame;

import org.joml.Vector3f;

import bengine.Scene;
import bengine.animation.Animation;
import bengine.animation.Animator;
import bengine.entities.Entity;
import bengine.rendering.Material;

import javax.swing.JComboBox;

public class AnimatedChicken extends Entity {

	public static final int OBJECT_TYPE = generateTypeId();
	
	public AnimatedChicken() {	
		super();
	}
	
	@Override
	public void onCreated(Scene scene) {
		super.onCreated(scene);
		
		this.model = scene.getAssets().getAsset("chickenModel");
		this.transform.scale = new Vector3f(0.1f, 0.1f, 0.1f);
		
		this.material = new Material(scene.getAssets().getAsset("defaultShader"));
		this.material.texture = scene.getAssets().getAsset("chickenTexture");
		
		this.model.bindMaterial(this.material);
		
		this.animator = new Animator(this.model.getAnimations());
		
		animator.doLoop = false;
		
		for (Animation a : this.model.getAnimations()) {
			animator.addIdleAnimation(a.getName());
		}
		
	}
	
	@Override
	public void onUpdate(float delta) {
		animator.update(delta);
		
		
		transform.rotate(new Vector3f(0, (float) Math.PI / 8 * delta, 0));
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
		return OBJECT_TYPE;
	}

}
