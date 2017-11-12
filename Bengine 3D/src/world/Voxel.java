package world;

import org.lwjgl.util.vector.Vector3f;

public class Voxel {
	public enum VoxelTypes {
		DIRT, STONE, ROOFING, WOOD, RED_WOOD, TILES, BUSH, WATER
	}
	
	//format top, side, bottom
	public Vector3f tileset = new Vector3f(0, 0, 0);
	public boolean solid = false;
	
	public void setVoxel(VoxelTypes type){
		solid = true;
		switch(type){
		case DIRT:
			tileset = new Vector3f(0, 0, 0);
			break;
		case STONE:
			tileset = new Vector3f(1, 1, 1);
			break;
		case ROOFING:
			tileset = new Vector3f(2, 2, 2);
			break;
		case WOOD:
			tileset = new Vector3f(3, 3, 3);
			break;
		case RED_WOOD:
			tileset = new Vector3f(4, 4, 4);
			break;
		case TILES:
			tileset = new Vector3f(5, 5, 5);
			break;
		case BUSH:
			tileset = new Vector3f(6, 6, 6);
			break;
		case WATER:
			tileset = new Vector3f(7, 7, 7);
			break;
		}
	}
}
