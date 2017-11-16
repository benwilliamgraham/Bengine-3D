package networking;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class UDPServer extends PacketSource {
	
	public static final int SERVER_PORT = 9001;
	public static final int CLIENT_PORT = 27016;
	
	private static Map<Integer, Class> packetTypes =  new HashMap<Integer, Class>();
	
	protected Map<String, NetworkedClient> clients; 
	
	protected DatagramSocket serverSocket;
	
	public UDPServer() throws IOException {
		super();
		
		this.clients = new HashMap<String, NetworkedClient>();
		this.serverSocket = new DatagramSocket(SERVER_PORT);
		
		while (true) {
			byte[] incomingData = new byte[256];
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			
			this.serverSocket.receive(incomingPacket);
			
			int packetType = (int)incomingData[0];
			
			try {
				
				Packet p = (Packet) packetTypes.get(packetType).newInstance();
				p.loadPacket(incomingData);
				System.out.println("Emitting packet event");
				System.out.println(p);
				this.EmitPacket(p);
				
			} catch (Exception e) {
				System.out.println("Invalid packet type recieved.");
				e.printStackTrace(System.err);
			}
			
			
		}
	}
	
	public static void registerPacket(int packetId, Class p) {
		packetTypes.put(packetId, p);
	}
	
	
	public static void main(String[] args) {
		UDPServer.registerPacket(HandshakePacket.packetId, HandshakePacket.class);
		
		try {
			UDPServer server = new UDPServer();
			server.OnPacket(new int[]{0}, (Packet p) -> {
				HandshakePacket h = (HandshakePacket) p;
				
				System.out.println("Welcome " + h.name);
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class NetworkedClient  {
	
	public InetAddress address;
	public String name;
	public int id;
	
	public NetworkedClient() {
		
	}
}
