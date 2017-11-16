package networking;

public class HandshakePacket implements Packet {
	public static int packetId = 0;

	public String name;
	
	public HandshakePacket() {}
	
	public HandshakePacket(String name) {
		this.name = name;
	}
	
	public HandshakePacket(byte[] data) {
		loadPacket(data);
	}

	@Override
	public void loadPacket(byte[] data) {
		byte[] nameData = new byte[8];
		System.arraycopy(data, 1, nameData, 0, 8);
		this.name = new String(nameData);
	}

	@Override
	public byte[] getBytes() {
		byte[] data = new byte[256];
		data[0] = (byte) this.packetId;
		System.arraycopy(name.getBytes(), 0, data, 1, 8);
		return data;
	}

	@Override
	public int getId() {
		return 0;
	}
}
