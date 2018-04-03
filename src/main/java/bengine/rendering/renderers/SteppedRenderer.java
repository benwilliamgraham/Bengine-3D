package bengine.rendering.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bengine.rendering.Material;

public class SteppedRenderer<T> extends Renderer<T> {

	public Map<String, Object> stepPassData = new HashMap<String, Object>();
	
	List<RenderStep<T>> renderSteps;
	
	RenderStep<T> primaryRenderStep;
	
	public SteppedRenderer(Material defaultMaterial, RenderStep<T> primaryRenderStep) {
		super(defaultMaterial);
		this.primaryRenderStep = primaryRenderStep;
		this.primaryRenderStep.setRenderer(this);
		this.renderSteps = new ArrayList<RenderStep<T>>();
	}
	
	public void addStep(RenderStep<T> renderStep) {
		this.renderSteps.add(renderStep);
		renderStep.setRenderer(this);
	}
	
	@Override
	public void clear() {
		
		for (RenderStep<T> step : renderSteps) {
			step.bind();
			step.clear();
		}
		
		
		primaryRenderStep.bind();
		primaryRenderStep.clear();
	}
	
	@Override
	public void render(T t) {
		for (RenderStep<T> step : renderSteps) {
			step.bind();
			step.render(t);
		}
		
		primaryRenderStep.bind();
		primaryRenderStep.render(t);
	}

}
