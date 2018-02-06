package bengine.networking.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;

import bengine.networking.Connection;
import bengine.networking.Endpoint;
import bengine.networking.messages.DebugMessage;
import bengine.networking.messages.HandshakeMessage;
import bengine.networking.messages.NetworkMessage;
import bengine.networking.messages.ObjectMessage;
import bengine.networking.messages.RPCMessage;
import bengine.networking.server.Server;
import bengine.networking.sync.SyncedObject;
import bengine.networking.sync.SyncedObjectManager;

public abstract class Client implements Endpoint {
	
	private static final int BUFFER_SIZE = 512;
	
	public String name;
	
	protected Connection connection;
	protected SyncedObjectManager objectManager;
	protected long id;
	protected int tickrate;
	protected long serverEndpointId;
	
	
	private SocketAddress server;
	private boolean isOpen = false;
	
	private Thread listenerThread;
	
	private long lastTick = 0;
	private boolean doTick = false;
	
	public Client(String name, SocketAddress addr) {
		this.name = name;
		this.objectManager = new SyncedObjectManager();
		this.server = addr;
		this.tickrate = 20;
		try {
			Selector selector = Selector.open();
			this.connection = new Connection(BUFFER_SIZE, selector);
			this.connection.attach(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.listenerThread = new Thread(() -> {
			try {
				sendHandshake();
				
				while (isOpen) {
					
					long currentTime = new Date().getTime();
					
					if (currentTime - lastTick > (1000 / tickrate) && doTick) {
						lastTick = currentTime;
						onTick();
					}
					
					int select = connection.getSelector().selectNow();
					
					if (select > 0) {
						Iterator<SelectionKey> keyIter = connection.getSelector().selectedKeys().iterator();
						
						while(keyIter.hasNext()) {
							SelectionKey k = keyIter.next();
							
							Endpoint e = ((Endpoint) k.attachment());
							
							SocketAddress remoteAddr = e.getConnection().update();
							
							NetworkMessage msg = e.getConnection().read();
							
							if (msg != null) {
								msg.setSender(remoteAddr);
								
								msg.setEndpoint(e);
								
								e.onMessage(msg);
							}
							
							keyIter.remove();
						}
					}
					
				}
				
				HandshakeMessage hmsg = new HandshakeMessage();
				hmsg.handshakeState = HandshakeMessage.STATE_DISCONNECT;
				hmsg.id = this.id;
				hmsg.name = this.name;
				connection.send(hmsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	private void sendHandshake() {
		HandshakeMessage hm = new HandshakeMessage();
		hm.handshakeState = HandshakeMessage.STATE_CONNECT;
		hm.name = this.name;
		hm.id = this.id;
		
		connection.send(hm, this.server);
	}

	public abstract void onConnected();
	
	public abstract void onNewObject(SyncedObject obj);
	
	public void onTick() {
		if (connection.isConnected()) {
			objectManager.update(this.connection);
		}
	}
	
	public abstract void onDisconnected();
	
	public void trackObject(SyncedObject obj) {
		objectManager.registerObject(obj, connection);//Register the object with the server, set all the values. Object will contain it's visibility status.
	}
	
	public void updateTrackedObjects() {
		for (SyncedObject obj: objectManager.getObjects()) {
			if (obj.getOwner() == this.id) { //TODO: Object permission visibilty stuff.
				ObjectMessage om = new ObjectMessage(obj);
				connection.send(om);
			}
		}
	}
	
	public void onMessage(NetworkMessage m) { //TODO: Beautify at some point.
		if (m instanceof HandshakeMessage) {
			HandshakeMessage hm = (HandshakeMessage) m;
			if (hm.handshakeState == HandshakeMessage.STATE_CONNECT) {
				if (hm.name.equals("server")) {
					this.serverEndpointId = hm.id;
					HandshakeMessage msg = new HandshakeMessage();
					msg.name = this.name;
					msg.id = this.id;
					msg.handshakeState = HandshakeMessage.STATE_REGISTER;
					
					connection.connect(hm.getSender());
					
					connection.send(msg);
				}
			} if (hm.handshakeState == HandshakeMessage.STATE_REGISTER) {
				this.id = hm.id;
				this.name = hm.name;
				HandshakeMessage completeMessage = new HandshakeMessage();
				completeMessage.name = this.name;
				completeMessage.id = this.id;
				completeMessage.handshakeState = HandshakeMessage.STATE_COMPLETE;
				
				connection.send(completeMessage);
			} else if (hm.handshakeState == HandshakeMessage.STATE_COMPLETE) {
				this.onConnected();
				doTick = true;
			} else if (hm.handshakeState == HandshakeMessage.STATE_DISCONNECT) {
				HandshakeMessage hmsg = new HandshakeMessage();
				hmsg.name = this.name;
				hmsg.id = this.id;
				hmsg.handshakeState = HandshakeMessage.STATE_DISCONNECT;
				
				connection.send(hmsg);
				
				onDisconnected();
				connection.close();
			}
		} else if (m instanceof ObjectMessage) {
			objectManager.onMessage((ObjectMessage) m);
		} else if (m instanceof DebugMessage) {
			System.out.println("Recieved debug message: " + ((DebugMessage) m).message);
		} else if (m instanceof RPCMessage) {
			objectManager.handleRPC((RPCMessage) m);
		}
	}
	
	@Override
	public Connection getConnection() {
		return connection;
	}
	
	@Override
	public long getEndpointId() {
		return id;
	}
	
	@Override
	public boolean isRemote() {
		return false;
	}
	
	@Override
	public Server getServer() {
		return null;
	}
	
	public void open() {
		isOpen = true;
		listenerThread.start();
	}
	
	public void close() {
		isOpen = false;
	}
}
