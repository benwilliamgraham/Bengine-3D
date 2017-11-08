package world;

import java.io.*;
import java.awt.*;
import javax.imageio.ImageIO;

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
				case 250:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.QUARTZ);
					break;
				case 125:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.GRASS);
					break;
				case 110:
					world.voxels[nx][ny][nz].setVoxel(VoxelTypes.STONE);
					break;
				}
			}
		}
  }
}
