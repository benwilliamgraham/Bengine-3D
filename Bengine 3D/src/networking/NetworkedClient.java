package networking;

import java.net.InetAddress;

import networking.packets.Packet;

public class NetworkedClient extends PacketSource {
	
	public InetAddress address;
	public String name;
	public byte id;
	protected UDPServer server;
	
	public NetworkedClient(String name, byte id, InetAddress addr, UDPServer server) {
		this.name = name;
		this.id = id;
		this.address = addr;
		this.server = server;
	}
	
	public void send(Packet p) {
		this.server.send(p, this.address);
	}
}