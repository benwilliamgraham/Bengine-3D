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
	
	private int vaoId;
	private IntBuffer indices;
	
	public EntityRenderer(Material defaultMaterial) {
		this.defaultMaterial = defaultMaterial;
		
		Vertex[] vertices = new Vertex[3];
		vertices[0] = new Vertex();
		vertices[0].position = new Vector3f(-0.5f, -0.5f, 0.0f);
		//vertices[0].normal = new Vector3f(0.0f, 0.0f, 0.0f);
		//vertices[0].texCoord = new Vector3f(0.0f, 0.0f, 0.0f);
		vertices[1] = new Vertex();
		vertices[1].position = new Vector3f(0.5f, -0.5f, 0.0f);
		//vertices[1].normal = new Vector3f(0.0f, 0.0f, 0.0f);
		//vertices[1].texCoord = new Vector3f(0.0f, 0.0f, 0.0f);
		vertices[2] = new Vertex();
		vertices[2].position = new Vector3f(0.5f, 0.5f, 0.0f);
		//vertices[2].normal = new Vector3f(0.0f, 0.0f, 0.0f);
		//vertices[2].texCoord = new Vector3f(0.0f, 0.0f, 0.0f);
		
		//testMesh = new Mesh(vertices, indices);
		
		if (defaultMaterial != null) {
			int[] indices = new int[] {0, 1, 2};
			
			this.indices = ByteBuffer.allocateDirect(indices.length * Integer.BYTES).order(ByteOrder.nativeOrder()).asIntBuffer();
			
			this.indices.put(0);this.indices.put(1);this.indices.put(2);
			
			this.indices.flip();
			
			FloatBuffer positionData = ByteBuffer.allocateDirect(6 * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
			positionData.put(-0.5f);positionData.put(-0.5f);
			positionData.put(-0.5f);positionData.put( 0.5f);
			positionData.put( 0.5f);positionData.put( 0.5f);
			positionData.flip();
			//testMesh.create();
			vaoId = glGenVertexArrays();
			
			glBindVertexArray(vaoId);
			
			int positionBuffer = glGenBuffers();
			
			glBindBuffer(GL_ARRAY_BUFFER, positionBuffer);
			glBufferData(GL_ARRAY_BUFFER, positionData, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0L);
			glBindBuffer(GL_ARRAY_BUFFER, -1);
			
			glBindVertexArray(-1);
		}
	}
	
	public void render(Entity e, Camera c) {
		
		Shader simpleShader = e.getScene().getAssets().getAsset("simpleShader");
		
		Matrix4f transformMatrix = c.generateViewmodel();
		
		e.transform.apply(transformMatrix);
		
		Mesh[] meshes = e.model.getMeshes();
		
		//VAO renderObject = testMesh.getRenderable();
		//IntBuffer indices = testMesh.getIndices();
		
		simpleShader.bind();
		
		//renderObject.bind();
		glBindVertexArray(vaoId);
		
		glEnableVertexAttribArray(0);
		
		glDrawElements(GL_TRIANGLES, indices);
		
		glDisableVertexAttribArray(0);
		
		glBindVertexArray(-1);
		//renderObject.unbind();
		
		/*glBegin(GL_TRIANGLES);
			glVertex2f(-0.5f, -0.5f);
			glVertex2f( 0.5f, -0.5f);
			glVertex2f( 0.5f,  0.5f);
		glEnd();*/
		
		simpleShader.unbind();
		
		//simpleShader.bind();
		
		//renderObject.bind();
		
		//glBindVertexArray();
		
		//glDrawElements(GL_TRIANGLES, indices);
		
		//renderObject.unbind();
		
		//simpleShader.unbind();
		
		/*for (Mesh m : meshes) { 
			
			int matIndex = m.materialIndex;
			
			//Animation anim = e.getActiveAnimation();
			
			Material mat = (e.model.getMaterial(matIndex) == null)? defaultMaterial : e.model.getMaterial(matIndex);
			
			VAO renderObject = m.getRenderable();
			IntBuffer indices = m.getIndices();
			
			//mat.bind();
			
			if (m.skeleton != null && anim != null) {
				if (anim.getSkeleton().equals(m.skeleton)) {
					mat.animate(anim);
				}
			}
			
			//mat.camera(transformMatrix);
			
			renderObject.bind();
			
			glDrawElements(GL_TRIANGLES, indices);
			
			renderObject.unbind();
			
			//mat.unbind();
		}*/
	}
}
