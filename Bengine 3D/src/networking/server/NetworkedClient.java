package networking.server;

import static networking.Util.generateId;

import java.net.SocketAddress;

import networking.messages.ObjectMessage;
import networking.messages.RPCMessage;
import networking.sync.SyncedObject;
import networking.messages.DebugMessage;
import networking.messages.HandshakeMessage;
import networking.messages.NetworkMessage;
import networking.Connection;
import networking.Endpoint;

public class NetworkedClient implements Endpoint {
	
	protected Server server;
	protected Connection connection;
	private String name;
	private SocketAddress remote;
	private long id;
	
	public NetworkedClient(String name, Server server, Connection connection) {
		this.id = generateId();
		this.name = name;
		this.server = server;
		this.connection = connection;
		this.connection.attach(this);
	}
	
	public void onConnected() {
		
	}
	
	public void onDisconnect() {
		
	}
	
	@Override
	public void onMessage(NetworkMessage message) { //TODO: beautify at some point.
		if (message instanceof HandshakeMessage) {
			HandshakeMessage hm = (HandshakeMessage) message;
			if (hm.handshakeState == HandshakeMessage.STATE_CONNECT) {
				connection.connect(hm.getSender());
				
				this.name = hm.name;
				
				HandshakeMessage hmsg = new HandshakeMessage();
				hmsg.id = getServer().getEndpointId();
				hmsg.name = "server";
				hmsg.handshakeState = HandshakeMessage.STATE_CONNECT;
				
				connection.send(hmsg, this.connection.connectedAddress);
			} else if (hm.handshakeState == HandshakeMessage.STATE_REGISTER) {
				
				HandshakeMessage hmsg = new HandshakeMessage();
				hmsg.id = this.id;
				hmsg.name = this.name;
				hmsg.handshakeState = HandshakeMessage.STATE_REGISTER;
				
				connection.send(hmsg);
				
			} else if (hm.handshakeState == HandshakeMessage.STATE_COMPLETE) {
			
				//System.out.println("recieved final step of handshake.");
				
				if (hm.name.equals(this.name) && hm.id == this.id) {
					//System.out.println("Client has been verified");
					HandshakeMessage hmsg = new HandshakeMessage();
					hmsg.id = this.id;
					hmsg.name = this.name;
					hmsg.handshakeState = HandshakeMessage.STATE_COMPLETE;
					
					connection.send(hmsg, this.connection.connectedAddress);
					
					onConnected();
					server.onConnect(this);
				} else {
					System.out.println("Client failed to verify, retrying.");
					
					HandshakeMessage hmsg = new HandshakeMessage();
					hmsg.id = this.id;
					hmsg.name = this.name;
					hmsg.handshakeState = HandshakeMessage.STATE_REGISTER;
					
					connection.send(hmsg, this.connection.connectedAddress);
				}
			} else if (hm.handshakeState == HandshakeMessage.STATE_DISCONNECT) {
				onDisconnect();
				server.handleDisconnect(this);
				connection.close();
			}
		} else if (message instanceof ObjectMessage) {
			ObjectMessage om = (ObjectMessage) message;
			server.objectManager.onMessage(om);
		} else if (message instanceof DebugMessage) {
			System.out.println("Recieved debug message: " + ((DebugMessage) message).message);
		} else if (message instanceof RPCMessage) {
			RPCMessage msg = (RPCMessage) message;
			
			if (msg.rpcMode == SyncedObject.RPC.SERVER_ONLY) {
				server.objectManager.handleRPC(msg);
			} else if (msg.rpcMode == SyncedObject.RPC.ALL_REMOTES) {
				SyncedObject obj = server.objectManager.getObject(msg.objectInstanceId);
				if (obj.mutability.hasPermission(msg.getEndpoint().getEndpointId())) {
					for (NetworkedClient c : server.getClients()) {
						if (obj.visibility.hasPermission(c.getEndpointId())) {
							c.connection.send(msg);
						}
					}
				}
			}
		}
	}
	
	void setRemote(SocketAddress remote) {
		this.remote = remote;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public SocketAddress getRemote() {
		return remote;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public long getEndpointId() {
		return this.id;
	}
	
	@Override
	public boolean isRemote() {
		return true;
	}
	
	@Override
	public Server getServer() {
		return server;
	}
}
