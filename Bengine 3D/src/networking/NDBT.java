package networking;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import networking.server.Server;

//Stands for Non-Descript Binary Tag

public class NDBT {
	
	private static final byte TERMINATOR = 0xF; //terminates the NDBT when this value is read as an ID. 
	
	private Map<Byte, ByteBuffer> unpackedData = new HashMap<Byte, ByteBuffer>();
	private ByteBuffer data;
	private byte packetType;
	private boolean isPacket = true;
	
	public NDBT() {
		isPacket = false;
	}
	
	public NDBT(byte packetType) {
		this.packetType = packetType;
	}
	
	public NDBT(ByteBuffer data) {
		this.data = data;
		
		while (data.hasRemaining()) {
			byte id = data.get();
			if (id == TERMINATOR) {
				break;
			} else {
				int size = data.getInt();
				byte[] value = new byte[size];
				data.get(value);
				unpackedData.put(id, ByteBuffer.wrap(value));
			}
			
		}
		
	}
	
	public void pack() {
		ByteBuffer d = ByteBuffer.allocate(Server.BUFFER_SIZE);
		if (isPacket) {d.put(packetType);}
		for (Entry<Byte, ByteBuffer> item : unpackedData.entrySet()) {
			
			ByteBuffer bb = ByteBuffer.allocate(item.getValue().limit() + Byte.BYTES + Integer.BYTES)
					   .put(item.getKey())
					   .putInt(item.getValue().limit())
					   .put(item.getValue());
			bb.flip();	
			d.put(bb);
			
		}
		
		d.put(TERMINATOR);
		d.flip();
		
		this.data = d;
	}
	
	public void add(byte id, boolean value) {
		ByteBuffer bb = ByteBuffer.allocate(Byte.BYTES).put((byte) ((value)? 1:0));
		bb.flip();
		unpackedData.put(id, bb);
		//unpackedData.put(id, new byte[] {(byte)((value)? 1:0)});
	}
	
	public void add(byte id, int value) {
		ByteBuffer b = ByteBuffer.allocate(Integer.BYTES);
		b.putInt(value);
		b.flip();
		unpackedData.put(id, b);
	}
	
	public void add(byte id, long value) {
		ByteBuffer b = ByteBuffer.allocate(Long.BYTES);
		b.putLong(value);
		b.flip();
		unpackedData.put(id, b);
	}
	
	public void add(byte id, float value) {
		ByteBuffer b = ByteBuffer.allocate(Float.BYTES);
		b.putFloat(value);
		b.flip();
		unpackedData.put(id, b);
	}
	
	public void add(byte id, byte x) {
		ByteBuffer b = ByteBuffer.allocate(Byte.BYTES); //Lul, never gets old
		b.put(x);
		b.flip();
		unpackedData.put(id, b);
	}
	
	public void add(byte id, String str) {
		ByteBuffer d = ByteBuffer.wrap(str.getBytes());
		
		unpackedData.put(id, d);
	}
	
	public void add(byte id, byte[] data) {
		unpackedData.put(id, ByteBuffer.wrap(data));
	}
	
	public void add(byte id, ByteBuffer data) {
		unpackedData.put(id, data);
	}
	
	public byte getByte(byte id) {
		return unpackedData.get(id).duplicate().get();
	}
	
	public boolean getBool(byte id) {
		return (unpackedData.get(id).duplicate().get() == 0)? false:true;
	}
	
	public int getInt(byte id) {
		return unpackedData.get(id).duplicate().getInt();
	}
	
	public long getLong(byte id) {
		return unpackedData.get(id).duplicate().getLong();
	}
	
	public float getFloat(byte id) {
		return unpackedData.get(id).duplicate().getFloat();
	}
	
	public String getString(byte id) {
		return new String(unpackedData.get(id).duplicate().array());
	}
	
	public ByteBuffer getBytes(byte id) {
		return (unpackedData.get(id).duplicate());
	}
	
	public boolean hasElement(byte id) {
		return (unpackedData.containsKey(id));
	}
	
	public ByteBuffer toBytes() {
		return data.duplicate();
	}
}
