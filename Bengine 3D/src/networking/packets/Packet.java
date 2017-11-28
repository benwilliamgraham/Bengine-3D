package networking.packets;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import networking.NDBT;
import networking.NetworkedClient;

public abstract class Packet {

	public static final int PACKET_SIZE = 256; //Packet size in bytes.
	public static final byte PACKET_TYPE = 0;
	
	public static final Map<Integer, Class<?>> TYPES = new HashMap<Integer, Class<?>>();
	
	protected NetworkedClient sender;
	protected SocketAddress remoteAddress;
	protected NDBT data;
	
	public Packet() {
		data = new NDBT(PACKET_SIZE);
		data.add(PACKET_TYPE, (byte) getId());
	}
	
	public abstract void onLoad();
	
	public abstract int getId();
	
	public byte[] getBytes() {
		return data.toBytes();
	}
	
	public void setData(NDBT n) {
		data = n;
	}
	
	public void setSender(NetworkedClient sender) {
		this.sender = sender;
	}
	
	public void setAddress(SocketAddress s) {
		remoteAddress = s;
	}
	
	public SocketAddress getAddress() {
		return remoteAddress;
	}
	
	public NetworkedClient getSender() {
		return sender;
	}
	
	public static final void register(Class<?> p) {
		try {
			int id = p.getField("packetId").getInt(null);
			TYPES.put(id, p);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}