package magica;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultComboBoxModel;

public class MagicaLauncher extends JFrame {
	
	private static final String[] sampleNames = new String[] {
			"Richard Schlick",
			"Ben",
			"Marin",
			"Lin",
			"Jack",
			"Dorothy",
			"Morris",
			"Christopher",
			"Danielle",
			"Howard",
			"Gene",
			"Michelle",
			"Elizabeth",
			"Gene",
			"Daniel",
			"Gabe",
			"Shanon",
			"Phillip",
			"Hudson"
	};
	
	private static final long serialVersionUID = 5248339920103188643L;
	private JPanel contentPane;
	private JTextField serverAddress;
	private JTextField name;
	
	public MagicaLauncher(MagicaCallback callback) {
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
		serverAddress.setText("localhost");
		serverAddress.setBounds(115, 33, 174, 20);
		contentPane.add(serverAddress);
		serverAddress.setColumns(10);
		
		name = new JTextField(sampleNames[(int) Math.floor(Math.random() * sampleNames.length)]);
		name.setBounds(115, 8, 174, 20);
		contentPane.add(name);
		name.setColumns(10);
		
		JCheckBox chckbxFullscreen = new JCheckBox("Fullscreen");
		chckbxFullscreen.setBounds(5, 110, 97, 23);
		contentPane.add(chckbxFullscreen);
		
		DisplayMode[] displayModes = new DisplayMode[] {
				new DisplayMode(640, 480),
				new DisplayMode(800, 600),
				new DisplayMode(1024, 768),
				new DisplayMode(1280, 1024),
				new DisplayMode(1366, 768),
				new DisplayMode(1920, 1080)
		};
		
		JComboBox resolution = new JComboBox(displayModes);
		resolution.setBounds(115, 64, 174, 20);
		contentPane.add(resolution);
		
		JLabel lblResolut = new JLabel("Resolution:");
		lblResolut.setBounds(5, 67, 87, 14);
		contentPane.add(lblResolut);
		
		JLabel lblDemo = new JLabel("Demo:");
		lblDemo.setBounds(5, 89, 46, 14);
		contentPane.add(lblDemo);
		
		JComboBox demoName = new JComboBox();
		demoName.setModel(new DefaultComboBoxModel(new String[] {"Skeletal Animation", "Chicken Demo", "Collision Demo"}));
		demoName.setBounds(115, 86, 174, 20);
		contentPane.add(demoName);
		
		btnStartGame.addActionListener((ActionEvent e) -> {
			
			DisplayMode displayMode = displayModes[resolution.getSelectedIndex()];
			setVisible(false);
			callback.start(displayMode.getWidth(), displayMode.getHeight(), chckbxFullscreen.isSelected(), (String) demoName.getSelectedItem(), serverAddress.getText(), name.getText());
		});
	}
	
	public static interface MagicaCallback {
		public void start(int width, int height, boolean isFullscreen, String demoName, String serverAddress, String playerName);
	}
	
	public static class DisplayMode {
		public int width, height;
		
		public DisplayMode(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		@Override 
		public String toString() {
			return this.width + " X " + this.height;
		}
	}
}
