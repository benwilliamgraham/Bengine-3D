package bengine.networking.messages;

import java.nio.ByteBuffer;

import bengine.networking.NDBT;

public class HandshakeMessage extends NetworkMessage {

	public static final byte MESSAGE_TYPE = genMessageType();
	
	public static final int STATE_CONNECT = 0;
	public static final int STATE_REGISTER = 1;
	public static final int STATE_COMPLETE = 2;
	public static final int STATE_DISCONNECT = 3;
	
	private static final byte NAME = 0;
	private static final byte ID = 1;
	private static final byte HANDSHAKE_STATE = 2;
	
	public String name;
	public long id;
	public int handshakeState = 0;
	
	public HandshakeMessage() {
		
	}

	public void parseFrom(ByteBuffer d) {
		NDBT data = new NDBT(d);
		
		
		this.name = data.getString(NAME);
		this.id = data.getLong(ID);
		this.handshakeState = data.getInt(HANDSHAKE_STATE);
	}

	@Override
	public ByteBuffer getBytes() {
		NDBT data = new NDBT(MESSAGE_TYPE);
		
		data.add(NAME, this.name);
		data.add(ID, this.id);
		data.add(HANDSHAKE_STATE, this.handshakeState);
		
		data.pack();
		return data.toBytes();
	}

	@Override
	public byte getType() {
		return MESSAGE_TYPE;
	}

}
