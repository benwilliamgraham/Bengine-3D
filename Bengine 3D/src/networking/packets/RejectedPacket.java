package networking.packets;

public class RejectedPacket extends Packet {
	public static int packetId = 1;
	
	public String message;
	
	private static final byte MESSAGE_ID = 1;
	
	public RejectedPacket(String message) {
		this.message = message;
		
		data.add(MESSAGE_ID, message);
		data.pack();
	}

	@Override
	public void onLoad() {
		message = data.getString(MESSAGE_ID);
	}

	@Override
	public int getId() {
		return packetId;
	}
	
}
