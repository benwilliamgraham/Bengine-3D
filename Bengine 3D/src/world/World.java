package world;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import entities.Bullet;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import networking.client.Client;
import networking.sync.SyncedObject;
import renderEngine.Renderer;
import shaders.StaticShader;
import toolBox.Loader;
import toolBox.OpenSimplexNoise;
import world.Voxel.VoxelTypes;

public class World extends Client {
	
	public static final int XSIZE = 256;
	public static final int YSIZE = 64;
	public static final int ZSIZE = 256;
	
	public static final int CHUNK_SIZE = 16;
	public static final int X_CHUNKS = (int) Math.ceil(XSIZE / CHUNK_SIZE);
	public static final int Y_CHUNKS = (int) Math.ceil(YSIZE / CHUNK_SIZE);
	public static final int Z_CHUNKS = (int) Math.ceil(ZSIZE / CHUNK_SIZE);
	
	public static final float GRAVITY = 80;
	
	public List<Light> lights = new ArrayList<Light>();
	public Voxel[][][] voxels = new Voxel[XSIZE][YSIZE][ZSIZE];
	
	public boolean lockMap = false;
	public Map<Long, Entity> entities = new ConcurrentHashMap<Long, Entity>();
	public FaceMapRepeating[][][] faceMaps = new FaceMapRepeating[X_CHUNKS][Y_CHUNKS][Z_CHUNKS];
	public List<FaceMapRepeating> faceMapsToUpdate = new ArrayList<FaceMapRepeating>();
	public List<FaceMapRepeating> faceMapsToUpload = new ArrayList<FaceMapRepeating>();
	
	public Camera camera;
	public Player player = new Player(new Vector3f((float) (Math.random() * World.XSIZE), YSIZE - 10, (float) (Math.random() * World.ZSIZE / 2)));
	public Camera spectatorCamera = new Camera();
	
	private long lastTime = 0;
	
	public World(Loader loader, String serverAddress, String name){
		super(name, new InetSocketAddress(serverAddress, 2290));
		//Update the position, rotation and velocity of an entity, whenever we recieve an entity update packet.
		/*this.networkClient.OnPacket(new int[] {UpdateEntityPacket.packetId}, (Packet p) -> {
			UpdateEntityPacket u = (UpdateEntityPacket) p;
					
			if (this.entities.containsKey(u.entity)) {
				DynEntity e = this.entities.get(u.entity);						
				e.onNetworkUpdate(u);
			}
		});*/
		
		/*this.networkClient.OnPacket(new int[] {RegisterEntityPacket.packetId}, (Packet p) -> {
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
		
		this.networkClient.OnPacket(new int[] {DestroyEntityPacket.packetId}, (Packet p) -> {
			DestroyEntityPacket dp = (DestroyEntityPacket) p;
			
			if (this.entities.containsKey(dp.entityId)) {
				this.entities.remove(dp.entityId);
			}
		});
		
		this.networkClient.setWorld(this);*/
		
		
		//add lights
		lights.add(new Light(new Vector3f(XSIZE * 2f, 1000, ZSIZE * 2f), 100000, 1.1f, 2));
		for(int n = 0; n < 100; n++){
			//lights.add(new Light(new Vector3f(Math.random() * (float) XSIZE, Math.random() * (float) YSIZE, Math.random() * (float) ZSIZE), 10, 1, 2f));
		}
		
		boolean level = false;
		
		System.out.println("Loading Level");
		if(level){
			try {
				LevelLoader.loadLevel(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			OpenSimplexNoise noise = new OpenSimplexNoise(12345L); //Changed to a static seed, so that we can keep it consistent across clients.
			
			//fill
			float gradient = 42;
			float maxHeight = YSIZE / 4f;
			for(int x = 0; x < XSIZE; x++){
				for(int y = 0; y < YSIZE; y++){
					for(int z = 0; z < ZSIZE; z++){
						voxels[x][y][z] = new Voxel();
						
						float hVal = (float) ((noise.eval(x / gradient, z / gradient) + 2.5f) / 2f * maxHeight - y) / YSIZE * 1f;
						float tVal = (float) noise.eval(x / gradient, y / gradient, z / gradient);
						
						//check if a block should exist
						if(hVal + tVal > 0 || y == 0){
							if(y == 0){
								voxels[x][y][z].setVoxel(VoxelTypes.WATER);
							}else if(y < 6){
								voxels[x][y][z].setVoxel(VoxelTypes.SAND);
							}else{
								voxels[x][y][z].setVoxel(VoxelTypes.DIRT);
							}
						}
					}
				}
			}
			
			//add vegetation
			for(int x = 0; x < XSIZE; x++){
				for(int z = 0; z < ZSIZE; z++){
					for(int y = YSIZE - 1; y >= 0; y--){
						if(voxels[x][y][z].solid){
							if(voxels[x][y][z].type == VoxelTypes.DIRT){
								voxels[x][y][z].setVoxel(VoxelTypes.GRASS);
							}
							break;
						}
					}
				}
			}
			
		}
		
		
		System.out.println("Adding Camera");
		
		
		//add camera
		spectatorCamera.position = new Vector3f(XSIZE / 2f, 2f * YSIZE / 3f, ZSIZE / 2f);
		spectatorCamera.pitch = (float) (Math.PI / 2f);
		spectatorCamera.yaw = (float) (Math.PI / 2f);
		this.camera = spectatorCamera;
		
		System.out.println("Creating Face Maps");
		//create face maps
		for(int x = 0; x < X_CHUNKS; x++){
			for(int y = 0; y < Y_CHUNKS; y++){
				for(int z = 0; z < Z_CHUNKS; z++){
					faceMaps[x][y][z] = new FaceMapRepeating(loader, this, lights, new Vector3f(x, y, z));
				}
			}
		}
		
		System.out.println("World Creation Done");
	}
	
	@Override
	public void onConnected() {
		System.out.println("Connected to server; Registering player.");
		spawnEntity(player);
	}
	
	@Override
	public void onNewObject(SyncedObject obj) {
		if (obj instanceof Entity) {
			Entity e = ((Entity) obj);
			e.world = this;
			entities.put(obj.getInstanceID(), e);
			e.onCreated();
		}
		
		if (obj instanceof Bullet) {
			System.out.println("Bullet added to scene For:" + this.name);
		}
	}
	
	@Override
	public void onDisconnected() {
		
	}
	
	public void update(){
		if (Keyboard.isKeyDown(Keyboard.KEY_O) && player.health <= 0) {
			player = new Player(new Vector3f((float) (Math.random() * World.XSIZE), YSIZE - 10, (float) (Math.random() * World.ZSIZE / 2)));
			spawnEntity(player);
		}
		
		//update any new face maps
		while(!faceMapsToUpdate.isEmpty()){
			faceMapsToUpdate.get(0).createFaceMap();
			faceMapsToUpdate.remove(0);
		}
		
		
		//upload any new face maps
		while(!faceMapsToUpload.isEmpty()){
			faceMapsToUpload.get(0).uploadFaceMap();
		}
		
		
		long time = Sys.getTime();
		long delta = time - lastTime;
		lastTime = time;
		
		for (Entity e : this.entities.values()) {
			
			e.onUpdate(1000.0f / delta);		
		}
		
		
	}
	
	public void render(Renderer renderer, StaticShader shader){
		shader.start();
		shader.loadViewMatrix(camera);
		float renderDist = 6;
		for(int x = (int) (camera.position.x / CHUNK_SIZE - renderDist); x <= (int) (camera.position.x / CHUNK_SIZE + renderDist); x++){
			if(x < 0 || x >= X_CHUNKS) continue;
			for(int y = (int) (camera.position.y / CHUNK_SIZE - renderDist); y <= (int) (camera.position.y / CHUNK_SIZE + renderDist); y++){
				if(y < 0 || y >= Y_CHUNKS) continue;
				for(int z = (int) (camera.position.z / CHUNK_SIZE - renderDist); z <= (int) (camera.position.z / CHUNK_SIZE + renderDist); z++){
					if(z < 0 || z >= Z_CHUNKS) continue;
					if(faceMaps[x][y][z].model == null) continue;
					renderer.render(faceMaps[x][y][z]);
				}
			}
		}
		
		//render dynamic entities
		renderer.render(this.entities, shader);
		shader.stop();
	}
	
	public void spawnEntity(Entity entity){
		trackObject(entity);
	}
	
	public void spawnEntity(Entity entity, long owner) {
		entity.setOwner(this.serverEndpointId);
		trackObject(entity);
	}
	
	public void destroyVoxel(int x, int y, int z){
		if(checkBounds(x, y, z) || !voxels[x][y][z].solid) return;
		voxels[x][y][z] = new Voxel();
		updateFaceMap(x / CHUNK_SIZE, y / CHUNK_SIZE, z / CHUNK_SIZE);
		if(x / CHUNK_SIZE != (x + 1) / CHUNK_SIZE){
			if(!checkBounds(x + 1, y, z)) updateFaceMap((x + 1) / CHUNK_SIZE, y / CHUNK_SIZE, z / CHUNK_SIZE);
		}
		if(x / CHUNK_SIZE != (x - 1) / CHUNK_SIZE){
			if(!checkBounds(x - 1, y, z)) updateFaceMap((x - 1) / CHUNK_SIZE, y / CHUNK_SIZE, z / CHUNK_SIZE);
		}
		if(y / CHUNK_SIZE != (y + 1) / CHUNK_SIZE){
			if(!checkBounds(x, y + 1, z)) updateFaceMap(x / CHUNK_SIZE, (y + 1) / CHUNK_SIZE, z / CHUNK_SIZE);
		}
		if(y / CHUNK_SIZE != (y - 1) / CHUNK_SIZE){
			if(!checkBounds(x, y - 1, z)) updateFaceMap(x / CHUNK_SIZE, (y - 1) / CHUNK_SIZE, z / CHUNK_SIZE);
		}
		if(z / CHUNK_SIZE != (z + 1) / CHUNK_SIZE){
			if(!checkBounds(x, y, z + 1)) updateFaceMap(x / CHUNK_SIZE, y / CHUNK_SIZE, (z + 1) / CHUNK_SIZE);
		}
		if(z / CHUNK_SIZE != (z - 1) / CHUNK_SIZE){
			if(!checkBounds(x, y, z - 1)) updateFaceMap(x / CHUNK_SIZE, y / CHUNK_SIZE, (z - 1) / CHUNK_SIZE);
		}
	}
	
	public void updateFaceMap(int x, int y, int z){
		FaceMapRepeating faceMap = faceMaps[x][y][z];
		if(!faceMapsToUpdate.contains(faceMap)){
			faceMapsToUpdate.add(faceMap);
		}
	}
	
	public boolean checkBounds(Vector3f position){
		int x = (int) (position.x);
		int y = (int) (position.y);
		int z = (int) (position.z);

		return checkBounds(x, y, z);
	}
	
	public boolean checkBounds(int x, int y, int z){
		if(x < 0 || x >= XSIZE || y >= YSIZE || z < 0 || z >= ZSIZE){
			return true;
		}else if(y < 0){
			return true;
		}
		return false;
	}
	
	public boolean checkSolid(Vector3f position){
		int x = (int) (position.x);
		int y = (int) (position.y);
		int z = (int) (position.z);

		return checkSolid(x, y, z);
	}
	
	public boolean checkSolid(int x, int y, int z){
		if(checkBounds(x, y, z)) return true;
		return voxels[x][y][z].solid;
	}

	public void destroyEntity(long instanceID) {
		this.entities.remove(instanceID);
		this.objectManager.destroyObject(instanceID);
	}
}
