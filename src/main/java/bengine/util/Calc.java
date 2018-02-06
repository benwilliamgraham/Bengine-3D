package bengine.util;

import org.joml.*;

import bengine.entities.Camera;

public class Calc {
	public static Vector3f normaliseVector(Vector3f vector){
		float mag = vector.length();
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
