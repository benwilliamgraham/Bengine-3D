package bengine.assets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import bengine.Game;

public abstract class Asset {
	public AssetImportance importance = AssetImportance.CRITICAL;

	
	private Game game;
	private File file;
	private boolean isLoaded = false;
	
	public Asset(File file) {
		this.file = file;
	}
	
	public abstract void onLoad(File file) throws AssetCreationException;
	
	public abstract void create();
	
	public abstract void destroy();
	
	public final void load() {
		onLoad(this.file);
		
		isLoaded = true;
	}
	
	protected String loadFileAsString(String filePath) {
		return loadFileAsString(new File(filePath));
	}
	
	protected String loadFileAsString(File file) {
		try {
			
			FileInputStream is = new FileInputStream(file);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			String line;
			
			String fileSource = "";
			
			while ((line = reader.readLine()) != null) {
				fileSource += line + "\n";
			}
			
			reader.close();
			
			return fileSource;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public boolean isLoaded() {
		return isLoaded;
	}
	
	public File getFile() {
		return this.file;
	}
	
	protected Game getGame() {
		return game;
	}
	
	public static enum AssetImportance {
		CRITICAL,
		OPTIONAL
	}
}
