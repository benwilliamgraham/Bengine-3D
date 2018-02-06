package bengine.entities;

import org.joml.Vector3f;

public class Light {
	public Vector3f position;
	public float lightDist;
	public float brightness;
	//<1: fast initial brightness drop, slower overtime; >1: low initial brightness drop, more toward end
	public float dropOff;
	
	public Light(Vector3f position, float lightDist, float brightness, float dropOff) {
		this.position = position;
		this.lightDist = lightDist;
		this.brightness = brightness;
		this.dropOff = dropOff;
	}
}
