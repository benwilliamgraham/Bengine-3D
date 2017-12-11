package networking.packets;

import org.lwjgl.util.vector.Vector3f;

import entities.DynEntity;
import entities.Entity;
import networking.NetworkedEntity;

public class UpdateEntityPacket extends Packet {
	public static int packetId = 3;
	
	private static final byte POS_ID = 1;
	private static final byte ROT_ID = 2;
	private static final byte VEL_ID = 3;
	private static final byte ENTITY_ID = 4;
	
	public Vector3f pos, rot, vel;
	public String entity;
	
	public UpdateEntityPacket() {
		super();
	}
	
	public UpdateEntityPacket(Vector3f pos, Vector3f rot, Vector3f vel, String entityId) {
		super();
		this.pos = pos;
		this.rot = rot;
		this.vel = vel;
		this.entity = entityId;
		
		data.add(POS_ID, pos);
		data.add(ROT_ID, rot);
		data.add(VEL_ID, vel);
		data.add(ENTITY_ID, entity);
		data.pack();
	}
	
	public UpdateEntityPacket(NetworkedEntity e) {
		this(e.position, e.rotation, e.velocity, e.id);
	}
	
	public UpdateEntityPacket(Entity e) {
		this(e.position, e.rotation, e.velocity, e.id);
	}

	@Override
	public void onLoad() {
		this.pos = data.getVec3(POS_ID);
		this.rot = data.getVec3(ROT_ID);
		this.vel = data.getVec3(VEL_ID);
		this.entity = data.getString(ENTITY_ID);
	}

	@Override
	public int getId() {
		return packetId;
	}

}
