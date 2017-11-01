package world;

import java.io.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class LevelLoader{
	
	public static void loadLevel(World world) throws IOException{
		File file= new File("assets/Stairs.png");
		BufferedImage image = ImageIO.read(file);
		
		for(int x = 0; x < World.XSIZE; x++){
			for(int y = 0; y < World.YSIZE * World.ZSIZE; y++){
				Color c = new Color(image.getRGB(x, y), true);
				int nx = x;
				int ny = World.YSIZE - 1 - y / World.YSIZE;
				int nz = y % World.XSIZE;
				
				int tileset = 0;
				switch (c.getRed()){
				case 131:
					tileset = 0;
					break;
				case 229:
					tileset = 1;
					break;
				case 72:
					tileset = 2;
					break;
				case 149:
					tileset = 3;
					break;
				case 184:
					tileset = 4;
					break;
				case 14:
					tileset = 5;
					break;
				case 68:
					tileset = 6;
					break;
				case 220:
					tileset = 7;
					break;
				}
				
				world.voxels[nx][ny][nz] = new Voxel();
				world.voxels[nx][ny][nz].tileset = tileset;
				
				if(c.getAlpha() == 0){
					world.voxels[nx][ny][nz].solid = false;
				}else{
					world.voxels[nx][ny][nz].solid = true;
				}
			}
		}
  }
}
