package networking;

public interface Packet {

	public abstract void loadPacket(byte[] data);
	public abstract byte[] getBytes();
	public abstract int getId();
}