package bengine.animation;

import java.util.ArrayList;
import java.util.List;

public class Skeleton {
	public Bone[] bones;
	
	public Skeleton(int numBones) {
		this.bones = new Bone[numBones];
	}
	
	public void AddBone(int index, Bone bone) {
		this.bones[index] = bone;
	}
	
	public Bone GetBone(String boneName) {
		for (Bone b : this.bones) {
			if (b.name.equals(boneName)) {
				return b;
			}
		}
		
		return null;
	}
	
	public int ResolveName(String boneName) {
		for (int i = 0; i < bones.length; i++) {
			if (bones[i].name.equals(boneName)) {
				return i;
			}
		}
		
		return -1;
	}
}
