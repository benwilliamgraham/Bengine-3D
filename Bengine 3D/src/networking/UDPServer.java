package networking;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class UDPServer {
	
	public static final int SERVER_PORT = 9001;
	public static final int CLIENT_PORT = 27016;
	
	protected Map<String, NetworkedClient> clients; 
	
	protected DatagramSocket serverSocket;
	
	public UDPServer() throws IOException {
		this.clients = new HashMap<String, NetworkedClient>();
		this.serverSocket = new DatagramSocket(SERVER_PORT);
		
		while (true) {
			byte[] incomingData = new byte[256];
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			
			this.serverSocket.receive(incomingPacket);
			
			System.out.println(incomingData[0]);
			
		}
	}
	
	
	
	
	public static void main(String[] args) {
		try {
			UDPServer server = new UDPServer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class NetworkedClient {
	
	public InetAddress address;
	
	public NetworkedClient() {
		
	}
}
