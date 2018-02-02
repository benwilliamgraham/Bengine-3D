package networking.messages;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import java.util.Map;
import java.util.Map.Entry;

import networking.NDBT;
import networking.PermissionManager;
import networking.serialization.ObjectParser;
import networking.server.Server;
import networking.sync.SyncedObject;
import networking.sync.SyncedObjectManager;

import java.util.HashMap;

public class ObjectMessage extends NetworkMessage {
	
	//Hierarchy [MESSAGE_TYPE, objectIntanceId, objectOwner, (FieldName, FieldValue)]
	
	public static final byte MESSAGE_TYPE = genMessageType();
	
	private static final byte OBJECT_TYPE = 0;
	private static final byte OBJECT_INSTANCE_ID = 1;
	private static final byte OBJECT_OWNER = 2;
	private static final byte OBJECT_FIELDS = 3;
	private static final byte OBJECT_VISIBILITY = 4;
	private static final byte OBJECT_MUTABILITY = 5;
	
    public int objectType;
    public long objectInstanceId, objectOwner;
    public Map<String, Object> fields;
    public PermissionManager visibility, mutability;
    
    
    public ObjectMessage() {}
    
    public ObjectMessage(SyncedObject obj) {
    	
    	this.objectType = obj.getType();
    	this.objectInstanceId = obj.getInstanceID();
    	this.objectOwner = obj.getOwner();
    	this.visibility = obj.visibility;
    	this.mutability = obj.mutability;
    	
    	//Load the fields of the object into the packet.
    	Map<String, Field> fields = SyncedObjectManager.getTrackedFields(obj.getClass());
    	this.fields = new HashMap<String, Object>();
    	try {
    		for (Entry<String, Field> e : fields.entrySet()) {
    			Object value = e.getValue().get(obj);
    			
    			this.fields.put(e.getKey(), value); //TODO: Rename the fields variable to make this a little less confusing..
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    }

    public void parseFrom(ByteBuffer d) {
    	
    	NDBT data = new NDBT(d);
    	
    	this.objectType = data.getInt(OBJECT_TYPE);
    	this.objectInstanceId = data.getLong(OBJECT_INSTANCE_ID);
    	this.objectOwner = data.getLong(OBJECT_OWNER);
    	this.fields = new HashMap<String, Object>();
    	
    	ByteBuffer fieldsData = data.getBytes(OBJECT_FIELDS);
    	
    	loadFields(fields, fieldsData);
    	
    	ObjectParser parser = new ObjectParser();
    	
    	if (data.hasElement(OBJECT_VISIBILITY)) {
    		ByteBuffer visibility = data.getBytes(OBJECT_VISIBILITY);
        	
        	this.visibility = parser.deserialize(visibility);
    	}
    	
    	if (data.hasElement(OBJECT_MUTABILITY)) {
    		ByteBuffer mutability = data.getBytes(OBJECT_MUTABILITY);
        	
        	this.mutability = parser.deserialize(mutability);
    	}
    }

    public void parseFrom(byte[] data) {
    	parseFrom(ByteBuffer.wrap(data));
    }

    public ByteBuffer getBytes() throws IOException {
    	NDBT data = new NDBT(MESSAGE_TYPE);
    	
    	data.add(OBJECT_TYPE, objectType);
    	data.add(OBJECT_INSTANCE_ID, objectInstanceId);
    	data.add(OBJECT_OWNER, objectOwner);
    	
    	data.add(OBJECT_FIELDS, writeFields(this.fields));
    	
    	ObjectParser parser = new ObjectParser();
    	
    	if (this.mutability != null) {
    		data.add(OBJECT_VISIBILITY, parser.serialize(this.visibility));
    	}
    	
    	if (this.visibility != null) {
    		data.add(OBJECT_MUTABILITY, parser.serialize(this.mutability));
    	}
    	
    	data.pack();
    	return data.toBytes();
    }

    public byte getType() {
        return MESSAGE_TYPE;
    }
    
    private ByteBuffer writeFields(Map<String, Object> fields) {
    	ByteBuffer bb = ByteBuffer.allocate(Server.BUFFER_SIZE);
    	
    	try {
    		ObjectParser parser = new ObjectParser();
        	
        	for (Entry<String, Object> e : this.fields.entrySet()) {
        		//Serialize the key
        		String key = e.getKey();
        		
        		//Serialize the object.
        		Object obj = e.getValue();
        		
        		ByteBuffer b = parser.serialize(obj);
        		
        		if (b != null) {
        			bb.putInt(key.length());
            		
            		for (char c : key.toCharArray()) {
            			bb.putChar(c);
            		}
            		
            		bb.putInt(b.limit());
            		bb.put(b);
        		}
        	}
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    	
    	bb.flip();
    	return bb;
    }
    
    private void loadFields(Map<String, Object> fields, ByteBuffer fieldsData) {
    	ObjectParser parser = new ObjectParser();
    	
        while (fieldsData.hasRemaining()) {
            int keyLength = fieldsData.getInt();
            
            String key = "";

            for (int x = 0; x < keyLength; x++) {
            	key += fieldsData.getChar();
            }
            
            int objLength = fieldsData.getInt();
            byte objData[] = new byte[objLength];
            fieldsData.get(objData);
            
            try {
            	Object obj = parser.deserialize(objData);
            	fields.put(key, obj);
            } catch (Exception e) {}
        }
    }
}
