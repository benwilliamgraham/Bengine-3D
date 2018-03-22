package magica.entities;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import bengine.Scene;
import bengine.animation.Animation;
import bengine.animation.Bone;
import bengine.animation.Skeleton;
import bengine.entities.Entity;
import bengine.input.Keyboard;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Renderer;

public class TestEntity extends Entity {

	public static Material debugMaterial;
	
	private Animation activeAnimation;
	
	public TestEntity() {
		
	}

	@Override
	public void onCreated(Scene scene) {
		super.onCreated(scene);
		
		this.model = scene.getAssets().getAsset("chickenModel");
		this.scale = new Vector3f(0.1f, 0.1f, 0.1f);
		
		//this.transform.rotate(new Vector3f(-(float) Math.PI / 2.0f, 0.0f, 0.0f));
		
		this.material = new Material(scene.getAssets().getAsset("defaultShader"));
		this.material.texture = scene.getAssets().getAsset("chickenTexture");
		
		this.model.bindMaterial(this.material);
		
		for (Animation a : this.model.getAnimations()) {
			System.out.println(a.getName());
			
			if (a.getName().equals("Armature|Walk")) {
				activeAnimation = a;
				activeAnimation.play();
			}
		}
		
		
	}

	@Override
	public void onUpdate(float delta) {
		if (activeAnimation != null) {
			activeAnimation.update(delta);
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_DOWN)) {
			transform.position.z += 2.0f * delta;
		}	
		
	}
	
	@Override
	public void onDraw() {
		
		if (this.activeAnimation != null) {
		
			/*glClear(GL_DEPTH_BUFFER_BIT);
			
			debugMaterial.bind();
			
			Matrix4f[] matrices = activeAnimation.GetBoneData();
			
			for (Bone b : this.activeAnimation.getSkeleton().bones) {
				
				System.out.println(this.activeAnimation.getSkeleton().bones.length);
				
				Vector4f posX = new Vector4f(-1.0f,  0.0f,  0.0f, 1.0f).mul(matrices[activeAnimation.getSkeleton().ResolveName(b.name)]);
				Vector4f negX = new Vector4f( 1.0f,  0.0f,  0.0f, 1.0f).mul(matrices[activeAnimation.getSkeleton().ResolveName(b.name)]);
				Vector4f posY = new Vector4f( 0.0f,  1.0f,  0.0f, 1.0f).mul(matrices[activeAnimation.getSkeleton().ResolveName(b.name)]);
				Vector4f negY = new Vector4f( 0.0f, -1.0f,  0.0f, 1.0f).mul(matrices[activeAnimation.getSkeleton().ResolveName(b.name)]);
				Vector4f posZ = new Vector4f( 0.0f,  0.0f,  1.0f, 1.0f).mul(matrices[activeAnimation.getSkeleton().ResolveName(b.name)]);
				Vector4f negZ = new Vector4f( 0.0f,  0.0f, -1.0f, 1.0f).mul(matrices[activeAnimation.getSkeleton().ResolveName(b.name)]);
				
				debugMaterial.camera(getScene().getCamera().generateView(), transform.generateMatrix());
				
				glLineWidth(3);
				
				debugMaterial.color(new Vector3f(1.0f, 0.0f, 0.0f));
				
				glBegin(GL_LINES);
				glVertex3f(negX.x, negX.y, negX.z);
				glVertex3f(posX.x, posX.y, posX.z);
				glEnd();
				
				debugMaterial.color(new Vector3f(0.0f, 1.0f, 0.0f));
				
				glBegin(GL_LINES);
				glVertex3f(negY.x, negY.y, negY.z);
				glVertex3f(posY.x, posY.y, posY.z);
				glEnd();
				
				debugMaterial.color(new Vector3f(0.0f, 0.0f, 1.0f));
				
				glBegin(GL_LINES);
				glVertex3f(negZ.x, negZ.y, negZ.z);
				glVertex3f(posZ.x, posZ.y, posZ.z);
				glEnd();
				
			}
			
			debugMaterial.unbind();*/
			
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
