package bengine.entities;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.Display;

public class Camera {

	public Vector3f position;
	public Quaternionf rotation;
	
	public Vector3f clearColor;
	
	protected Matrix4f viewMatrix;
	
	public Camera(Vector3f position, float fov, float farPlane) {
		this.position = new Vector3f(position);
		viewMatrix = new Matrix4f();
		viewMatrix.perspective(fov, (float) Display.getWidth() / Display.getHeight(), 0.1f, farPlane);
		
		this.clearColor = new Vector3f();
	}
	
	public Matrix4f generateViewmodel() {
		Matrix4f mat = new Matrix4f(viewMatrix);
		mat.rotate(rotation);
		mat.translate(this.position);
		
		return mat;
	}
}
