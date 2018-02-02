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
import toolBox.Calc;
import toolBox.Loader;

public class FaceMap extends Entity{
	
	public static final int OBJECT_TYPE = generateTypeId();
	
	public TexturedModel model;
	
	private Random rand;
	private Loader loader;
	private World world;
	private List<Light> lights;
	
	public FaceMap(Loader loader, World world, Random rand, List<Light> lights) {
		super(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		this.loader = loader;
		this.world = world;
		this.rand = rand;
		this.lights = lights;
	}
	
	
	public void createFaceMap(){
		
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Float> shades = new ArrayList<Float>();
		List<Integer> indices = new ArrayList<Integer>();
		
		
		//cycle
		for(int x = 0; x < world.XSIZE; x++){
			for(int y = 0; y < world.YSIZE; y++){
				for(int z = 0; z < world.ZSIZE; z++){
					if(world.voxels[x][y][z].solid){
						
						//set tileset
						Vector3f tileset = world.voxels[x][y][z].tileset;
						
						//x+
						if(!world.checkSolid(x + 1, y, z)){
							Vector3f p1 = new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);
							
							int LRUD = 0;
							if(!world.checkSolid(x, y, z + 1)) LRUD += 1000;
							if(!world.checkSolid(x, y, z - 1)) LRUD += 100;
							if(!world.checkSolid(x, y + 1, z)) LRUD += 10;
							if(!world.checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, (int) tileset.y,
									vertices, textures, shades, indices);
						}
						
						//x-
						if(!world.checkSolid(x - 1, y, z)){
							Vector3f p1 = new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p2 = new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p3 = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
							Vector3f p4 = new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
							
							int LRUD = 0;
							if(!world.checkSolid(x, y, z - 1)) LRUD += 1000;
							if(!world.checkSolid(x, y, z + 1)) LRUD += 100;
							if(!world.checkSolid(x, y + 1, z)) LRUD += 10;
							if(!world.checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, (int) tileset.y,
									vertices, textures, shades, indices);
						}
						
						//y+
						if(!world.checkSolid(x, y + 1, z)){
							Vector3f p1 = new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
							
							int LRUD = 0;
							if(!world.checkSolid(x, y, z + 1)) LRUD += 1000;
							if(!world.checkSolid(x, y, z - 1)) LRUD += 100;
							if(!world.checkSolid(x - 1, y, z)) LRUD += 10;
							if(!world.checkSolid(x + 1, y, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, (int) tileset.x,
									vertices, textures, shades, indices);
						}
						
						//y-
						if(!world.checkSolid(x, y - 1, z)){
							Vector3f p1 = new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
							
							int LRUD = 0;
							if(!world.checkSolid(x, y, z + 1)) LRUD += 1000;
							if(!world.checkSolid(x, y, z - 1)) LRUD += 100;
							if(!world.checkSolid(x + 1, y, z)) LRUD += 10;
							if(!world.checkSolid(x - 1, y, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, (int) tileset.z,
									vertices, textures, shades, indices);
						}
						
						//z+
						if(!world.checkSolid(x, y, z + 1)){
							Vector3f p1 = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p2 = new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
							
							int LRUD = 0;
							if(!world.checkSolid(x - 1, y, z)) LRUD += 1000;
							if(!world.checkSolid(x + 1, y, z)) LRUD += 100;
							if(!world.checkSolid(x, y + 1, z)) LRUD += 10;
							if(!world.checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, (int) tileset.y,
									vertices, textures, shades, indices);
						}
						
						//z-
						if(!world.checkSolid(x, y, z - 1)){
							Vector3f p1 = new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p3 = new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);
							Vector3f p4 = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
							
							int LRUD = 0;
							if(!world.checkSolid(x + 1, y, z)) LRUD += 1000;
							if(!world.checkSolid(x - 1, y, z)) LRUD += 100;
							if(!world.checkSolid(x, y + 1, z)) LRUD += 10;
							if(!world.checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, (int) tileset.y,
									vertices, textures, shades, indices);
						}
						
						
					}
				}
			}
		}
		
		//convert
		float[] verticesArray = new float[vertices.size() * 3];
		for(int pointer = 0; pointer < vertices.size(); pointer++){
			verticesArray[pointer * 3 + 0] = vertices.get(pointer).x;
			verticesArray[pointer * 3 + 1] = vertices.get(pointer).y;
			verticesArray[pointer * 3 + 2] = vertices.get(pointer).z;
		}
		
		float[] texturesArray = new float[textures.size() * 3];
		for(int pointer = 0; pointer < textures.size(); pointer++){
			texturesArray[pointer * 3 + 0] = textures.get(pointer).x;
			texturesArray[pointer * 3 + 1] = textures.get(pointer).y;
			texturesArray[pointer * 3 + 2] = shades.get(pointer);
		}
		
		int[] indicesArray = new int[indices.size()];
		for(int pointer = 0; pointer < indices.size(); pointer++){
			indicesArray[pointer * 1 + 0] = indices.get(pointer);
		}
		
		System.out.println("Loading model with " + indicesArray.length / 3 + "tris");
		RawModel rawModel = loader.loadToVAO(verticesArray, texturesArray, indicesArray);
		ModelTexture texture = new ModelTexture(loader.loadTexture("T3"));
		
		model = new TexturedModel(rawModel, texture);
	}
	
	
	private void createFace(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, int LRUD, int tileset,
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
		
		setCoords(LRUD, tileset, textures);
		
		indices.add(index + 0);
		indices.add(index + 1);
		indices.add(index + 2);
		indices.add(index + 0);
		indices.add(index + 2);
		indices.add(index + 3);
	}
	
	
	private void setCoords(int LRUD, int tileset, List<Vector2f> textures){		
		Vector2f bc = getBaseCoords(0, tileset);
		
		switch(LRUD){
		case 0:
			int[] opt = {11, 12, 13, 21, 22, 23, 31, 32, 33};
			bc = getBaseCoords(randInSet(opt), tileset);
			break;
		case 1:
			int[] opt1 = {41, 42, 43};
			bc = getBaseCoords(randInSet(opt1), tileset);
			break;
		case 10:
			int[] opt2 = {1, 2, 3};
			bc = getBaseCoords(randInSet(opt2), tileset);
			break;
		case 11:
			int[] opt3 = {37};
			bc = getBaseCoords(randInSet(opt3), tileset);
			break;
		case 100:
			int[] opt4 = {14, 24, 34};
			bc = getBaseCoords(randInSet(opt4), tileset);
			break;
		case 101:
			int[] opt5 = {44};
			bc = getBaseCoords(randInSet(opt5), tileset);
			break;
		case 110:
			int[] opt6 = {4};
			bc = getBaseCoords(randInSet(opt6), tileset);
			break;
		case 111:
			int[] opt7 = {39};
			bc = getBaseCoords(randInSet(opt7), tileset);
			break;
		case 1000:
			int[] opt8 = {10, 20, 30};
			bc = getBaseCoords(randInSet(opt8), tileset);
			break;
		case 1001:
			int[] opt9 = {40};
			bc = getBaseCoords(randInSet(opt9), tileset);
			break;
		case 1010:
			int[] opt10 = {0};
			bc = getBaseCoords(randInSet(opt10), tileset);
			break;
		case 1011:
			int[] opt11 = {36};
			bc = getBaseCoords(randInSet(opt11), tileset);
			break;
		case 1100:
			int[] opt12 = {28};
			bc = getBaseCoords(randInSet(opt12), tileset);
			break;
		case 1101:
			int[] opt13 = {48};
			bc = getBaseCoords(randInSet(opt13), tileset);
			break;
		case 1110:
			int[] opt14 = {18};
			bc = getBaseCoords(randInSet(opt14), tileset);
			break;
		case 1111:
			int[] opt15 = {38};
			bc = getBaseCoords(randInSet(opt15), tileset);
			break;
		}
			
		
		float xv = 10f / 128f;
		float yv = 10f / 512f;
		textures.add(new Vector2f(bc.x + xv, bc.y + 0));
		textures.add(new Vector2f(bc.x + 0, bc.y + 0));
		textures.add(new Vector2f(bc.x + 0, bc.y + yv));
		textures.add(new Vector2f(bc.x + xv, bc.y + yv));
	}
	
	
	private Vector2f getBaseCoords(int selection, int tileset){
		int col = selection % 10;
		int row = selection / 10;
		
		return new Vector2f((float) col * 10f / 128f, (float) row * 10f / 512f + tileset / 8f);
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
			
			diffuse *= 1 - Math.pow(mag / light.lightDist, light.dropOff);
			
			Vector3f checkPos = new Vector3f(point.x + 0.5f, point.y + 0.5f, point.z + 0.5f);
			float distTest = Math.min(mag, 16);
			for(int n = 0; n < distTest; n++){
				Vector3f.add(checkPos, toLight, checkPos);
				if(world.checkSolid(checkPos)){
					diffuse *= 0.4f;
					break;
				}
			}
			totalDiffuse += diffuse;
			
		}
		
		return totalDiffuse;
	}
	
	
	private int randInSet(int[] set){
		return set[rand.nextInt(set.length)];
	}

	@Override
	public void onUpdate(float delta) {
		
	}


	@Override
	public void onCreated() {
		
	}


	@Override
	public void onDestroyed() {
		
	}


	@Override
	public void onRegistered() {
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
