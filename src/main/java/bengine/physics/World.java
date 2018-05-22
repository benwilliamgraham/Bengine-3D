package bengine.physics;

import org.joml.Rayf;

import java.util.ArrayList;
import java.util.List;

public class World {
	List<Body> bodies = new ArrayList<Body>();
	
	public World() {
		
	}
	
	public boolean testRay(Rayf ray) {
		for (Body b : bodies) {
			if (b.getBounds().testRay(ray)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void addBody(Body b) {
		bodies.add(b);
		b.setWorld(this);
	}
	
	public List<Body> getBodies() {
		return bodies;
	}
}
