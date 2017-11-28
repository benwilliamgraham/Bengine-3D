package networking;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import networking.packets.HandshakePacket;
import networking.packets.Packet;
import networking.packets.RejectedPacket;
import networking.packets.RegisterEntityPacket;

public class UDPClient extends PacketSource {

	public static final int SERVER_PORT = 9001;
	public static final int CLIENT_PORT = 27016;
	
	public DatagramSocket socket;
	
	private Thread localServerThread;
	private Thread packetListenerThread;
	private InetAddress serverAddress;
	private boolean isConnected = false;
	
	public UDPClient(boolean localMode) {
		
		if (localMode) {
			localServerThread = new Thread(() -> {
				try {
					UDPServer server = new UDPServer();
					
					server.open();
				} catch (IOException e) {
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
		}
		
		packetListenerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isConnected) {
					byte[] incomingData = new byte[Packet.PACKET_SIZE];
					DatagramPacket incomingPacket = new DatagramPacket(incomingData, Packet.PACKET_SIZE);
					try {
						socket.receive(incomingPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					NDBT packetData = new NDBT(incomingData);
					
					try {
						Packet p = (Packet) Packet.TYPES.get((int) packetData.getByte(Packet.PACKET_TYPE)).newInstance();
						p.setData(packetData);
						p.setAddress(incomingPacket.getSocketAddress());
						p.onLoad();
						
						
						EmitPacket(p);
					} catch (Exception e) {
						System.err.println("Invalid packet type.");
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public void registerEntity(Entity e) {
		
	}
	
	public void open() {
		isConnected = true;
		packetListenerThread.start();
		this.send(new HandshakePacket("aTlas"));
	}
	
	public void send(Packet p) {
		byte[] data = p.getBytes();
		
		DatagramPacket pack = new DatagramPacket(data, Packet.PACKET_SIZE);
		
		if (!socket.isConnected()) {
			pack.setAddress(serverAddress);
			pack.setPort(SERVER_PORT);
		}
		
		try {
			this.socket.send(pack);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String args[]) {
		Packet.register(HandshakePacket.class);
		Packet.register(RejectedPacket.class);
		Packet.register(RegisterEntityPacket.class);
		
		UDPClient c = new UDPClient(false);
		
		c.OnPacket(new int[] {HandshakePacket.packetId}, (Packet p) -> {
			HandshakePacket hp = (HandshakePacket) p;
			
			try {
				c.socket.connect(hp.getAddress());
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
			System.out.println("Connected as " + hp.name);
			
			c.send(new RegisterEntityPacket(new Vector3f(1.0f, 2.0f, 3.0f), new Vector3f(), new Vector3f(), 0, "TEST"));
		}); 
		
		c.open();
	}
}
