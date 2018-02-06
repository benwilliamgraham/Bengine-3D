package bengine.rendering;

import org.joml.Matrix4f;

public interface Drawable {
	public void render(Matrix4f transformMatrix);
	public void setRenderer(Renderer renderer);
}
