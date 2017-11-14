package networking;

public abstract class Packet {
	public int packetId = -1;
	
	public Packet() {
		
	}
	
	public abstract void loadPacket(byte[] data);
	public abstract byte[] getBytes();
}