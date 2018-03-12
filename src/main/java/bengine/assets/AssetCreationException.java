package bengine.assets;

public class AssetCreationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2755516162868236433L;
	private Asset asset;
	
	public AssetCreationException(Asset asset, String message) {
		super(message);
		this.asset = asset;
	}
	
	public Asset getAsset() {
		return asset;
	}
}
