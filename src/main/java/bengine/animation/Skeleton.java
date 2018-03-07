package bengine.animation;

import java.util.HashMap;
import java.util.Map;

public class Skeleton {
	public Map<String, Bone> bones;
	
	public Skeleton() {
		this.bones = new HashMap<String, Bone>();
	}
	
	public void AddBone(Bone bone) {
		this.bones.put(bone.name, bone);
	}
}
