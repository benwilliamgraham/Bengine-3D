package networking.server;

import static networking.Util.generateId;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.net.SocketAddress;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import networking.Endpoint;
import networking.messages.HandshakeMessage;
import networking.messages.NetworkMessage;
import networking.sync.SyncedObject;
import networking.sync.SyncedObjectManager;
import networking.Connection;

public abstract class Server implements Endpoint {
	
	public static final int BUFFER_SIZE = 2048;

	protected Map<Long, NetworkedClient> clients;
	protected SyncedObjectManager objectManager;
	protected SocketAddress port;
	protected int tickrate;
	
	
	private Connection connection;
	private Selector selector;
	private Thread selectionThread;
	private long endpointId = generateId();
	private boolean isOpen;
	
	private long lastTick = 0;
	
	public Server(int port) throws IOException {
		this.selector = Selector.open();
		this.port = new InetSocketAddress(port);
		this.connection = new Connection(BUFFER_SIZE, selector);
		this.connection.attach(this);
		this.connection.bind(this.port);
		
		this.clients = new ConcurrentHashMap<Long, NetworkedClient>();
		
		this.objectManager = new SyncedObjectManager();
		
		this.tickrate = 20;
		
		this.selectionThread = new Thread(() -> {
			
			onOpen();
			
			while (isOpen) {
				
				long currentTime = new Date().getTime();
				
				if (currentTime - lastTick > (1000 / tickrate)) {
					lastTick = currentTime;
					onTick();
				}
				
				int select = 0;
				
				try {
					
					select = selector.selectNow();
					
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				
				if (select > 0) {
					
					Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
					
					while (keyIter.hasNext()) {
						SelectionKey k = keyIter.next();
						
						Endpoint e = ((Endpoint) k.attachment());
						
						Connection c = e.getConnection();
						
						SocketAddress recieveAddress = c.update();
						
						NetworkMessage m = c.read();
						
						if (m != null) {
							
							m.setSender(recieveAddress);
							m.setEndpoint(e);
							
							e.onMessage(m);
						}
						
						keyIter.remove();
					}
				}
			}
		});
		
	}
	
	public void onMessage(NetworkMessage message) {
		if (message instanceof HandshakeMessage) {
			HandshakeMessage hmsg = (HandshakeMessage) message;
			
			try {
				Connection connection = new Connection(BUFFER_SIZE, selector);
				NetworkedClient client = new NetworkedClient(hmsg.name, this, connection);
				clients.put(client.getId(), client);
				client.onMessage(hmsg);
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	public void onTick() {
		objectManager.update(getConnection());
	}
	
	public abstract void onOpen();
	
	public abstract void onConnect(NetworkedClient c);
	
	public abstract void onNewObject(SyncedObject obj);
	
	public abstract void onDisconnect(NetworkedClient c);
	
	public abstract void onClose();
	
	public NetworkedClient getClient(long id) {
		return this.clients.get(id);
	}
	
	public Collection<NetworkedClient> getClients() {
		return this.clients.values();
	}
	
	@Override
	public Connection getConnection() {
		return this.connection;
	}
	
	@Override
	public long getEndpointId() {
		return endpointId;
	}
	
	@Override
	public boolean isRemote() {
		 return true;
	}
	
	@Override
	public Server getServer() {
		return this;
	}

	public boolean isOpen() {
		return isOpen;
	}
	
	public void open() throws IOException {
		isOpen = true;
		
		selectionThread.start();
	}
	
	void handleDisconnect(NetworkedClient c) {
		onDisconnect(c);
		
		this.clients.remove(c.getId(), c);
	}
	
	
}
