package networking;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;

import networking.packets.HandshakePacket;
import networking.packets.Packet;
import networking.packets.RejectedPacket;
import networking.packets.SpawnEntityPacket;

import java.io.IOException;

public class UDPServer extends PacketSource {
	
	public static final int SERVER_PORT = 9001;
	public static final int CLIENT_PORT = 27016;
	
	private static Map<Integer, Class> packetTypes =  new HashMap<Integer, Class>();
	
	protected Map<Byte, NetworkedClient> clients; 
	
	protected DatagramSocket serverSocket;
	
	private Thread packetListener;
	
	private static byte currentId = 0;
	
	public UDPServer() throws IOException {
		super();
		
		this.clients = new HashMap<Byte, NetworkedClient>();
		this.serverSocket = new DatagramSocket(SERVER_PORT);
		
		this.OnPacket(new int[] {HandshakePacket.packetId}, (Packet p) -> {
			HandshakePacket hp = (HandshakePacket) p; //Register that someone has joined the server.
			
			NetworkedClient client = new NetworkedClient(hp.name, currentId, p.getSender(), this);
			currentId++;
			
			this.clients.put(client.id, client);
			
			System.out.printf("%s connected. Assigning ID: %d %n", client.name, (int) client.id);
		
			//Accept the connection.
			client.send(new HandshakePacket(client.name, client.id));
		});
		
		this.OnPacket(new int[] {SpawnEntityPacket.packetId}, (Packet p) -> {
			SpawnEntityPacket s = (SpawnEntityPacket) p;
			
			
		});
		
		this.packetListener = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					
					byte[] incomingData = new byte[Packet.PACKET_SIZE];
					DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
					
					try {
						serverSocket.receive(incomingPacket);
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
					
					int packetType = (int)incomingData[0];
					
					try {
						
						Packet p = (Packet) packetTypes.get(packetType).newInstance();
						p.loadPacket(incomingPacket);
						
						EmitPacket(p);
						
					} catch (Exception e) {
						System.out.println("Invalid packet type recieved.");
						e.printStackTrace(System.err);
					}
				}
			}
		}); 
	}
	
	public void open() {
		this.packetListener.start();
	}
	
	public byte generateId() {
		return 0;
	}
	
	public void send(Packet p, InetAddress addr) {
		byte[] data = p.getBytes();
		
		DatagramPacket pack = new DatagramPacket(data, Packet.PACKET_SIZE, addr, CLIENT_PORT);
		
		try {
			this.serverSocket.send(pack);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void broadcast(Packet p) {
		for (NetworkedClient c : this.clients.values()) {
			c.send(p);
		}
	}
	
	public static void registerPacket(int packetId, Class p) {
		packetTypes.put(packetId, p);
	}
	
	
	public static void main(String[] args) {
		UDPServer.registerPacket(HandshakePacket.packetId, HandshakePacket.class);
		UDPServer.registerPacket(RejectedPacket.packetId, RejectedPacket.class);
		UDPServer.registerPacket(SpawnEntityPacket.packetId, SpawnEntityPacket.class);
		
		try {
			UDPServer server = new UDPServer();
			
			server.open();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
