package bengine.networking.messages;

import java.io.IOException;
import java.nio.ByteBuffer;

import bengine.networking.NDBT;
import bengine.networking.sync.SyncedObject;

public class DestroyObjectMessage extends NetworkMessage {

	public static final byte MESSAGE_TYPE = genMessageType();
	
	private static final byte OBJECT_ID = 0;
	
	public long objectId;
	
	public DestroyObjectMessage() {
		
	}
	
	public DestroyObjectMessage(SyncedObject obj) {
		this.objectId = obj.getInstanceID();
	}
	
	@Override
	public void parseFrom(ByteBuffer bb) {
		NDBT data = new NDBT(bb);
		this.objectId = data.getLong(OBJECT_ID);
	}

	@Override
	public ByteBuffer getBytes() throws IOException {
		NDBT data = new NDBT(MESSAGE_TYPE);
		
		data.add(OBJECT_ID, this.objectId);
		
		data.pack();
		
		return data.toBytes();
	}

	@Override
	public byte getType() {
		return MESSAGE_TYPE;
	}

}
