package bengine.entities;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import bengine.Game;
import bengine.Transform;

public class Camera {

	public String name = "unnamed";
	
	public Transform transform;
	
	public Vector3f clearColor;
	
	protected Matrix4f viewMatrix;
	
	public Camera(Vector3f position, float fov, float farPlane) {
		this.transform = new Transform(position, new Quaternionf());
		viewMatrix = new Matrix4f()
				.identity()
				.perspectiveLH((float) (fov * Math.PI / 180.0f), Game.getCurrent().getAspect(), 0.1f, farPlane);
		
		this.clearColor = new Vector3f(0.4f, 0.6f, 0.9f);
	}
	
	public void onUpdate(float delta) {}
	
	public Matrix4f generateView() {
		return new Matrix4f(viewMatrix).mul(transform.generateCameraMatrix());
	}
	
	public Matrix4f generateProjection() {
		return new Matrix4f(viewMatrix);
	}
}
