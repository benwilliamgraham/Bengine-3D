package bengine.physics;

import org.joml.AABBf;
import org.joml.Intersectionf;
import org.joml.Rayf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.LineSegmentf;

import bengine.entities.Entity;
import magica.entities.GrassPlane;

public class Body {
	
	private static final float collisionSkin = 0.01f;
	
	public Vector3f position;
	
	protected Collider collider;
	
	protected World world;
	
	public Body(Collider collider) {
		this.collider = collider;
		this.position = new Vector3f(0, 0, 0);
	}
	
	public boolean testRay(Rayf ray, float distance) {
		
		Rayf nRay = new Rayf(ray.oX + position.x, ray.oY + position.y, ray.oZ + position.z,
				ray.dX, ray.dY, ray.dZ);
		
		
		LineSegmentf segment = new LineSegmentf(
				new Vector3f(nRay.oX, nRay.oY, nRay.oZ),
				new Vector3f(nRay.oX, nRay.oY, nRay.oZ)
				.add(new Vector3f(nRay.dX, nRay.dY, nRay.dZ)
						.mul(distance))
				);
		
		
		for (Body b : world.getBodies()) {
			if (b == this) continue;
			
			if (b.getBounds().intersectLineSegment(segment, new Vector2f()) != (Intersectionf.OUTSIDE)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void move(Vector3f movement) {
		for (Body b : world.getBodies()) {	 
			if (b == this) continue;
			
			 AABBf thisBounds = getBounds();
			 AABBf groundBounds = b.getBounds();
			 
			 AABBf thisBoundsX = new AABBf(thisBounds.minX + movement.x, thisBounds.minY, thisBounds.minZ,
					 thisBounds.maxX + movement.x, thisBounds.maxY, thisBounds.maxZ);
			 
			 if (thisBoundsX.testAABB(groundBounds)) {
				 if (position.x < b.position.x) {
					 movement.x = Math.min(groundBounds.minX - thisBounds.maxX - collisionSkin, movement.x);
				 } else {
					 movement.x = Math.max(groundBounds.maxX - thisBounds.minX + collisionSkin, movement.x);
				 }
			 }
			 
			 AABBf thisBoundsY = new AABBf(thisBounds.minX, thisBounds.minY + movement.y, thisBounds.minZ,
					 thisBounds.maxX, thisBounds.maxY + movement.y, thisBounds.maxZ);
			 
			 if (thisBoundsY.testAABB(groundBounds)) {
				 if (b.position.y < b.position.y) {
					 movement.y = Math.min(groundBounds.minY - thisBounds.maxY - collisionSkin, movement.y);
				 } else {
					 movement.y = Math.max(groundBounds.maxY - thisBounds.minY + collisionSkin, movement.y);
				 }
			 }
			 
			 AABBf thisBoundsZ = new AABBf(thisBounds.minX, thisBounds.minY, thisBounds.minZ + movement.z,
					 thisBounds.maxX, thisBounds.maxY, thisBounds.maxZ + movement.z);
			 
			 if (thisBoundsZ.testAABB(groundBounds)) {
				 if (b.position.z < b.position.z) {
					 movement.z = Math.min(groundBounds.minZ - thisBounds.maxZ - collisionSkin, movement.z);
				 } else {
					 movement.z = Math.max(groundBounds.maxZ - thisBounds.minZ + collisionSkin, movement.z);
				 }
			 }
		}
		
		this.position.add(movement);
	}
	
	void setWorld(World world) {
		this.world = world;
	}
	
	public AABBf getBounds() {
		return collider.getAABB(position);
	}
}
