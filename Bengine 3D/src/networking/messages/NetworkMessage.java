package networking.messages;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import networking.Endpoint;

public abstract class NetworkMessage {
	
	static Map<Byte, Class<? extends NetworkMessage>> messageTypes = new HashMap<Byte, Class<? extends NetworkMessage>>();
	
	private static byte currentMessageType = 0;
	
	private SocketAddress sender;
	private Endpoint endpoint;
	
	public abstract void parseFrom(ByteBuffer data);
	public abstract ByteBuffer getBytes() throws IOException;
	public abstract byte getType();
	
	public void setSender(SocketAddress addr) {
		this.sender = addr;
	}
	public SocketAddress getSender() {
		return sender;
	}
	public void setEndpoint(Endpoint e) {
		this.endpoint = e;
	}
	
	public Endpoint getEndpoint() {
		return endpoint;
	}
	
	
	
	public enum MessageDirection {
		SERVER_FORWARDS(0),
		CLIENT_FORWARDS(1);
		
		byte value;
		
		MessageDirection(int v) {
			this.value = (byte)v;
		}
		
		public byte getValue() {
			return this.value;
		}
		
		public static MessageDirection getFromValue(byte v) {
			if (v == 0) {
				return MessageDirection.SERVER_FORWARDS;
			} else if (v == 1) {
				return MessageDirection.CLIENT_FORWARDS;
			} else {
				return null;
			}
		}
	}
	
	static byte genMessageType() {
		return currentMessageType++;
	}
	
	public static void registerMessage(Class<? extends NetworkMessage> messageType) {
		try {
			byte key = messageType.getField("MESSAGE_TYPE").getByte(null);
			messageTypes.put(key, messageType);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static Class<? extends NetworkMessage> getType(byte key) {
		return messageTypes.get(key);
	}
}
