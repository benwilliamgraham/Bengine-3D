package master;

import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JOptionPane;

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
import entities.Bullet;
import networking.UDPClient;
import networking.packets.*;
import renderEngine.DisplayManager;
import renderEngine.Renderer;
import shaders.StaticShader;
import toolBox.Assets;
import toolBox.Loader;
import world.FaceMap;
import world.World;

public class GameLoop {

	public static void main(String[] args) throws IOException {
		//Register packets
		Packet.register(HandshakePacket.class);
		Packet.register(RejectedPacket.class);
		Packet.register(RegisterEntityPacket.class);
		Packet.register(UpdateEntityPacket.class);
		Packet.register(DestroyEntityPacket.class);
		
		//Register entities so that the server can see them.
		Entity.register(Player.class);
		Entity.register(Bullet.class);
		
		Loader loader = new Loader();
		
		String serverAddress = JOptionPane.showInputDialog("Enter the Server IP: ");

		//Create Display
		DisplayManager.createDisplay(800, 600, false);
		
		//connect to a server
		UDPClient client = new UDPClient(false, InetAddress.getByName(serverAddress));
		
		//Load  assets
		Assets.loadAssets(loader);
		System.out.println("Finished loading assets");
		
		//create the world
		System.out.println("Creating world");
		World world = new World(loader, client);
		
		StaticShader shader = new StaticShader();
		
		Renderer renderer = new Renderer(shader);
		
		System.out.println("Connecting to Server");
		client.open();
		
		System.out.println("Starting Timer");
		long startTime = Sys.getTime();
		int frames = 0;
		while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			long time = Sys.getTime();
			
			world.update();
			renderer.prepare();
			world.render(renderer, shader);
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
