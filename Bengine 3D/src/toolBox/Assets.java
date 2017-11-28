package toolBox;

import data.ModelTexture;
import data.RawModel;
import data.TexturedModel;

public class Assets {

	public static ModelTexture tex;
	public static ModelTexture tiles;
	public static RawModel cube;
	public static TexturedModel cubert;
	
	static float[] vertices = {			
			-0.5f,0.5f,-0.5f,	
			-0.5f,-0.5f,-0.5f,	
			0.5f,-0.5f,-0.5f,	
			0.5f,0.5f,-0.5f,		
			
			-0.5f,0.5f,0.5f,	
			-0.5f,-0.5f,0.5f,	
			0.5f,-0.5f,0.5f,	
			0.5f,0.5f,0.5f,
			
			0.5f,0.5f,-0.5f,	
			0.5f,-0.5f,-0.5f,	
			0.5f,-0.5f,0.5f,	
			0.5f,0.5f,0.5f,
			
			-0.5f,0.5f,-0.5f,	
			-0.5f,-0.5f,-0.5f,	
			-0.5f,-0.5f,0.5f,	
			-0.5f,0.5f,0.5f,
			
			-0.5f,0.5f,0.5f,
			-0.5f,0.5f,-0.5f,
			0.5f,0.5f,-0.5f,
			0.5f,0.5f,0.5f,
			
			-0.5f,-0.5f,0.5f,
			-0.5f,-0.5f,-0.5f,
			0.5f,-0.5f,-0.5f,
			0.5f,-0.5f,0.5f
			
	};
	
	static float[] textureCoords = {
			
			0,0,1,
			0,1,1,
			1,1,1,
			1,0,1,			
			0,0,1,
			0,1,1,
			1,1,1,
			1,0,1,			
			0,0,1,
			0,1,1,
			1,1,1,
			1,0,1,
			0,0,1,
			0,1,1,
			1,1,1,
			1,0,1,
			0,0,1,
			0,1,1,
			1,1,1,
			1,0,1,
			0,0,1,
			0,1,1,
			1,1,1,
			1,0,1

			
	};
	
	static int[] indices = {
			3,1,0,	
			2,1,3,	
			
			4,5,7,
			7,5,6,
			
			11,9,8,
			10,9,11,
			
			12,13,15,
			15,13,14,	
			
			19,17,16,
			18,17,19,
			
			20,21,23,
			23,21,22

	};
	
	public static void loadAssets(Loader loader){
		tiles = new ModelTexture(loader.loadTexture("T"));
		tex = new ModelTexture(loader.loadTexture("Cubert"));
		cube = loader.loadToVAO(vertices, textureCoords, indices);
		cubert = new TexturedModel(cube, tex);
	}
}
