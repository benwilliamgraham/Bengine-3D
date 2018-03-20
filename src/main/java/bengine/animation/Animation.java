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
	
	private boolean isPlaying;
	
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
	
	public void play() {
		isPlaying = true;
	}
	
	public void pause() {
		isPlaying = false;
	}
	
	public void update(float delta) {
		if (isPlaying) {
			time += delta;
		}
	}
	
	public void reset() {
		time = 0;
	}
	
	public Matrix4f[] GetBoneData() {
		
		float animTime = convertAnimTime(time);
		
		if (animTime > duration) {
			if (doLoop) {
				animTime = animTime % duration;
			} else {
				animTime = duration;
			}
		}
		
		globalInverseTransform = convertMat(scene.mRootNode().mTransformation()).invert();
		
		ReadSkeletonPositions(animTime, scene.mRootNode(), new Matrix4f().identity()); //"Poses" the skeleton, so to speak.
		
		Matrix4f[] boneMatricies = new Matrix4f[skeleton.bones.length]; 
		
		for (int b = 0; b < boneMatricies.length; b++) {
			boneMatricies[b] = new Matrix4f(skeleton.bones[b].finalTransform);
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
			Matrix4f scaling  = GetScalingAt(time, nodeAnimation);
			
			//System.out.println(scaling.toString());
			
			nodeTransformation = new Matrix4f()
					.identity()
					.mul(position)
					.mul(rotation);
					//.mul(scaling);
		} else {
			//System.out.println(node.mName().dataString());
		}
		
		System.out.println(node.mName().dataString());
		System.out.println(parentTransform.toString());
		System.out.println("Children: " + node.mNumChildren());
		
		
		Matrix4f globalTransform = new Matrix4f()
				.mul(parentTransform)
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
	
	private Matrix4f GetPositionAt(float time, AINodeAnim nodeAnimation) {
		
		Matrix4f mat = new Matrix4f().identity();
		
		if (nodeAnimation.mNumPositionKeys() == 1) {
			return mat.translate(convertVec(nodeAnimation.mPositionKeys().get().mValue()));
		} else if (nodeAnimation.mNumPositionKeys() == 0) {
			return mat;
		}
		
		AIVectorKey start = null, end = null;
		
		float factor = 0;
		
		for (int k = 0; k < nodeAnimation.mNumPositionKeys() - 1; k++) {
			
			if (time == nodeAnimation.mPositionKeys().get(k).mTime()) {
				return mat.translate(convertVec(nodeAnimation.mPositionKeys().get(k).mValue()));
			}
			
			if (time < nodeAnimation.mPositionKeys().get(k + 1).mTime()) {
				
				start = nodeAnimation.mPositionKeys().get(k);
				end = nodeAnimation.mPositionKeys().get(k + 1);
				
				factor = (float) ((end.mTime() - time) / (end.mTime() - start.mTime()));
				break;
			}
		}
		
		if (start == null && end == null) {
			mat.translate(convertVec(nodeAnimation.mPositionKeys().get(nodeAnimation.mNumPositionKeys() - 1).mValue()));
		} else {
			Vector3f position = convertVec(start.mValue())
					.lerp(convertVec(end.mValue()), factor);
			
			mat.translate(position);
		}
		
		return mat;
	}
	
	private Matrix4f GetRotationAt(float time, AINodeAnim nodeAnimation) {
		Matrix4f mat = new Matrix4f().identity();
		
		if (nodeAnimation.mNumRotationKeys() == 1) {
			return mat.rotate(convertQuat(nodeAnimation.mRotationKeys().get().mValue()));
		} else if (nodeAnimation.mNumRotationKeys() == 0) {
			return mat;
		}
		
		AIQuatKey start = null, end = null;
		
		float factor = 0;
		
		for (int k = 0; k < nodeAnimation.mNumRotationKeys() - 1; k++) {
			if (time < nodeAnimation.mRotationKeys().get(k + 1).mTime()) {
				start = nodeAnimation.mRotationKeys().get(k);
				end = nodeAnimation.mRotationKeys().get(k + 1);
				
				factor = (float) ((end.mTime() - time) / (end.mTime() - start.mTime()));
				break;
			}
		}
		
		if (start == null && end == null) {
			mat.rotate(convertQuat(nodeAnimation.mRotationKeys().get(nodeAnimation.mNumRotationKeys() - 1).mValue()));
		} else {
			Quaternionf rotation = convertQuat(start.mValue()).nlerp(convertQuat(end.mValue()), factor);
			
			mat.rotate(rotation);
		}
		
		return mat;
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
	
	private float convertAnimTime(float time) {
		float ticksPerSecond = (animationData.mTicksPerSecond() == 0)? 24: (float) animationData.mTicksPerSecond();
		return time * ticksPerSecond;
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
