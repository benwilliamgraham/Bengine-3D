package bengine.animation;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Animation {
	
	public float duration;
	
	protected String name;
	protected Skeleton skeleton;
	protected Map<String, BoneAnimationData> animationData;
	
	public Animation(String name) {
		this.name = name;
		this.animationData = new HashMap<String, BoneAnimationData>();
	}
	
	public void AddPositionKeyframe(String bone, KeyFrame frame) {
		
		if (!animationData.containsKey(bone)) {
			animationData.put(bone, new BoneAnimationData());
		}
		
		animationData.get(bone).AddPositionKeyframe(frame);
	}
	
	public void AddRotationKeyframe(String bone, KeyFrame frame) {
		
		if (!animationData.containsKey(bone)) {
			animationData.put(bone, new BoneAnimationData());
		}
		
		animationData.get(bone).AddRotationKeyframe(frame);
	}
	
	public void AddScalingKeyframe(String bone, KeyFrame frame) {

		if (!animationData.containsKey(bone)) {
			animationData.put(bone, new BoneAnimationData());
		}
		
		animationData.get(bone).AddScalingKeyframe(frame);
	}
	
	public void attach(Skeleton k) {
		if (skeleton == null) {
			skeleton = k;
		}
	}
	
	public FloatBuffer GetBoneDataAtTime(float t) {
		
		float time = t % this.duration;
		
		int numBones = this.skeleton.bones.size();
		
		FloatBuffer fb = ByteBuffer.allocateDirect(numBones * 16 * Float.BYTES)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		for (Bone b : this.skeleton.bones.values()) {
			
			Matrix4f transform = new Matrix4f(b.offsetMatrix);
			
			BoneAnimationData a = this.animationData.get(b.name);
			
			Vector3f position = InterpolatePosition(a.positionKeyframes, time);
			Quaternionf rotation = InterpolateRotation(a.rotationKeyframes, time);
			Vector3f scale = InterpolateScale(a.scaleKeyframes, time);
			
			transform
				.scale(scale)
				.rotate(rotation)
				.translateLocal(position);
			
			transform.get(fb);
			fb.position(fb.position() + 16);
		}
		
		fb.flip();
		
		return fb;
	}
	
	public Skeleton getSkeleton() {
		return skeleton;
	}
	
	public String getName() {
		return name;
	}
	
	private Vector3f InterpolatePosition(List<KeyFrame> frames, float time) {
		
		Vector3f position = new Vector3f();
		
		ListIterator<KeyFrame> i = frames.listIterator();
		
		while (i.hasNext()) {
			KeyFrame k1 = i.next();
			
			if (k1.time > time) {
				position = new Vector3f();
			} else if (k1.time == time) {
				position = new Vector3f(k1.position);
			} if (k1.time < time) {
				if (i.hasNext()) { //is there another element in the list after this.
					KeyFrame k2 = i.next();
					
					if (k2.time > time) {
						float delta = (time - k1.time) / (k2.time - k1.time);
						position = new Vector3f();
						k1.position.lerp(k2.position, delta, position);
						break;
					} else {
						i.previous();
						continue;
					}
				}
			}
		}
		
		return position;
	}
	
	private Vector3f InterpolateScale(List<KeyFrame> frames, float time) {
		Vector3f scale = new Vector3f();
		
		ListIterator<KeyFrame> i = frames.listIterator();
		
		while (i.hasNext()) {
			KeyFrame k1 = i.next();
			
			if (k1.time > time) {
				scale = new Vector3f(1, 1, 1);
			} else if (k1.time == time) {
				scale = new Vector3f(k1.scale);
			} if (k1.time < time) {
				if (i.hasNext()) { //is there another element in the list after this.
					KeyFrame k2 = i.next();
					
					if (k2.time > time) {
						float delta = (time - k1.time) / (k2.time - k1.time);
						scale = new Vector3f();
						k1.scale.lerp(k2.scale, delta, scale);
						break;
					} else {
						i.previous();
						continue;
					}
				}
			}
		}
		
		return scale;
	}
	
	private Quaternionf InterpolateRotation(List<KeyFrame> frames, float time) {
		Quaternionf rotation = new Quaternionf();
		
		ListIterator<KeyFrame> i = frames.listIterator();
		
		while (i.hasNext()) {
			KeyFrame k1 = i.next();
			
			if (k1.time > time) {
				rotation = new Quaternionf();
			} else if (k1.time == time) {
				rotation = new Quaternionf(k1.rotation);
			} if (k1.time < time) {
				if (i.hasNext()) { //is there another element in the list after this.
					KeyFrame k2 = i.next();
					
					if (k2.time > time) {
						float delta = (time - k1.time) / (k2.time - k1.time);
						rotation = new Quaternionf();
						k1.rotation.slerp(k2.rotation, delta, rotation);
						break;
					} else {
						i.previous();
						continue;
					}
				}
			}
		}
		
		return rotation;
	}
	
	public static class BoneAnimationData {
		
		public List<KeyFrame> positionKeyframes, rotationKeyframes, scaleKeyframes;
		
		public BoneAnimationData() {
			this.positionKeyframes = new ArrayList<KeyFrame>();
			this.rotationKeyframes = new ArrayList<KeyFrame>();
			this.scaleKeyframes = new ArrayList<KeyFrame>();
		}
		
		public void AddPositionKeyframe(KeyFrame frame) {
			this.positionKeyframes.add(frame);
			
			this.positionKeyframes.sort(new KFComparator());
		}
		
		public void AddRotationKeyframe(KeyFrame frame) {
			this.rotationKeyframes.add(frame);
			
			this.rotationKeyframes.sort(new KFComparator());
		}
		
		public void AddScalingKeyframe(KeyFrame frame) {
			this.scaleKeyframes.add(frame);
			
			this.scaleKeyframes.sort(new KFComparator());
		}
	}
	
	static class KFComparator implements Comparator<KeyFrame> {

		@Override
		public int compare(KeyFrame o1, KeyFrame o2) {
			if (o1.time > o2.time) {
				return 1;
			} else if (o1.time < o2.time) {
				return -1;
			}
			return 0;
		}
		
	}
}
