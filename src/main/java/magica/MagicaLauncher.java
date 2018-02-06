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

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

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
		
		ArrayList<DisplayMode> displayModes = new ArrayList<DisplayMode>();
		
		try {
			for (DisplayMode mode : Display.getAvailableDisplayModes()) {
				if (mode.getFrequency() >= 60 && mode.isFullscreenCapable()) {
					displayModes.add(mode);
				}
			}
			
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
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
			callback.start(displayMode, chckbxFullscreen.isSelected(), serverAddress.getText(), name.getText());
		});
	}
	
	public static interface MagicaCallback {
		public void start(DisplayMode mode, boolean isFullscreen, String serverAddress, String playerName);
	}
}
