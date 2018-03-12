package bengine.rendering;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;

import bengine.Scene;
import bengine.State;
import bengine.assets.Shader;
import bengine.entities.Entity;
import bengine.rendering.renderers.EntityRenderer;

public class Renderer {
	
	EntityRenderer renderer;
	
	public Renderer(Material defaultMaterial) {
		renderer = new EntityRenderer(defaultMaterial);
	}
	
	public void clear(Vector3f color) {
		glClearColor(color.x, color.y, color.z, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void render(Scene scene) {
		
		for (Entity e : scene.getEntities()) {
			renderer.render(e, scene.getCamera());
		}
		
	}
}
