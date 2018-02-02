package networking.serialization.serializers;

import java.nio.ByteBuffer;

public class LongSerializer extends Serializer<Long> {

	@Override
	public ByteBuffer serialize(Long obj) {
		ByteBuffer bb = genBuffer(Long.BYTES, Long.class);
		bb.putLong(obj);
		bb.flip();
		return bb;
	}

	@Override
	public Long deserialize(ByteBuffer data) {
		return data.getLong();
	}
	
}
