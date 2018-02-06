package bengine;

import bengine.rendering.Renderer;

public interface State {
	public void onCreated();
	public void onUpdate(float delta);
	public void onDraw(Renderer renderer);
	public void onDestroyed();
}
