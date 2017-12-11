package networking;

import networking.packets.Packet;

interface Handler<T> {
	public void handle(T p);
}
