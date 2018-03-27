package bengine.rendering.renderers;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.opengl.GL;

import bengine.Game;
import bengine.animation.Animation;
import bengine.animation.Bone;
import bengine.animation.Skeleton;
import bengine.assets.Shader;
import bengine.entities.Camera;
import bengine.entities.Entity;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Vertex;
import bengine.rendering.gl.VAO;

public class EntityRenderer {
	
	Material defaultMaterial;
	
	public EntityRenderer(Material defaultMaterial) {
		this.defaultMaterial = defaultMaterial;
	}
	
	public void render(Entity e, Camera c) {
		
		Matrix4f viewMatrix = c.generateView();
		Matrix4f transformMatrix = new Matrix4f().identity();
		
		transformMatrix.mul(e.transform.generateMatrix());
		
		drawNode(e.getModel().getRootNode(), e, viewMatrix, transformMatrix);
		
		e.onDraw();
	}
	
	private void drawNode(AINode node, Entity e, Matrix4f viewMatrix, Matrix4f transformMatrix) {
		Mesh[] meshes = e.getModel().getMeshes();
		
		Matrix4f offset = new Matrix4f(transformMatrix).mul(convertMat(node.mTransformation()));
		
		if (node.mNumMeshes() > 0) {
			
			for (int x = 0; x < node.mNumMeshes(); x++) {
				int id = node.mMeshes().get(x);
				
				Mesh mesh = meshes[node.mMeshes().get(x)];
				
				drawMesh(mesh, e, viewMatrix, offset);
			}
		}
		
		if (node.mNumChildren() > 0) {
			for (int x = 0; x < node.mNumChildren(); x++) {
				AINode child = AINode.create(node.mChildren().get(x));
				
				drawNode(child, e, viewMatrix, new Matrix4f(transformMatrix));
			}
		}
	}
	
	private void drawMesh(Mesh m, Entity e, Matrix4f viewMatrix, Matrix4f transformMatrix) {
		int matIndex = m.materialIndex;
		
		Skeleton s = m.skeleton;
		
		Material mat = (e.getModel().getMaterial(matIndex) == null)? defaultMaterial : e.getModel().getMaterial(matIndex);
		
		VAO renderObject = m.getRenderable();
		IntBuffer indices = m.getIndices();
		
		mat.bind();
		
		if (m.skeleton != null && e.getAnimator() != null) {
			Animation anim = e.getAnimator().getActiveAnimation();
			
			if (anim != null) {
				anim.attach(m.skeleton);
				mat.animate(anim);
				mat.camera(viewMatrix, e.transform.generateMatrix());
			} else {
				mat.camera(viewMatrix, transformMatrix);
			}
		} else {
			mat.camera(viewMatrix, transformMatrix);
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
	
	private Quaternionf convertQuat(AIQuaternion quat) {
		return new Quaternionf(quat.x(), quat.y(), quat.z(), quat.w());
	}
	
	private Vector3f convertVec(AIVector3D vec) {
		return new Vector3f(vec.x(), vec.y(), vec.z());
	}
}
