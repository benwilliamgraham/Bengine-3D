package networking.packets;

public class HandshakePacket extends Packet {
	public static int packetId = 0;
	
	public String name;
	
	private static final byte NAME_ID = 1;
	
	public HandshakePacket() {
		super();
	}
	
	public HandshakePacket(String name) {
		super();
		this.name = name;
		
		data.add(NAME_ID, name);
		data.pack();
	}
	
	@Override
	public void onLoad() {
		name = data.getString(NAME_ID);
	}

	@Override
	public int getId() {
		return packetId;
	}
}
