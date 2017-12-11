package networking.packets;

public class DisconnectPacket extends Packet {
	public static final int packetId = 5;
	
	public String message;
	
	private final byte MSG_ID = 1;
	
	public DisconnectPacket() {
		super();
	}
	
	public DisconnectPacket(String message) {
		super();
		this.message = message;
		
		data.add(MSG_ID, message);
		data.pack();
	}

	@Override
	public void onLoad() {
		message = data.getString(MSG_ID);
	}

	@Override
	public int getId() {
		return packetId;
	}
}
