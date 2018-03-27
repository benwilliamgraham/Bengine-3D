package bengine.physics;

import org.joml.AABBf;

public class Collider {
	
	protected AABBf boundingBox;
	
	public Collider() {
		
	}
	
	public AABBf getAABB() {
		return boundingBox;
	}
}
