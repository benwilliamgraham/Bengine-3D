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
import entities.Camera;
import entities.DynEntity;
import entities.Light;
import entities.NPC;
import entities.Player;
import networking.UDPClient;
import toolBox.Calc;
import toolBox.Loader;
import toolBox.OpenSimplexNoise;

public class World {
	
	public static final int XSIZE = 125;
	public static final int YSIZE = 125;
	public static final int ZSIZE = 125;
	
	public UDPClient networkClient;
	
	public static final float GRAVITY = 80;
	
	public List<Light> lights = new ArrayList<Light>();
	public Voxel[][][] voxels = new Voxel[XSIZE][YSIZE][ZSIZE];
	
	public boolean lockMap = false;
	public Map<String, DynEntity> dynEntities = new HashMap<String, DynEntity>();
	public Map<String, DynEntity> localDynEntities = new HashMap<String, DynEntity>();
	public FaceMap faceMap;
	
	public Camera camera;
	public Player player = new Player(new Vector3f((float) (Math.random() * World.XSIZE), 40, (float) (Math.random() * World.ZSIZE / 2)));
	public Camera spectatorCamera = new Camera();
	
	public World(Loader loader, UDPClient client){
		this.networkClient = client;
		
		//add lights
		lights.add(new Light(new Vector3f(XSIZE / 2f, 1000, ZSIZE / 2f), 100000, 0.1f, 2));
		for(int n = 0; n < 100; n++){
			//lights.add(new Light(new Vector3f(Math.random() * (float) XSIZE, Math.random() * (float) YSIZE, Math.random() * (float) ZSIZE), 10, 1, 2f));
		}

		//load level
		try {
			LevelLoader.loadLevel(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//add player
		createDynEntity(player);
		for(int n = 0; n < 0; n++){
			createDynEntity(new NPC(new Vector3f((float) (Math.random() * World.XSIZE), 40, (float) (Math.random() * World.ZSIZE / 2))));
		}
		
		//add camera
		camera = player.camera;
		spectatorCamera.position = new Vector3f(XSIZE / 2f, 2f * YSIZE / 3f, ZSIZE / 2f);
		spectatorCamera.pitch = (float) (Math.PI / 2f);
		spectatorCamera.yaw = (float) (Math.PI / 2f);
		
		//create face map
		faceMap = new FaceMap(loader, new Random(Sys.getTime()), lights);
		faceMap.createFaceMap(this);
	}
	
	public void update(){
		//update 3d
		boolean stillActive;
		Map<String, DynEntity> updateEnts = new HashMap<String, DynEntity>();
		lockMap = true;
		updateEnts.putAll(localDynEntities);
		lockMap = false;
		for(Iterator<Map.Entry<String, DynEntity>> it = updateEnts.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, DynEntity> entry = it.next();
			stillActive = entry.getValue().update(this);
			if(!stillActive){
				String key = entry.getKey();
				localDynEntities.remove(key);
				deleteDynEntity(key);
				client.deleteEntity(key);
			}
		}
	}
	
	public void createDynEntity(DynEntity entity){
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
		
		//wait for the map to be open
		while(lockMap){
			continue;//System.out.println("");
		}
		//add to list
		dynEntities.put(key, entity);
	}
	
	public void deleteDynEntity(String key){
		//wait for the map to be open
		while(lockMap){
			System.out.println("");
		}
		//delete
		dynEntities.remove(key);
	}
	
	public boolean checkSolid(Vector3f position){
		int x = (int) (position.x);
		int y = (int) (position.y);
		int z = (int) (position.z);

		return checkSolid(x, y, z);
	}
	
	public boolean checkSolid(int x, int y, int z){
		if(x < 0 || x >= XSIZE || y >= YSIZE || z < 0 || z >= ZSIZE){
			return true;
		}else if(y < 0){
			return true;
		}
		return voxels[x][y][z].solid;
	}
}
