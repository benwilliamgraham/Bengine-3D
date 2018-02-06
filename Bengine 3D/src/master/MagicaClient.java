package master;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
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
	
	private JPanel contentPane;
	private JTextField serverAddress;
	private JTextField name;
	
	public MagicaClient() throws LWJGLException {
		super("Magica: The game about sand.");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 315, 267);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnStartGame = new JButton("START GAME");
		btnStartGame.setBounds(5, 162, 289, 62);
		contentPane.add(btnStartGame);
		
		JLabel lblName = new JLabel("Name: ");
		lblName.setBounds(5, 11, 46, 14);
		contentPane.add(lblName);
		
		JLabel lblServerAddress = new JLabel("Server Address:");
		lblServerAddress.setBounds(5, 36, 100, 14);
		contentPane.add(lblServerAddress);
		
		serverAddress = new JTextField();
		serverAddress.setText("127.0.0.1");
		serverAddress.setBounds(115, 33, 174, 20);
		contentPane.add(serverAddress);
		serverAddress.setColumns(10);
		
		name = new JTextField();
		name.setBounds(115, 8, 174, 20);
		contentPane.add(name);
		name.setColumns(10);
		
		JCheckBox chckbxFullscreen = new JCheckBox("Fullscreen");
		chckbxFullscreen.setBounds(5, 110, 97, 23);
		contentPane.add(chckbxFullscreen);
		
		ArrayList<DisplayMode> displayModes = new ArrayList<DisplayMode>();
		
		for (DisplayMode mode : Display.getAvailableDisplayModes()) {
			if (mode.getFrequency() >= 60 && mode.isFullscreenCapable()) {
				displayModes.add(mode);
			}
		}
		
		displayModes.sort(Comparator.comparingInt(DisplayMode::getWidth));
		
		JComboBox resolution = new JComboBox(displayModes.toArray());
		resolution.setBounds(115, 64, 174, 20);
		contentPane.add(resolution);
		
		JLabel lblResolut = new JLabel("Resolution:");
		lblResolut.setBounds(5, 67, 87, 14);
		contentPane.add(lblResolut);
		
		btnStartGame.addActionListener((ActionEvent e) -> {
			
			DisplayMode displayMode = displayModes.get(resolution.getSelectedIndex());
			setVisible(false);
			startGame(displayMode, chckbxFullscreen.isSelected(), serverAddress.getText(), name.getText());
		});
	}
	
	void startGame(DisplayMode mode, boolean fullscreen, String server, String name) {
		Loader loader = new Loader();

		//Create Display
		DisplayManager.createDisplay(mode, fullscreen);
		
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
	
	public static void main(String[] args) throws Exception {
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
