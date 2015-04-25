import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;


public class ClientStage extends JPanel {
	private SpringLayout currentLayout;
	private JPanel loginPanel;
	
	public ClientStage() {
		this.setBackground(new Color(0, 139, 139));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		/*
		double screenWidth = screenSize.getWidth();
		int width = (int) screenWidth;
		double screenHeight = screenSize.getHeight();
		int height = (int) screenHeight;
		*/
		this.setSize(screenSize);
		
//		/this.setTitle("Set Game Client");
		//this.setPreferredSize(new Dimension(width, height));
		//this.setResizable(false);
		//this.setContentPane(mainStage);
		
		currentLayout = new SpringLayout();
		loginPanel = new JPanel();
		loginPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		setupStagePanel();
	}
	
	private void setupStagePanel() {
		this.setLayout(currentLayout);
		
		setupLoginPanel();
		
		loginPanel.setBounds(0,0,0,0);
		this.add(loginPanel);
	}
	
	
	private void setupLoginPanel() {

		loginPanel.setLayout(null);

		JLabel userLabel = new JLabel("User");
		userLabel.setBounds(10, 10, 80, 25);
		loginPanel.add(userLabel);

		JTextField userText = new JTextField(20);
		userText.setBounds(100, 10, 160, 25);
		loginPanel.add(userText);

		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(10, 40, 80, 25);
		loginPanel.add(passwordLabel);

		JPasswordField passwordText = new JPasswordField(20);
		passwordText.setBounds(100, 40, 160, 25);
		loginPanel.add(passwordText);

		JButton loginButton = new JButton("login");
		loginButton.setBounds(10, 80, 80, 25);
		loginPanel.add(loginButton);
		
		JButton registerButton = new JButton("register");
		registerButton.setBounds(180, 80, 80, 25);
		loginPanel.add(registerButton);
	
	}

	
}
