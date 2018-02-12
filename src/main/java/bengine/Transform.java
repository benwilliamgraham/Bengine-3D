package bengine;

import org.joml.Vector3f;
import org.joml.Quaternionf;

public class Transform {
	public Vector3f position;
	public Quaternionf rotation;

	public Transform(Vector3f position, Quaternionf rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	public void move(Vector3f delta) {
		this.position.add(delta);
	}
	
	public Vector3f forwards() {
		return new Vector3f(0.0f, 0.0f, 1.0f)
				.rotate(this.rotation);
	}
	
	public Vector3f right() {
		return new Vector3f(1.0f, 0.0f, 0.0f)
				.rotate(this.rotation);
	}
	
	public Vector3f up() {
		return new Vector3f(0.0f, 1.0f, 0.0f)
				.rotate(rotation);
	}
}
