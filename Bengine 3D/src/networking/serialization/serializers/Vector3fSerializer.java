package networking.serialization.serializers;

import java.nio.ByteBuffer;

import org.lwjgl.util.vector.Vector3f;

public class Vector3fSerializer extends Serializer<Vector3f> {
	@Override
	public ByteBuffer serialize(Vector3f obj) {
		ByteBuffer bb = genBuffer(Float.BYTES * 3, Vector3f.class);
		
		bb.putFloat(obj.x);
		bb.putFloat(obj.y);
		bb.putFloat(obj.z);
		bb.flip();
		return bb;
	}

	@Override
	public Vector3f deserialize(ByteBuffer data) {
		float x = data.getFloat();
		float y = data.getFloat();
		float z = data.getFloat();
		
		return new Vector3f(x, y, z);
	}

}
