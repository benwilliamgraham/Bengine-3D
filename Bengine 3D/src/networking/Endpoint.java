package networking;

import networking.messages.NetworkMessage;
import networking.server.Server;

public interface Endpoint {
	public Connection getConnection();
	public long getEndpointId();
	public void onMessage(NetworkMessage message);
	public boolean isRemote();
	public Server getServer();
}
