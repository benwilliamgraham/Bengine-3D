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
import toolBox.OpenSimplexNoise;

public class FaceNet extends Entity {
	
	public static final int OBJECT_TYPE = generateTypeId();
	
	public TexturedModel model;
	
	private Random rand;
	private Loader loader;
	private World world;
	private List<Light> lights;
	private OpenSimplexNoise noise;
	
	public FaceNet(Loader loader, World world, Random rand, List<Light> lights) {
		super(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		this.loader = loader;
		this.world = world;
		this.rand = rand;
		this.lights = lights;
		this.noise = new OpenSimplexNoise();
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
						Vector3f position = new Vector3f(x, y, z);
						
						//set tileset
						Vector3f tileset = world.voxels[x][y][z].tileset;
						
						Vector3f c1 = setIntersection(position, new Vector3f(1, 1, -1));
						Vector3f c2 = setIntersection(position, new Vector3f(1, 1, 1));
						Vector3f c3 = setIntersection(position, new Vector3f(1, -1, 1));
						Vector3f c4 = setIntersection(position, new Vector3f(1, -1, -1));
						Vector3f c5 = setIntersection(position, new Vector3f(-1, 1, -1));
						Vector3f c6 = setIntersection(position, new Vector3f(-1, 1, 1));
						Vector3f c7 = setIntersection(position, new Vector3f(-1, -1, 1));
						Vector3f c8 = setIntersection(position, new Vector3f(-1, -1, -1));
						
						//x+
						if(!world.checkSolid(x + 1, y, z)){
							Vector3f p1 = new Vector3f(c1);
							Vector3f p2 = new Vector3f(c2);
							Vector3f p3 = new Vector3f(c3);
							Vector3f p4 = new Vector3f(c4);
							
							createFace(p1, p2, p3, vertices, textures, shades, indices);
							createFace(p1, p3, p4, vertices, textures, shades, indices);
						}
						
						//x-
						if(!world.checkSolid(x - 1, y, z)){
							Vector3f p1 = new Vector3f(c6);
							Vector3f p2 = new Vector3f(c5);
							Vector3f p3 = new Vector3f(c8);
							Vector3f p4 = new Vector3f(c7);
							
							createFace(p1, p2, p3, vertices, textures, shades, indices);
							createFace(p1, p3, p4, vertices, textures, shades, indices);
						}
						
						//y+
						if(!world.checkSolid(x, y + 1, z)){
							Vector3f p1 = new Vector3f(c1);
							Vector3f p2 = new Vector3f(c5);
							Vector3f p3 = new Vector3f(c6);
							Vector3f p4 = new Vector3f(c2);
							
							createFace(p1, p2, p3, vertices, textures, shades, indices);
							createFace(p1, p3, p4, vertices, textures, shades, indices);
						}
						
						//y-
						if(!world.checkSolid(x, y - 1, z)){
							Vector3f p1 = new Vector3f(c4);
							Vector3f p2 = new Vector3f(c3);
							Vector3f p3 = new Vector3f(c7);
							Vector3f p4 = new Vector3f(c8);
							
							createFace(p1, p2, p3, vertices, textures, shades, indices);
							createFace(p1, p3, p4, vertices, textures, shades, indices);
						}
						
						//z+
						if(!world.checkSolid(x, y, z + 1)){
							Vector3f p1 = new Vector3f(c2);
							Vector3f p2 = new Vector3f(c6);
							Vector3f p3 = new Vector3f(c7);
							Vector3f p4 = new Vector3f(c3);
							
							createFace(p1, p2, p3, vertices, textures, shades, indices);
							createFace(p1, p3, p4, vertices, textures, shades, indices);
						}
						
						//z-
						if(!world.checkSolid(x, y, z - 1)){
							Vector3f p1 = new Vector3f(c5);
							Vector3f p2 = new Vector3f(c1);
							Vector3f p3 = new Vector3f(c4);
							Vector3f p4 = new Vector3f(c8);
							
							createFace(p1, p2, p3, vertices, textures, shades, indices);
							createFace(p1, p3, p4, vertices, textures, shades, indices);
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
		
		System.out.println("Loading model with " + indicesArray.length / 3 + "tris and " + vertices.size() + " vertices");
		RawModel rawModel = loader.loadToVAO(verticesArray, texturesArray, indicesArray);
		ModelTexture texture = new ModelTexture(loader.loadTexture("flat"));
		
		model = new TexturedModel(rawModel, texture);
	}
	
	
	private Vector3f setIntersection(Vector3f position, Vector3f direction){
		
		Vector3f newDir = new Vector3f(direction.x, direction.y, direction.z);
		
		float fc = 0.5f;
		float mc = 0.0f;
		
		//check x
		if(world.checkSolid((int)(position.x + direction.x), (int) position.y, (int) position.z) ||
				world.checkSolid((int)(position.x + direction.x), (int) (position.y + direction.y), (int) position.z) ||
				world.checkSolid((int)(position.x + direction.x), (int) position.y, (int) (position.z + direction.z)) ||
				world.checkSolid((int)(position.x + direction.x), (int) (position.y + direction.y), (int) (position.z + direction.z))){
			newDir.x *= fc;
		}else{
			newDir.x *= mc;
		}
		
		//check y
		if(world.checkSolid((int)position.x, (int) (position.y + direction.y), (int) position.z) ||
				world.checkSolid((int) (position.x + direction.x), (int) (position.y + direction.y), (int) position.z) ||
				world.checkSolid((int) position.x, (int) (position.y + direction.y), (int) (position.z + direction.z)) ||
				world.checkSolid((int) (position.x + direction.x), (int) (position.y + direction.y), (int) (position.z + direction.z))){
			newDir.y *= fc;
		}else{
			newDir.y *= mc;
		}
		
		//check z
		if(world.checkSolid((int) position.x, (int) position.y, (int) (position.z + direction.z)) ||
				world.checkSolid((int) (position.x + direction.x), (int) position.y, (int) (position.z + direction.z)) ||
				world.checkSolid((int) position.x, (int) (position.y + direction.y), (int) (position.z + direction.z)) ||
				world.checkSolid((int) (position.x + direction.x), (int) (position.y + direction.y), (int) (position.z + direction.z))){
			newDir.z *= fc;
		}else{
			newDir.z *= mc;
		}
		
		return Vector3f.add(newDir, position, null);
	}
	
	
	private void createFace(Vector3f p1, Vector3f p2, Vector3f p3,
			List<Vector3f> vertices, List<Vector2f> textures, List<Float> shades, List<Integer> indices){
		int index = vertices.size();
		
		//make sure that it is a valid face
		if((p1.x == p2.x && p1.y == p2.y && p1.z == p2.z) ||
				(p1.x == p3.x && p1.y == p3.y && p1.z == p3.z) ||
				(p2.x == p3.x && p2.y == p3.y && p2.z == p3.z)){
			return;
		}
		
		Vector3f normal = Calc.calculateNormal(p1, p2, p3);
		addVertex(p1, normal, vertices, textures, shades, indices);
		addVertex(p2, normal, vertices, textures, shades, indices);
		addVertex(p3, normal, vertices, textures, shades, indices);
	}
	
	private void addVertex(Vector3f point, Vector3f normal, List<Vector3f> vertices, List<Vector2f> textures, List<Float> shades, List<Integer> indices){
		vertices.add(point);
		shades.add(calcShade(normal, point));
		textures.add(new Vector2f(1, 1));
		indices.add(vertices.size() - 1);
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
			float diffuse = light.brightness * (Vector3f.dot(toLight, normal) + 2f) / 3f;
			
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
	public void onCreated() {
		// TODO Auto-generated method stub
		
	}

	public void onUpdate(float delta) {
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
