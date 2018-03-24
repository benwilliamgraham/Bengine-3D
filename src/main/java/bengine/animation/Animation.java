package bengine.animation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;

public class Animation {
	
	public boolean doLoop = true;
	
	public float duration;
	public float time;
	
	protected String name;
	protected Skeleton skeleton;
	protected AIAnimation animationData;
	protected AIScene scene;
	
	private Matrix4f globalInverseTransform;
	
	public Animation(AIAnimation animation, AIScene scene) {
		this.name = animation.mName().dataString();
		this.duration = (float) animation.mDuration();
		this.animationData = animation;
		this.scene = scene;
	}
	
	public void attach(Skeleton k) {
		if (skeleton == null) {
			skeleton = k;
		}
	}
	
	public float convertAnimTime(float time) {
		float ticksPerSecond = (animationData.mTicksPerSecond() == 0)? 24: (float) animationData.mTicksPerSecond();
		return time * ticksPerSecond;
	}
	
	public Matrix4f[] GetBoneData() {
		float animTime = time;
		
		if (animTime > duration) {
			if (doLoop) {
				animTime = animTime % duration;
			} else {
				animTime = duration;
			}
		}
		
		//System.out.println(time + " : " + animTime);
		
		globalInverseTransform = convertMat(scene.mRootNode().mTransformation()).invert();
		
		ReadSkeletonPositions(animTime, scene.mRootNode(), new Matrix4f().identity()); //"Poses" the skeleton, so to speak.
		
		Matrix4f[] boneMatricies = new Matrix4f[skeleton.bones.size()]; 
		
		for (int b = 0; b < boneMatricies.length; b++) {
			boneMatricies[b] = new Matrix4f(skeleton.bones.get(b).finalTransform);
		}
		
		return boneMatricies;
	}
	
	public Skeleton getSkeleton() {
		return skeleton;
	}
	
	public String getName() {
		return name;
	}
	
	private void ReadSkeletonPositions(float time, AINode node, Matrix4f parentTransform) {
		//System.out.println("Reading node: " + node.mName().dataString());
		
		Matrix4f nodeTransformation = convertMat(node.mTransformation());
		
		AINodeAnim nodeAnimation = GetNodeAnim(animationData, node.mName().dataString());
		
		if (nodeAnimation != null) {
			
			//System.out.println("Animating node: " + node.mName().dataString());
			
			Matrix4f position = GetPositionAt(time, nodeAnimation);
			Matrix4f rotation = GetRotationAt(time, nodeAnimation);
			Matrix4f scaling  = GetScalingAt(time, nodeAnimation);//.scale(100); //Blender has an issue where the units are off. so fixing it.
			
			//System.out.println(node.mName().dataString());
			//System.out.print ln(position.toString());
			
			nodeTransformation = new Matrix4f(position)
					.mul(rotation)
					;//.mul(scaling);
		} else {
			//System.out.println(node.mName().dataString());
		}
		
		//System.out.println(parentTransform.toString());
		//System.out.println("Children: " + node.mNumChildren());
		
		
		Matrix4f globalTransform = new Matrix4f(parentTransform)
				.mul(nodeTransformation);
		
		Bone b = skeleton.GetBone(node.mName().dataString());
		
		if (b != null) {
			b.finalTransform = new Matrix4f(globalInverseTransform)
					.mul(globalTransform)
					.mul(new Matrix4f(b.offsetMatrix));
		} else {
			//System.out.println(node.mName().dataString());
		}
		
		for (int c = 0; c < node.mNumChildren(); c++) {
			ReadSkeletonPositions(time, AINode.create(node.mChildren().get(c)), globalTransform);
		}
	}
	
	private AINodeAnim GetNodeAnim(AIAnimation a, String nodeName) {
		
		for (int c = 0; c < a.mNumChannels(); c++) {
			AINodeAnim nodeAnim = AINodeAnim.create(a.mChannels().get(c));
			
			if (nodeAnim.mNodeName().dataString().equals(nodeName)) {
				return nodeAnim;
			}
		}
		
		return null;
	}
	
	private int GetPositionKey(float time, AINodeAnim nodeAnimation) {
		for (int x = 0; x < nodeAnimation.mNumPositionKeys() - 1; x++) {
			if ((float) nodeAnimation.mPositionKeys().get(x + 1).mTime() > time) {
				return x;
			}
		}
		
		return 0;
	}
	
	private int GetRotationKey(float time, AINodeAnim nodeAnimation) {
		for (int x = 0; x < nodeAnimation.mNumRotationKeys() - 1; x++) {
			if ((float) nodeAnimation.mRotationKeys().get(x + 1).mTime() > time) {
				return x;
			}
		}
		
		return 0;
	}
	
	private int GetScalingKey(float time, AINodeAnim nodeAnimation) {
		for (int x = 0; x < nodeAnimation.mNumScalingKeys() - 1; x++) {
			if ((float) nodeAnimation.mScalingKeys().get(x + 1).mTime() > time) {
				return x;
			}
		}
		
		return 0;
	}
	
	private Matrix4f GetPositionAt(float time, AINodeAnim nodeAnimation) {
		
		Matrix4f posMatrix = new Matrix4f();
		
		if (nodeAnimation.mNumPositionKeys() == 1) {
			
			Vector3f position = convertVec(nodeAnimation.mPositionKeys().get(0).mValue());
			
			posMatrix.translate(position);
			
		} else {
			int posKey = GetPositionKey(time, nodeAnimation);
			
			if (posKey == nodeAnimation.mNumPositionKeys() - 1) { //Is this the last position in the animation? if so just stay there.
				
				posMatrix.translate(convertVec(nodeAnimation.mPositionKeys().get(posKey).mValue()));
			} else {
				AIVectorKey start = nodeAnimation.mPositionKeys().get(posKey);
				AIVectorKey   end = nodeAnimation.mPositionKeys().get(posKey + 1);
				
				float startTime = (float) start.mTime();
				float endTime = (float) end.mTime();
				
				float factor = (time - startTime) / (endTime - startTime);
				
				Vector3f startPos = convertVec(start.mValue());
				Vector3f endPos = convertVec(end.mValue());
				
				Vector3f position = startPos.lerp(endPos, factor);
				
				posMatrix.translate(position);
			}
			
		}
		
		return posMatrix;
	}
	
	private Matrix4f GetRotationAt(float time, AINodeAnim nodeAnimation) {
		Matrix4f rotMatrix = new Matrix4f();
		
		if (nodeAnimation.mNumRotationKeys() == 1) {
			Quaternionf rotation = convertQuat(nodeAnimation.mRotationKeys().get(0).mValue());
			
			rotMatrix.rotate(rotation);
		} else {
			int rotKey = GetRotationKey(time, nodeAnimation);
			
			if (rotKey == nodeAnimation.mNumRotationKeys() - 1) {
				
				rotMatrix.rotate(convertQuat(nodeAnimation.mRotationKeys().get(rotKey).mValue()));
			} else {
				AIQuatKey start = nodeAnimation.mRotationKeys().get(rotKey);
				AIQuatKey   end = nodeAnimation.mRotationKeys().get(rotKey + 1);
				
				float startTime = (float) start.mTime();
				float endTime = (float) end.mTime();
				
				float factor = (time - startTime) / (endTime - startTime);
				
				Quaternionf startRot = convertQuat(start.mValue());
				Quaternionf   endRot = convertQuat(end.mValue());
				
				Quaternionf rotation = startRot.nlerp(endRot, factor);
				
				rotMatrix.rotate(rotation);
			}
		}
		
		return rotMatrix;
	}
	
	private Matrix4f GetScalingAt(float time, AINodeAnim nodeAnimation) {
		
		Matrix4f mat = new Matrix4f().identity();
		
		if (nodeAnimation.mNumScalingKeys() == 1) {
			return mat.scale(convertVec(nodeAnimation.mPositionKeys().get().mValue()));
		} else if (nodeAnimation.mNumScalingKeys() == 0) {
			return mat;
		}
		
		AIVectorKey start = null, end = null;
		
		float factor = 0;
		
		for (int k = 0; k < nodeAnimation.mNumScalingKeys() - 1; k++) {
			if (time < nodeAnimation.mScalingKeys().get(k + 1).mTime()) {
				start = nodeAnimation.mScalingKeys().get(k);
				end = nodeAnimation.mScalingKeys().get(k + 1);
				
				factor = (float) ((end.mTime() - time) / (end.mTime() - start.mTime()));
				break;
			}
		}
		
		if (start == null && end == null) {
			mat.scale(convertVec(nodeAnimation.mScalingKeys().get(nodeAnimation.mNumScalingKeys() - 1).mValue()));
		} else {
			Vector3f scale = convertVec(start.mValue()).lerp(convertVec(end.mValue()), factor);
			mat.scale(scale);
		}
		
		return mat;
	}
	
	private Matrix4f convertMat(AIMatrix4x4 mat) {
		return new Matrix4f(
				mat.a1(), mat.a2(), mat.a3(), mat.a4(),
				mat.b1(), mat.b2(), mat.b3(), mat.b4(),
				mat.c1(), mat.c2(), mat.c3(), mat.c4(),
				mat.d1(), mat.d2(), mat.d3(), mat.d4());
	}
	
	private Quaternionf convertQuat(AIQuaternion quat) {
		return new Quaternionf(quat.x(), quat.y(), quat.z(), quat.w());
	}
	
	private Vector3f convertVec(AIVector3D vec) {
		return new Vector3f(vec.x(), vec.y(), vec.z());
	}
}
