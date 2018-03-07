package bengine.rendering;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4i;

public class Vertex {
	
	public Vector3f position, normal, texCoord;
	public SkinData skinData;
	
	public Vertex() {
		this.position = new Vector3f();
		this.normal = new Vector3f();
		this.texCoord = new Vector3f();
	}
	
	public void transform(Matrix4f transformMatrix) {
		Vector4f transformedPos = transformMatrix.transform(new Vector4f(position, 1.0f));
		Vector4f transformedNormal = transformMatrix.transform(new Vector4f(normal, 0.0f));
		
		position.set(transformedPos.x, transformedPos.y, transformedPos.z);
		normal.set(transformedNormal.x, transformedNormal.y, transformedNormal.z);
	}
	
	public static class SkinData {
		public Vector4i joints = new Vector4i();
		public Vector4f weights = new Vector4f();
	}
}
