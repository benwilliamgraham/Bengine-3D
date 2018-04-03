package bengine.assets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

import org.joml.Vector3f;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import bengine.Game;
import bengine.State;
import bengine.assets.Asset.AssetImportance;
import bengine.rendering.renderers.Renderer;
import bengine.rendering.renderers.SceneRenderer;

public abstract class AssetLoader {
	private static final Logger LOGGER = Logger.getLogger(AssetLoader.class.getName());
	
	protected Map<String, Asset> assets;
	
	private AssetLoaderThread loaderThread;
	
	private Game game;
	
	public AssetLoader(Game game) {
		this.assets = new ConcurrentHashMap<String, Asset>();
		this.game = game;
	}
	
	public void addAssets(File jsonFile) { //Seriously, just don't worry about it.
		Function<JsonValue, List<String>> parseAssetList = (JsonValue assetList) -> {
			ArrayList<String> listPaths = new ArrayList<String>();
			
			if (assetList.isArray()) {
				JsonArray jListPaths = assetList.asArray();
				
				jListPaths.forEach((JsonValue v) -> {
					if (v.isString()) {
						listPaths.add(v.asString());
					}
				});
				
			} else if (assetList.isString()) {
				listPaths.add(assetList.asString());
			}
			
			return listPaths;
		};
		
		try {
			JsonValue assetsFile = Json.parse(new FileReader(jsonFile));
			
			if (!assetsFile.isArray()) return;
			
			JsonArray assets = assetsFile.asArray();
			
			assets.forEach((JsonValue a) -> {
				if (!a.isObject()) return;
				
				JsonObject assetObject = a.asObject();
				
				if (assetObject.get("name") == null) return;
				if (assetObject.get("path") == null) return;
				if (assetObject.get("type") == null) return;
				
				String assetName = assetObject.getString("name", "");
				String assetPath = assetObject.getString("path", "");
				String assetType = assetObject.getString("type", "");
				
				switch (assetType) {
				case "texture":
					addAsset(assetName, new Texture(new File(assetPath)));
					break;
				case "model":
					addAsset(assetName, new Model(new File(assetPath)));
					break;
				case "shader":
					addAsset(assetName, new Shader(new File(assetPath)));
					break;
				}
				
				
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addAsset(String name, Asset asset) {
		this.assets.put(name, asset);
		asset.setGame(game);
	}
	
	public final State load() {
		
		this.loaderThread = new AssetLoaderThread(assets); 
		
		this.loaderThread.start();
		
		return new State() {
			
			Renderer r = new SceneRenderer(null);
			Game g;
			
			@Override
			public void onCreated(Game game) {
				g = game;
				
			}

			@Override
			public void onUpdate(float delta) {
				if (loaderThread.isLoaded()) {
					getLogger().info("Finished loading assets. Initializing them ...");
					
					for (Asset a : assets.values()) {
						try {
							a.create();
							
						} catch (AssetCreationException ex) {
							
							StringBuilder stackTrace = new StringBuilder();
							
							stackTrace.append(ex.getMessage());
							for (StackTraceElement el : ex.getStackTrace()) {
								stackTrace.append(el.toString());
								stackTrace.append(System.lineSeparator());
							}
							
							getLogger().severe(String.format("Error creating asset: %s %n%s", ex.getAsset().getFile().toString(), stackTrace.toString()));
							
							if (ex.getAsset().importance == AssetImportance.CRITICAL) {
								System.exit(1);
							}
						}
					}
					
					getLogger().info("Done.");
					
					onLoaded(new AssetManager(assets));
				}
			}

			@Override
			public void onDraw() {
				r.clear();
				
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