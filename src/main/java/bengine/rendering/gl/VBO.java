package bengine.rendering.gl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VBO {
	
	protected int glBufferObject = -1;
	protected boolean dynamic = false;
	
	public VBO(FloatBuffer data, boolean dynamic) {
		this.glBufferObject = glGenBuffers();
		this.dynamic = dynamic;
		
		bind();
		glBufferData(GL_ARRAY_BUFFER, data, (dynamic)? GL_DYNAMIC_DRAW : GL_STATIC_DRAW);
		unbind();
	}
	
	public VBO(IntBuffer data, boolean dynamic) {
		this.glBufferObject = glGenBuffers();
		this.dynamic = dynamic;
		
		bind();
		glBufferData(GL_ARRAY_BUFFER, data, (dynamic)? GL_DYNAMIC_DRAW : GL_STATIC_DRAW);
		unbind();
	}
	
	public void setData(FloatBuffer data) {
		if (dynamic) {
			bind();
			glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
			unbind();
		}
	}
	
	public void setData(IntBuffer data) {
		if (dynamic) {
			bind();
			glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
			unbind();
		}
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
