package master;

import java.io.IOException;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import networking.UDPClient;
import networking.packets.*;
import renderEngine.DisplayManager;
import renderEngine.Renderer;
import shaders.StaticShader;
import toolBox.Assets;
import toolBox.Loader;
import world.World;

public class GameLoop {

	public static void main(String[] args) throws IOException{
		Packet.register(HandshakePacket.class);
		Packet.register(RejectedPacket.class);
		Packet.register(RegisterEntityPacket.class);
		Packet.register(UpdateEntityPacket.class);
		
		DisplayManager.createDisplay(800, 600, false);
		
		Loader loader = new Loader();
		
		Assets.loadAssets(loader);
		
		StaticShader shader = new StaticShader();
		
		Renderer renderer = new Renderer(shader);

		//connect to a server
		UDPClient client = new UDPClient(true);
		
		System.out.println("Creating world");
		
		//create the world
		World world = new World(loader, client);
		
		//start connection
		client.open();
				
		long startTime = Sys.getTime();
		int frames = 0;
		while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			long time = Sys.getTime();
			
			world.update();
			renderer.prepare();
			shader.start();
			shader.loadViewMatrix(world.camera);
			renderer.render(world.faceMap, shader);
			renderer.render(world.entities, shader);
			shader.stop();
			DisplayManager.updateDisplay();
			
			while(1000f / (Sys.getTime() + 1 - time) > DisplayManager.FPS){
				continue;
			}
			
			frames++;
		}
		float totTime = 1f/1000f * (Sys.getTime() - startTime);
		System.out.println(totTime + " seconds for " + frames + " frames: " + frames / totTime + " fps");
		
		shader.cleanUp();
		loader.cleanUp();
		client.close();
		DisplayManager.closeDisplay();
		System.exit(0);
	}
}