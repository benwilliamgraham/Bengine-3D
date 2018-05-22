package bengine;

import org.joml.Vector3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class Transform {
	public Vector3f position, scale;
	public Quaternionf rotation;

	public Transform() {
		this.position = new Vector3f();
		this.rotation = new Quaternionf();
		this.scale = new Vector3f(1.0f, 1.0f, 1.0f);
	}
	
	public Transform(Vector3f position, Quaternionf rotation) {
		this.position = position;
		this.rotation = rotation;
		this.scale = new Vector3f(1.0f, 1.0f, 1.0f);
	}
	
	public void move(Vector3f delta) {
		this.position.add(delta);
	}
	
	public void rotate(Vector3f euler) {
		this.rotation.rotateAxis(euler.x, right());
		this.rotation.rotateAxis(euler.y, up());
		this.rotation.rotateAxis(euler.z, forwards());
	}
	
	public void lookAt(Vector3f pos) {
		Vector3f lookDirection = new Vector3f(this.position).sub(pos).normalize();
		
		new Quaternionf().lookAlong(lookDirection, new Vector3f(0.0f, 1.0f, 0.0f), this.rotation);
	}
	
	public Vector3f forwards() {
		Vector3f direction = new Vector3f();
		
		this.rotation.positiveZ(direction);
		
		return direction;
	}
	
	public Vector3f right() {
		Vector3f direction = new Vector3f();
		
		this.rotation.positiveX(direction);
		
		return direction;
	}
	
	public Vector3f up() {
		Vector3f direction = new Vector3f();
		
		this.rotation.positiveY(direction);
		
		return direction;
	}
	
	public Matrix4f generateMatrix() {
		return new Matrix4f().identity().translate(this.position).rotate(new Quaternionf(this.rotation).invert()).scale(this.scale);
	}
	
	public Matrix4f generateCameraMatrix() {
		
		return new Matrix4f().rotate(this.rotation).translate(new Vector3f(this.position).mul(-1));
	}
}
