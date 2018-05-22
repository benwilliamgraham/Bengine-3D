package bengine.rendering.renderers;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;

import bengine.Scene;
import bengine.State;
import bengine.assets.Shader;
import bengine.entities.Entity;
import bengine.rendering.Material;
import bengine.rendering.renderers.EntityRenderer;
import bengine.rendering.renderers.steps.ShadowEntityStep;

public class SceneRenderer extends Renderer<Scene> {
	
	EntityRenderer renderer;
	SkyboxRenderer skyRenderer;
	
	public SceneRenderer(Material defaultMaterial) {
		super(defaultMaterial);
		
		renderer = new EntityRenderer(defaultMaterial);
		skyRenderer = new SkyboxRenderer();
	}
	
	public void render(Scene scene) {
		
		if (scene.getSky() != null) skyRenderer.render(scene.getSky());
		
		glClear(GL_DEPTH_BUFFER_BIT);
		
		for (Entity e : scene.getEntities()) {
			renderer.render(e);
		}
	}

	@Override
	public void clear() {
		renderer.clear();
	}
	
	public EntityRenderer getEntityRenderer() {
		return renderer;
	}
}
