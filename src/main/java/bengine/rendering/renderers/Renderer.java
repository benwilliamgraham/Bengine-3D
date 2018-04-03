package bengine.rendering.renderers;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.joml.Vector3f;

import bengine.rendering.Material;

public abstract class Renderer<T> {
	
	private Material defaultMaterial;
	
	public Renderer(Material defaultMaterial) {
		this.defaultMaterial = defaultMaterial;
	}
	
	public abstract void clear();
	
	public abstract void render(T t);
	
	public Material getDefaultMaterial() {
		return defaultMaterial;
	}
}
