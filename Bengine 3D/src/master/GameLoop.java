package master;

import java.io.IOException;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import data.ModelTexture;
import data.RawModel;
import data.TexturedModel;
import entities.Camera;
import entities.Entity;
import entities.Player;
import networking.Client;
import renderEngine.DisplayManager;
import renderEngine.Renderer;
import shaders.StaticShader;
import toolBox.Assets;
import toolBox.Loader;
import world.FaceMap;
import world.World;

public class GameLoop {

	public static void main(String[] args) throws IOException{
		
		DisplayManager.createDisplay(1200, 800, false);
		
		Loader loader = new Loader();
		
		Assets.loadAssets(loader);
		
		StaticShader shader = new StaticShader();
		
		Renderer renderer = new Renderer(shader);

		//connect to a server
		Client client = new Client(true);
		
		System.out.println("Creating world");
		
		//create the world
		World world = new World(loader, client);
		
		//start connection
		client.start(world);
				
		long startTime = Sys.getTime();
		int frames = 0;
		while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			long time = Sys.getTime();
			
			world.update();
			renderer.prepare();
			shader.start();
			shader.loadViewMatrix(world.player.camera);
			renderer.render(world.faceMap, shader);
			world.lockMap = true;
			renderer.render(world.dynEntities, shader);
			world.lockMap = false;
			shader.stop();
			DisplayManager.updateDisplay();
			
			
			while(1000f / (Sys.getTime() + 1 - time) > DisplayManager.FPS){
				
			}
			
			frames++;
		}
		float totTime = 1f/1000f * (Sys.getTime() - startTime);
		System.out.println(totTime + " seconds for " + frames + " frames: " + frames / totTime + " fps");
		
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		System.exit(0);
	}
}
