import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Client {
	
	static final int LOGIN = 0;
	static final int LOBBY = 1;
	static final int GAME = 2;
	
	private static JFrame mainframe;
	private static String username;
	private static String password;
	private static int currstate;
	public static Dimension screenSize;
	
	private static JPanel states;
	private static JPanel loginpanel;
	private static JPanel lobbypanel;
	private static JPanel gamepanel;
	
	private static String LOGINSTATE = "Login Panel State";
	private static String LOBBYSTATE = "Lobby State";
	private static String GAMESTATE = "Game Room State";
	
	public static void main(String[] args) {
		mainframe = new JFrame("Set Client");
		states = new JPanel(new CardLayout());
		mainframe.add(states);
		mainframe.setContentPane(states);
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		// mainframe.add(mainpanel); // or set as content pane?
		//mainframe.setLayout();
		
		//mainframe.setSize(screenSize);
		//mainframe.setPreferredSize(screenSize);
		
		mainframe.setResizable(false);
		mainframe.pack();
		mainframe.setVisible(true);
		
		createLogin();
		createLobby();
		
		switchState(LOGIN);
		mainframe.pack();
	}
	
	
	private static void createLogin() {

		loginpanel = new JPanel();
		TitledBorder logintitle;
		JTextField unameText = new JTextField(15);
		JPasswordField passwdText = new JPasswordField(15);
		JButton loginButton = new JButton("Login!");
		JButton registerButton = new JButton("Register");
		
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		int panelWidth = 200;
		int panelHeight = 200;
		loginpanel.setSize(new Dimension(panelWidth, panelHeight));
		loginpanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
		//loginpanel.setLocation(new Point((screenWidth/2) - (panelWidth/2), (screenHeight/2) - (panelHeight/2)));
		mainframe.setLocation(new Point((screenWidth/2) - (panelWidth/2), (screenHeight/2) - (panelHeight/2)));
		loginpanel.setOpaque(false);
		
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
				
				DBUtils.signUp("bob", "123", "t@gmail.com");
				byte a = DBUtils.signIn("bob", "123");
				System.out.println(a);
				DBUtils.signOut("bob");
				
				
				switchState(LOBBY);
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
			}
		});
		
		loginpanel.add(loginButton);
		loginpanel.add(registerButton);
		
		states.add(loginpanel, LOGINSTATE);
		currstate = LOGIN;
	}
	
	private static void createLobby() {
		lobbypanel = new JPanel();
		states.add(lobbypanel, LOBBYSTATE);
	}
	
	private static void switchState(int endState) {
		CardLayout cards = (CardLayout)(states.getLayout());
		
		switch (endState) {
			case LOGIN:
				currstate = LOGIN;
				cards.show(states, LOGINSTATE);
				break;
			
			case LOBBY: 
				currstate = LOBBY;
				cards.show(states, LOBBYSTATE);
				break;
				
			case GAME:
				currstate = GAME;
				cards.show(states, GAMESTATE);
				break;
		}
	}
}
