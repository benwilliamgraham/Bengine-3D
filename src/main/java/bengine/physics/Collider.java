package bengine.physics;

import org.joml.AABBf;

import bengine.entities.Entity;
import bengine.entities.EntityComponent;

public abstract class Collider implements EntityComponent {
	
	protected AABBf boundingBox;
	
	private Entity entity;
	
	public Collider(Entity e) {
		this.entity = e;
	}
	
	public AABBf getAABB() {
		return boundingBox;
	}
	
	@Override
	public Entity getEntity() {
		return entity;
	}
}
