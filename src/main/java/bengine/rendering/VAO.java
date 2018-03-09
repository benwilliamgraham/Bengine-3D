package bengine.rendering;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class VAO {
	protected int glArrayObject = -1;
	protected List<Integer> attribs = new ArrayList<Integer>();
	
	public VAO() {
		this.glArrayObject = glGenVertexArrays();
	}
	
	public void attach(int index, VBO buffer, int glType, int size, int bytes) {
		attribs.add(index);
		
		bind();
		buffer.bind();
		glVertexAttribPointer(index, size, glType, false, 0, 0L);
		buffer.unbind();
		unbind();
	}
	
	public void bind() {
		glBindVertexArray(glArrayObject);
		
		for (Integer i : attribs) {
			glEnableVertexAttribArray(i);
		}
	}
	
	public void unbind() {
		glBindVertexArray(0);
		
		for (Integer i : attribs) {
			glDisableVertexAttribArray(i);
		}
	}
	
	public void destroy() {
		glDeleteVertexArrays(this.glArrayObject);
	}
}
