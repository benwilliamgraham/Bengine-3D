package world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.DynEntity;
import entities.Entity;
import entities.Light;
import entities.Player;
import networking.UDPClient;
import networking.packets.Packet;
import networking.packets.RegisterEntityPacket;
import networking.packets.UpdateEntityPacket;
import toolBox.Loader;

public class World {
	
	public static final int XSIZE = 39;
	public static final int YSIZE = 39;
	public static final int ZSIZE = 39;
	
	public UDPClient networkClient;
	
	public static final float GRAVITY = 160;
	
	public List<Light> lights = new ArrayList<Light>();
	public Voxel[][][] voxels = new Voxel[XSIZE][YSIZE][ZSIZE];
	
	public boolean lockMap = false;
	//public Map<String, DynEntity> dynEntities = new HashMap<String, DynEntity>();
	//public Map<String, DynEntity> localDynEntities = new HashMap<String, DynEntity>();
	public Map<String, DynEntity> entities = new HashMap<String, DynEntity>();
	public FaceMap faceMap;
	
	public Camera camera;
	public Player player;// = new Player(new Vector3f((float) (Math.random() * World.XSIZE), 40, (float) (Math.random() * World.ZSIZE / 2)));
	public Camera spectatorCamera = new Camera();
	
	//List of entities that we've created locally, but haven't been initialized on the server yet.
	private Map<String, DynEntity> cachedEntities = new HashMap<String, DynEntity>();
	
	private long lastTime = Sys.getTime();
	
	public World(Loader loader, UDPClient client){
		this.networkClient = client;
		
		//Update the position, rotation and velocity of an entity, whenever we recieve an entity update packet.
		this.networkClient.OnPacket(new int[] {UpdateEntityPacket.packetId}, (Packet p) -> {
			UpdateEntityPacket u = (UpdateEntityPacket) p;
			
			if (this.entities.containsKey(u.entity)) {
				DynEntity e = this.entities.get(u.entity);
				
				e.onNetworkUpdate(u);
			}
		});
		
		this.networkClient.OnPacket(new int[] {RegisterEntityPacket.packetId}, (Packet p) -> {
			RegisterEntityPacket r = (RegisterEntityPacket) p;
			
			if (cachedEntities.containsKey(r.entityId)) {
				DynEntity e = cachedEntities.remove(r.entityId);
				e.world = this;
				e.owner = r.owner;
				e.isRemote = !(r.owner.equals(this.networkClient.clientId));
				entities.put(e.id, e);
				e.onCreate();
			} else {
				try {
					DynEntity e = (DynEntity) Entity.entities.get(r.entityType).newInstance();
					e.id = r.entityId;
					e.position = r.pos;
					e.rotation = r.rot;
					e.scale = r.scale;
					e.owner = r.owner;
					e.isRemote = !(r.owner.equals(this.networkClient.clientId));
					e.world = this;
					entities.put(e.id, e);
					e.onCreate();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
		this.networkClient.setWorld(this);
		
		this.camera = spectatorCamera;
		
		this.player = new Player(new Vector3f(36.0f, 10f, 42.7f));
		
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
		//createDynEntity(player);
		for(int n = 0; n < 0; n++){
			//createDynEntity(new NPC(new Vector3f((float) (Math.random() * World.XSIZE), 40, (float) (Math.random() * World.ZSIZE / 2))));
		}
		
		//add camera
		//camera = player.camera;
		spectatorCamera.position = new Vector3f(XSIZE / 2f, 2f * YSIZE / 3f, ZSIZE / 2f);
		spectatorCamera.pitch = (float) (Math.PI / 2f);
		spectatorCamera.yaw = (float) (Math.PI / 2f);
		
		//create face map
		faceMap = new FaceMap(loader, new Random(Sys.getTime()), lights);
		faceMap.createFaceMap(this);
	}
	
	public void onConnected() {
		addDynEntity(player);
	}
	
	public void update(){
		long time = Sys.getTime();
		long delta = time - lastTime;
		lastTime = time;
		
		for (Entry<String, DynEntity> it : this.entities.entrySet()) {
			DynEntity e = it.getValue();
			
			if (!e.onUpdate(delta / 1000.0f) && !e.isNetworked) {
				//Entity needs destroyed. Locally at least.
				this.entities.remove(it.getKey());
			}		
		}
	}
	
	public void addDynEntity(DynEntity entity){
		if (entity.isNetworked) {
			cachedEntities.put(entity.id, entity);
			this.networkClient.registerEntity(entity);
		} else { 
			entities.put(entity.id, entity);
		}
	}
	
	public boolean checkSolid(Vector3f position){
		int x = (int) (position.x);
		int y = (int) (position.y);
		int z = (int) (position.z);

		return checkSolid(x, y, z);
	}
	
	public boolean checkSolid(float x, float y, float z) {
		return checkSolid(Math.round(x - 0.5f), (int) (y + 1), (int) (z + 1));
	}
	
	public boolean checkSolid(int x, int y, int z){
		if(x < 0 || x >= XSIZE || y >= YSIZE || z < 0 || z >= ZSIZE){
			return true;
		}else if(y < 0){
			return true;
		}
		return voxels[x][y][z].solid;
	}
	
	public Vector3f getClosestVoxelPos(float x, float y, float z) {
		return new Vector3f(Math.round(x - 0.5f), (int) (y + 1), (int) (z + 1));
	}
}
