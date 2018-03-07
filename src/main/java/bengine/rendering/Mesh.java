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

public class Mesh implements Drawable {
	
	public Vertex[] vertices;
	public IntBuffer indices;
	
	private int[] renderObject;
	
	private Renderer renderer;
	
	private boolean isStatic = true;
	
	private boolean hasSkinData;
	
	public Mesh() {}
	
	public Mesh(Vertex[] vertices, int[] indices, boolean isStatic) {
		this.vertices = vertices;
		this.indices = store(indices);
		this.isStatic = isStatic;
	}
	
	public Mesh(Vertex[] vertices, int[] indicies) {
		this(vertices, indicies, true);
	}
	
	public void create(Renderer renderer) {
		this.renderer = renderer;
		
		Vector3f[] positions = new Vector3f[vertices.length];
		Vector3f[] normals = new Vector3f[vertices.length];
		Vector3f[] texCoords = new Vector3f[vertices.length];
		Vector4f[] jointWeights = new Vector4f[vertices.length];
		Vector4i[] jointIDS = new Vector4i[vertices.length];
		
		hasSkinData = false;
		
		for (int x = 0; x < vertices.length; x++) {
			Vertex v = vertices[x];
			
			positions[x] = v.position;
			normals[x] = v.normal;
			texCoords[x] = v.texCoord;
			
			if (v.skinData != null) {
				jointWeights[x] = v.skinData.weights;
				jointIDS[x] = v.skinData.joints;
				
				hasSkinData = true;
			}
		}
		
		if (hasSkinData) {
			renderObject = renderer.createVAO(isStatic, store(positions), store(normals), store(texCoords), store(jointWeights), store(jointIDS));
		} else {
			renderObject = renderer.createVAO(isStatic, store(positions), store(normals), store(texCoords));
		}
	}
	
	public void destroy() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(renderObject[1]);
		glDeleteBuffers(renderObject[2]);
		glDeleteBuffers(renderObject[3]);
		
		
		glBindVertexArray(0);
		glDeleteBuffers(renderObject[0]);
		
	}
	
	public void update() {
		Vector3f[] positions = new Vector3f[vertices.length];
		Vector3f[] normals = new Vector3f[vertices.length];
		
		for (int x = 0; x < vertices.length; x++) {
			Vertex v = vertices[x];
			
			positions[x] = v.position;
			normals[x] = v.normal;
		}
		
		renderer.updateBuffer(renderObject[1], store(positions));
		renderer.updateBuffer(renderObject[2], store(normals));
	}
	
	public void transform(Matrix4f transformMatrix) {
		
		for (Vertex v : vertices) {
			v.transform(transformMatrix);
		}
		
		if (!isStatic) {
			update();
		}
	}
	
	@Override
	public void render(Matrix4f transformMatrix) {
		Shader s = renderer.getShader();
		Camera c = renderer.getCamera();
		
		glBindVertexArray(renderObject[0]);
		
		glEnableVertexAttribArray(Renderer.VERTEX_INDEX);
		glEnableVertexAttribArray(Renderer.NORMAL_INDEX);
		glEnableVertexAttribArray(Renderer.TEX_COORD_INDEX);
		glEnableVertexAttribArray(4);
		glEnableVertexAttribArray(5);
		
			Matrix4f transformedView = c.generateViewmodel()
					 .mul(transformMatrix);
			
			s.pushView(transformedView);
			
			glDrawElements(GL_TRIANGLES, indices);
			
		
		glDisableVertexAttribArray(Renderer.VERTEX_INDEX);
		glDisableVertexAttribArray(Renderer.NORMAL_INDEX);
		glDisableVertexAttribArray(Renderer.TEX_COORD_INDEX);
		glDisableVertexAttribArray(4);
		glDisableVertexAttribArray(5);
		
		glBindVertexArray(0);
	}
	
	public void setRenderer(Renderer r) {
		this.renderer = r;
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
