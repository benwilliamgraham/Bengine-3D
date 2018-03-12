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
	
	public boolean doLoop = false;
	
	public float duration;
	public float time;
	
	protected String name;
	protected Skeleton skeleton;
	protected AIAnimation animationData;
	protected AIScene scene;
	
	private boolean isPlaying;
	
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
		
		Matrix4f globalInverseTransform = convertMat(scene.mRootNode().mTransformation()).invert();
		
		Matrix4f nodeTransformation = convertMat(node.mTransformation());
		
		AINodeAnim nodeAnimation = GetNodeAnim(animationData, node.mName().dataString());
		
		if (nodeAnimation != null) {
			
			Matrix4f position = GetPositionAt(time, nodeAnimation);
			Matrix4f rotation = GetRotationAt(time, nodeAnimation);
			Matrix4f scaling  = GetScalingAt(time, nodeAnimation);
			
			nodeTransformation = new Matrix4f()
					.identity()
					.mul(position)
					.mul(rotation)
					.mul(scaling);
		}
		
		Matrix4f globalTransform = new Matrix4f()
				.mul(parentTransform)
				.mul(nodeTransformation);
		
		Bone b = skeleton.GetBone(node.mName().dataString());
		
		if (b != null) {
			b.finalTransform = globalInverseTransform
					.mul(globalTransform)
					.mul(b.offsetMatrix);
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
			mat.translate(convertVec(nodeAnimation.mPositionKeys().get().mValue()));
		}
		
		AIVectorKey start = null, end = null;
		
		float factor = 0;
		
		for (int k = 0; k < nodeAnimation.mNumPositionKeys() - 1; k++) {
			if (time < nodeAnimation.mPositionKeys().get(k + 1).mTime()) {
				start = nodeAnimation.mPositionKeys().get(k);
				end = nodeAnimation.mPositionKeys().get(k + 1);
				
				factor = (float) ((end.mTime() - time) / (end.mTime() - start.mTime()));
				break;
			}
		}
		
		Vector3f position = convertVec(start.mValue()).lerp(convertVec(end.mValue()), factor);
		
		mat.translate(position);
		
		return mat;
	}
	
	private Matrix4f GetRotationAt(float time, AINodeAnim nodeAnimation) {
		Matrix4f mat = new Matrix4f().identity();
		
		if (nodeAnimation.mNumRotationKeys() == 1) {
			mat.rotate(convertQuat(nodeAnimation.mRotationKeys().get().mValue()));
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
		
		Quaternionf rotation = convertQuat(start.mValue()).nlerp(convertQuat(end.mValue()), factor);
		
		mat.rotate(rotation);
		
		return mat;
	}
	
	private Matrix4f GetScalingAt(float time, AINodeAnim nodeAnimation) {
		Matrix4f mat = new Matrix4f().identity();
		
		if (nodeAnimation.mNumScalingKeys() == 1) {
			mat.scale(convertVec(nodeAnimation.mPositionKeys().get().mValue()));
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
		
		Vector3f scale = convertVec(start.mValue()).lerp(convertVec(end.mValue()), factor);
		
		mat.scale(scale);
		
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
