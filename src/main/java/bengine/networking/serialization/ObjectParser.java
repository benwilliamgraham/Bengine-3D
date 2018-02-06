package bengine.networking.serialization;

import java.nio.ByteBuffer;

import java.util.List;
import java.util.Map;

import bengine.networking.serialization.serializers.Serializer;

import java.util.ArrayList;
import java.util.HashMap;

public class ObjectParser {
    private static Map<Class<?>, Serializer> serializers = new HashMap<Class<?>, Serializer>();
    private static List<Class<?>> types = new ArrayList<Class<?>>();


    public ByteBuffer serialize(Object obj) {
    	if (obj == null) {
    		return null;
    	}
    	
        if (serializers.containsKey(obj.getClass())) {
            Serializer s = serializers.get(obj.getClass());
            return s.serialize(obj);
        } else {
            //TODO: probably should throw an error here.
        }

        return null;
    }

    public<T> T deserialize(ByteBuffer b) {
        int type = b.getInt();
        Serializer s = serializers.get(types.get(type));
        return ((T) s.deserialize(b));
    }
    
    public<T> T deserialize(byte[] data) {
    	ByteBuffer b = ByteBuffer.allocate(data.length);
    	b.put(data);
    	b.flip();
    	return deserialize(b);
    }
    
    public boolean canParse(Class<?> c) {
    	return serializers.containsKey(c);
    }
    
    public static void registerType(Class<?> c, Serializer s) {
        serializers.put(c, s);
        types.add(c);
    }

    public static int getType(Class<?> c) {
        return types.indexOf(c);
    }
}