package networking;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class PacketSource {
	
	private Map<Integer, ArrayList<PacketHandler>> packetHandlers;
	
	public PacketSource() {
		this.packetHandlers = new HashMap<Integer, ArrayList<PacketHandler>>();
	}
	
	public void OnPacket(int[] types, PacketHandler handler) {
		for (int type : types) {
			if (packetHandlers.containsKey(type)) {
				packetHandlers.get(type).add(handler);
			} else {
				ArrayList<PacketHandler> handlers = new ArrayList<PacketHandler>();
				handlers.add(handler);
				packetHandlers.put(type, handlers);
			}
		}
	}
	
	public void EmitPacket(Packet p) {
		if (packetHandlers.containsKey(p.getId())) {
			for (PacketHandler r : packetHandlers.get(p.getId())) {
				r.handle(p);
			}
		}
	}
}
