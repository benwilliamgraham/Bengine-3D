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

import bengine.entities.Camera;

public class Mesh implements Drawable {
	
	public Vector3f[] vertices, normals, texCoords;
	public IntBuffer indices;
	
	private int[] renderObject;
	
	private Renderer renderer;
	
	private boolean isStatic = true;
	
	public Mesh() {}
	
	public Mesh(Vector3f[] vertices, Vector3f[] normals, Vector3f[] texCoords, int[] indices, boolean isStatic) {
		this.vertices = vertices;
		this.normals = normals;
		this.texCoords = texCoords;
		this.indices = store(indices);
		this.isStatic = isStatic;
	}
	
	public Mesh(Vector3f[] verticies, Vector3f[] normals, Vector3f[] texCoords, int[] indicies) {
		this(verticies, normals, texCoords, indicies, true);
	}
	
	public void create(Renderer renderer) {
		this.renderer = renderer;
		
		renderObject = renderer.createVAO(isStatic, store(vertices), store(normals), store(texCoords));
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
		renderer.updateBuffer(renderObject[1], store(vertices));
		renderer.updateBuffer(renderObject[2], store(normals));
	}
	
	@Override
	public void render(Matrix4f transformMatrix) {
		Shader s = renderer.getShader();
		Camera c = renderer.getCamera();
		
		glBindVertexArray(renderObject[0]);
		
		glEnableVertexAttribArray(Renderer.VERTEX_INDEX);
		glEnableVertexAttribArray(Renderer.NORMAL_INDEX);
		glEnableVertexAttribArray(Renderer.TEX_COORD_INDEX);
		
			Matrix4f transformedView = c.generateViewmodel()
					 .mul(transformMatrix);
			
			s.pushView(transformedView);
			
			glDrawElements(GL_TRIANGLES, indices);
			
		
		glDisableVertexAttribArray(Renderer.VERTEX_INDEX);
		glDisableVertexAttribArray(Renderer.NORMAL_INDEX);
		glDisableVertexAttribArray(Renderer.TEX_COORD_INDEX);
		
		glBindVertexArray(0);
	}
	
	public void setRenderer(Renderer r) {
		this.renderer = r;
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
