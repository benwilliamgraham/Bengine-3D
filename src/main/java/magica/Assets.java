package magica;

import org.joml.Vector3f;

import bengine.rendering.Mesh;

public class Assets {
	public static Mesh squareMesh = new Mesh(
			new Vector3f[] {
					new Vector3f(-0.5f, -0.5f, 0.0f), 
					new Vector3f(0.5f, -0.5f, 0.0f),
					new Vector3f(0.5f, 0.5f, 0.0f)},
			new Vector3f[] {
					new Vector3f(),
					new Vector3f(),
					new Vector3f()
			},
			new Vector3f[] {
				new Vector3f(0.0f, 0.0f, 0.0f),
				new Vector3f(1.0f, 0.0f, 0.0f),
				new Vector3f(1.0f, 1.0f, 0.0f)
			},
			new int[] {0, 1, 2});
}
