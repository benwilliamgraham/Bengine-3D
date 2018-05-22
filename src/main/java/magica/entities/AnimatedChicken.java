package magica.entities;

import javax.swing.JFrame;

import org.joml.Vector3f;

import bengine.Scene;
import bengine.animation.Animation;
import bengine.animation.Animator;
import bengine.entities.Entity;
import bengine.rendering.Material;
import magica.ChickenController;

import javax.swing.JComboBox;

public class AnimatedChicken extends Entity {

	public static final int OBJECT_TYPE = generateTypeId();
	
	public AnimatedChicken() {	
		
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
	}
	
	@Override
	public void onUpdate(float delta) {
		animator.update(delta);
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
