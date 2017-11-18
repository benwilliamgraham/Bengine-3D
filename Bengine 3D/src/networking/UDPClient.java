package networking;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

import networking.packets.HandshakePacket;
import networking.packets.Packet;

public class UDPClient extends PacketSource {

	public static final int SERVER_PORT = 9001;
	public static final int CLIENT_PORT = 27016;
	
	protected DatagramSocket socket;
	
	private Thread localServerThread;
	private InetAddress serverAddress;
	
	public UDPClient(boolean localMode) {
		if (localMode) {
			localServerThread = new Thread(() -> {
				try {
					UDPServer server = new UDPServer();
					
					server.open();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			
			localServerThread.start();
			
			try {
				serverAddress = InetAddress.getLocalHost();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			
			try {
				this.socket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
		} else {
			
		}
	}
	
	public void send(Packet p) {
		byte[] data = p.getBytes();
		
		DatagramPacket pack = new DatagramPacket(data, Packet.PACKET_SIZE, serverAddress, SERVER_PORT);
		
		try {
			this.socket.send(pack);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
