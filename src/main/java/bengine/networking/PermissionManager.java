package bengine.networking;

import java.util.List;
import java.util.ArrayList;

public class PermissionManager {
	
	public boolean allowAll = false;
	List<Long> permittedClients;
	
	public PermissionManager() {
		this.permittedClients = new ArrayList<Long>();
	}
	
	public PermissionManager(List<Long> permittedClients) {
		this.permittedClients = permittedClients;
	}
	
	public boolean hasPermission(long id) {
		return permittedClients.contains(id) | this.allowAll;
	}
	
	public void addPermissions(long id) {
		if (!permittedClients.contains(id)) {
			permittedClients.add(id);
		}
	}
	
	public void setPermissions(List<Long> permittedClients) {
		this.permittedClients = permittedClients;
	}
	
	public List<Long> getPermissions() {
		return permittedClients;
	}
	
	public void removePermissions(long id) {
		while (permittedClients.contains(id)) {
			if (!permittedClients.remove(id)) {
				return;
			}
		}
	}
}
