package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	public static float FPS = 64;
	
	public static void createDisplay(DisplayMode mode, boolean fullscreen){
		
		ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
		
		try {
			Display.setDisplayMode(mode);
			Display.setFullscreen(fullscreen);
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("Magica: The game about sand.");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}
	
	public static void updateDisplay(){
		Display.update();
	}
	
	public static void closeDisplay(){
		Display.destroy();
	}
}
