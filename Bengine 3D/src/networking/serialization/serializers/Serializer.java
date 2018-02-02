package networking.serialization.serializers;

import java.nio.ByteBuffer;

import networking.serialization.ObjectParser;

public abstract class Serializer<T extends Object> {
    
	ByteBuffer genBuffer(int size, Class<T> _class) {
		return ByteBuffer.allocate(size + Integer.BYTES).putInt(ObjectParser.getType(_class));
	}
	
	public abstract ByteBuffer serialize(T obj);
    public abstract T deserialize(ByteBuffer data);
}
