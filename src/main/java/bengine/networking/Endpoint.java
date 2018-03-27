package bengine.networking;

import bengine.networking.messages.NetworkMessage;

public interface Endpoint {
	public Connection getConnection();
	public long getEndpointId();
	public void onMessage(NetworkMessage message);
	public boolean isRemote();
	public Server getServer();
}
