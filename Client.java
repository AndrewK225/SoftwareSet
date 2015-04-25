import java.awt.*;

import java.awt.event.*;
//import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Client {
	
	static final int LOGIN = 0;
	static final int LOBBY = 1;
	static final int GAME = 2;
	
	static int screenWidth;
	static int screenHeight;
	static int loginWidth;
	static int loginHeight;
	
	
	private static JFrame mainframe;
	private static String username;
	private static String password;
	private static int currstate;
	private static Dimension screenSize;
	
	private static JPanel states;
	private static JPanel loginpanel;
	private static JPanel lobbypanel;
	private static JPanel gamepanel;
	
	private static String LOGINSTATE = "Login Panel State";
	private static String LOBBYSTATE = "Lobby State";
	private static String GAMESTATE = "Game Room State";
	
	public static void main(String[] args) {
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenHeight = screenSize.height;
		screenWidth = screenSize.width;
		
		mainframe = new JFrame("Set Client");
		states = new JPanel(new CardLayout());
		mainframe.add(states);
		mainframe.setContentPane(states);
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		// mainframe.add(mainpanel); // or set as content pane?
		//mainframe.setLayout();
		
		//mainframe.setSize(screenSize);
		//mainframe.setPreferredSize(screenSize);
		
		mainframe.setResizable(false);
		mainframe.pack();
		mainframe.setVisible(true);
		
		createLobby();
		createLogin();
		
		switchState(LOGIN);
		mainframe.setSize(loginWidth, loginHeight);
		mainframe.setPreferredSize(new Dimension(loginWidth, loginHeight));
		mainframe.pack();
		
		mainframe.addWindowListener(new WindowAdapter() {
		 
	        public void windowClosing(WindowEvent e) {
	        	System.out.println("Exiting...\n");
	        	// Check if logout required
	        	System.exit(0);
	        }
		});
	}
	
	
	private static void createLogin() {

		loginpanel = new JPanel();
		TitledBorder logintitle;
		JTextField unameText = new JTextField(15);
		JPasswordField passwdText = new JPasswordField(15);
		JButton loginButton = new JButton("Login!");
		JButton registerButton = new JButton("Register");
		
		loginWidth = 200;
		loginHeight = 200;
		loginpanel.setSize(new Dimension(loginWidth, loginHeight));
		loginpanel.setPreferredSize(new Dimension(loginWidth, loginHeight));
		//mainframe.setLocation(new Point((screenWidth/2) - (loginWidth/2), (screenHeight/2) - (loginHeight/2)));
		//loginpanel.setOpaque(false);
		
		logintitle = BorderFactory.createTitledBorder("Login");
		logintitle.setTitleJustification(TitledBorder.CENTER);
		loginpanel.setBorder(logintitle);
		//loginpanel.setPreferredSize(new Dimension(300,100));
		// 6 rows, 1 columns, 2 horizontal gaps, 5 vertical gap
		loginpanel.setLayout(new GridLayout(6, 1, 2, 5));
		
		//loginpanel.setSize(100,400);
		//loginpanel.setLayout(new FlowLayout());
		
		loginpanel.add(new JLabel("Username:"));
		unameText.setEditable(true);
		loginpanel.add(unameText);
		loginpanel.add(new JLabel("Password:"));
		passwdText.setEditable(true);
		loginpanel.add(passwdText);
		
		
		loginButton.addActionListener(new ActionListener()
		{	
			public void actionPerformed(ActionEvent e)
			{
				username = unameText.getText();
				password = passwdText.getText();
				unameText.setText("");
				passwdText.setText("");
				int ret;
				if (username.length() > 0) {
					ret = DBUtils.signIn(username, password);
				}
				else {
					ret = DBUtils.signIn("bob", "123");	
				}
				
				switch(ret) {
				
					case 0: 
						System.out.println("Sign in successful\n");
						switchState(LOBBY);
						break;
					case 1:
						System.out.println("Username not found\n");
						break;
					case 2:
						System.out.println("Wrong password\n");
				
				}
				
				/*
				
				// display/center the jdialog when the button is pressed
				JDialog d = new JDialog(mainframe, "Login Results", true);
				d.setSize(200,200);
				JPanel p = new JPanel();
				String message = "<html>Username entered: " + username + "\n<br>Password: " + password + "\n<br></html>";
				p.add(new JLabel(message));
				p.setSize(500,500);
				d.add(p);
				d.setLocationRelativeTo(mainframe);
				d.setVisible(true);
				
				*/
				
			}
		});
		
		registerButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				  
				// display/center the jdialog when the button is pressed
				JDialog d = new JDialog(mainframe, "Hello", true);
				d.setLocationRelativeTo(mainframe);
				d.setVisible(true);
				
				DBUtils.signUp("bob", "123", "t@gmail.com");
				byte a = DBUtils.signIn("bob", "123");
				System.out.println(a);
				DBUtils.signOut("bob");
				
			}
		});
		
		loginpanel.add(loginButton);
		loginpanel.add(registerButton);
		
		states.add(loginpanel, LOGINSTATE);
	}
	
	private static void createLobby() {
		lobbypanel = new JPanel();
		lobbypanel.setSize(new Dimension(screenWidth, screenHeight));
		lobbypanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
		states.add(lobbypanel, LOBBYSTATE);
	}
	
	private static void switchState(int endState) {
		CardLayout cards = (CardLayout)(states.getLayout());
		
		switch (endState) {
			case LOGIN:
				currstate = LOGIN;
				cards.show(states, LOGINSTATE);
				mainframe.setPreferredSize(new Dimension(loginWidth, loginHeight));
				mainframe.setLocation(new Point((screenWidth/2) - (loginWidth/2), (screenHeight/2) - (loginHeight/2)));
				mainframe.pack();
				System.out.println("CHANGED TO LOGIN\n" + loginpanel.getHeight());
				break;
			
			case LOBBY: 
				currstate = LOBBY;
				cards.show(states, LOBBYSTATE);
				mainframe.setPreferredSize(screenSize);
				mainframe.setLocation(new Point(0,0));
				mainframe.pack();
				System.out.println("CHANGED TO LOBBY\n");
				break;
				
			case GAME:
				currstate = GAME;
				cards.show(states, GAMESTATE);
				mainframe.setPreferredSize(screenSize);
				mainframe.setLocation(new Point(0,0));
				mainframe.pack();
				System.out.println("CHANGED TO GAME\n");
				break;
		}
	}
}
