package networking.packets;

import java.net.DatagramPacket;

public class RejectedPacket extends Packet {
	public static int packetId = 1;
	
	public String message;
	
	private static final int MESSAGE_OFFSET = 1;
	private static final int MESSAGE_LENGTH = 128;
	
	public RejectedPacket(String message) {
		this.message = message;
	}
	
	@Override
	public void loadPacket(DatagramPacket p) {
		this.sender = p.getAddress();
		byte[] messageData = new byte[MESSAGE_LENGTH];
		System.arraycopy(p.getData(), MESSAGE_OFFSET, messageData, 0, MESSAGE_LENGTH);
		this.message = new String(messageData).trim();
	}

	@Override
	public byte[] getBytes() {
		byte[] data = new byte[256];
		data[0] = (byte) packetId;
		System.arraycopy(this.message.getBytes(), 0, data, MESSAGE_OFFSET, MESSAGE_LENGTH);		
		return data;
	}

	@Override
	public int getId() {
		return packetId;
	}
	
}
