package world;

import org.lwjgl.util.vector.Vector3f;

public class Voxel {
	public enum VoxelTypes {
		GRASS, DIRT, SAND, WATER
	}
	
	//format top, side, bottom
	public Vector3f tileset = new Vector3f(0, 0, 0);
	public boolean solid = false;
	public VoxelTypes type;
	
	public void setVoxel(VoxelTypes type){
		this.type = type;
		solid = true;
		switch(type){
		case GRASS:
			tileset = new Vector3f(0, 0, 0);
			break;
		case DIRT:
			tileset = new Vector3f(1, 1, 1);
			break;
		case SAND:
			tileset = new Vector3f(2, 2, 2);
			break;
		case WATER:
			tileset = new Vector3f(3, 3, 3);
			break;
		}
	}
}
