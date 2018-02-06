package bengine.networking;

import java.io.IOException;

import java.net.SocketAddress;

import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import bengine.networking.messages.NetworkMessage;

import java.nio.ByteBuffer;

public class Connection {
	public SocketAddress connectedAddress;
	
	protected DatagramChannel channel;
	
	protected SelectionKey key;
	
	private ByteBuffer readBuffer, writeBuffer;
	
	private Selector selector;
	
	private Endpoint endpoint;
	
	public Connection(int bufferSize, Selector selector) throws IOException {
		readBuffer = ByteBuffer.allocate(bufferSize);
		writeBuffer = ByteBuffer.allocate(bufferSize);
		channel = selector.provider().openDatagramChannel();
		channel.configureBlocking(false);
		key = channel.register(selector, SelectionKey.OP_READ);
		this.selector = selector;
	}
	
	public void send(NetworkMessage message) {
		try {
			ByteBuffer data = message.getBytes();
			
			writeBuffer.put(data);
			writeBuffer.flip();
			
			channel.write(writeBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writeBuffer.clear();
		}
	}
	
	public void send(NetworkMessage message, SocketAddress addr) {
		try {
			ByteBuffer data = message.getBytes();
			writeBuffer.put(data);
			writeBuffer.flip();
			channel.send(writeBuffer, addr);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writeBuffer.clear();
		}
	}

	public NetworkMessage read() {
		
		NetworkMessage message = null;
		
		try {
			readBuffer.flip();
			
			if (readBuffer.limit() == 0) {
				return null;
			}
			
			byte typeId = readBuffer.get();
				
			Class<? extends NetworkMessage> messageType = NetworkMessage.getType(typeId);
			
			message = messageType.newInstance();

			message.parseFrom(readBuffer);

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			readBuffer.clear();
		}
		
		return message;
	}

	public SocketAddress update() {
		try {
			if (!channel.isConnected()) {
				return channel.receive(readBuffer);
			}
			channel.read(readBuffer);
			
		} catch (IOException e) {
			//e.printStackTrace(); //TODO: Add a logger.
			return null;
		}
		
		return connectedAddress;
	}
	
	public void bind(SocketAddress port) { 
		readBuffer.clear();
		writeBuffer.clear();
		try {
			channel.socket().bind(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void attach(Endpoint e) {
		this.key.attach(e);
		this.endpoint = e;
	}
	
	public void connect(SocketAddress addr) {
		readBuffer.clear();
		writeBuffer.clear();
		try {
			channel.socket().connect(addr);
			connectedAddress = addr;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		return (connectedAddress != null) | channel.socket().isConnected();
	}
	
	public void close() {
		connectedAddress = null; 
		key.cancel();
		if (channel != null) {
			try {
				channel.close();
				channel = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Selector getSelector() {
		return selector;
	}
	
	public DatagramChannel getChannel() {
		return this.channel;
	}

	public Endpoint getEndpoint() {
		return this.endpoint;
	}
}
