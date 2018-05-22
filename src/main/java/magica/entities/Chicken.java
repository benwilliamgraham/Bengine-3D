package magica.entities;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import bengine.Scene;
import bengine.animation.Animator;
import bengine.entities.Entity;
import bengine.input.Keyboard;
import bengine.rendering.Material;

public class Chicken extends Entity {
	
	public static final int OBJECT_TYPE = generateTypeId();
	
	public static Material debugMaterial;
	
	@SyncedField("position")
	public Vector3f networkedPosition = new Vector3f(0.0f, 0.0f, 0.0f);
	
	@SyncedField("rotation")
	public Quaternionf networkedRotation = new Quaternionf();
	
	public Chicken() {
		super();
		this.visibility.allowAll = true;
	}

	@Override
	public void onCreated(Scene scene) {
		super.onCreated(scene);
		
		this.networkedPosition = this.transform.position;
		
		this.model = scene.getAssets().getAsset("chickenModel");
		this.transform.scale = new Vector3f(0.1f, 0.1f, 0.1f);
		
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
		
		//if (!this.getEndpoint().isRemote()) { //TODO: fix an issue where an object can be updated before it is created.
			animator.update(delta);
		//}
		
		if (/*isLocalAuthority()*/true) {
			//transform.position = getScene().getCamera().transform.position;
			//transform.rotation = getScene().getCamera().transform.rotation;
			if (animator.getActiveAnimation().getName().equals("Armature|Walk")) {
				transform.move(transform.forwards().mul(0.5f * delta));
			}
			
			/*if (Keyboard.isKeyJustPressed(GLFW_KEY_SPACE)) {
				RPC("playAnimation", RPC.ALL_REMOTES, "Armature|Eating");
			}
			
			if (Keyboard.isKeyJustPressed(GLFW_KEY_W)) {
				RPC("playAnimation", RPC.ALL_REMOTES_AND_LOCAL, "Armature|Walk");
			}*/
			
			if (Keyboard.isKeyDown(GLFW_KEY_Q)) {
				transform.rotate(new Vector3f(0, (float) (-delta * Math.PI / 4.0), 0));
			} else if (Keyboard.isKeyDown(GLFW_KEY_E)) {
				transform.rotate(new Vector3f(0, (float) ( delta * Math.PI / 4.0), 0));
			}
			
			//getScene().getCamera().transform.lookAt(new Vector3f(transform.position));
			
			networkedPosition = new Vector3f(transform.position);
			networkedRotation = new Quaternionf(transform.rotation);
		}
	}
	
	@Override
	public void onDraw() {
		/*Shader simpleShader = getScene().getAssets().getAsset("simpleShader");
		
		Camera c  = getScene().getCamera();
		
		simpleShader.bind();
		
		simpleShader.push("viewMatrix", c.generateView());
		simpleShader.push("transformMatrix", new Matrix4f().translate(transform.position));
		
		simpleShader.push("fragColor", new Vector3f(0, 0, 1));
		
		Vector3f lineX = transform.right().normalize();
		Vector3f lineY = transform.up().normalize();
		Vector3f lineZ = transform.forwards().normalize();
		
		glBegin(GL_LINES);
			glVertex3f(0, 0, 0);
			glVertex3f(lineZ.x, lineZ.y, lineZ.z);
		glEnd();
		
		simpleShader.push("fragColor", new Vector3f(0, 1, 0));
		
		glBegin(GL_LINES);
			glVertex3f(0, 0, 0);
			glVertex3f(lineY.x, lineY.y, lineY.z);
		glEnd();
		
		simpleShader.push("fragColor", new Vector3f(1, 0, 0));
		
		glBegin(GL_LINES);
			glVertex3f(0, 0, 0);
			glVertex3f(lineX.x, lineX.y, lineX.z);
		glEnd();
		
		simpleShader.unbind();*/
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
			this.transform.position = new Vector3f(networkedPosition);
			this.transform.rotation = new Quaternionf(networkedRotation);
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
