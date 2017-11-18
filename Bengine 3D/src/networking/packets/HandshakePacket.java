package networking.packets;

import java.net.DatagramPacket;

public class HandshakePacket extends Packet {
	public static int packetId = 0;
	
	public String name;
	public byte id; 
	
	private static final int ID_OFFSET = 1;
	private static final int NAME_OFFSET = 2;
	private static final int NAME_LENGTH = 32;
	
	public HandshakePacket() {}
	
	public HandshakePacket(String name) {
		this.name = name;
	}
	
	public HandshakePacket(String name, byte id) {
		this.name = name;
		this.id = id;
	}
	
	public HandshakePacket(DatagramPacket p) {
		loadPacket(p);
	}

	@Override
	public void loadPacket(DatagramPacket p) {
		this.sender = p.getAddress();
		byte[] nameData = new byte[NAME_LENGTH];
		System.arraycopy(p.getData(), NAME_OFFSET, nameData, 0, NAME_LENGTH);
		this.name = new String(nameData).trim();
	}

	@Override
	public byte[] getBytes() {
		byte[] data = new byte[256];
		data[0] = (byte) packetId;
		data[ID_OFFSET] = this.id;
		System.arraycopy(name.getBytes(), 0, data, NAME_OFFSET, name.length());
		return data;
	}

	@Override
	public int getId() {
		return 0;
	}
}
