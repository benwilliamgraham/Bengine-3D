package bengine;

import bengine.rendering.renderers.Renderer;

public interface State {
	public void onCreated(Game game);
	public void onUpdate(float delta);
	public void onDraw();
	public void onDestroyed();
	
	public Renderer getRenderer();
}
