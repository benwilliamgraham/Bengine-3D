package entities;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;

public class Entity {
	
	public TexturedModel model;
	public Vector3f position, rotation, scale;
		
	public Entity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
}
