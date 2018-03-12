package bengine.assets;

import java.util.Map;

public class AssetManager {
	
	protected Map<String, Asset> assets;
	
	public AssetManager(Map<String, Asset> assets) {
		this.assets = assets;
	}
	
	public void destroy() {
		for (Asset a : assets.values()) {
			a.destroy();
		}
		
		assets.clear();
	}
	
	@SuppressWarnings("unchecked")
	public<T extends Asset>  T getAsset(String name) {
		return (T) assets.get(name);
	}

}
