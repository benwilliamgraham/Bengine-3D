package bengine.networking.messages;

import java.io.IOException;
import java.nio.ByteBuffer;

import bengine.networking.NDBT;
import bengine.networking.serialization.ObjectParser;
import bengine.networking.server.Server;
import bengine.networking.sync.SyncedObject;

public class RPCMessage extends NetworkMessage {
	//Hierarchy: [objectInstanceId, rpcMode, functionName, #params, [paramLength, parameter, ...]];
	public static final byte MESSAGE_TYPE = genMessageType();
	
	private static final byte OBJECT_INSTANCE_ID = 0;
	private static final byte RPC_MODE = 1;
	private static final byte FUNCTION_NAME = 2;
	private static final byte PARAMS = 3;
	
	public long objectInstanceId;
	public String functionName;
	public Object[] params;
	public int rpcMode;
	
	public RPCMessage() {
		
	}
	
	public RPCMessage(SyncedObject obj, String fnName, Object[] params, int rpcMode) {
		this.objectInstanceId = obj.getInstanceID();
		this.functionName = fnName;
		this.params = params;
		this.rpcMode = rpcMode;
	}
	
	@Override
	public void parseFrom(ByteBuffer d) {
		NDBT data = new NDBT(d);
		
		this.objectInstanceId = data.getLong(OBJECT_INSTANCE_ID);
		this.rpcMode = data.getInt(RPC_MODE);
		this.functionName = data.getString(FUNCTION_NAME);
		
		ByteBuffer params = data.getBytes(PARAMS);
		
		this.params = parseParams(params);
	}

	@Override
	public ByteBuffer getBytes() throws IOException {
		NDBT data = new NDBT(MESSAGE_TYPE);
		
		data.add(OBJECT_INSTANCE_ID, this.objectInstanceId);
		data.add(RPC_MODE, this.rpcMode);
		data.add(FUNCTION_NAME, this.functionName);
		data.add(PARAMS, writeParams(this.params));
		
		return data.toBytes();
	}

	@Override
	public byte getType() {
		return MESSAGE_TYPE;
	}

	private Object[] parseParams(ByteBuffer data) {
			
		int numParams = data.getInt();
		
		Object[] params = new Object[numParams];
		
		ObjectParser p = new ObjectParser();
		
		for (int x = 0; x < numParams; x++) {
			int paramLength = data.getInt();
			
			byte[] paramData = new byte[paramLength];
			data.get(paramData);
			
			Object param = p.deserialize(paramData);
			params[x] = param;
		}
		
		return params;
	}
	
	private ByteBuffer writeParams(Object[] params) {
		ByteBuffer bb = ByteBuffer.allocate(Server.BUFFER_SIZE);
		
		bb.putInt(params.length);
		
		ObjectParser p = new ObjectParser();
		
		for (Object param : params) {
			ByteBuffer b = p.serialize(param);
			
			bb.putInt(b.limit());
			bb.put(b);
		}
		
		bb.flip();
		return bb;
	}
	
}
