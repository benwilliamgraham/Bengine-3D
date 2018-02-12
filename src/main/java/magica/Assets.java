package magica;

import org.joml.Vector3f;

import bengine.rendering.Material;
import bengine.rendering.Mesh;
import bengine.rendering.Renderer;

public class Assets {
	public static Mesh squareMesh = null;
	
	static {
		try {
			Vector3f[] vertices = new Vector3f[] {
					new Vector3f(-0.5f,  0.5f,  0.0f),
					new Vector3f(-0.5f, -0.5f,  0.0f),
					new Vector3f( 0.5f, -0.5f,  0.0f),
					
					new Vector3f( 0.5f, -0.5f,  0.0f),
					new Vector3f( 0.5f,  0.5f,  0.0f),
					new Vector3f(-0.5f,  0.5f,  0.0f),
				};
				
				Vector3f[] normals = new Vector3f[] {
					new Vector3f(0.0f, 0.0f, 0.0f),
					new Vector3f(0.0f, 0.0f, 0.0f),
					new Vector3f(0.0f, 0.0f, 0.0f),
					
					new Vector3f(0.0f, 0.0f, 0.0f),
					new Vector3f(0.0f, 0.0f, 0.0f),
					new Vector3f(0.0f, 0.0f, 0.0f),
				};
				
				Vector3f[] texCoords = new Vector3f[] {
					new Vector3f( 0.0f,  1.0f,  0.0f),
					new Vector3f( 0.0f,  0.0f,  0.0f),
					new Vector3f( 1.0f,  0.0f,  0.0f),
					
					new Vector3f( 1.0f,  0.0f,  0.0f),
					new Vector3f( 1.0f,  1.0f,  0.0f),
					new Vector3f( 0.0f,  1.0f,  0.0f)
				};
				
				int[] indices = new int[] {0, 1, 2, 3, 4, 5};
				
				squareMesh = new Mesh(vertices, normals, texCoords, indices);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void create(Renderer r) {
		squareMesh.create(r);
	}
}
