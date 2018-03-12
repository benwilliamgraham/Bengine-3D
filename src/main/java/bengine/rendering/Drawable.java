package bengine.rendering;

import java.nio.IntBuffer;

import bengine.rendering.gl.VAO;

public interface Drawable {
	public VAO getRenderable();
	
	public IntBuffer getIndices();
}
