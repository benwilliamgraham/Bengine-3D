package world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import data.ModelTexture;
import data.RawModel;
import data.TexturedModel;
import entities.Bullet;
import entities.DynEntity;
import entities.Light;
import entities.Player;
import networking.Client;
import toolBox.Calc;
import toolBox.Loader;
import toolBox.OpenSimplexNoise;

public class World {
	
	public static final int XSIZE = 125;
	public static final int YSIZE = 125;
	public static final int ZSIZE = 125;
	
	public Client client;
		
	public List<Light> lights = new ArrayList<Light>();
	public Voxel[][][] voxels = new Voxel[XSIZE][YSIZE][ZSIZE];
	
	public List<DynEntity> newDynEntities = new ArrayList<DynEntity>();
	public Map<String, DynEntity> dynEntities = new HashMap<String, DynEntity>();
	public Map<String, DynEntity> localDynEntities = new HashMap<String, DynEntity>();
	public FaceMap faceMap;
	
	public Player player = new Player(new Vector3f(World.XSIZE / 2, World.YSIZE - 4, World.ZSIZE / 2));
	
	public World(Loader loader, Client client){
		this.client = client;
		
		//add lights
		lights.add(new Light(new Vector3f(XSIZE / 2f, 1000, ZSIZE / 2f), 100000, 0.9f, 2));
		for(int n = 0; n < 100; n++){
			//lights.add(new Light(new Vector3f(rand.nextInt(XSIZE), rand.nextInt(YSIZE), rand.nextInt(ZSIZE)), 10, 1, 2f));
		}

		//load level
		try {
			LevelLoader.loadLevel(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//add player
		addDynEntity(player);
		
		//create face map
		faceMap = new FaceMap(loader, new Random(Sys.getTime()), lights);
		faceMap.createFaceMap(this);
	}
	
	public void update(){
		//update 3d
		boolean stillActive;
		for(Iterator<Map.Entry<String, DynEntity>> it = localDynEntities.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, DynEntity> entry = it.next();
			stillActive = entry.getValue().update(this);
			if(!stillActive){
				String key = entry.getKey();
				dynEntities.remove(key);
				client.deleteEntity(key);
				it.remove();
			}
		}
		//add all of the new entities that have been created
		for(DynEntity entity: newDynEntities){
			addDynEntity(entity);
		}
		newDynEntities.clear();
	}
	
	public void createDynEntity(DynEntity entity){
		newDynEntities.add(entity);
	}
	
	private void addDynEntity(DynEntity entity){
		String key;
		
		//assign a random key
		do{
			key = client.name + "K" + (int)(Math.random() * 999);
		}while(dynEntities.containsKey(key));
		entity.key = key;
		
		//add it to the lists of entities
		localDynEntities.put(key, entity);
		addDynEntity(key, entity);
		
		//broadcast addition
		if(entity instanceof Player){
			client.addPlayer(key, entity.position);
		}else if(entity instanceof Bullet){
			client.addBullet(key, entity.position);
		}
	}
	
	public void addDynEntity(String key, DynEntity entity){
		//assign key
		entity.key = key;
		//add to list
		dynEntities.put(key, entity);
	}
	
	public boolean checkSolid(Vector3f position){
		int x = (int) (position.x);
		int y = (int) (position.y);
		int z = (int) (position.z);

		return checkSolid(x, y, z);
	}
	
	public boolean checkSolid(int x, int y, int z){
		if(x < 0 || x >= XSIZE || y < 0 || y >= YSIZE || z < 0 || z >= ZSIZE){
			return true;
		}
		return voxels[x][y][z].solid;
	}
}
