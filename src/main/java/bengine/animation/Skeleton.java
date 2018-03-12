package bengine.animation;

import java.util.ArrayList;
import java.util.List;

public class Skeleton {
	public List<Bone> bones;
	
	public Skeleton() {
		this.bones = new ArrayList<Bone>();
	}
	
	public void AddBone(Bone bone) {
		this.bones.add(bone);
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
