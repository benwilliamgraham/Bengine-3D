package bengine.animation;

import java.util.ArrayList;
import java.util.List;

public class Skeleton {
	public ArrayList<Bone> bones;
	
	public Skeleton() {
		this.bones = new ArrayList<Bone>();
	}
	
	public void AddBone(int index, Bone bone) {
		if (index == bones.size()) {
			bones.add(bone);
		} else {
			bones.set(index, bone);
		}
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
		for (int i = 0; i < bones.size(); i++) {
			if (bones.get(i).name.equals(boneName)) {
				return i;
			}
		}
		
		return -1;
	}
}
