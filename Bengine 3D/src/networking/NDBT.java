package networking;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector3f;

import networking.packets.Packet;

//Stands for Non-Descript Binary Tag

public class NDBT {
	
	private static final byte TERMINATOR = 0xF; //terminates the NDBT when this value is read as an ID. 
	
	private Map<Byte, byte[]> unpackedData = new HashMap<Byte, byte[]>();
	private byte[] data;
	
	public NDBT(int size) {
		data = new byte[size];
	}
	
	public NDBT(byte[] data) {
		this.data = data;
		
		int pos = 0;
		while (pos < data.length) {
			byte id = data[pos];
			if (id == TERMINATOR) {
				break;
			} else {
				int size = data[pos + 1];
				byte[] value = new byte[size];
				System.arraycopy(data, pos + 2, value, 0, size);			
				unpackedData.put(id, value);
				pos += (2 + size);
			}
			
		}
		
	}
	
	public void pack() {
		ByteBuffer d = ByteBuffer.allocate(Packet.PACKET_SIZE);
		for (Entry<Byte, byte[]> item : unpackedData.entrySet()) {
			
			ByteBuffer bb = ByteBuffer.allocate(item.getValue().length + 2)
					   .put(item.getKey())
					   .put((byte) item.getValue().length)
					   .put(item.getValue());
			bb.flip();	
			d.put(bb);
			
			/*System.out.println(item.getKey());
			System.out.println((byte) item.getValue().length);
			
			for (byte b : item.getValue()) {
				System.out.print(b + " ");
			}
			System.out.println();*/
		}
		
		d.put(TERMINATOR);
		d.flip();
		
		if (d.hasArray()) {
			this.data = d.array();
		}
	}
	
	public void add(byte id, boolean value) {
		unpackedData.put(id, new byte[] {(byte)((value)? 1:0)});
	}
	
	public void add(byte id, int value) {
		ByteBuffer b = ByteBuffer.allocate(Integer.BYTES);
		b.putInt(value);
		b.flip();
		unpackedData.put(id, b.array());
	}
	
	public void add(byte id, float value) {
		ByteBuffer b = ByteBuffer.allocate(Float.BYTES);
		b.putFloat(value);
		b.flip();
		unpackedData.put(id, b.array());
	}
	
	public void add(byte id, byte x) {
		unpackedData.put(id, new byte[] {x});
	}
	
	public void add(byte id, String str) {
		byte[] data = new byte[str.length()];
		byte[] stringData = str.getBytes();
		System.arraycopy(stringData, 0, data, 0, stringData.length);
		
		unpackedData.put(id, data);
	}
	
	public void add(byte id, Vector3f v) {
		ByteBuffer data = ByteBuffer.allocate(3 * Float.BYTES);
		data.putFloat(v.x)
			.putFloat(v.y)
			.putFloat(v.z);
		data.flip();
		
		unpackedData.put(id, data.array());
	}
	
	public byte getByte(byte id) {
		return unpackedData.get(id)[0];
	}
	
	public boolean getBool(byte id) {
		return (unpackedData.get(id)[0] == 0)? false:true;
	}
	
	public int getInt(byte id) {
		IntBuffer b = ByteBuffer.allocate(Integer.BYTES)
				.put(unpackedData.get(id))
				.asIntBuffer();
		b.flip();
		
		return b.get();
	}
	
	public float getFloat(byte id) {
		FloatBuffer b = ByteBuffer.allocate(Float.BYTES)
				.put(unpackedData.get(id))
				.asFloatBuffer();
		b.flip();
		
		return b.get();
	}
	
	public String getString(byte id) {
		return new String(unpackedData.get(id));
	}
	
	public Vector3f getVec3(byte id) {
		
		ByteBuffer b = ByteBuffer.allocate(unpackedData.get(id).length);
		b.put(unpackedData.get(id));
		b.flip();
		
		Vector3f v = new Vector3f(b.getFloat(), b.getFloat(), b.getFloat());
		
		return v;
	}
	
	public byte[] toBytes() {
		return data;
	}
}
