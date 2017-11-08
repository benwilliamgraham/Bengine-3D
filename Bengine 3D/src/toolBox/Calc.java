package toolBox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

public class Calc {
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate(rotation.x, new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate(rotation.y, new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(rotation.z, new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(scale, matrix, matrix);
		return matrix;
	}

	
	public static Matrix4f createViewMatrix(Camera camera){
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate(camera.pitch, new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate(camera.yaw, new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Matrix4f.translate(new Vector3f(-camera.position.x, -camera.position.y, -camera.position.z), viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static float calculateMagnitude(Vector3f vector){
		return (float) Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
	}
	
	public static Vector3f normaliseVector(Vector3f vector){
		float mag = calculateMagnitude(vector);
		return new Vector3f(vector.x / mag, vector.y / mag, vector.z / mag);
	}
	
	public static Vector3f calculateNormal(Vector3f p1, Vector3f p2, Vector3f p3){
		Vector3f U = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
		Vector3f V = new Vector3f(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);
		
		Vector3f normal = new Vector3f(0, 0, 0);
		normal.x = (U.y * V.z) - (U.z * V.y);
		normal.y = (U.z * V.x) - (U.x * V.z);
		normal.z = (U.x * V.y) - (U.y * V.x);
		
		return normal;
	}
}
