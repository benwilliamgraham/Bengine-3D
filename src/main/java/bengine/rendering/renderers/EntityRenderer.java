package bengine.rendering.renderers;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import bengine.Game;
import bengine.animation.Animation;
import bengine.assets.Shader;
import bengine.entities.Camera;
import bengine.entities.Entity;
import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Vertex;
import bengine.rendering.gl.VAO;

public class EntityRenderer {
	
	Material defaultMaterial;
	
	private Mesh testMesh;
	
	public EntityRenderer(Material defaultMaterial) {
		this.defaultMaterial = defaultMaterial;
		
		Vertex[] vertices = new Vertex[3];
		vertices[0] = new Vertex();
		vertices[0].position = new Vector3f(-0.5f, 0.5f, 0.0f);
		vertices[0].normal = new Vector3f(0.0f, 0.0f, 0.0f);
		vertices[0].texCoord = new Vector3f(0.0f, 0.0f, 0.0f);
		vertices[1] = new Vertex();
		vertices[1].position = new Vector3f(-0.5f, -0.5f, 0.0f);
		vertices[1].normal = new Vector3f(0.0f, 0.0f, 0.0f);
		vertices[1].texCoord = new Vector3f(0.0f, 0.0f, 0.0f);
		vertices[2] = new Vertex();
		vertices[2].position = new Vector3f(0.5f, -0.5f, 0.0f);
		vertices[2].normal = new Vector3f(0.0f, 0.0f, 0.0f);
		vertices[2].texCoord = new Vector3f(0.0f, 0.0f, 0.0f);
		
		int[] indices = new int[] {0, 1, 2};
		
		testMesh = new Mesh(vertices, indices);
		
		if (defaultMaterial != null) {
			testMesh.create();
		}
	}
	
	public void render(Entity e, Camera c) {
		
		Shader simpleShader = e.getScene().getAssets().getAsset("simpleShader");
		
		Matrix4f transformMatrix = c.generateViewmodel();
		
		e.transform.apply(transformMatrix);
		
		Mesh[] meshes = e.model.getMeshes();
		
		/*VAO renderObject = testMesh.getRenderable();
		IntBuffer indices = testMesh.getIndices();
		
		simpleShader.bind();
		
		renderObject.bind();
		
		glDrawElements(GL_TRIANGLES, indices);
		
		renderObject.unbind();
		
		simpleShader.unbind();*/
		
		for (Mesh m : meshes) { 
			
			int matIndex = m.materialIndex;
			
			Animation anim = e.getActiveAnimation();
			
			Material mat = (e.model.getMaterial(matIndex) == null)? defaultMaterial : e.model.getMaterial(matIndex);
			
			VAO renderObject = m.getRenderable();
			IntBuffer indices = m.getIndices();
			
			mat.bind();
			
			if (m.skeleton != null && anim != null) {
				if (anim.getSkeleton().equals(m.skeleton)) {
					mat.animate(anim);
				}
			}
			
			mat.camera(transformMatrix);
			
			renderObject.bind();
			
			glDrawElements(GL_TRIANGLES, indices);
			
			renderObject.unbind();
			
			mat.unbind();
		}
	}
}
