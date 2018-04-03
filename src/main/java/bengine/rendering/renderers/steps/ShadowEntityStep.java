package bengine.rendering.renderers.steps;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;

import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AINode;

import bengine.animation.Animation;
import bengine.assets.Shader;
import bengine.assets.Texture;
import bengine.entities.Entity;
import bengine.rendering.Framebuffer;
import bengine.rendering.Mesh;
import bengine.rendering.gl.VAO;
import bengine.rendering.renderers.RenderStep;

public class ShadowEntityStep extends RenderStep<Entity> {
	
	Shader shader;
	Framebuffer shadowBuffer;
	Texture depthMap;
	
	public ShadowEntityStep(Shader shader) {
		this.shader = shader;
		this.shadowBuffer = new Framebuffer(512, 512);
		depthMap = shadowBuffer.addColorBuffer(GL_DEPTH_ATTACHMENT, GL_DEPTH_COMPONENT, GL_FLOAT);
		
		shadowBuffer.bind();
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		shadowBuffer.unbind();
	}
	
	@Override
	public void clear() {
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_DEPTH_BUFFER_BIT);
	}
	
	@Override
	public void render(Entity e) {
		getRenderer().stepPassData.put("shadowDepthMap", depthMap);
		
		Matrix4f transformMatrix = new Matrix4f().identity();
		
		transformMatrix.mul(e.transform.generateMatrix());
		
		drawNode(e.getModel().getRootNode(), e, transformMatrix);
		
	}

	@Override
	public void bind() {
		shadowBuffer.bind();
	}
	
	private void drawNode(AINode node, Entity e, Matrix4f transformMatrix) {
		Mesh[] meshes = e.getModel().getMeshes();
		
		Matrix4f offset = new Matrix4f(transformMatrix).mul(convertMat(node.mTransformation()));
		
		if (node.mNumMeshes() > 0) {
			
			for (int x = 0; x < node.mNumMeshes(); x++) {
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
		
		VAO renderObject = m.getRenderable();
		IntBuffer indices = m.getIndices();
		
		shader.bind();
		
		if (m.skeleton != null && e.getAnimator() != null) {
			Animation anim = e.getAnimator().getActiveAnimation();
			
			if (anim != null) {
				anim.attach(m.skeleton);
				
				//Equivalent to mat.animate(skeleton);
				Matrix4f[] boneTransforms = new Matrix4f[50];
				
				Matrix4f[] oBoneTransforms = anim.GetBoneData();
				
				for (int i = 0; i < 50; i++) {
					if (i < oBoneTransforms.length) {
						boneTransforms[i] = oBoneTransforms[i];
					} else {
						boneTransforms[i] = new Matrix4f().identity();
					}
				}
				
				shader.push("boneTransforms", boneTransforms);
					
				shader.push("transformMatrix", e.transform.generateMatrix());
			} else {
				shader.push("transformMatrix", transformMatrix);
			}
		} else {
			shader.push("transformMatrix", transformMatrix);
		}
		
		Matrix4f lightSpaceMatrix = new Matrix4f().ortho(-10, 10, -10, 10, 0.1f, 10f)
				.translate(e.getScene().getSun().position)
				.lookAlong(new Vector3f(e.getScene().getSun().position).mul(-1), new Vector3f(0, 1, 0));//.lookAt(new Vector3f(e.getScene().getSun().position), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
		
		shader.push("viewMatrix", lightSpaceMatrix);
		
		getRenderer().stepPassData.put("lightMatrix", lightSpaceMatrix);
		
		renderObject.bind();
		
		glCullFace(GL_FRONT);
		
		glDrawElements(GL_TRIANGLES, indices);
		
		renderObject.unbind();
		
		glCullFace(GL_BACK);
		
		shader.unbind();
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
