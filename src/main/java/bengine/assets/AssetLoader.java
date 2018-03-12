package bengine.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import bengine.Game;
import bengine.State;
import bengine.assets.Asset.AssetImportance;
import bengine.rendering.Renderer;

public abstract class AssetLoader {
	private static final Logger LOGGER = Logger.getLogger(AssetLoader.class.getName());
	
	protected Map<String, Asset> assets;
	
	private AssetLoaderThread loaderThread;
	
	private Game game;
	
	public AssetLoader(Game game) {
		this.assets = new ConcurrentHashMap<String, Asset>();
		this.game = game;
	}
	
	public void addAsset(String name, Asset asset) {
		this.assets.put(name, asset);
		asset.setGame(game);
	}
	
	public final State load() {
		
		getLogger().info("Started loading assets.");
		
		this.loaderThread = new AssetLoaderThread(assets); 
		
		this.loaderThread.start();
		
		return new State() {
			
			Renderer r = new Renderer(null);
			Game g;
			
			@Override
			public void onCreated(Game game) {
				g = game;
				
			}

			@Override
			public void onUpdate(float delta) {
				
				if (loaderThread.isLoaded()) {
					g.switchState(null);
					getLogger().info("Finished loading assets.");
					onLoaded(new AssetManager(assets));
				}
			}

			@Override
			public void onDraw() {
				r.clear(new Vector3f(1.0f, 1.0f, 1.0f));
				
				//TODO: Loading screen.
			}

			@Override
			public void onDestroyed() {}

			@Override
			public Renderer getRenderer() {
				return r;
			}
			
		};
	}
	
	protected abstract void onLoaded(AssetManager assets);

	private Logger getLogger() {
		return LOGGER;
	}
}

class AssetLoaderThread extends Thread {
	private static final Logger LOGGER = Logger.getLogger(AssetLoaderThread.class.getName());
	
	Map<String, Asset> assets;
	
	public AssetLoaderThread(Map<String, Asset> assets) {
		super("Bengine-LoaderThread");
		
		this.assets = assets;
	}
	
	@Override
	public void run() {
		int assetNum = 0;
		
		for (Entry<String, Asset> e : assets.entrySet()) {
			Asset a = e.getValue();
			assetNum++;
			
			getLogger().info(String.format("Loading asset [%d/%d]: %s (%s)", assetNum, assets.entrySet().size(), e.getKey(), a.getFile().toString()));
			
			try {
				a.load();
			} catch (AssetCreationException ex) {
				
				StringBuilder stackTrace = new StringBuilder();
				
				stackTrace.append(ex.getMessage());
				for (StackTraceElement el : ex.getStackTrace()) {
					stackTrace.append(el.toString());
					stackTrace.append(System.lineSeparator());
				}
				
				getLogger().severe(String.format("Error loading asset: %s %n%s", ex.getAsset().getFile().toString(), stackTrace.toString()));
				
				if (ex.getAsset().importance == AssetImportance.CRITICAL) {
					System.exit(1);
				}
			}
			
		}
	}
	
	public boolean isLoaded() {
		for (Asset a : assets.values()) {
			if (!a.isLoaded()) {
				return false;
			}
		}
		
		return true;
	}
	
	private Logger getLogger() {
		return LOGGER;
	}
}