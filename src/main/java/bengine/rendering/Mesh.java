package bengine.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4i;

import bengine.entities.Camera;

public class Mesh {
	
	public Vertex[] vertices;
	public IntBuffer indices;
	
	private VAO renderObject;
	private VBO positionObject, normalObject, texCoordObject, jointWeightObject, jointObject;
	
	public Mesh() {}
	
	public Mesh(Vertex[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = store(indices);
	}
	
	public void create() {
		Vector3f[] positions = new Vector3f[vertices.length];
		Vector3f[] normals = new Vector3f[vertices.length];
		Vector3f[] texCoords = new Vector3f[vertices.length];
		Vector4f[] jointWeights = new Vector4f[vertices.length];
		Vector4i[] jointIDS = new Vector4i[vertices.length];
		
		boolean hasSkinData = false;
		
		for (int x = 0; x < vertices.length; x++) {
			Vertex v = vertices[x];
			
			positions[x] = v.position;
			normals[x] = v.normal;
			texCoords[x] = v.texCoord;
			
			if (v.skinData != null) {
				jointWeights[x] = v.skinData.getWeightData();
				jointIDS[x] = v.skinData.getBoneData();
				
				hasSkinData = true;
			}
		}
		
		this.renderObject = new VAO();
		
		positionObject = new VBO(store(positions), false);
		normalObject = new VBO(store(normals), false);
		texCoordObject = new VBO(store(texCoords), false);
		
		if (hasSkinData) {
			jointWeightObject = new VBO(store(jointWeights), false);
			jointObject = new VBO(store(jointIDS), false);
		}
		
	}
	
	public void destroy() {
		this.renderObject.destroy();
		this.positionObject.destroy();
		this.normalObject.destroy();
		this.texCoordObject.destroy();
		this.jointObject.destroy();
		this.jointWeightObject.destroy();
	}
	
	public void update() {
		Vector3f[] positions = new Vector3f[vertices.length];
		Vector3f[] normals = new Vector3f[vertices.length];
		
		for (int x = 0; x < vertices.length; x++) {
			Vertex v = vertices[x];
			
			positions[x] = v.position;
			normals[x] = v.normal;
		}
		
		
	}
	
	public void transform(Matrix4f transformMatrix) {
		for (Vertex v : vertices) {
			v.transform(transformMatrix);
		}
	}
	
	public void render(Matrix4f transformMatrix) {
		//Shader s = renderer.getShader();
		//Camera c = renderer.getCamera();
		
		renderObject.bind();
		
			//Matrix4f transformedView = c.generateViewmodel()
			//		 .mul(transformMatrix);
			
			//s.pushView(transformedView);
			
			glDrawElements(GL_TRIANGLES, indices);
			
		
		renderObject.unbind();
	}
	
	private FloatBuffer store(Vector4f[] data) {
		FloatBuffer buf = ByteBuffer.allocateDirect(data.length * 4 * Float.BYTES)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		
		for (Vector4f vec : data) {
			vec.get(buf);
			buf.position(buf.position() + 4);
		}
		
		buf.flip();
		
		return buf;
	}
	
	private FloatBuffer store(Vector3f[] data) {
		FloatBuffer buf = ByteBuffer.allocateDirect(data.length * 3 * Float.BYTES)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		
		for (Vector3f vec : data) {
			vec.get(buf);
			buf.position(buf.position() + 3);
		}
		
		buf.flip();
		
		return buf;
	}
	
	private IntBuffer store(Vector4i[] data) {
		IntBuffer buf = ByteBuffer.allocateDirect(data.length * 4 * Integer.BYTES)
				.order(ByteOrder.nativeOrder())
				.asIntBuffer();
		
		for (Vector4i vec : data) {
			vec.get(buf);
			buf.position(buf.position() + 4);
		}
		
		buf.flip();
		
		return buf;
	}
	
	private IntBuffer store(int[] data) {
		IntBuffer buf = ByteBuffer.allocateDirect(data.length * Integer.BYTES)
				.order(ByteOrder.nativeOrder())
				.asIntBuffer();
		
		buf.put(data);
		
		buf.flip();
		return buf;
	}
	
	protected IntBuffer store(Integer[] data) {
		IntBuffer buf = ByteBuffer.allocateDirect(data.length * Integer.BYTES)
				.order(ByteOrder.nativeOrder())
				.asIntBuffer();
		
		for (int x : data) {
			buf.put(x);
		}
		
		buf.flip();
		
		return buf;
	}
}
