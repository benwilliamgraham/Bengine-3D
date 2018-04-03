package bengine.rendering.renderers;

public abstract class RenderStep<T> {
	
	private SteppedRenderer<T> renderer = null;
	
	public abstract void bind();
	
	public abstract void clear();
	
	public abstract void render(T t);
	
	void setRenderer(SteppedRenderer<T> renderer) {
		this.renderer = renderer;
	}
	
	protected SteppedRenderer<T> getRenderer() {
		return renderer;
	}
}
