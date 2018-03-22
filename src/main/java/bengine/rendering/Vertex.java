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
	
	public Vector3f position, normal;
	public Vector3f[] uvData;
	public SkinData skinData;
	
	public Vertex(int uvChannels) {
		this.position = new Vector3f();
		this.normal = new Vector3f();
		this.uvData = new Vector3f[uvChannels];
		
		for (int c = 0; c < uvChannels; c++) {
			uvData[c] = new Vector3f(0, 0, 0);
		}
		
		this.skinData = new SkinData();
	}
	
	public void transform(Matrix4f transformMatrix) {
		Vector4f transformedPos = transformMatrix.transform(new Vector4f(position, 1.0f));
		Vector4f transformedNormal = transformMatrix.transform(new Vector4f(normal, 0.0f));
		
		position.set(transformedPos.x, transformedPos.y, transformedPos.z);
		normal.set(transformedNormal.x, transformedNormal.y, transformedNormal.z);
	}
	
	public static class SkinData {
		protected int[] jointIds = new int[] {-1, -1, -1, -1};
		protected float[] jointWeights = new float[] {0, 0, 0, 0};
		
		private int joints = 0;
		
		public Vector4f getWeightData() {
			return new Vector4f(jointWeights[0], jointWeights[1], jointWeights[2], jointWeights[3]);
		}
		
		public Vector4i getBoneData() { 
			return new Vector4i(jointIds[0], jointIds[1], jointIds[2], jointIds[3]);
		}
		
		public void AddWeight(int bone, float weight) {
			if (joints == 4) {
				System.out.println("Too many weights!");
				return;
			}
			
			jointIds[joints] = bone;
			jointWeights[joints] = weight;
			
			joints++;
		}
	}
}
