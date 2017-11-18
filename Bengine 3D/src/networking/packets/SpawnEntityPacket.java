package networking.packets;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Vector3f;

import networking.NetworkedClient;

public class SpawnEntityPacket extends Packet {

	public static int packetId = 2;
	
	public Vector3f pos, rot, scale;
	public int entityType;
	public byte owner;
	
	//PACKET LAYOUT [packetID, ownerID, entityType, position, rotation, scale]
	
	private static final int ID_OFFSET = 1;
	private static final int TYPE_OFFSET = 2;
	private static final int TYPE_LENGTH = Integer.BYTES;
	private static final int POS_OFFSET = TYPE_OFFSET + TYPE_LENGTH;
	private static final int POS_LENGTH = Float.BYTES * 3;
	private static final int ROT_OFFSET = POS_OFFSET + POS_LENGTH;
	private static final int ROT_LENGTH = Float.BYTES * 3;
	private static final int SCALE_OFFSET = ROT_OFFSET + ROT_LENGTH;
	private static final int SCALE_LENGTH = Float.BYTES * 3;
	
	public SpawnEntityPacket(Vector3f position, Vector3f rotation, Vector3f scale, int entityType, NetworkedClient owner) {
		this.pos = position;
		this.rot = rotation;
		this.scale = scale;
		this.entityType = entityType;
		this.owner = owner.id;
		
	}
	
	@Override
	public void loadPacket(DatagramPacket p) {
		byte[] data = p.getData();
		
		byte owner = data[ID_OFFSET];
		
		byte[] typeData = new byte[TYPE_LENGTH];
		System.arraycopy(data, TYPE_OFFSET, typeData, 0, TYPE_LENGTH);
		int type = ByteBuffer.wrap(typeData).getInt();
		
		byte[] positionData = new byte[POS_LENGTH];
		System.arraycopy(data, POS_OFFSET, positionData, 0, POS_LENGTH);
		Vector3f position = (Vector3f) new Vector3f().load(ByteBuffer.wrap(positionData).asFloatBuffer());
		
		byte[] rotationData = new byte[ROT_LENGTH];
		System.arraycopy(data, ROT_OFFSET, rotationData, 0, ROT_LENGTH);
		Vector3f rotation = (Vector3f) new Vector3f().load(ByteBuffer.wrap(rotationData).asFloatBuffer());
		
		byte[] scaleData = new byte[SCALE_LENGTH];
		System.arraycopy(data, SCALE_OFFSET, scaleData, 0, SCALE_LENGTH);
		Vector3f scale = (Vector3f) new Vector3f().load(ByteBuffer.wrap(scaleData).asFloatBuffer());
		
		this.owner = owner;
		this.entityType = type;
		this.pos = position;
		this.rot = rotation;
		this.scale = scale;
	}

	@Override
	public byte[] getBytes() {
		byte[] data = new byte[Packet.PACKET_SIZE];
		data[0] = (byte) packetId;
		
		data[ID_OFFSET] = this.owner;
		
		byte[] positionData = ByteBuffer.allocate(POS_LENGTH)
			.putFloat(pos.x)
			.putFloat(pos.y)
			.putFloat(pos.z)
			.array();
		System.arraycopy(positionData, 0, data, POS_OFFSET, POS_LENGTH);
		byte[] rotationData = ByteBuffer.allocate(ROT_LENGTH)
				.putFloat(rot.x)
				.putFloat(rot.y)
				.putFloat(rot.z)
				.array();
		System.arraycopy(rotationData, 0, data, ROT_OFFSET, ROT_LENGTH);
		byte[] scaleData = ByteBuffer.allocate(SCALE_LENGTH)
				.putFloat(scale.x)
				.putFloat(scale.y)
				.putFloat(scale.z)
				.array();
		System.arraycopy(scaleData, 0, data, SCALE_OFFSET, SCALE_LENGTH);
		return data;
	}

	@Override
	public int getId() {
		return packetId;
	}

}
