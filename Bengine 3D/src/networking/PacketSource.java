package networking;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class PacketSource {
	
	private Map<Integer, ArrayList<PacketHandler>> packetHandlers;
	
	public PacketSource() {
		this.packetHandlers = new HashMap<Integer, ArrayList<PacketHandler>>();
		System.out.println("init");
	}
	
	public void OnPacket(int[] types, PacketHandler handler) {
		System.out.println("Added packet handler");
		System.out.println(types);
		for (int type : types) {
			if (packetHandlers.containsKey(type)) {
				System.out.println("adding handler 1");
				packetHandlers.get(type).add(handler);
			} else {
				System.out.println("adding handler");
				ArrayList<PacketHandler> handlers = new ArrayList<PacketHandler>();
				handlers.add(handler);
				packetHandlers.put(type, handlers);
			}
		}
	}
	
	public void EmitPacket(Packet p) {
		if (packetHandlers.containsKey(p.getId())) {
			for (PacketHandler r : packetHandlers.get(p.getId())) {
				System.out.println(r);
				r.handle(p);
			}
		}
	}
}
