package networking.serialization.serializers;

import java.nio.ByteBuffer;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import networking.serialization.ObjectParser;
import networking.server.Server;

public class CollectionSerializer extends Serializer<Collection<?>> {

	@Override
	public ByteBuffer serialize(Collection<?> obj) {
		ByteBuffer bb = ByteBuffer.allocate(Server.BUFFER_SIZE);
		
		ObjectParser parser = new ObjectParser();
		
		bb.putInt(obj.size());
		
		for (Object o : obj) {
			if (parser.canParse(o.getClass())) {
				ByteBuffer objData = parser.serialize(o);
				bb.putInt(objData.limit());
				bb.put(objData);
			}
		}
		
		bb.flip();
		return bb;
	}

	@Override
	public Collection<?> deserialize(ByteBuffer data) {
		int numObjects = data.getInt();
		
		List objects = new ArrayList<Object>();
		
		ObjectParser parser = new ObjectParser();
		
		for (int x = 0; x < numObjects; x++) {
			int objSize = data.getInt();
			byte[] objData = new byte[objSize];
			data.get(objData);
			
			Object object = parser.deserialize(objData);
			objects.add(object);
		}
		
		return objects;
	}

}
 