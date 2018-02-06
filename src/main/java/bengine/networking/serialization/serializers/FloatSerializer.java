package bengine.networking.serialization.serializers;

import java.nio.ByteBuffer;

public class FloatSerializer extends Serializer<Float> {

	@Override
	public ByteBuffer serialize(Float obj) {
		ByteBuffer bb = genBuffer(Float.BYTES, Float.class);
		bb.putFloat(obj);
		bb.flip();
		return bb;
	}

	@Override
	public Float deserialize(ByteBuffer data) {
		return data.getFloat();
	}

}
