package networking.packets;

public class DestroyEntityPacket extends Packet {
	
	public static final int packetId = 4;
	
	private static final byte ENT_ID = 1;
	
	public String entityId;
	
	public DestroyEntityPacket() {
		super();
	}
	
	public DestroyEntityPacket(String entityId) {
		super();
		this.entityId = entityId;
		
		data.add(ENT_ID, entityId);
		data.pack();
	}
	
	@Override
	public void onLoad() {
		this.entityId = data.getString(ENT_ID);
	}

	@Override
	public int getId() {
		return packetId;
	}

}
