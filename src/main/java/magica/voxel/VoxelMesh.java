package magica.voxel;

import java.util.ArrayList;

import org.joml.Vector3f;
import org.joml.Vector3i;

import bengine.rendering.Mesh;

public class VoxelMesh extends Mesh {
	public VoxelMesh(int[][][] mapData, Vector3i size) {
		super();
		
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
		ArrayList<Vector3f> normals   = new ArrayList<Vector3f>();
		ArrayList<Vector3f> texCoords = new ArrayList<Vector3f>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		Vector3f offset = new Vector3f(-size.x / 2.0f, -size.y / 2.0f, -size.z / 2.0f);
		
		for (int z = 0; z < size.z; z++) {
			for (int y = 0; y < size.y; y++) {
				for (int x = 0; x < size.x; x++) { //Iterate through all of the cubes in the voxel map.
					Vector3f bfl = new Vector3f(offset).add(new Vector3f(x, y, z)); // Calculating all the verticies for the corners.
					Vector3f bbl = new Vector3f(offset).add(new Vector3f(x, y, z+1));
					Vector3f bfr = new Vector3f(offset).add(new Vector3f(x + 1, y, z));
					Vector3f bbr = new Vector3f(offset).add(new Vector3f(x + 1, y, z + 1));
					Vector3f tfl = new Vector3f(offset).add(new Vector3f(x, y + 1, z));
					Vector3f tbl = new Vector3f(offset).add(new Vector3f(x, y + 1, z+1));
					Vector3f tfr = new Vector3f(offset).add(new Vector3f(x + 1, y + 1, z));
					Vector3f tbr = new Vector3f(offset).add(new Vector3f(x + 1, y + 1, z + 1));
					
					if (x == 0) {
						Vector3f normal = new Vector3f(-1.0f, 0.0f, 0.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);
						
						vertices.add(bfl);vertices.add(tfl);vertices.add(tbl);
						vertices.add(bfl);vertices.add(tbl);vertices.add(bbl);
						
					} else if (mapData[x - 1][y][z] == 0) {
						Vector3f normal = new Vector3f(-1.0f, 0.0f, 0.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);
						
						vertices.add(bfl);vertices.add(tfl);vertices.add(tbl);
						vertices.add(bfl);vertices.add(tbl);vertices.add(bbl);
					}
					
					if (x == mapData.length - 1) {
						Vector3f normal = new Vector3f(1.0f, 0.0f, 0.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);
						
						vertices.add(bfr);vertices.add(tbr);vertices.add(tfr);
						vertices.add(bfr);vertices.add(bbr);vertices.add(tbr);
					} else if (mapData[x + 1][y][z] == 0) {
						Vector3f normal = new Vector3f(1.0f, 0.0f, 0.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);
						
						vertices.add(bfr);vertices.add(tbr);vertices.add(tfr);
						vertices.add(bfr);vertices.add(bbr);vertices.add(tbr);
					}
					
					if (y == mapData[x].length - 1) { // Check if we need a face on the top.
						Vector3f normal = new Vector3f(0.0f, 1.0f, 0.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);
						
						vertices.add(tbr);vertices.add(tfl);vertices.add(tfr);
						vertices.add(tbr);vertices.add(tbl);vertices.add(tfl);
						
					} else if (mapData[x][y + 1][z] == 0) {
						Vector3f normal = new Vector3f(0.0f, 1.0f, 0.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);

						vertices.add(tbr);vertices.add(tfl);vertices.add(tfr);
						vertices.add(tbr);vertices.add(tbl);vertices.add(tfl);
					}
					
					if (y == 0) { //Check if we need a face on the bottom, if we do, add it.
						Vector3f normal = new Vector3f(0.0f, -1.0f, 0.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);

						vertices.add(bbr);vertices.add(bfl);vertices.add(bfr);
						vertices.add(bbr);vertices.add(bbl);vertices.add(bfl);
						
					} else if (mapData[x][y - 1][z] == 0) {
						Vector3f normal = new Vector3f(0.0f, -1.0f, 0.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);
						
						vertices.add(bbr);vertices.add(bfl);vertices.add(bfr);
						vertices.add(bfr);vertices.add(bbl);vertices.add(bfl);
					}
					
					if (z == 0) {
						Vector3f normal = new Vector3f(0.0f, 0.0f, -1.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);
						
						vertices.add(tfr);vertices.add(bfl);vertices.add(bfr);
						vertices.add(tfr);vertices.add(tfl);vertices.add(bfr);
						
					} else if (mapData[x][y][z - 1] == 0) {
						Vector3f normal = new Vector3f(0.0f, 0.0f, -1.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);
						
						vertices.add(tfr);vertices.add(bfl);vertices.add(bfr);
						vertices.add(tfr);vertices.add(tfl);vertices.add(bfr);
						
					}
					
					if (z == mapData[x][y].length - 1) {
						Vector3f normal = new Vector3f(0.0f, 0.0f, 1.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);
						
						vertices.add(tbr);vertices.add(bbr);vertices.add(bbl);
						vertices.add(tbr);vertices.add(bbl);vertices.add(tbl);
					} else if (mapData[x][y][z + 1] == 0) {
						Vector3f normal = new Vector3f(0.0f, 0.0f, 1.0f);
						
						normals.add(normal);normals.add(normal);normals.add(normal);
						normals.add(normal);normals.add(normal);normals.add(normal);
						
						vertices.add(tbr);vertices.add(bbr);vertices.add(bbl);
						vertices.add(tbr);vertices.add(bbl);vertices.add(tbl);
					}
					
				}
			}
		}
		
		/*this.vertices = vertices.toArray(new Vector3f[vertices.size()]);
		this.normals = normals.toArray(new Vector3f[normals.size()]);
		this.texCoords = new Vector3f[] {};*/
		this.indices = store(indices.toArray(new Integer[indices.size()]));
	}
}