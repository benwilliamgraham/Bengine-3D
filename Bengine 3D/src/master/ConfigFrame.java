package master;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class ConfigFrame extends JFrame {

	private JPanel contentPane;
	private JTextField serverAddress;
	private JTextField name;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigFrame frame = new ConfigFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ConfigFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 315, 267);
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
		
		name = new JTextField();
		name.setBounds(115, 8, 174, 20);
		contentPane.add(name);
		name.setColumns(10);
		
		JCheckBox chckbxFullscreen = new JCheckBox("Fullscreen");
		chckbxFullscreen.setBounds(5, 110, 97, 23);
		contentPane.add(chckbxFullscreen);
		
		JComboBox resolution = new JComboBox();
		resolution.setBounds(115, 64, 174, 20);
		contentPane.add(resolution);
		
		JLabel lblResolut = new JLabel("Resolution:");
		lblResolut.setBounds(5, 67, 87, 14);
		contentPane.add(lblResolut);
	}
}
