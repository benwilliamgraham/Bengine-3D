package networking.packets;

public class HandshakePacket extends Packet {
	public static int packetId = 0;
	
	public String name, id;
	
	private static final byte NAME_ID = 1;
	private static final byte ID_ID = 2;
	
	public HandshakePacket() {
		super();
	}
	
	public HandshakePacket(String name) {
		super();
		this.name = name;
		
		data.add(NAME_ID, name);
		data.pack();
	}
	
	public HandshakePacket(String name, String id) {
		super();
		this.name = name;
		this.id = id;
		
		data.add(NAME_ID, name);
		data.add(ID_ID, id);
		data.pack();
	}
	
	@Override
	public void onLoad() {
		name = data.getString(NAME_ID);
		
		try {
			id = data.getString(ID_ID);
		} catch (Exception e) {} //If it doesn't exist, it will throw an exception, which we don't care about.
	}

	@Override
	public int getId() {
		return packetId;
	}
}
