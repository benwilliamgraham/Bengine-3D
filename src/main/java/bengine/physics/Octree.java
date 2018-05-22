package bengine.physics;

import org.joml.Vector3f;

public class Octree {
	
	protected final int depth;
	
	protected Octree[] subTrees;
	
	protected Object[] bodies;
	
	public Octree(Vector3f origin, final int maxBodies) {
		this(origin, maxBodies, 0);
	}
	
	public Octree(Vector3f origin, final int maxBodies, final int depth) {
		this.depth = depth;
		
		this.subTrees = new Octree[8];
		this.bodies = new Object[maxBodies];
	}
	
	public int getDepth() {
		return depth;
	}
}
