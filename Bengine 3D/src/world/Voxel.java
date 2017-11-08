package world;

import org.lwjgl.util.vector.Vector3f;

public class Voxel {
	public enum VoxelTypes {
		GRASS, QUARTZ, STONE
	}
	
	//format top, side, bottom
	public Vector3f tileset = new Vector3f(0, 0, 0);
	public boolean solid = false;
	
	public void setVoxel(VoxelTypes type){
		solid = true;
		switch(type){
		case GRASS:
			tileset = new Vector3f(4, 4, 4);
			break;
		case QUARTZ:
			tileset = new Vector3f(0, 0, 0);
			break;
		case STONE:
			tileset = new Vector3f(0, 0, 0);
			break;
		}
	}
}
