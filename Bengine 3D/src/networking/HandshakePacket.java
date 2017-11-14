package networking;

public class HandshakePacket extends Packet {
	public int packetId = 0;

	public String name;
	
	public HandshakePacket(String name) {
		this.name = name;
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
}
