package bengine.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.joml.Matrix4f;

public class Animator {
	
	public boolean doLoop;
	public boolean isPlaying;
	
	public float timeScale = 1.0f;
	
	private Map<String, Animation> anims;
	private Animation activeAnimation;
	private List<Animation> idleAnimations;
	private Stack<Animation> animationQueue;
	
	private float timer = 0.0f;
	
	public Animator(Animation[] anims) {
		this.anims = new HashMap<String, Animation>();
		
		for (Animation a : anims) {
			this.anims.put(a.getName(), a);
		}
		
		this.idleAnimations = new ArrayList<Animation>();
		this.animationQueue = new Stack<Animation>();
	}
	
	public void playNow(String animationName) {
		setAnimation(this.anims.get(animationName));
	}
	
	public void queueAnimation(String animationName) {
		if (this.anims.containsKey(animationName)) {
			Animation anim = this.anims.get(animationName);
			this.animationQueue.push(anim);
		}
	}
	
	public void queueAnimations(String ...animationNames) {
		for (String name : animationNames) {
			queueAnimation(name);
		}
	}
	
	public void addIdleAnimation(String animationName) {
		this.idleAnimations.add(this.anims.get(animationName));
	}
	
	public void update(float delta) {
		timer += delta * timeScale;
		
		if (activeAnimation != null) {
			float animTime = activeAnimation.convertAnimTime(timer);
			if (animTime >= activeAnimation.duration) { //Is the current animation finished
				if (doLoop) { //Do we play it again?
					timer = 0;
				} else { //Select the next animation
					if (animationQueue.empty()) {
						//Randomly select one of our idle animations if we can, if not, just set active animation to null.
						if (idleAnimations.size() > 0) {
							int index = (int) (Math.random() * idleAnimations.size());
							
							setAnimation(idleAnimations.get(index));
						} else {
							setAnimation(null);
						}
					} else {
						setAnimation(animationQueue.pop());
					}
				}
			} else { //Is the current animation still playing?
				activeAnimation.time = animTime;
			}
		} else {
			if (idleAnimations.size() > 0) {
				int index = (int) (Math.random() * idleAnimations.size());
				
				setAnimation(idleAnimations.get(index));
			}
		}
	}
	
	private void setAnimation(Animation a) {
		timer = 0;
		activeAnimation = a;
	}
	
	public void setTime(float time) {
		this.timer = time;
	}
	
	public float getTime() {
		return timer;
	}
	
	public Animation getAnimation(String name) {
		return this.anims.get(name);
	}
	
	public Animation getActiveAnimation() {
		return activeAnimation;
	}
	
	public Matrix4f[] getActiveAnimationData() {
		if (activeAnimation != null) {
			return activeAnimation.GetBoneData();
		} else {
			return null;
		}
	}
}
