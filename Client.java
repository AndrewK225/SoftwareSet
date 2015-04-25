import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
	
	private static String address = "199.98.20.120";
	private static int port = 4445;
	
	private static JFrame mainframe;
	private static String username;
	private static String password;
	private static int currstate;
	private static Dimension screenSize;
	
	private static JPanel states;
	private static JPanel loginpanel;
	private static JPanel lobbypanel;
	private static JPanel gamepanel;
	
	private static String line = null;
	private static BufferedReader br = null;
	private static BufferedReader is = null;
	private static PrintWriter os = null;
	
	private static String LOGINSTATE = "Login Panel State";
	private static String LOBBYSTATE = "Lobby State";
	private static String GAMESTATE = "Game Room State";
	
	private static Socket clientSocket;
	
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
		
		try {
		    clientSocket = new Socket(address, port); // You can use static final constant PORT_NUM
		    br = new BufferedReader(new InputStreamReader(System.in));
		    is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    os = new PrintWriter(clientSocket.getOutputStream());
		}
		catch (IOException e){
		    e.printStackTrace();
		    System.err.print("IO Exception");
		}
		
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
		JButton registerButton = new JButton("Register?");
		
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
				username = unameText.getText();
				password = passwdText.getText();
				int ret;
				
				JTextField uname2Text = new JTextField(15);
				uname2Text.setText(username);
				JPasswordField passwd2Text = new JPasswordField(15);
				passwd2Text.setText(password);
				JTextField emailText = new JTextField(25);
				emailText.setText("");
				JButton regButton = new JButton("Register!");
				JButton canButton = new JButton("Cancel");
				
				JDialog d = new JDialog(mainframe, "Register for a new account?", true);
				d.setSize(500,200);
				JPanel p = new JPanel();
				p.setLayout(new GridLayout(5, 1, 2, 3));
				//p.add(new JLabel(""));
				//p.add(new JLabel(""));
				p.add(new JLabel("Username:"));
				unameText.setEditable(true);
				p.add(uname2Text);
				p.add(new JLabel("Password:"));
				passwdText.setEditable(true);
				p.add(passwd2Text);
				p.add(new JLabel("Email:"));
				emailText.setEditable(true);
				p.add(emailText);
				
				JLabel status1 = new JLabel("................................................................................");
				//p.add(new JLabel(""));
				p.add(status1);
				//status.setVisible(false);
				JLabel status2 = new JLabel("................................................................................");
				
				regButton.addActionListener(new ActionListener()
				{	
					public void actionPerformed(ActionEvent e)
					{
						System.out.println("Register button pressed\n");
						
						String email = emailText.getText();
						username = uname2Text.getText();
						password = passwd2Text.getText();
						if ((username.length() > 0) && (password.length() > 0) && (email.length() > 0)) {
							//status.setText("");
							//status.setVisible(false);
							
							DBUtils.signUp(username, password, email);
							
							line = "L:" + username + ":" + password + "\n";
							
							os.println(line);
			                os.flush();
			                
			                switchState(LOBBY);
						}
						else {
							System.out.println("Invalid registration info entered!\n");
							status1.setText("Invalid registration info!");
							status2.setText("");
							d.pack();
						}
					}
				});
				
				canButton.addActionListener(new ActionListener()
				{	
					public void actionPerformed(ActionEvent e)
					{
						d.dispose();
					}
				});
				
				p.add(status2);
				p.add(regButton);
				p.add(canButton);
				
				
				//String message = "<html>Username entered: " + username + "\n<br>Password: " + password + "\n<br></html>";
				//p.add(new JLabel(message));
				//p.setSize(500,500);
				d.add(p);
				d.setLocationRelativeTo(mainframe);
				d.setVisible(true);
				
				
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
				System.out.println("CHANGED TO LOGIN\n");
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
