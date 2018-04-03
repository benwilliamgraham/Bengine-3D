package bengine.entities;

import org.joml.Vector3f;

public class Light {
	public Vector3f position, ambient, diffuse, specular;
	
	public Light(Vector3f position) {
		this.position = position;
		this.ambient = new Vector3f(1, 1, 1);
		this.diffuse = new Vector3f(1, 1, 1);
		this.specular = new Vector3f(1, 1, 1);
	}
}
