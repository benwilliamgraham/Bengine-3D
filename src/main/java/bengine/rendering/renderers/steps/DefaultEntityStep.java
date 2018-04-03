package bengine.rendering.renderers.steps;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AINode;

import bengine.Game;
import bengine.animation.Animation;
import bengine.assets.Texture;
import bengine.entities.Entity;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.gl.VAO;
import bengine.rendering.renderers.RenderStep;

public class DefaultEntityStep extends RenderStep<Entity>{
	
	@Override
	public void clear() {
		glClearColor(0.4f, 0.6f, 0.9f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //Clear the color buffer and depth buffer of our current framebuffer.
	}
	
	@Override
	public void render(Entity e) {
		Matrix4f transformMatrix = new Matrix4f().identity();
		
		transformMatrix.mul(e.transform.generateMatrix());
		
		drawNode(e.getModel().getRootNode(), e, transformMatrix);
		
		e.onDraw();
	}
	
	@Override
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0); //Use the default framebuffer. for now.
		glViewport(0, 0, Game.getCurrent().getWidth(), Game.getCurrent().getHeight());
		
	}
	
	private void drawNode(AINode node, Entity e, Matrix4f transformMatrix) {
		Mesh[] meshes = e.getModel().getMeshes();
		
		Matrix4f offset = new Matrix4f(transformMatrix).mul(convertMat(node.mTransformation()));
		
		if (node.mNumMeshes() > 0) {
			
			for (int x = 0; x < node.mNumMeshes(); x++) {
				int id = node.mMeshes().get(x);
				
				Mesh mesh = meshes[node.mMeshes().get(x)];
				
				drawMesh(mesh, e, offset);
			}
		}
		
		if (node.mNumChildren() > 0) {
			for (int x = 0; x < node.mNumChildren(); x++) {
				AINode child = AINode.create(node.mChildren().get(x));
				
				drawNode(child, e, new Matrix4f(transformMatrix));
			}
		}
	}
	
	private void drawMesh(Mesh m, Entity e, Matrix4f transformMatrix) {
		
		Material mat = (e.getModel().getMaterial(m.materialIndex) == null)? getRenderer().getDefaultMaterial() : e.getModel().getMaterial(m.materialIndex);
		
		VAO renderObject = m.getRenderable();
		IntBuffer indices = m.getIndices();
		
		mat.bind();
		
		mat.sun(e.getScene().getSun());
		
		if (m.skeleton != null && e.getAnimator() != null) {
			Animation anim = e.getAnimator().getActiveAnimation();
			
			if (anim != null) {
				anim.attach(m.skeleton);
				mat.animate(anim);
				mat.camera(e.getScene().getCamera(), e.transform.generateMatrix());
			} else {
				mat.camera(e.getScene().getCamera(), transformMatrix);
			}
		} else {
			mat.camera(e.getScene().getCamera(), transformMatrix);
		}
		
		if (getRenderer().stepPassData.containsKey("shadowDepthMap")) {
			Texture shadowDepthMap = (Texture) getRenderer().stepPassData.get("shadowDepthMap");
			
			shadowDepthMap.bind(GL_TEXTURE1);
			mat.getShader().push("depthMap", 1);
			
			Matrix4f lightMatrix = (Matrix4f) getRenderer().stepPassData.get("lightMatrix");
			
			mat.getShader().push("lightMatrix", lightMatrix);
		}
		
		renderObject.bind();
		
		glDrawElements(GL_TRIANGLES, indices);
		
		renderObject.unbind();
		
		mat.unbind();
	}
	
	private Matrix4f convertMat(AIMatrix4x4 mat) {
		return new Matrix4f(
				mat.a1(), mat.a2(), mat.a3(), mat.a4(),
				mat.b1(), mat.b2(), mat.b3(), mat.b4(),
				mat.c1(), mat.c2(), mat.c3(), mat.c4(),
				mat.d1(), mat.d2(), mat.d3(), mat.d4())
				.transpose();
	}
}
