package master;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Bullet;
import entities.Player;
import networking.PermissionManager;
import networking.messages.*;
import networking.serialization.ObjectParser;
import networking.serialization.serializers.*;
import networking.sync.SyncedObjectManager;
import renderEngine.DisplayManager;
import renderEngine.Renderer;
import shaders.StaticShader;
import toolBox.Assets;
import toolBox.Loader;
import world.World;

public class MagicaClient extends JFrame {
	
	public MagicaClient() {
		super("Magica: The game about sand.");
		setLocationRelativeTo(null); //Center the window.
		setSize(300, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel contentPane = new JPanel();
		contentPane.setSize(300, 500);
		
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		JTextField nameField = new JTextField("name");
		nameField.setAlignmentX(CENTER_ALIGNMENT);
		nameField.setMaximumSize(new Dimension(300, 50));
		
		
		JTextField ipField = new JTextField("localhost");
		ipField.setAlignmentX(CENTER_ALIGNMENT);
		ipField.setMaximumSize(new Dimension(300, 50));
		
		JButton startButton = new JButton("Start Magica!");
		startButton.setAlignmentX(CENTER_ALIGNMENT);
		
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				setVisible(false);
				startGame(ipField.getText(), nameField.getText());
				
			}
			
		});
		
		contentPane.add(nameField);
		contentPane.add(ipField);
		contentPane.add(startButton);
		
		setContentPane(contentPane);
	}
	
	void startGame(String server, String name) {
		Loader loader = new Loader();

		//Create Display
		DisplayManager.createDisplay(800, 600, false);
		
		//Load  assets
		Assets.loadAssets(loader);
		System.out.println("Finished loading assets");
		
		//create the world
		System.out.println("Creating world");
		World world = new World(loader, server, name);
		
		StaticShader shader = new StaticShader();
		
		Renderer renderer = new Renderer(shader);
		
		System.out.println("Connecting to Server");
		world.open();
		
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
		world.close();
		DisplayManager.closeDisplay();
		System.exit(0);
	}
	
	public static void main(String[] args) throws IOException {
		//Register everything for networking.
		NetworkMessage.registerMessage(HandshakeMessage.class);
		NetworkMessage.registerMessage(ObjectMessage.class);
		NetworkMessage.registerMessage(DebugMessage.class);
		NetworkMessage.registerMessage(RPCMessage.class);
		
		ObjectParser.registerType(Integer.class, new IntSerializer());
		ObjectParser.registerType(Float.class, new FloatSerializer());
		ObjectParser.registerType(String.class, new StringSerializer());
		ObjectParser.registerType(Double.class, new DoubleSerializer());
		ObjectParser.registerType(Long.class, new LongSerializer());
		ObjectParser.registerType(List.class, new CollectionSerializer());
		ObjectParser.registerType(PermissionManager.class, new PermissionSerializer());
		ObjectParser.registerType(Vector3f.class, new Vector3fSerializer());
		
		SyncedObjectManager.registerTrackedType(Player.class);
		SyncedObjectManager.registerTrackedType(Bullet.class);
		
		MagicaClient game = new MagicaClient();
		
		game.setVisible(true);
	}

}
