package networking.packets;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import networking.NetworkedEntity;

public class RegisterEntityPacket extends Packet {

	public static int packetId = 2;
	
	protected static final byte POS_ID = 1;
	protected static final byte ROT_ID = 2;
	protected static final byte SCALE_ID = 3;
	protected static final byte ENT_ID = 4;
	protected static final byte ENT_TYPE_ID = 5;
	protected static final byte OWNER_ID = 6;
	
	public Vector3f pos, rot, scale;
	public String entityId;
	public String owner;
	public int entityType;
	
	public RegisterEntityPacket() {
		super();
	}
	
	public RegisterEntityPacket(Vector3f position, Vector3f rotation, Vector3f scale, int entityType, String entityId, String owner) {
		super();
		
		this.pos = position;
		this.rot = rotation;
		this.scale = scale;
		this.entityId = entityId;
		this.entityType = entityType;
		this.owner = owner;
		
		data.add(POS_ID, pos);
		data.add(ROT_ID, rot);
		data.add(SCALE_ID, scale);
		data.add(ENT_ID, entityId);
		data.add(ENT_TYPE_ID, entityType);
		data.add(OWNER_ID, owner);
		data.pack();
		
	}
	
	public RegisterEntityPacket(NetworkedEntity e) {
		this(e.position, e.rotation, e.scale, e.entityType, e.id, (e.owner == null)? "":e.owner.id);
	}
	
	public RegisterEntityPacket(Entity e) {
		this(e.position, e.rotation, e.scale, e.getEntityType(), e.id, (e.owner == null)? "":e.owner);
	}

	@Override
	public void onLoad() {
		pos = data.getVec3(POS_ID);
		rot = data.getVec3(ROT_ID);
		scale = data.getVec3(SCALE_ID);
		owner = data.getString(OWNER_ID);
		entityId = data.getString(ENT_ID);
		entityType = data.getInt(ENT_TYPE_ID);
	}
	
	@Override
	public int getId() {
		return packetId;
	}
}
