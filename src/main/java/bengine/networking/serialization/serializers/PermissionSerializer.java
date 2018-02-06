package bengine.networking.serialization.serializers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import bengine.networking.PermissionManager;
import bengine.networking.serialization.serializers.Serializer;;

public class PermissionSerializer extends Serializer<PermissionManager> {

	@Override
	public ByteBuffer serialize(PermissionManager obj) {
		ByteBuffer data = genBuffer(obj.getPermissions().size() * Long.BYTES + Byte.BYTES, PermissionManager.class);
		
		data.put((byte)(obj.allowAll? 1:0));
		
		for (long permission : obj.getPermissions()) {
			data.putLong(permission);
		}
		
		data.flip();
		return data;
	}

	@Override
	public PermissionManager deserialize(ByteBuffer data) {
		boolean allowAll = (data.get() == 1);
		List<Long> permissions = new ArrayList<Long>();
		
		while (data.hasRemaining()) {
			permissions.add(data.getLong());
		}
		
		PermissionManager pm = new PermissionManager(permissions);
		pm.allowAll = allowAll;
		return pm;
	}

}
