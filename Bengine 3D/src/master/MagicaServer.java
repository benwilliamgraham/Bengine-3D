package master;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.util.vector.Vector3f;

import entities.Bullet;
import entities.Entity;
import entities.Player;
import networking.PermissionManager;
import networking.messages.DebugMessage;
import networking.messages.HandshakeMessage;
import networking.messages.NetworkMessage;
import networking.messages.ObjectMessage;
import networking.messages.RPCMessage;
import networking.serialization.ObjectParser;
import networking.serialization.serializers.CollectionSerializer;
import networking.serialization.serializers.DoubleSerializer;
import networking.serialization.serializers.FloatSerializer;
import networking.serialization.serializers.IntSerializer;
import networking.serialization.serializers.LongSerializer;
import networking.serialization.serializers.PermissionSerializer;
import networking.serialization.serializers.StringSerializer;
import networking.serialization.serializers.Vector3fSerializer;
import networking.server.NetworkedClient;
import networking.server.Server;
import networking.sync.SyncedObject;
import networking.sync.SyncedObjectManager;

public class MagicaServer extends Server {

	public int updateRate = 120;
	
	public Map<Long, Entity> entities = new ConcurrentHashMap<Long, Entity>();
	
	public MagicaServer() throws IOException {
		super(2290);
		
	}

	@Override
	public void onOpen() {
		System.out.println("Listening on port 2290");
		
	}

	@Override
	public void onConnect(NetworkedClient c) {
		System.out.printf("%s connected to server. %n", c.getName());
	}

	public void onUpdate(float delta) {
		for (Entity e : entities.values()) {
			e.onUpdate(delta);
		}
	}
	
	@Override
	public void onNewObject(SyncedObject object) {
		if (object instanceof Entity) {
			Entity e = (Entity) object;
			entities.put(object.getInstanceID(), e);
		}
	}
	
	@Override
	public void onDisconnect(NetworkedClient c) {
		System.out.printf("%s disconnected from the server. %n", c.getName());
	}

	@Override
	public void onClose() {
		System.out.println("Server closed.");
	}
	
	public static void main(String args[]) throws IOException {
		NetworkMessage.registerMessage(HandshakeMessage.class);
		NetworkMessage.registerMessage(ObjectMessage.class);
		NetworkMessage.registerMessage(DebugMessage.class);
		NetworkMessage.registerMessage(RPCMessage.class);
		
		ObjectParser.registerType(Integer.class, new IntSerializer());
		ObjectParser.registerType(Float.class, new FloatSerializer());
		ObjectParser.registerType(String.class, new StringSerializer());
		ObjectParser.registerType(Double.class, new DoubleSerializer());
		ObjectParser.registerType(Long.class, new LongSerializer());
		ObjectParser.registerType(List.class, new CollectionSerializer());
		ObjectParser.registerType(PermissionManager.class, new PermissionSerializer());
		ObjectParser.registerType(Vector3f.class, new Vector3fSerializer());
		
		SyncedObjectManager.registerTrackedType(Player.class);
		SyncedObjectManager.registerTrackedType(Bullet.class);
		
		MagicaServer gameServer = new MagicaServer();
		
		gameServer.open();
		
		long lastTick = new Date().getTime();
		
		while (gameServer.isOpen()) {
			long currentTime = new Date().getTime();
			
			if ((currentTime - lastTick) >= 1000 / gameServer.updateRate) {
				lastTick = currentTime;
				
				gameServer.onUpdate((1000.0f / (currentTime - lastTick)));
			}
		}
	}
}
