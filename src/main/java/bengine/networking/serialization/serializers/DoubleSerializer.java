package bengine.networking.serialization.serializers;

import java.nio.ByteBuffer;

public class DoubleSerializer extends Serializer<Double> {

	@Override
	public ByteBuffer serialize(Double obj) {
		ByteBuffer bb = genBuffer(Double.BYTES, Double.class);
		bb.putDouble(obj);
		bb.flip();
		return bb;
	}

	@Override
	public Double deserialize(ByteBuffer data) {
		return data.getDouble();
	}

}
