package bengine.networking.serialization.serializers;

import java.nio.ByteBuffer;

import org.joml.Vector3f;

public class Vector3fSerializer extends Serializer<Vector3f> {
	@Override
	public ByteBuffer serialize(Vector3f obj) {
		ByteBuffer bb = genBuffer(Float.BYTES * 3, Vector3f.class);
		
		obj.get(bb);
		
		bb.flip();
		return bb;
	}

	@Override
	public Vector3f deserialize(ByteBuffer data) {
		return new Vector3f().set(data);
	}

}
