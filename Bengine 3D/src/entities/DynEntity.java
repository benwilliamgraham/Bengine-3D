package entities;

import org.lwjgl.util.vector.Vector3f;

import data.TexturedModel;
import world.World;

public abstract class DynEntity extends Entity{

	public TexturedModel model;
	
	public DynEntity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
		super(position, rotation, scale);
		this.model = model;
	}
	
	public abstract boolean update(World world, String key);
}
