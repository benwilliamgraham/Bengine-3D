package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

import networking.packets.HandshakePacket;
import networking.packets.Packet;
import networking.packets.RegisterEntityPacket;

public class NetworkedClient extends PacketSource {
	
	public String name, id;
	protected DatagramSocket sock;
	protected UDPServer server;
	private Thread listenerThread;
	
	public NetworkedClient(String name, DatagramSocket sock, UDPServer server) {
		this.name = name;
		this.id = UUID.randomUUID().toString();
		this.sock = sock;
		this.server = server;
		this.listenerThread = new Thread(() -> {
			while (true) {
				byte[] incomingData = new byte[Packet.PACKET_SIZE];
				DatagramPacket p = new DatagramPacket(incomingData, Packet.PACKET_SIZE);
				
				try {
					sock.receive(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				NDBT packetData = new NDBT(incomingData);
				
				try {
					Packet packet = (Packet) Packet.TYPES.get((int) packetData.getByte(Packet.PACKET_TYPE)).newInstance();
					packet.setData(packetData);
					packet.setSender(this);
					packet.onLoad();
					
					EmitPacket(packet);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
		this.OnPacket(new int[] {HandshakePacket.packetId}, (Packet p) -> {
			HandshakePacket hp = (HandshakePacket) p;
			
			System.out.println(hp.name + " has connected.");
			
			send(hp);
		});
		
		this.OnPacket(new int[] {RegisterEntityPacket.packetId}, (Packet p) -> {
			System.out.println("Client attempted to register entity.");
			
			RegisterEntityPacket r = (RegisterEntityPacket) p;
			
			
		});
		
		this.listenerThread.start();
	}
	
	public void send(Packet p) {
		DatagramPacket outgoingPacket = new DatagramPacket(p.getBytes(), Packet.PACKET_SIZE);
		
		outgoingPacket.setSocketAddress(this.sock.getRemoteSocketAddress());
		
		try {
			this.sock.send(outgoingPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}