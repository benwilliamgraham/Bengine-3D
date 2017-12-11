package networking;

import java.util.UUID;

import org.lwjgl.util.vector.Vector3f;

import networking.packets.RegisterEntityPacket;

public class NetworkedEntity {
	public Vector3f position, rotation, scale, velocity;
	public String id;
	public int entityType;
	public NetworkedClient owner;
	
	
	
	public NetworkedEntity(RegisterEntityPacket p) {
		this.position = p.pos;
		this.rotation = p.rot;
		this.scale = p.scale;
		this.entityType = p.entityType;
		
		if (p.entityId != null) {
			this.id = p.entityId;
		} else {
			this.id = UUID.randomUUID().toString();
		}
		
		this.velocity = new Vector3f();
	}
}
