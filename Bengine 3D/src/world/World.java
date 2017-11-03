package world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import data.ModelTexture;
import data.RawModel;
import data.TexturedModel;
import toolBox.Calc;
import toolBox.Loader;
import toolBox.OpenSimplexNoise;

public class World {
	
	public static final int XSIZE = 32;
	public static final int YSIZE = 32;
	public static final int ZSIZE = 32;
	
	public static final Vector3f SUN = new Vector3f(XSIZE * 1.5f, 1000f, ZSIZE / 2);
	
	public Voxel[][][] voxels;
	
	public TexturedModel model;
	
	private Loader loader;
	private Random rand;
	
	
	public World(Loader loader){
		this.loader = loader;
		
		rand = new Random(Sys.getTime());
		OpenSimplexNoise noise = new OpenSimplexNoise(Sys.getTime());
		
		voxels = new Voxel[XSIZE][YSIZE][ZSIZE];
		
		try {
			LevelLoader.loadLevel(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//fill
		/*
		float gradient = 16;
		for(int x = 0; x < XSIZE; x++){
			for(int y = 0; y < YSIZE; y++){
				for(int z = 0; z < ZSIZE; z++){
					if(noise.eval(x / gradient, y / gradient, z / gradient) <= -0.5f){
						voxels[x][y][z] = true;
					}else{
						voxels[x][y][z] = false;
					}
				}
			}
		}
		*/
		
		//create face map
		createFaceMap();
	}
	
	
	private void createFaceMap(){
		
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Float> shades = new ArrayList<Float>();
		List<Integer> indices = new ArrayList<Integer>();
		
		
		//cycle
		for(int x = 0; x < XSIZE; x++){
			for(int y = 0; y < YSIZE; y++){
				for(int z = 0; z < ZSIZE; z++){
					if(voxels[x][y][z].solid){
						
						//set tileset
						int tileset = voxels[x][y][z].tileset;
						
						//x+
						if(!checkSolid(x + 1, y, z)){
							Vector3f p1 = new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);
							
							int LRUD = 0;
							if(!checkSolid(x, y, z + 1)) LRUD += 1000;
							if(!checkSolid(x, y, z - 1)) LRUD += 100;
							if(!checkSolid(x, y + 1, z)) LRUD += 10;
							if(!checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, tileset,
									vertices, textures, shades, indices);
						}
						
						//x-
						if(!checkSolid(x - 1, y, z)){
							Vector3f p1 = new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p2 = new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p3 = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
							Vector3f p4 = new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
							
							int LRUD = 0;
							if(!checkSolid(x, y, z - 1)) LRUD += 1000;
							if(!checkSolid(x, y, z + 1)) LRUD += 100;
							if(!checkSolid(x, y + 1, z)) LRUD += 10;
							if(!checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, tileset,
									vertices, textures, shades, indices);
						}
						
						//y+
						if(!checkSolid(x, y + 1, z)){
							Vector3f p1 = new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
							
							int LRUD = 0;
							if(!checkSolid(x, y, z + 1)) LRUD += 1000;
							if(!checkSolid(x, y, z - 1)) LRUD += 100;
							if(!checkSolid(x - 1, y, z)) LRUD += 10;
							if(!checkSolid(x + 1, y, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, tileset,
									vertices, textures, shades, indices);
						}
						
						//y-
						if(!checkSolid(x, y - 1, z)){
							Vector3f p1 = new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
							
							int LRUD = 0;
							if(!checkSolid(x, y, z + 1)) LRUD += 1000;
							if(!checkSolid(x, y, z - 1)) LRUD += 100;
							if(!checkSolid(x + 1, y, z)) LRUD += 10;
							if(!checkSolid(x - 1, y, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, tileset,
									vertices, textures, shades, indices);
						}
						
						//z+
						if(!checkSolid(x, y, z + 1)){
							Vector3f p1 = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p2 = new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
							Vector3f p3 = new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
							Vector3f p4 = new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
							
							int LRUD = 0;
							if(!checkSolid(x - 1, y, z)) LRUD += 1000;
							if(!checkSolid(x + 1, y, z)) LRUD += 100;
							if(!checkSolid(x, y + 1, z)) LRUD += 10;
							if(!checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, tileset,
									vertices, textures, shades, indices);
						}
						
						//z-
						if(!checkSolid(x, y, z - 1)){
							Vector3f p1 = new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p2 = new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
							Vector3f p3 = new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);
							Vector3f p4 = new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
							
							int LRUD = 0;
							if(!checkSolid(x + 1, y, z)) LRUD += 1000;
							if(!checkSolid(x - 1, y, z)) LRUD += 100;
							if(!checkSolid(x, y + 1, z)) LRUD += 10;
							if(!checkSolid(x, y - 1, z)) LRUD += 1;
							createFace(p1, p2, p3, p4, LRUD, tileset,
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
		
		RawModel rawModel = loader.loadToVAO(verticesArray, texturesArray, indicesArray);
		ModelTexture texture = new ModelTexture(loader.loadTexture("Tiles"));
		
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
		Vector3f toLight = new Vector3f(SUN.x - point.x, SUN.y - point.y, SUN.z - point.z);
		toLight = Calc.normaliseVector(toLight);
		return (Vector3f.dot(toLight, normal) + 2f) / 2f;
	}
	
	
	private int randInSet(int[] set){
		return set[rand.nextInt(set.length)];
	}
	
	public boolean checkSolid(Vector3f position){
		int x = (int) (position.x + 0.5f);
		int y = (int) (position.y + 0.5f);
		int z = (int) (position.z + 1.5f);
		
		if(x < 0 || x >= XSIZE || y < 0 || y >= YSIZE || z < 0 || z >= ZSIZE){
			return true;
		}
		return voxels[x][y][z].solid;
	}
	
	
	public boolean checkSolid(int x, int y, int z){
		if(x < 0 || x >= XSIZE || y < 0 || y >= YSIZE || z < 0 || z >= ZSIZE){
			return true;
		}
		return voxels[x][y][z].solid;
	}
}
