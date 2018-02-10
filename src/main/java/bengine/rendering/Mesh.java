package bengine.rendering;

import static org.lwjgl.opengl.GL11.*;
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
	
	public Vector3f[] verticies, normals, texCoords;
	public IntBuffer indicies;
	
	private int[] renderObject;
	
	private Renderer renderer;
	
	private boolean isStatic = false;
	
	public Mesh(Vector3f[] verticies, Vector3f[] normals, Vector3f[] texCoords, int[] indicies, boolean isStatic) {
		this.verticies = verticies;
		this.normals = normals;
		this.texCoords = texCoords;
		this.indicies = store(indicies);
		this.isStatic = isStatic;
	}
	
	public Mesh(Vector3f[] verticies, Vector3f[] normals, Vector3f[] texCoords, int[] indicies) {
		this(verticies, normals, texCoords, indicies, true);
	}
	
	public void create(Renderer renderer) {
		this.renderer = renderer;
		
		renderObject = renderer.createVAO(isStatic, store(verticies), store(normals), store(texCoords));
	}
	
	public void update() {
		renderer.updateBuffer(renderObject[1], store(verticies));
		renderer.updateBuffer(renderObject[2], store(normals));
	}
	
	@Override
	public void render(Matrix4f transformMatrix) {
		Shader s = renderer.getShader();
		//Camera c = renderer.getCamera();
		
		glBindVertexArray(renderObject[0]);
		
		glEnableVertexAttribArray(Renderer.VERTEX_INDEX);
		//glEnableVertexAttribArray(Renderer.NORMAL_INDEX);
		//glEnableVertexAttribArray(Renderer.TEX_COORD_INDEX);
		
			//Matrix4f transformedView = c.generateViewmodel()
			//		.mul(transformMatrix);
			
			//s.pushView(transformedView);
			
			//glDrawArrays(GL_TRIANGLES, 0, 6);
			
			glDrawElements(GL_TRIANGLES, indicies);
			
		
		glDisableVertexAttribArray(Renderer.VERTEX_INDEX);
		//glDisableVertexAttribArray(Renderer.NORMAL_INDEX);
		//glDisableVertexAttribArray(Renderer.TEX_COORD_INDEX);
		
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
}
