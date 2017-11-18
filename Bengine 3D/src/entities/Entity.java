package entities;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;

public abstract class Entity {
	
	public Vector3f position, rotation, scale;
		
	public Entity(Vector3f position, Vector3f rotation, Vector3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public abstract int getEntityId();
}
