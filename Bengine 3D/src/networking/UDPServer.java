package networking;

import java.net.InetAddress;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;

import entities.Entity;

import networking.packets.HandshakePacket;
import networking.packets.Packet;
import networking.packets.RegisterEntityPacket;
import networking.packets.RejectedPacket;
import networking.packets.RegisterEntityPacket;


import java.io.IOException;

public class UDPServer {
	
	public static final int SERVER_PORT = 9001;
	public static final int CLIENT_PORT = 27016;
	
	protected Map<String, NetworkedClient> clients;
	protected Map<String, NetworkedEntity> entities;
	
	protected DatagramSocket serverSocket;
	
	private Thread packetListener;
	
	private boolean isOpen = false;
	
	public UDPServer() throws IOException {
		super();
		
		this.clients = new HashMap<String, NetworkedClient>();
		this.entities = new HashMap<String, NetworkedEntity>();
		this.serverSocket = new DatagramSocket(SERVER_PORT);
		
		this.packetListener = new Thread(() -> {
			while (isOpen) {
				
				byte[] incomingData = new byte[Packet.PACKET_SIZE];
				DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				
				try {
					serverSocket.receive(incomingPacket);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
				
				NDBT packetData = new NDBT(incomingData);
				
				try {
					Packet p = (Packet) Packet.TYPES.get((int) packetData.getByte(Packet.PACKET_TYPE)).newInstance();
					p.setData(packetData);
					p.onLoad();
					
					if (p.getId() == HandshakePacket.packetId) {
						DatagramSocket clientSock = new DatagramSocket();
						clientSock.connect(incomingPacket.getSocketAddress());
						
						NetworkedClient client = new NetworkedClient(((HandshakePacket) p).name, clientSock, this);
						p.setSender(client);
						clients.put(client.name, client);
						client.EmitPacket(p);
					}
					
				} catch (Exception e) {
					System.err.println("Invalid packet type recieved.");
					e.printStackTrace();
				}
			}
		}); 
	}
	
	public void open() {
		isOpen = true;
		this.packetListener.start();
	}
	
	public void close() {
		isOpen = false;
	}
	
	public void broadcast(Packet p) {
		for (NetworkedClient c : this.clients.values()) {
			c.send(p);
		}
	}
	
	public static void registerPacket(int packetId, Class p) {
		Packet.TYPES.put(packetId, p);
	}
	
	
	public static void main(String[] args) {
		Packet.register(HandshakePacket.class);
		Packet.register(RejectedPacket.class);
		Packet.register(RegisterEntityPacket.class);
		
		
		try {
			UDPServer server = new UDPServer();
			
			server.open();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}