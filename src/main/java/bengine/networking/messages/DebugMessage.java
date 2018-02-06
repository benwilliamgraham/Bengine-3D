package bengine.networking.messages;

import java.nio.ByteBuffer;

import bengine.networking.NDBT;
public class DebugMessage extends NetworkMessage {

	public static final byte MESSAGE_TYPE = genMessageType();
	
	private static final byte MESSAGE = 0;
	
	public String message;
	
	public DebugMessage() {
		
	}
	
	public DebugMessage(String message) {
		this.message = message;
	}
	
	@Override
	public void parseFrom(ByteBuffer d) {
		NDBT data = new NDBT(d);
		
		this.message = data.getString(MESSAGE);
	}

	@Override
	public ByteBuffer getBytes() {
		NDBT data = new NDBT(MESSAGE_TYPE);
		
		data.add(MESSAGE, this.message);
		
		data.pack();
		return data.toBytes();
	}

	@Override
	public byte getType() {
		return MESSAGE_TYPE;
	}
}
