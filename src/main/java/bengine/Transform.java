package bengine;

import org.joml.Vector3f;
import org.joml.Matrix4f;
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
		this.rotation.rotateAxis(euler.x, right());
		this.rotation.rotateAxis(euler.y, up());
		this.rotation.rotateAxis(euler.z, forwards());
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
	
	public Matrix4f generateMatrix() {
		return new Matrix4f().identity().translate(this.position).rotate(this.rotation);
	}
	
	public Matrix4f generateCameraMatrix() {
		return new Matrix4f().identity().rotate(this.rotation).translate(new Vector3f(this.position).mul(-1));
	}
}
