package bengine;

import org.joml.Vector3f;
import org.joml.Quaternionf;

public class Transform {
	public Vector3f position;
	public Quaternionf rotation;

	public Transform() {
		this.position = new Vector3f();
		this.rotation = new Quaternionf();
	}
	
	public Transform(Vector3f position, Quaternionf rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	public void move(Vector3f delta) {
		this.position.add(delta);
	}
	
	public void rotate(Vector3f euler) {
		this.rotation.rotate(euler.x, euler.y, euler.z);
	}
	
	public Vector3f forwards() {
		Quaternionf invRot = new Quaternionf();
		this.rotation.invert(invRot);
		return new Vector3f(0.0f, 0.0f, 1.0f)
				.rotate(invRot);
	}
	
	public Vector3f right() {
		Quaternionf invRot = new Quaternionf();
		this.rotation.invert(invRot);
		return new Vector3f(1.0f, 0.0f, 0.0f)
				.rotate(invRot);
	}
	
	public Vector3f up() {
		Quaternionf invRot = new Quaternionf();
		this.rotation.invert(invRot);
		return new Vector3f(0.0f, 1.0f, 0.0f)
				.rotate(invRot);
	}
}
