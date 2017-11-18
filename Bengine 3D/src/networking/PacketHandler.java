package networking;

import networking.packets.Packet;

public interface PacketHandler {
	public void handle(Packet p);
}
