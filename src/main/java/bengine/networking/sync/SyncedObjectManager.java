package bengine.networking.sync;

import java.util.Map.Entry;

import bengine.networking.Connection;
import bengine.networking.client.Client;
import bengine.networking.messages.ObjectMessage;
import bengine.networking.messages.RPCMessage;
import bengine.networking.server.NetworkedClient;
import bengine.networking.server.Server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

public class SyncedObjectManager {
	
	private static HashMap<Integer, TrackedObjectMeta> trackedTypesById = new HashMap<Integer, TrackedObjectMeta>();
	private static HashMap<Class<? extends SyncedObject>, TrackedObjectMeta> trackedTypesByClass = new HashMap<Class<? extends SyncedObject>, TrackedObjectMeta>();
	
	private HashMap<Long, SyncedObject> trackedObjects;
	private HashMap<Long, SyncedObject> cachedObjects;
	
	
	public SyncedObjectManager() {
		this.trackedObjects = new HashMap<Long, SyncedObject>();
		this.cachedObjects = new HashMap<Long, SyncedObject>();
	}
	
	public void registerObject(SyncedObject obj, Connection connection) {
		ObjectMessage om = new ObjectMessage(obj);
		
		cachedObjects.put(obj.getInstanceID(), obj);
		
		connection.send(om);
	}

	public boolean isRegistered(ObjectMessage om) {
		return trackedObjects.containsKey(om.objectInstanceId);
	}
	
	public SyncedObject getObject(long id) {
		return trackedObjects.get(id);
	}
	
	public void handleRPC(RPCMessage m) {
		if (trackedObjects.containsKey(m.objectInstanceId)) {
			SyncedObject obj = trackedObjects.get(m.objectInstanceId);
			
			if (m.getEndpoint().isRemote() && !obj.mutability.hasPermission(m.getEndpoint().getEndpointId())) {
				return ;
			}
			
			TrackedObjectMeta meta = getObjectMeta(obj.getClass());
			
			Method method = meta.rpcMethods.get(m.functionName);
			
			try {
				method.invoke(obj, m.params);
			} catch (Exception e) {
				//TODO: When we get a logger.
			}
		}
	}
	
	public void onMessage(ObjectMessage om) {
		
		if (trackedObjects.containsKey(om.objectInstanceId)) { //Are we creating or updating an object?
			//Updating:
			SyncedObject obj = trackedObjects.get(om.objectInstanceId);
			
			if (om.getEndpoint().isRemote()) { //Check if we are on the server.
				if (obj.getOwner() == om.getEndpoint().getEndpointId()) {
					obj.mutability = om.mutability;
					obj.visibility = om.visibility;
				} else {
					if (!obj.mutability.hasPermission(om.getEndpoint().getEndpointId())) {
						return ; //If the client trying to modify the object doesn't have permissions, don't let them.
					}
				}
			}
			
			//Update the object.
			
			HashMap<String, Field> objectFields = getTrackedFields(om.objectType);
			
			for (Entry<String, Object> fieldValue : om.fields.entrySet()) {
				Field f = objectFields.get(fieldValue.getKey());
				
				try {
					f.set(obj, fieldValue.getValue());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			
			obj.onObjectUpdate();
			
		} else { //Creating:
			SyncedObject obj = null;
			
			if (cachedObjects.containsKey(om.objectInstanceId)) {
				obj = cachedObjects.remove(om.objectInstanceId);
				
				obj.setManager(this);
				obj.setEndpoint(om.getEndpoint());
				obj.mutability = om.mutability;
				obj.visibility = om.visibility;
				
				if (om.getEndpoint().isRemote()) { //Check if we are on the server.
					obj.setOwner((om.objectOwner == om.getEndpoint().getServer().getEndpointId())? 
							om.objectOwner : om.getEndpoint().getEndpointId()); //if we are, we say who owns it (no owner spoofing), the owner can be either the creator or the server.
					Server s = om.getEndpoint().getServer();
					s.onNewObject(obj);
				} else {
					obj.setOwner(om.objectOwner); //if we're not, then we just accept what the server says.
					Client c = (Client) om.getEndpoint();
					c.onNewObject(obj);
				}
				
				trackedObjects.put(obj.getInstanceID(), obj);
				obj.onRegistered();
				
			} else {
				Class<? extends SyncedObject> type = getObjectType(om.objectType);
				
				try {
					obj = type.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
				
				HashMap<String, Field> fields = getTrackedFields(om.objectType);
				
				for (Entry<String, Object> e : om.fields.entrySet()) {
					String k = e.getKey();
					
					if (fields.containsKey(k)) {
						Field f = fields.get(k);
						
						try {
							f.set(obj, e.getValue());
						} catch (IllegalArgumentException | IllegalAccessException e1) {
							e1.printStackTrace();
						}
					}
				}
				
				obj.setManager(this);
				obj.setEndpoint(om.getEndpoint());
				obj.setInstanceId(om.objectInstanceId);
				obj.mutability = om.mutability;
				obj.visibility = om.visibility;
				
				if (om.getEndpoint().isRemote()) {
					obj.setOwner(om.getEndpoint().getEndpointId());
					Server s = om.getEndpoint().getServer();
					s.onNewObject(obj);
				} else {
					obj.setOwner(om.objectOwner);
					Client c = (Client) om.getEndpoint();
					c.onNewObject(obj);
				}
				
				
				
				
				trackedObjects.put(obj.getInstanceID(), obj);
				obj.onRegistered(); 
				
				if (om.getEndpoint().isRemote()) { //If we're the server, then register the object with all the clients.
					Server server = om.getEndpoint().getServer();
					ObjectMessage pom = new ObjectMessage(obj);
					//TODO: Add visibility for certain objects and certain clients.
					for (NetworkedClient c : server.getClients()) {
						c.getConnection().send(pom);
					}
				}
			}
			
		}
	}
	
	public void update(Connection connection) {
		if (connection.getEndpoint().isRemote()) {
			//If we're on the server, broadcast the changes to all clients who can view the object.
			for (NetworkedClient client : connection.getEndpoint().getServer().getClients()) {
				for (SyncedObject obj : this.trackedObjects.values()) {
					if (obj.visibility.hasPermission(client.getEndpointId()) && obj.getOwner() != client.getEndpointId()) {
						ObjectMessage om = new ObjectMessage(obj);
						client.getConnection().send(om);
					}
				}
			}
		} else {
			for (SyncedObject obj : this.trackedObjects.values()) {
				if (obj.mutability.hasPermission(connection.getEndpoint().getEndpointId()) || connection.getEndpoint().getEndpointId() == obj.getOwner()) {
					ObjectMessage om = new ObjectMessage(obj);
					connection.send(om);
				}
			}
		}
	}
	
	public Collection<SyncedObject> getObjects() {
		return this.trackedObjects.values();
	}
	
	public static void registerTrackedType(Class<? extends SyncedObject> t) {
		try {
			int objectType = t.getField("OBJECT_TYPE").getInt(null);
			
			
			HashMap<String, Field> objTrackedFields = new HashMap<String, Field>();
			
			for (Field f : t.getFields()) {
				SyncedObject.SyncedField sf;
				if ((sf = f.getAnnotation(SyncedObject.SyncedField.class)) != null) {
					objTrackedFields.put(sf.value(), f);
				}
			}
			
			HashMap<String, Method> objRPCMethods = new HashMap<String, Method>();
			
			for (Method m : t.getMethods()) {
				SyncedObject.RPC rpc;
				if ((rpc = m.getAnnotation(SyncedObject.RPC.class)) != null) {
					objRPCMethods.put(rpc.value(), m);
				}
			}
			
			TrackedObjectMeta meta = new TrackedObjectMeta();
			meta.objectType = objectType;
			meta.type = t;
			meta.trackedFields = objTrackedFields;
			meta.rpcMethods = objRPCMethods;
			
			trackedTypesById.put(objectType, meta);
			trackedTypesByClass.put(t, meta);
			
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, Field> getTrackedFields(int t) {
		return trackedTypesById.get(t).trackedFields;
	}
	
	public static HashMap<String, Field> getTrackedFields(Class<? extends SyncedObject> c) {
		return trackedTypesByClass.get(c).trackedFields;
	}
	
	public static Class<? extends SyncedObject> getObjectType(int b) {
		return trackedTypesById.get(b).type;
	}
	
	public static TrackedObjectMeta getObjectMeta(Class<? extends SyncedObject> c) {
		return trackedTypesByClass.get(c);
	}
	
	private static class TrackedObjectMeta {
		public TrackedObjectMeta() {
			
		}
		@SuppressWarnings("unused")
		public int objectType;
		public Class<? extends SyncedObject> type;
		public HashMap<String, Field> trackedFields;
		public HashMap<String, Method> rpcMethods;
	}
}
