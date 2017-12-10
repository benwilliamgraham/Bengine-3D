package networking;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.DynEntity;
import networking.packets.HandshakePacket;
import networking.packets.Packet;
import networking.packets.RejectedPacket;
import networking.packets.UpdateEntityPacket;
import world.World;
import networking.packets.RegisterEntityPacket;

public class UDPClient extends PacketSource {

	public static final int SERVER_PORT = 9001;
	public static final int CLIENT_PORT = 27016;
	public static final int TICKRATE = 20;
	
	
	public DatagramSocket socket;
	public String clientId;
	
	protected World world;
	
	private Process localServer;
	private Thread packetListenerThread;
	private Thread serverTickThread;
	private InetAddress serverAddress;
	private boolean isConnected = false;
	
	public UDPClient(boolean localMode, InetAddress serverAddress) {
		
		if (localMode) {
			
			try {
				localServer = spawnServerProcess();
				
			} catch (IOException e) {
				System.err.println("Failed to start local server.");
				System.exit(1);
			}
			
			try {
				this.serverAddress = InetAddress.getLocalHost();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			
			try {
				this.socket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
		} else {
			this.serverAddress = serverAddress;
			
			try {
				this.socket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		
		packetListenerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Listening for packets from : " + serverAddress.toString());
				
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
		
		serverTickThread = new Thread(() -> {
			while (isConnected) {
				long time = Sys.getTime(); 
				
				updateEntitiesRemote();
				
				//Slow down there buddy. We only need TICKRATE ticks per second.
				while ((Sys.getTime() - time) < (1000 / TICKRATE)) {
					continue;
				}
			}
		});
		
		
		
		OnPacket(new int[] {HandshakePacket.packetId}, (Packet p) -> {
			HandshakePacket hp = (HandshakePacket) p;
			
			try {
				this.socket.connect(hp.getAddress());
			} catch (SocketException e) {
				e.printStackTrace();
			}
			
			System.out.println("Connected as " + hp.name + " with id: " + hp.id);
			
			if (hp.id != null) {
				this.clientId = hp.id;
			}
			
			this.world.onConnected();
		});
	}
	
	public void registerEntity(Entity e) {
		this.send(new RegisterEntityPacket(e));
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public void updateEntity(Entity e) {
		this.send(new UpdateEntityPacket(e)); 
	}
	
	public void open() {
		isConnected = true;
		packetListenerThread.start();
		serverTickThread.start();
		this.send(new HandshakePacket("aTlas" + Math.random()));
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
	
	public void close() {
		if (localServer != null) {
			localServer.destroyForcibly();
		}
	}
	
	private void updateEntitiesRemote() {
		for (DynEntity e : world.entities.values()) {
			if (e.owner.equals(this.clientId)) {
				send(new UpdateEntityPacket(e));
			}
		}
	}
	
	private Process spawnServerProcess() throws IOException { //Little bit of an ugly hack, but hey, at least it's not for something important, like the whole backend of the game or anything, right guys?
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String libraryPath = System.getProperty("java.library.path");
		String className = UDPServer.class.getCanonicalName();
		
		ProcessBuilder pb = new ProcessBuilder(javaBin, "-Djava.library.path="+libraryPath, "-cp", classpath, className);
		System.out.println(pb.command().toString());
		pb.inheritIO();
		return pb.start();
	}
}
