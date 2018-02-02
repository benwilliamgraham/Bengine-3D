package networking.serialization.serializers;

import java.nio.ByteBuffer;

public class IntSerializer extends Serializer<Integer> {

	@Override
	public ByteBuffer serialize(Integer obj) {
		ByteBuffer bb = genBuffer(Integer.BYTES, Integer.class);
		bb.putInt(obj);
		bb.flip();
		return bb;
	}

	@Override
	public Integer deserialize(ByteBuffer data) {
		return data.getInt();
	}

}
