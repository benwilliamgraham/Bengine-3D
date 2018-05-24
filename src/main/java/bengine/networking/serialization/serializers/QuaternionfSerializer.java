package bengine.networking.serialization.serializers;

import java.nio.ByteBuffer;

import org.joml.Quaternionf;;

public class QuaternionfSerializer extends Serializer<Quaternionf> {
	@Override
	public ByteBuffer serialize(Quaternionf obj) {
		ByteBuffer bb = genBuffer(Float.BYTES * 4, Quaternionf.class);
		
		bb.putFloat(0, obj.x);
		bb.putFloat(1, obj.y);
		bb.putFloat(2, obj.z);
		bb.putFloat(3, obj.w);
		
		bb.flip();
		
		return bb;
	}

	@Override
	public Quaternionf deserialize(ByteBuffer data) {
		return new Quaternionf(data.getFloat(0), data.getFloat(1), data.getFloat(2), data.getFloat(3));
	}

}
