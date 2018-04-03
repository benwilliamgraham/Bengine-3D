package bengine.rendering.renderers;

import bengine.entities.Entity;
import bengine.rendering.Material;
import bengine.rendering.renderers.steps.DefaultEntityStep;

public class EntityRenderer extends SteppedRenderer<Entity> {

	public EntityRenderer(Material defaultMaterial) {
		super(defaultMaterial, new DefaultEntityStep());
	}
}
