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
	private static int numCards = 0;
	
	private static String commDelim = ":";
	
	static int screenWidth;
	static int screenHeight;
	static int loginWidth;
	static int loginHeight;
	
	//private static String address = "199.98.20.120"; // Sable's VM
	private static String address = "localhost";
	private static int port = 4445;
	
	private static JFrame mainframe;
	private static String username;
	private static String password;
	private static int currState;
	private static Dimension screenSize;
	
	private static JPanel states;
	private static JPanel loginpanel;
	private static JPanel lobbypanel;
	private static JPanel gamepanel3;
	private static JPanel gamepanel6;
	private static JPanel gamepanel9;
	private static JPanel gamepanel12;
	private static JPanel gamepanel15;
	private static JPanel gamepanel18;
	private static JPanel gamepanel21;
	
	private static String line = "";
	private static BufferedReader br = null;
	private static BufferedReader socketReader = null;
	private static PrintWriter socketWriter = null;
	
	private static JTextArea chatList = null;
	private static JTextArea activePlayersList = null; 
	private static JScrollPane activePlayersBox = null;
	
	private static String LOGINSTATE = "Login Panel State";
	private static String LOBBYSTATE = "Lobby State";
	private static String GAMESTATE3 = "Game Room State with 3 Cards";
	private static String GAMESTATE6 = "Game Room State with 6 Cards";
	private static String GAMESTATE9 = "Game Room State with 9 Cards";
	private static String GAMESTATE12 = "Game Room State with 12 Cards";
	private static String GAMESTATE15 = "Game Room State with 15 Cards";
	private static String GAMESTATE18 = "Game Room State with 18 Cards";
	private static String GAMESTATE21 = "Game Room State with 21 Cards";
	
	private static Socket clientSocket;
	
	public static void main(String[] args) {
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenHeight = screenSize.height;
		screenWidth = screenSize.width;
		
		mainframe = new JFrame("Set Client");
		states = new JPanel(new CardLayout());
		mainframe.add(states);
		//mainframe.setContentPane(states);
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
		
		int connectAttempt = 0;
		while (clientSocket == null) {
			try {
			    clientSocket = new Socket(address, port); // You can use static final constant PORT_NUM
			    br = new BufferedReader(new InputStreamReader(System.in));
			    socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			    socketWriter = new PrintWriter(clientSocket.getOutputStream());
			}
			
			catch (IOException e){
			    //e.printStackTrace();
				connectAttempt++;
			    if (connectAttempt == 1)
			    	System.err.print("IO Exception: Connecting to " + address + ":" +port + " failed\n");
			}
		}
		
		if (clientSocket != null) {
			switchState(LOGIN);
			mainframe.setSize(loginWidth, loginHeight);
			mainframe.setPreferredSize(new Dimension(loginWidth, loginHeight));
			mainframe.pack();
			
			mainframe.addWindowListener(new WindowAdapter() {
			 
		        public void windowClosing(WindowEvent e) {
		        	System.out.println("Exiting...\n");
		        	// Check if logout required
		        	sendMessage("X:Exiting");
		        	System.exit(0);
		        }
			});
			
			
			while (!line.equals("X:bye")) {
				try {
					line = socketReader.readLine();
					System.out.println("Server said: " + line);
					parseString(line);					
				}
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
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
		unameText.setDocument(new JTextFieldLimit(15));
		loginpanel.add(new JLabel("Password:"));
		passwdText.setEditable(true);
		passwdText.setDocument(new JTextFieldLimit(15));
		loginpanel.add(passwdText);
		
		
		loginButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				username = unameText.getText();
				password = passwdText.getText();
				unameText.setText("");
				passwdText.setText("");
				//int ret;
				
				String loginStr = "";
				if (username.length() > 0 && password.length() > 0) {
					//ret = DBUtils.signIn(username, password);
					loginStr = "LOGIN:" + username + ":" + password;
				}
				else {
					loginStr = "LOGIN:TaylorSwift:tswift";	
				}
				
				System.out.println("Attempting to login with: " + loginStr);
				sendMessage(loginStr);
				
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
				p.add(status1);
				JLabel status2 = new JLabel("................................................................................");
				
				regButton.addActionListener(new ActionListener()
				{	
					public void actionPerformed(ActionEvent e)
					{
						System.out.println("Register button pressed\n");
						
						String email = emailText.getText();
						username = uname2Text.getText();
						password = passwd2Text.getText();
						
						String regisStr = "";
						if ((username.length() > 0) && (password.length() > 0) && (email.length() > 0)) {
							
							regisStr = "REGISTER:" + username + ":" + password + ":" + email;
							sendMessage(regisStr);
							
			                //switchState(LOBBY);
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
		
	    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenHeight = screenSize.height;
		screenWidth = screenSize.width;
	    
		int chatBoxWidth = screenWidth;
		int chatBoxHeight = 200;
		int activePlayersBoxHeight = screenHeight - chatBoxHeight - 30;
		int gameBtnWidth = screenWidth/6;
	    int gameBtnHeight = 200;
	    int activePlayersBoxWidth = gameBtnWidth;
	    screenWidth = screenWidth - activePlayersBoxWidth;
		
	    lobbypanel.setPreferredSize(screenSize);
	    lobbypanel.setLayout(null);
	    
	    
	    JButton game1 = new JButton("Game1");
	    game1.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		// Tell server game 1 chosen...
	    	}
	    });
	    game1.setBounds((screenWidth/6) - (gameBtnWidth/2), 30, gameBtnWidth, gameBtnHeight);
	    lobbypanel.add(game1);
	    
	    
	    JButton game2 = new JButton("Game2");
	    game2.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		// Tell server game 2 chosen...
	    	}
	    });
	    game2.setBounds((3*screenWidth/6) - (gameBtnWidth/2), 30, gameBtnWidth, gameBtnHeight);
	    lobbypanel.add(game2);
	    
	    
	    JButton game3 = new JButton("Game3");
	    game3.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		// Tell server game 3 chosen...
	    	}
	    });
	    game3.setBounds((5*screenWidth/6) - (gameBtnWidth/2), 30, gameBtnWidth, gameBtnHeight);
	    lobbypanel.add(game3);
	    
	    activePlayersList = new JTextArea("Something really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\n");
	    activePlayersList.setLineWrap(true);
	    activePlayersList.setWrapStyleWord(true);
	    activePlayersList.setEditable(false);
	    //activePlayersList.setSize(activePlayersBoxWidth-30, activePlayersBoxHeight-30);
	    activePlayersList.setFont(new Font("Serif", Font.BOLD, 22));
	    
	    activePlayersBox = new JScrollPane(activePlayersList);
	    activePlayersBox.setSize(activePlayersBoxWidth, screenHeight - chatBoxHeight);
	    activePlayersBox.setBackground(Color.WHITE);
	    activePlayersBox.setBounds(screenSize.width - (activePlayersBoxWidth + 30), 30, activePlayersBoxWidth, activePlayersBoxHeight);
	    lobbypanel.add(activePlayersBox);
	    
	    
	    
	    chatList = new JTextArea("Something really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\n");
	    chatList.setLineWrap(true);
	    chatList.setWrapStyleWord(true);
	    chatList.setEditable(false);
	    //activePlayersList.setSize(activePlayersBoxWidth-30, activePlayersBoxHeight-30);
	    chatList.setFont(new Font("Serif", Font.PLAIN, 16));
	    
	    
	    JScrollPane chatBox = new JScrollPane(chatList);
	    chatBox.setSize(screenWidth, screenHeight - chatBoxHeight);
	    chatBox.setBackground(Color.WHITE);
	    chatBox.setBounds(gameBtnWidth/2, screenHeight - gameBtnHeight + 30, screenSize.width - gameBtnWidth, gameBtnHeight/2 + 30);
	    lobbypanel.add(chatBox);
	    
	    
	    states.add(lobbypanel, LOBBYSTATE);
	}
	
	private static void switchState(int endState) {
		CardLayout frames = (CardLayout)(states.getLayout());
		
		switch (endState) {
			case LOGIN:
				currState = LOGIN;
				frames.show(states, LOGINSTATE);
				mainframe.setPreferredSize(new Dimension(loginWidth, loginHeight));
				mainframe.setLocation(new Point((screenWidth/2) - (loginWidth/2), (screenHeight/2) - (loginHeight/2)));
				mainframe.pack();
				System.out.println("CHANGED TO LOGIN\n");
				break;
			
			case LOBBY: 
				currState = LOBBY;
				frames.show(states, LOBBYSTATE);
				mainframe.setPreferredSize(screenSize);
				mainframe.setLocation(new Point(0,0));
				mainframe.pack();
				System.out.println("CHANGED TO LOBBY\n");
				break;
				
			case GAME:
				currState = GAME;
				
				switch (numCards) {
					case 3:
						frames.show(states, GAMESTATE3);
						break;
					case 6:
						frames.show(states, GAMESTATE6);
						break;
					case 9:
						frames.show(states, GAMESTATE9);
						break;
					case 12:
						frames.show(states, GAMESTATE12);
						break;
					case 15:
						frames.show(states, GAMESTATE15);
						break;
					case 18:
						frames.show(states, GAMESTATE18);
						break;
					case 21:
						frames.show(states, GAMESTATE21);
						break;
				}
				mainframe.setPreferredSize(screenSize);
				mainframe.setLocation(new Point(0,0));
				mainframe.pack();
				System.out.println("CHANGED TO GAME STATE WITH " + Integer.toString(numCards) + " CARDS\n");
				break;
		}
	}
	
	private static void parseString(String serverLine) {
		String parts[] = line.split(commDelim);
		
		if ("L".equals(parts[0])) {
			// Login return info from server
			if (currState == LOGIN) {
				if ("0".equals(parts[1])) {
					System.out.println("Sign in successful\n");
					if (currState == LOGIN) {
						switchState(LOBBY);
					}
				}
				
				else if ("1".equals(parts[1])) {
					JDialog d = new JDialog(mainframe, "Login Failure", true);
					d.setSize(450,80);
					JPanel p = new JPanel();
					String message = "<html>Login attempt for Username: '" + username + "' failed - username not found!<br>Please try again.</html>";
					p.add(new JLabel(message));
					p.setSize(450,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
				
				else if ("2".equals(parts[1])) {
					JDialog d = new JDialog(mainframe, "Login Failure", true);
					d.setSize(450,80);
					JPanel p = new JPanel();
					String message = "<html>Login attempt for Username: '" + username + "' failed - wrong password!<br>Please try again.</html>";
					p.add(new JLabel(message));
					p.setSize(450,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
			}
		}
		
		if ("LBYACTIVE".equals(parts[0])) {
			System.out.println("Got active players list of:\n" + parts[1]);
			activePlayersList.setText(parts[1]);
			activePlayersBox.setViewportView(activePlayersList);
			//mainframe.pack();
		}
	}
	
	private static void sendMessage (String message) {
		if (socketWriter != null) {
    		socketWriter.println(message);
			socketWriter.flush();
    	}
	}
}
