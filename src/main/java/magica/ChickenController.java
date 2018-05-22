package magica;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;

public class ChickenController extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3487646386231613395L;

	public JComboBox<String> comboBox;
	
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public ChickenController() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		comboBox = new JComboBox<String>();
		comboBox.setBounds(10, 11, 100, 20);
		contentPane.add(comboBox);
	}
}
