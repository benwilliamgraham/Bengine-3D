package bengine.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		public Map<Integer, Float> weights = new HashMap<Integer, Float>();
		
		public Vector4f getWeightData() {
			Vector4f weightData = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
			
			Iterator<Float> it = weights.values().iterator();
			
			if (!it.hasNext()) return weightData;
			weightData.x = it.next();
			if (!it.hasNext()) return weightData;
			weightData.y = it.next();
			if (!it.hasNext()) return weightData;
			weightData.z = it.next();
			if (!it.hasNext()) return weightData;
			weightData.w = it.next();
			
			return weightData;
		}
		
		public Vector4i getBoneData() { 
			Vector4i boneData = new Vector4i(-1, -1, -1, -1);
			
			Iterator<Integer> it = weights.keySet().iterator();
			
			if (!it.hasNext()) return boneData;	
			boneData.x = it.next();
			if (!it.hasNext()) return boneData;
			boneData.y = it.next();
			if (!it.hasNext()) return boneData;
			boneData.z = it.next();
			if (!it.hasNext()) return boneData;
			boneData.w = it.next();
			
			
			return boneData;
		}
		
		public void AddWeight(int bone, float weight) {
			if (weights.size() == 4) {
				weights.put(bone, weight);
			}
		}
	}
}
