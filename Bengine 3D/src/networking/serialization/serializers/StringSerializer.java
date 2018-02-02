package networking.serialization.serializers;

import java.nio.ByteBuffer;

public class StringSerializer extends Serializer<String> {

	@Override
	public ByteBuffer serialize(String obj) {
		ByteBuffer bb = genBuffer(Character.BYTES * obj.length(), String.class);
		
		for (char c : obj.toCharArray()) {
			bb.putChar(c);
		}
		
		bb.flip();
		
		return bb;
	}

	@Override
	public String deserialize(ByteBuffer data) {
		String str = "";
		
		while (data.hasRemaining()) {
			str += data.getChar();
		}
		
		return str;
	}

}
