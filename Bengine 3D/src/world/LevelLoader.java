package world;

import java.io.*;
import java.awt.*;
import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;

import entities.Light;
import world.Voxel.VoxelTypes;

import java.awt.image.BufferedImage;

public class LevelLoader{
	
	public static void loadLevel(World world) throws IOException{
		File file= new File("assets/city.png");
		BufferedImage image = ImageIO.read(file);
		
		for(int x = 0; x < World.XSIZE; x++){
			for(int y = 0; y < World.YSIZE * World.ZSIZE; y++){
				Color c = new Color(image.getRGB(x, y), true);
				int nx = x;
				int ny = World.YSIZE - 1 - y / World.YSIZE;
				int nz = y % World.XSIZE;
				
				world.voxels[nx][ny][nz] = new Voxel();
				switch (c.getRed()){
				case 89:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.DIRT);
					break;
				case 66:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.STONE);
					break;
				case 27:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.ROOFING);
					break;
				case 85:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.WOOD);
					break;
				case 151:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.RED_WOOD);
					break;
				case 64:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.TILES);
					break;
				case 25:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.BUSH);
					break;
				case 24:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.WATER);
					break;
				case 255:
					world.lights.add(new Light(new Vector3f(nx, ny, nz), 15, 1, 1.2f));
				}
			}
		}
  }
}
