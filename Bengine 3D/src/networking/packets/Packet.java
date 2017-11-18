package networking.packets;

import java.net.InetAddress;

import java.net.DatagramPacket;

public abstract class Packet {

	public static final int PACKET_SIZE = 256; //Packet size in bytes.
	
	protected InetAddress sender;
	
	public abstract void loadPacket(DatagramPacket p);
	public abstract byte[] getBytes();
	public abstract int getId();
	
	public InetAddress getSender() {
		return this.sender;
	}
}