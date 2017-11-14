package networking;

import java.net.*;
import java.util.Arrays;

public class TestClient {

	public static final int SERVER_PORT = 9001;
	public static final int CLIENT_PORT = 27016;
	
	public static void main(String[] args) throws Exception {
		
		DatagramSocket socket = new DatagramSocket(CLIENT_PORT);
		
		InetAddress serverAddress = InetAddress.getLocalHost();
		
		byte[] outgoingData = new byte[256];
		
		outgoingData[0] = 0;
		
		System.out.println();
		
		DatagramPacket outgoingPacket = new DatagramPacket(outgoingData, outgoingData.length, serverAddress, SERVER_PORT);
		
		socket.send(outgoingPacket);
		
	}

}
