import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.util.*;
import java.util.Timer;

public class Client {
	
	static final int LOGIN = 0;
	static final int LOBBY = 1;
	static final int GAME = 2;
	
	private static boolean canexit = false;
	private static int numCards = 0;
	
	private static String commDelim = ":";
	
	private static Timer timer = new Timer();
	
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
	private static JScrollBar chatVertBar;
	private static JScrollBar playersVertBar;
	
	private static JPanel states;
	private static JPanel loginpanel;
	private static JPanel lobbypanel;
	private static JPanel gamepanel;
	
	private static String line = "";
	private static BufferedReader socketReader = null;
	private static PrintWriter socketWriter = null;
	
	private static JTextArea chatList = null;
	private static JTextArea activePlayersList = null; 
	private static JScrollPane activePlayersBox = null;
	private static JScrollPane chatBox = null;
	
	private static String LOGINSTATE = "Login Panel State";
	private static String LOBBYSTATE = "Lobby State";
	private static String GAMESTATE = "Game Room State";
	
	private static Socket clientSocket;
	
	public static void main(String[] args) {
		screenHeight = 768;
		screenWidth = 1024;
		
		mainframe = new JFrame("Set Client");
		states = new JPanel(new CardLayout());
		mainframe.add(states);
		//mainframe.setContentPane(states);
		mainframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
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
		        	// Check if logout required
		        	try {
		        		sendMessage("X:Exiting");
		        		System.out.println("Exiting...\n");
		        		Thread.sleep(500);
		        		System.exit(0);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

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
		
	    screenHeight = 768;
		screenWidth = 1024;
	    int padding = 15;
		int gameBtnWidth = (screenWidth - 5*padding)/4;
	    int gameBtnHeight = gameBtnWidth;
	    
	    lobbypanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
	    lobbypanel.setLayout(null);
	    
	    
	    JButton game1 = new JButton("Game1");
	    game1.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		// Tell server game 1 chosen...
	    		System.out.println(e.getActionCommand() + " was pressed ");
	    		sendMessage("GAMECHOICE:1");
	    	}
	    });
	    //game1.setEnabled(true);
	    game1.setBounds(padding, padding, gameBtnWidth, gameBtnHeight);
	    
	    lobbypanel.add(game1);
	    
	    
	    JButton game2 = new JButton("Game2");
	    game2.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		sendMessage("GAMECHOICE:2");
	    	}
	    });
	    game2.setBounds(gameBtnWidth + 2*padding, padding, gameBtnWidth, gameBtnHeight);
	    lobbypanel.add(game2);
	    
	    
	    JButton game3 = new JButton("Game3");
	    game3.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		sendMessage("GAMECHOICE:3");
	    	}
	    });
	    game3.setBounds(2*gameBtnWidth + 3*padding, padding, gameBtnWidth, gameBtnHeight);
	    lobbypanel.add(game3);
	    
	    activePlayersList = new JTextArea("Something really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\nSomething really long I don't know how long... Does this scroll? If so that'd be great - please God scroll over to the next line =(\n");
	    activePlayersList.setLineWrap(true);
	    activePlayersList.setWrapStyleWord(true);
	    activePlayersList.setEditable(false);
	    //activePlayersList.setSize(activePlayersBoxWidth-30, activePlayersBoxHeight-30);
	    activePlayersList.setFont(new Font("Serif", Font.BOLD, 20));
	    
	    activePlayersBox = new JScrollPane(activePlayersList);
	    activePlayersBox.setSize(gameBtnWidth, screenHeight - gameBtnWidth - 3*padding);
	    activePlayersBox.setBackground(Color.WHITE);
	    activePlayersBox.setBounds(3*gameBtnWidth + 4*padding, padding, gameBtnWidth, screenHeight - gameBtnWidth - 3*padding);
	    playersVertBar = activePlayersBox.getVerticalScrollBar();
	    playersVertBar.setValue( playersVertBar.getMaximum() );
	    lobbypanel.add(activePlayersBox);
	    
	    
	    chatList = new JTextArea("Some chat in the chatbox\n");
	    chatList.setLineWrap(true);
	    chatList.setWrapStyleWord(true);
	    chatList.setEditable(false);
	    //activePlayersList.setSize(activePlayersBoxWidth-30, activePlayersBoxHeight-30);
	    chatList.setFont(new Font("Serif", Font.PLAIN, 16));
	    
	    
	    chatBox = new JScrollPane(chatList);
	    chatBox.setSize(screenWidth, screenHeight - gameBtnWidth - 100);
	    chatBox.setBackground(Color.WHITE);
	    chatBox.setBounds(padding, screenHeight - gameBtnHeight - 2*padding, screenWidth - 2*padding, gameBtnHeight-50);
	    chatVertBar = chatBox.getVerticalScrollBar();
	    chatVertBar.setValue( chatVertBar.getMaximum() );
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
				mainframe.setPreferredSize(new Dimension(1024, 768));
				mainframe.setLocation(new Point(0,0));
				mainframe.pack();
				System.out.println("CHANGED TO LOBBY\n");
				break;
				
			case GAME:
				currState = GAME;
				frames.show(states, GAMESTATE);
				mainframe.setPreferredSize(new Dimension(1024, 768));
				mainframe.setLocation(new Point(0,0));
				mainframe.pack();
				System.out.println("CHANGED TO GAME STATE WITH " + Integer.toString(numCards) + " CARDS\n");
				break;
		}
	}
	
	private static void parseString(String serverLine) {
		String parts[] = line.split(commDelim);
		
		if ("LOGIN".equals(parts[0])) {
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
				
				else {
					JDialog d = new JDialog(mainframe, "Login Failure", true);
					d.setSize(450,80);
					JPanel p = new JPanel();
					String message = "<html>Login attempt for Username: '" + username + "' failed - database malfunctioned!<br>Please blame Andrew Koe.</html>";
					p.add(new JLabel(message));
					p.setSize(450,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
			}
		}
		
		else if ("REGISTER".equals(parts[0])) {
			if (currState == LOGIN) {
				if ("0".equals(parts[1])) {
					System.out.println("Sign in successful\n");
					if (currState == LOGIN) {
						switchState(LOBBY);
					}
				}
				
				else if ("1".equals(parts[1])) {
					JDialog d = new JDialog(mainframe, "Registration Failure", true);
					d.setSize(450,80);
					JPanel p = new JPanel();
					String message = "<html>Registration attempt for Username: '" + username + "' failed - username already registered!<br>Please try again.</html>";
					p.add(new JLabel(message));
					p.setSize(450,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
				
				else {
					JDialog d = new JDialog(mainframe, "Registration Failure", true);
					d.setSize(450,80);
					JPanel p = new JPanel();
					String message = "<html>Registration attempt for Username: '" + username + "' failed - database malfunctioned!<br>Please blame Andrew Koe.</html>";
					p.add(new JLabel(message));
					p.setSize(450,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
			}
		}
		
		else if ("LBYACTIVE".equals(parts[0])) {
			//System.out.println("Got active players list of:\n" + parts[1]);
			
			if (currState == LOBBY) {
				parts[1] = parts[1].replaceAll("\\\\n", "\\\n");
				//System.out.println("After replace, list is:\n" + parts[1]);
				activePlayersList.setText(parts[1]);
				activePlayersBox.setViewportView(activePlayersList);
				playersVertBar = activePlayersBox.getVerticalScrollBar();
			    playersVertBar.setValue( playersVertBar.getMaximum() );
				//mainframe.pack();
			}
		}
		
		// Incoming chat is like "CHAT: username - sometext"
		else if ("CHAT".equals(parts[0])) {
			chatList.setText(parts[1]);
			chatBox.setViewportView(chatList);
			chatVertBar = chatBox.getVerticalScrollBar();
		    chatVertBar.setValue( chatVertBar.getMaximum() );
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
