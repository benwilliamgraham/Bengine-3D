package bengine.networking.serialization.serializers;

import java.nio.ByteBuffer;

import org.joml.Quaternionf;;

public class QuaternionfSerializer extends Serializer<Quaternionf> {
	@Override
	public ByteBuffer serialize(Quaternionf obj) {
		ByteBuffer bb = genBuffer(Float.BYTES * 4, Quaternionf.class);
		
		bb.putFloat(obj.x);
		bb.putFloat(obj.y);
		bb.putFloat(obj.z);
		bb.putFloat(obj.w);
		
		bb.flip();
		
		return bb;
	}

	@Override
	public Quaternionf deserialize(ByteBuffer data) {
		return new Quaternionf(data.getFloat(), data.getFloat(), data.getFloat(), data.getFloat());
	}

}
