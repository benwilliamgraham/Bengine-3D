package bengine.rendering.gl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VBO {
	
	protected int glBufferObject = -1;
	
	private int target, usage;
	
	public VBO(FloatBuffer data, int target, int usage) {
		this.glBufferObject = glGenBuffers();
		
		this.target = target;
		this.usage = usage;
		
		bind();
		glBufferData(target, data, usage);
		unbind();
	}
	
	public VBO(IntBuffer data, int target, int usage) {
		this.glBufferObject = glGenBuffers();
		
		this.target = target;
		this.usage = usage;
		
		bind();
		glBufferData(target, data, usage);
		unbind();
	}
	
	public void setData(FloatBuffer data) {
		bind();
		glBufferData(target, data, usage);
		unbind();
	}
	
	public void setData(IntBuffer data) {
		bind();
		glBufferData(target, data, usage);
		unbind();
	}
	
	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, glBufferObject);
	}
	
	public void unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void destroy() {
		glDeleteBuffers(this.glBufferObject);
	}
	
	public int getBufferObject() {
		return glBufferObject;
	}
	
}
