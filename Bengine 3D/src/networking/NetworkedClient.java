package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.util.UUID;

import networking.packets.HandshakePacket;
import networking.packets.Packet;
import networking.packets.RegisterEntityPacket;
import networking.packets.UpdateEntityPacket;

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
				//Check for disconnect
				if (!this.sock.isConnected() || this.sock.isClosed()) {
					System.out.println("Client disconnected"); //TODO: Handle disconnection on server, (remove entites, etc)
					break;
				}
				
				
				//Recieve packet and handle
				byte[] incomingData = new byte[Packet.PACKET_SIZE];
				DatagramPacket p = new DatagramPacket(incomingData, Packet.PACKET_SIZE);
				
				try {
					sock.receive(p);
				} catch (PortUnreachableException e) {
					System.out.println("Client lost connection to server.");
					break;
				} catch (IOException e) {
					e.printStackTrace();
					continue; //Packet couldn't be recieved, so we skip trying to unencode it.
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
			
			System.out.println("Assigning " + hp.name + " an id of " + this.id);
			HandshakePacket h = new HandshakePacket(hp.name, this.id);
		
			send(h);
			
			System.out.println("Registering entities with " + hp.name);
			
			for (NetworkedEntity e : this.server.entities.values()) {
				send(new RegisterEntityPacket(e));
			}
		});
		
		this.OnPacket(new int[] {RegisterEntityPacket.packetId}, (Packet p) -> {
			System.out.println(this.name + " registered an entity.");
			
			RegisterEntityPacket r = (RegisterEntityPacket) p;
			
			NetworkedEntity e = new NetworkedEntity(r);
			e.owner = this;
			
			this.server.entities.put(e.id, e);
			
			this.server.broadcast(new RegisterEntityPacket(e));
		});
		
		this.OnPacket(new int[] {UpdateEntityPacket.packetId}, (Packet p) -> {
			UpdateEntityPacket u = (UpdateEntityPacket) p;
			
			if (this.server.entities.containsKey(u.entity)) {
				NetworkedEntity e = this.server.entities.get(u.entity);
				
				if (this.equals(e.owner)) {
					e.position = u.pos;
					e.rotation = u.rot;
					e.velocity = u.vel;
				}
			}
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