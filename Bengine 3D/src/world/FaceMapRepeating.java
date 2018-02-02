package world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import data.ModelTexture;
import data.RawModel;
import data.TexturedModel;
import entities.Entity;
import entities.Light;
import toolBox.Assets;
import toolBox.Calc;
import toolBox.Loader;

public class FaceMapRepeating extends Entity implements Runnable{
	
	public static final int OBJECT_TYPE = generateTypeId();
	
	private Loader loader;
	private World world;
	private List<Light> lights;
	private Vector3f cornerPosition;
	
	private float[] verticesArray;
	private float[] texturesArray;
	private int[] indicesArray;
	
	public FaceMapRepeating(Loader loader, World world, List<Light> lights, Vector3f cornerPosition) {
		super(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		this.loader = loader;
		this.world = world;
		this.lights = lights;
		this.cornerPosition = cornerPosition;
		Thread faceMapCreator = new Thread(this);
		faceMapCreator.start();
	}
	
	public void run(){
		this.createFaceMap();
	}
	
	public void uploadFaceMap(){
		RawModel rawModel = loader.loadToVAO(verticesArray, texturesArray, indicesArray);
		ModelTexture texture = Assets.tiles;
		
		model = new TexturedModel(rawModel, texture);
		world.faceMapsToUpload.remove(this);
	}
	
	public void createFaceMap(){
		
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Float> shades = new ArrayList<Float>();
		List<Integer> indices = new ArrayList<Integer>();
		
		
		//cycle
		for(int x = (int) (cornerPosition.x * World.CHUNK_SIZE); x < cornerPosition.x * World.CHUNK_SIZE + World.CHUNK_SIZE; x++){
			for(int y = (int) (cornerPosition.y * World.CHUNK_SIZE); y < cornerPosition.y * World.CHUNK_SIZE + World.CHUNK_SIZE; y++){
				for(int z = (int) (cornerPosition.z * World.CHUNK_SIZE); z < cornerPosition.z * World.CHUNK_SIZE + World.CHUNK_SIZE; z++){
					if(!world.checkBounds(x, y, z) && world.voxels[x][y][z].solid){
						
						//set tileset
						Vector3f tileset = world.voxels[x][y][z].tileset;
						
						//x+
						if(!world.checkSolid(x + 1, y, z)){
							Vector3f p1 = new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);
							
							createFace(p1, p2, p3, p4, (int) tileset.y, new Vector2f(-z, -y),
									vertices, textures, shades, indices);
						}
						
						//x-
						if(!world.checkSolid(x - 1, y, z)){
							Vector3f p1 = new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p2 = new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p3 = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
							Vector3f p4 = new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
							
							createFace(p1, p2, p3, p4, (int) tileset.y, new Vector2f(z, -y),
									vertices, textures, shades, indices);
						}
						
						//y+
						if(!world.checkSolid(x, y + 1, z)){
							Vector3f p1 = new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
							
							createFace(p1, p2, p3, p4, (int) tileset.x, new Vector2f(-z, x),
									vertices, textures, shades, indices);
						}
						
						//y-
						if(!world.checkSolid(x, y - 1, z)){
							Vector3f p1 = new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
							
							createFace(p1, p2, p3, p4, (int) tileset.z, new Vector2f(-z, -x),
									vertices, textures, shades, indices);
						}
						
						//z+
						if(!world.checkSolid(x, y, z + 1)){
							Vector3f p1 = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p2 = new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
							
							createFace(p1, p2, p3, p4, (int) tileset.y, new Vector2f(x, -y),
									vertices, textures, shades, indices);
						}
						
						//z-
						if(!world.checkSolid(x, y, z - 1)){
							Vector3f p1 = new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p3 = new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);
							Vector3f p4 = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
							createFace(p1, p2, p3, p4, (int) tileset.y, new Vector2f(-x, -y),
									vertices, textures, shades, indices);
						}
						
						
					}
				}
			}
		}
		
		//convert
		verticesArray = new float[vertices.size() * 3];
		for(int pointer = 0; pointer < vertices.size(); pointer++){
			verticesArray[pointer * 3 + 0] = vertices.get(pointer).x;
			verticesArray[pointer * 3 + 1] = vertices.get(pointer).y;
			verticesArray[pointer * 3 + 2] = vertices.get(pointer).z;
		}
		
		texturesArray = new float[textures.size() * 3];
		for(int pointer = 0; pointer < textures.size(); pointer++){
			texturesArray[pointer * 3 + 0] = textures.get(pointer).x;
			texturesArray[pointer * 3 + 1] = textures.get(pointer).y;
			texturesArray[pointer * 3 + 2] = shades.get(pointer);
		}
		
		indicesArray = new int[indices.size()];
		for(int pointer = 0; pointer < indices.size(); pointer++){
			indicesArray[pointer * 1 + 0] = indices.get(pointer);
		}
		
		//check to see if the model has any faces
		if(indicesArray.length > 0){
			world.faceMapsToUpload.add(this);
		}
	}
	
	
	private void createFace(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, int texture, Vector2f texPos,
			List<Vector3f> vertices, List<Vector2f> textures, List<Float> shades, List<Integer> indices){
		int index = vertices.size();
		
		vertices.add(p1);
		vertices.add(p2);
		vertices.add(p3);
		vertices.add(p4);
		
		Vector3f normal = Calc.calculateNormal(p1, p2, p3);
		shades.add(calcShade(normal, p1));
		shades.add(calcShade(normal, p2));
		shades.add(calcShade(normal, p3));
		shades.add(calcShade(normal, p4));
		
		setCoords(texPos, texture, textures);
		
		indices.add(index + 0);
		indices.add(index + 1);
		indices.add(index + 2);
		indices.add(index + 0);
		indices.add(index + 2);
		indices.add(index + 3);
	}
	
	
	private void setCoords(Vector2f position, int texture, List<Vector2f> textures){
		float df = 4f;
		float lineXs = 1f;
		float lineYs = 4f;
		
		float lineX = texture % lineXs;
		float lineY = texture / lineXs;
		
		position.x = position.x % df;
		position.y = position.y % df;
		
		float texX = lineX / lineXs + Math.abs(position.x / df / lineXs);
		float texY = lineY / lineYs + Math.abs(position.y / df / lineYs);
		
		float xOff = 1f / lineXs / df;
		float yOff = 1f / lineYs / df;
		textures.add(new Vector2f(texX + xOff, texY));
		textures.add(new Vector2f(texX, texY));
		textures.add(new Vector2f(texX, texY + yOff));
		textures.add(new Vector2f(texX + xOff, texY + yOff));
	}
	
	
	private float calcShade(Vector3f normal, Vector3f point){
		float totalDiffuse = 0;
		for(Light light: lights){
			Vector3f toLight = new Vector3f(light.position.x - point.x, light.position.y - point.y, light.position.z - point.z);
			float mag = Calc.calculateMagnitude(toLight);
			if(mag > light.lightDist){
				continue;
			}
			toLight = Calc.normaliseVector(toLight);
			float diffuse = light.brightness * (Vector3f.dot(toLight, normal) + 1f) / 2f;
			
			diffuse = Math.max(diffuse, 0.5f);
			
			diffuse *= 1 - Math.pow(mag / light.lightDist, light.dropOff);
			diffuse *= diffuse;
			
			Vector3f checkPos = new Vector3f(point.x + 0.5f, point.y + 0.5f, point.z + 0.5f);
			for(int n = 0; n < mag; n++){
				Vector3f.add(checkPos, toLight, checkPos);
				if(world.checkSolid(checkPos)){
					if(world.checkBounds(checkPos))break;
					diffuse *= 0.4f;
					break;
				}
			}
			totalDiffuse += diffuse;
			
		}
		
		return totalDiffuse;
	}

	@Override
	public void onUpdate(float delta) {
	}

	@Override
	public void onCreated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroyed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegistered() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getType() {
		return OBJECT_TYPE;
	}
}
