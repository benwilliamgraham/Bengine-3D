package bengine.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMatrix4x4;

public class Bone {
	
	public String name;
	public Matrix4f offsetMatrix, finalTransform;
	
	public Bone(String name, AIMatrix4x4 offsetMatrix) {
		this.name = name;
		
		this.offsetMatrix = new Matrix4f();
		
		this.offsetMatrix.m00(offsetMatrix.a1());
		this.offsetMatrix.m01(offsetMatrix.a2());
		this.offsetMatrix.m02(offsetMatrix.a3());
		this.offsetMatrix.m03(offsetMatrix.a4());
		
		this.offsetMatrix.m10(offsetMatrix.b1());
		this.offsetMatrix.m11(offsetMatrix.b2());
		this.offsetMatrix.m12(offsetMatrix.b3());
		this.offsetMatrix.m13(offsetMatrix.b4());
		
		this.offsetMatrix.m20(offsetMatrix.c1());
		this.offsetMatrix.m21(offsetMatrix.c2());
		this.offsetMatrix.m22(offsetMatrix.c3());
		this.offsetMatrix.m23(offsetMatrix.c4());
		
		this.offsetMatrix.m30(offsetMatrix.d1());
		this.offsetMatrix.m31(offsetMatrix.d2());
		this.offsetMatrix.m32(offsetMatrix.d3());
		this.offsetMatrix.m33(offsetMatrix.d4());
	}
}
