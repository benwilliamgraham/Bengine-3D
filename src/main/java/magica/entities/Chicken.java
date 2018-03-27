package magica.entities;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import bengine.Scene;
import bengine.animation.Animation;
import bengine.animation.Animator;
import bengine.animation.Bone;
import bengine.animation.Skeleton;
import bengine.entities.Entity;
import bengine.input.Keyboard;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Renderer;

public class Chicken extends Entity {
	
	public static final int OBJECT_TYPE = generateTypeId();
	
	public static Material debugMaterial;
	
	@SyncedField("position")
	public Vector3f networkedPosition = new Vector3f(0.0f, 0.0f, 0.0f);
	
	@SyncedField("rotation")
	public Quaternionf networkedRotation = new Quaternionf();
	
	@SyncedField("animation")
	public String animationName = "";
	
	@SyncedField("animationTime")
	public float animationTime = 0.0f;
	
	public Chicken() {
		super();
		this.visibility.allowAll = true;
	}

	@Override
	public void onCreated(Scene scene) {
		super.onCreated(scene);
		
		this.networkedPosition = this.transform.position;
		
		this.model = scene.getAssets().getAsset("chickenModel");
		//this.scale = new Vector3f(0.1f, 0.1f, 0.1f);
		
		this.material = new Material(scene.getAssets().getAsset("defaultShader"));
		this.material.texture = scene.getAssets().getAsset("chickenTexture");
		
		this.model.bindMaterial(this.material);
		
		this.animator = new Animator(this.model.getAnimations());
		
		if (isLocalAuthority()) {
			animator.doLoop = false;
			animator.addIdleAnimation("Armature|Idle1");
			animator.addIdleAnimation("Armature|Idle2");
		}
	}

	@Override
	public void onUpdate(float delta) {
		
		if (!this.getEndpoint().isRemote()) { //TODO: fix an issue where an object can be updated before it is created.
			animator.update(delta);
		}
		
		if (isLocalAuthority()) {
			if (animator.getActiveAnimation().getName().equals("Armature|Walk")) {
				transform.move(transform.forwards().mul(1.8f * delta));
			}
			
			if (Keyboard.isKeyJustPressed(GLFW_KEY_SPACE)) {
				RPC("playAnimation", RPC.ALL_REMOTES, "Armature|Eating");
			}
			
			if (Keyboard.isKeyJustPressed(GLFW_KEY_W)) {
				RPC("playAnimation", RPC.ALL_REMOTES_AND_LOCAL, "Armature|Walk");
			}
			
			if (Keyboard.isKeyDown(GLFW_KEY_D)) {
				transform.rotate(new Vector3f(0, (float) (-delta * Math.PI / 4.0), 0));
			} else if (Keyboard.isKeyDown(GLFW_KEY_A)) {
				transform.rotate(new Vector3f(0, (float) ( delta * Math.PI / 4.0), 0));
			}
			
			getScene().getCamera().transform.lookAt(new Vector3f(transform.position).add(new Vector3f(0, 5, 0)));
			
			networkedPosition = new Vector3f(transform.position);
			networkedRotation = new Quaternionf(transform.rotation);
			animationName = animator.getActiveAnimation().getName();
			animationTime = animator.getTime();
			//System.out.println("Setting: " + networkedPosition.toString());
		}
	}
	
	@Override
	public void onDestroyed() {}

	@Override
	public void onRegistered() {}
	
	@Override
	public void onDeregistered() {
		this.destroy();
	}

	@Override
	public void onObjectUpdate() {
		if (!isLocalAuthority() && !getEndpoint().isRemote()) {
			//System.out.println("Getting: " + networkedPosition.toString());
			this.transform.position = new Vector3f(networkedPosition);
			this.transform.rotation = new Quaternionf(networkedRotation);
			this.animator.playNow(animationName);
			this.animator.setTime(animationTime);
		}
	}

	@RPC("playAnimation")
	public void queueAnimation(String animationName) {
		 if (!this.getEndpoint().isRemote()) {
			 animator.queueAnimation(animationName);
		 }
	}
	
	@Override
	public int getType() {
		return OBJECT_TYPE;
	}
}
