package bengine.physics;

import org.joml.AABBf;
import org.joml.Vector3f;

public class Collider {
	
	protected AABBf bounds;
	
	public Collider(AABBf bounds) {
		this.bounds = bounds;
	}
	
	public AABBf getAABB(Vector3f position) {
		AABBf transformedBounds = new AABBf(bounds);
		transformedBounds.minX += position.x;
		transformedBounds.maxX += position.x;
		transformedBounds.minY += position.y;
		transformedBounds.maxY += position.y;
		transformedBounds.minZ += position.z;
		transformedBounds.maxZ += position.z;
		
		return transformedBounds;
	}
}
