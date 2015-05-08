import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;



import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import java.util.*;
import java.util.Timer;

public class Client {
	
	static final int LOGIN = 0;
	static final int LOBBY = 1;
	static final int GAME = 2;
	
	private static boolean canexit = false;
	private static int numCards = 0;
	
	private static String commDelim = ":";
	
	private static JPanel timer;
	private static String clientName = "";
	
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
	private static JDialog registerDialog;
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
	
	private static Timer setLockTimer = new Timer();
	
	// Game Room Stuff
	//panels
	public static JPanel mainpanel = new JPanel();
	public static JPanel boardpanel  = new JPanel();
	public static JPanel sidepanel = new JPanel();
	public static JPanel toppanel = new JPanel();
	public static JPanel chatpanel  = new JPanel();;
	public static JPanel chat_enterpanel = new JPanel();
	public static JPanel chat_textbox = new JPanel();
	public static JPanel chat_history = new JPanel();
	//panel components
	public static Icon[] images = new ImageIcon[81];
	public static JCheckBox[] checkBoxes = new JCheckBox[21];
	public static JLabel[] blanks = new JLabel[21];
	public static JTable playertable = new JTable();
	public static JLabel deck = new JLabel();
	//global dimensions
	public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();;
	//game number
	public static int gameNum = 0;
	public static JLabel gameID;
	public static JButton set_button;
	
	public static JTextArea game1Info;
	public static JTextArea game2Info;
	public static JTextArea game3Info;
	
	//set_lock variables
	public static boolean lock_set = false;
	public static String setChain = "";
	public static int counter = 0;
	//players
	public static int num_players;
	//public static String[][] players = new String[50][2];
	static int[] checked = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
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
		
		//mainframe.setResizable(false);
		mainframe.pack();
		mainframe.setVisible(true);
		createLobby();
		createLogin();
		createGameGUI();
		
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
		        	//try {
		        		sendMessage("X:Exiting");
		        		System.out.println("Exiting...\n");
		        		System.exit(0);
					//} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
					//	e1.printStackTrace();
					//}

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
					username = "TaylorSwift";
					loginStr = "LOGIN:TaylorSwift:tswift";	
				}
				
				System.out.println("Attempting to login with: " + loginStr);
				sendMessage(loginStr);
				password = "";
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
				
				registerDialog = new JDialog(mainframe, "Register for a new account?", true);
				registerDialog.setSize(500,200);
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
							password = "";
						}
						
						else {
							System.out.println("Invalid registration info entered!\n");
							status1.setText("Invalid registration info!");
							status2.setText("");
							registerDialog.pack();
						}
					}
				});
				
				canButton.addActionListener(new ActionListener()
				{	
					public void actionPerformed(ActionEvent e)
					{
						registerDialog.dispose();
					}
				});
				
				p.add(status2);
				p.add(regButton);
				p.add(canButton);
				
				//String message = "<html>Username entered: " + username + "\n<br>Password: " + password + "\n<br></html>";
				//p.add(new JLabel(message));
				//p.setSize(500,500);
				registerDialog.add(p);
				registerDialog.setLocationRelativeTo(mainframe);
				registerDialog.setVisible(true);
				
				
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
		Color lobbyColor = lobbypanel.getBackground();
		
	    screenHeight = 768;
		screenWidth = 1024;
	    int padding = 15;
		int gameBtnWidth = (screenWidth - 5*padding)/4;
	    int gameBtnHeight = gameBtnWidth;
	    
	    lobbypanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
	    lobbypanel.setLayout(null);
	    
	    
	    JButton game1 = new JButton("Game 1: Alpha");
	    game1.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		// Tell server game 1 chosen...
	    		System.out.println(e.getActionCommand() + " was pressed ");
	    		gameNum = 1;
	    		sendMessage("GAMECHOICE:1");
	    		switchState(GAME);
	    	}
	    });
	    //game1.setEnabled(true);
	    game1.setBounds(padding, padding, gameBtnWidth, gameBtnHeight);
	    game1.setFont(new Font("Serif", Font.BOLD, 30));
	    lobbypanel.add(game1);
	    
	    game1Info = new JTextArea("");
	    game1Info.setText("");
	    game1Info.setLineWrap(true);
	    game1Info.setWrapStyleWord(true);
	    game1Info.setEditable(false);
	    game1Info.setBounds(padding+25, padding + gameBtnHeight + 25, gameBtnWidth-50, gameBtnHeight/2);
	    game1Info.setFont(new Font("Serif", Font.ITALIC, 20));
	    game1Info.setBackground(lobbyColor);
	    lobbypanel.add(game1Info);
	    
	    JButton game2 = new JButton("Game 2: Beta");
	    game2.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		gameNum = 2;
	    		sendMessage("GAMECHOICE:2");
	    		switchState(GAME);
	    	}
	    });
	    game2.setBounds(gameBtnWidth + 2*padding, padding, gameBtnWidth, gameBtnHeight);
	    game2.setFont(new Font("Serif", Font.BOLD, 30));
	    lobbypanel.add(game2);
	    
	    game2Info = new JTextArea("");
	    game2Info.setText("");
	    game2Info.setLineWrap(true);
	    game2Info.setWrapStyleWord(true);
	    game2Info.setEditable(false);
	    game2Info.setBounds(gameBtnWidth + 2*padding +25, padding + gameBtnHeight + 25, gameBtnWidth-50, gameBtnHeight/2);
	    game2Info.setFont(new Font("Serif", Font.ITALIC, 20));
	    game2Info.setBackground(lobbyColor);
	    lobbypanel.add(game2Info);
	    
	    JButton game3 = new JButton("Game 3: Chi");
	    game3.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		gameNum = 3;
	    		sendMessage("GAMECHOICE:3");
	    		switchState(GAME);
	    	}
	    });
	    game3.setBounds(2*gameBtnWidth + 3*padding, padding, gameBtnWidth, gameBtnHeight);
	    game3.setFont(new Font("Serif", Font.BOLD, 30));
	    lobbypanel.add(game3);
	    
	    game3Info = new JTextArea("");
	    game3Info.setText("");
	    game3Info.setLineWrap(true);
	    game3Info.setWrapStyleWord(true);
	    game3Info.setEditable(false);
	    game3Info.setBounds(2*gameBtnWidth + 3*padding +25, padding + gameBtnHeight + 25, gameBtnWidth-50, gameBtnHeight/2);
	    game3Info.setFont(new Font("Serif", Font.ITALIC, 20));
	    game3Info.setBackground(lobbyColor);
	    lobbypanel.add(game3Info);
	    
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
	    
	    
	    chatList = new JTextArea("Welcome to the lobby!\n");
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
	
	private static void createGameGUI() {
		mainpanel.setLayout(new BorderLayout());
		GridLayout cardGridLayout = new GridLayout(3,7, 0, 0);
		boardpanel.setLayout(cardGridLayout);
		create_components();
		mainpanel.add(toppanel, BorderLayout.NORTH);
		mainpanel.add(boardpanel, BorderLayout.CENTER);
		mainpanel.add(sidepanel, BorderLayout.EAST);
		mainpanel.add(chatpanel, BorderLayout.SOUTH);
		mainpanel.setBackground(Color.WHITE);
		states.add(mainpanel, GAMESTATE);
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
				gameID.setText("Welcome to Game Room " + gameNum);
				//theGamePanel.setPreferredSize(new Dimension(1024, 768));
				mainframe.setPreferredSize(new Dimension(1300, 768));
				mainframe.setLocation(new Point(0,0));
				mainframe.pack();
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
					d.setSize(500,80);
					JPanel p = new JPanel();
					String message = "<html>Login attempt for Username: '" + username + "' failed - username not found!<br>Please try again.</html>";
					p.add(new JLabel(message));
					p.setSize(500,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
				
				else if ("2".equals(parts[1])) {
					JDialog d = new JDialog(mainframe, "Login Failure", true);
					d.setSize(500,80);
					JPanel p = new JPanel();
					String message = "<html>Login attempt for Username: '" + username + "' failed - wrong password!<br>Please try again.</html>";
					p.add(new JLabel(message));
					p.setSize(500,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
				
				else if ("-1".equals(parts[1])){
					JDialog d = new JDialog(mainframe, "Login Failure", true);
					d.setSize(500,80);
					JPanel p = new JPanel();
					String message = "<html>Login attempt for Username: '" + username + "' failed - account is already logged in!<br>Please use a different account.</html>";
					p.add(new JLabel(message));
					p.setSize(500,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
				
				else {
					JDialog d = new JDialog(mainframe, "Login Failure", true);
					d.setSize(500,80);
					JPanel p = new JPanel();
					String message = "<html>Login attempt for Username: '" + username + "' failed - database malfunctioned!<br>Please blame Andrew Koe.</html>";
					p.add(new JLabel(message));
					p.setSize(500,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
			}
		}
		
		else if ("REGISTER".equals(parts[0])) {
			if (currState == LOGIN) {
				if ("0".equals(parts[1])) {
					/*
					System.out.println("Sign up successful\n");
					if (currState == LOGIN) {
						switchState(LOBBY);
					}
					*/
					registerDialog.dispose();
					JDialog d = new JDialog(mainframe, "Registration Successful!", true);
					d.setSize(500,80);
					JPanel p = new JPanel();
					String message = "<html>Registration succeeded for Username: '" + username + "'. Please use it to login.</html>";
					p.add(new JLabel(message));
					p.setSize(500,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
				
				else if ("1".equals(parts[1])) {
					JDialog d = new JDialog(mainframe, "Registration Failure", true);
					d.setSize(500,80);
					JPanel p = new JPanel();
					String message = "<html>Registration attempt for Username: '" + username + "' failed - username already registered!<br>Please try again.</html>";
					p.add(new JLabel(message));
					p.setSize(500,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
				
				else {
					JDialog d = new JDialog(mainframe, "Registration Failure", true);
					d.setSize(500,80);
					JPanel p = new JPanel();
					String message = "<html>Registration attempt for Username: '" + username + "' failed - database malfunctioned!<br>Please blame Andrew Koe.</html>";
					p.add(new JLabel(message));
					p.setSize(500,80);
					d.add(p);
					d.setLocationRelativeTo(mainframe);
					d.setVisible(true);
				}
			}
		}
		
		else if ("LBYACTIVE".equals(parts[0])) {
			System.out.println("Got active players list.");
			
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
		
		else if ("LOBBYINFO".equals(parts[0])) {
			System.out.println("Got Lobby Info string");
			// Something like the following:
			//LOBBYINFO:Game1:0 players with 69 cards left in the deck.:
			//Game2:0 players with 69 cards left in the deck.:Game3:1 players with 69 cards left in the deck.
			String game1InfoStr = parts[2];
			String game2InfoStr = parts[4];
			String game3InfoStr = parts[6];
			
			game1Info.setText(game1InfoStr);
			game2Info.setText(game2InfoStr);
			game3Info.setText(game3InfoStr);
		}
		
		// Incoming chat is like "CHAT: username - sometext"
		else if ("CHAT".equals(parts[0])) {
			chatList.setText(parts[1]);
			chatBox.setViewportView(chatList);
			chatVertBar = chatBox.getVerticalScrollBar();
		    chatVertBar.setValue( chatVertBar.getMaximum() );
			//mainframe.pack();
		}
		
		else if ("UPDATECARDS".equals(parts[0])) {
			System.out.println("Got update cards request...");
			int affectedGame = Integer.parseInt(parts[1]);
			if (affectedGame == gameNum) {
				String updatedCardStr = "";
				
				int i;
				// Need to append back the parts with the ":"
				for (i = 2; i < Array.getLength(parts)-1; i++) {
					updatedCardStr = updatedCardStr + parts[i] + ":";
				}
				for (i = Array.getLength(parts)-1; i < Array.getLength(parts); i++) {
					updatedCardStr = updatedCardStr + parts[i];
				}
				
				update_board(updatedCardStr);
			}
		}
		
		else if ("SETLOCKED".equals(parts[0])) {
			int gameAffected = Integer.parseInt(parts[1]);
			String playerWithLock = parts[2];
			if (gameAffected == gameNum) {
				System.out.println("Set lock is claimed by: " + playerWithLock);
				if (playerWithLock.equals(username)) {
					// You got the lock, so enable all the buttons, and start timer
					obtainedSetLock();
				}
				else
					someoneElseLocked();
			}
		}
		
		else if ("SCORES".equals(parts[0])) {
			System.out.println("Received scores: " + line);
			int gameAffected = Integer.parseInt(parts[1]);
			if (gameAffected == gameNum) {
				DefaultTableModel model = (DefaultTableModel) playertable.getModel();
				model.setRowCount(0);
				for (int i = 2; i < parts.length; i=i+2) {
					Object[] row = { parts[i], parts[i+1] };
					model.addRow(row);
				}
			}
		}
	}
	
	private static void sendMessage (String message) {
		if (socketWriter != null) {
    		socketWriter.println(message);
			socketWriter.flush();
    	}
	}
	
	public static void create_components(){
		create_images_checkboxes();
		sidepanel.setLayout(new BorderLayout());
		sidepanel.setPreferredSize(new Dimension(150,400));
		toppanel.setLayout(new BorderLayout());		
		// Create the top panel that displays the game number
		gameID = new JLabel("Welcome to Game Room " + gameNum);
		toppanel.add(gameID, BorderLayout.WEST );
		// Create the set button
		set_button = new JButton("SET!");
		set_button.setPreferredSize(new Dimension(75, 25));
		
		set_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Upon clicking set, ask for the set lock so player can select 3 cards
				sendMessage("SETLOCKREQ:" + gameNum + ":" + username);
				
				/*
				lock_set = true;
				for ( int i = 0; i < 21; i++){
					checkBoxes[i].setEnabled(true);
				}
				*/
				
			}
		});
		set_button.setBackground(Color.YELLOW);
		sidepanel.add(set_button,BorderLayout.NORTH);
		
		
		// Display scoreboard/list of players with sets
		String columnNames[] = { "Player", "Score" };
		Object[][] data = {};
		//String dataValues[][] = { {"TaylorSwift","0"},{"katyperry","0"}};
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		playertable = new JTable(model);
		playertable.getColumnModel().getColumn(0).setMinWidth(90);
		playertable.getColumnModel().getColumn(1).setMinWidth(50);
		
		sidepanel.add(new JScrollPane(playertable),BorderLayout.CENTER);
		// Create leave room button to force player back to lobby
		JButton leave_button = new JButton("<html>Leave<br>room</html>");
		leave_button.setPreferredSize(new Dimension(75, 50));
		leave_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				leave_room();
			}
		});
		sidepanel.add(leave_button,BorderLayout.SOUTH);
	}
	
	public static void leave_room(){
		sendMessage("LEAVEROOM:" + gameNum);
		gameNum = 0;
		switchState(LOBBY);
	}
	
	public static void create_images_checkboxes(){
		for (int i = 0; i < 21; i++){
			checkBoxes[i] = new JCheckBox(Integer.toString(i+1));
			checkBoxes[i].setPreferredSize(new Dimension(200,200));
			checkBoxes[i].setName(Integer.toString(i));;
			checkBoxes[i].setText("");
			
			// Event listener on each card/checkbox that registers itself being clicked
			checkBoxes[i].addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent e) {
						int index = boardpanel.getComponentZOrder((Component) e.getSource());
						if (checkBoxes[index].isSelected()){
							checked[index] = 1;
							counter++;
						} 
						else{
							checked[index] = 0;
							if (counter > 0)
								counter--;
						}
						
						System.out.println("Now checked (" + index + ") = " + checked[index] + "counter= " + counter);
						if ((counter == 3)&&(lock_set)){
							setChain = "";
							counter = 0;
							for (int m = 0; m < 21; m++){
								if (checked[m]==1){
									checkBoxes[m].setSelected(false);
									checked[m] = 0;
									setChain += (":" + m);
								}
							}

							System.out.println("CLAIMSET:" + gameNum + setChain);
							sendMessage("CLAIMSET:" + gameNum + setChain);
							lock_set = false;
							for ( int i = 0; i < 21; i++){
								checkBoxes[i].setEnabled(false);
							}
						}
				}
			});
		}
		
		// Preload the 81 cards' images
		for (int i = 0; i < 81; i++){
			String four_digits = "";
			int temp = i;
			for (int j = 0; j < 4; j++){
				four_digits = (temp%3) + four_digits;
				temp = temp/3;
			}
			images[i] = new ImageIcon(".//src//setpics//" + four_digits + ".gif");
		}
	}
		
	public static void obtainedSetLock() {
		System.out.println("Obtained the set lock.");
		lock_set = true;
		set_button.setBackground(Color.GREEN);
		setLockTimer = new Timer();
		setLockTimer.schedule(new ReleaseSetLock(), 5000);
		
		for ( int i = 0; i < 21; i++){
				checkBoxes[i].setEnabled(true);
		}
	}
	
	public static void someoneElseLocked() {
		System.out.println("Someone else obtained the set lock.");
		lock_set = false;
		set_button.setBackground(Color.RED);
		setLockTimer = new Timer();
		setLockTimer.schedule(new ReleaseSetLock(), 5000);
		
		for ( int i = 0; i < 21; i++){
			checkBoxes[i].setEnabled(false);
			checkBoxes[i].setVisible(true);
		}
	}
	
	public static void update_board(String newCardsStr){
		//displays the cards on the board, given the board in the format deck_size:numcardsOnBoard:card1:card2:... and it will display.
		// Remove the previous cards
		System.out.println("Removing a total of " + numCards + " old cards from the board.");
		for (int i = 0; i < numCards; i++) {
			boardpanel.remove(checkBoxes[i]);
		}
		
		System.out.println("Updating board with: " + newCardsStr);
		int deck_size = Integer.parseInt((newCardsStr.substring(0,newCardsStr.indexOf(":"))));
		deck.setText("Cards left in deck: " + deck_size);
		toppanel.add(deck,BorderLayout.EAST);
		
		newCardsStr = newCardsStr.substring(newCardsStr.indexOf(":")+1);
		int board_size = Integer.parseInt((newCardsStr.substring(0,newCardsStr.indexOf(":"))));
		
		System.out.println("board_size = " + board_size);
		
		newCardsStr = newCardsStr.substring(newCardsStr.indexOf(":")+1);
		String[] cards = new String[board_size];
		
		GridLayout cardGridLayout = new GridLayout(3,(board_size/3), 0, 0);
		boardpanel.setLayout(cardGridLayout);
		mainframe.pack();
		
		for (int i = 0; i < board_size-1; i++){
			cards[i] = (newCardsStr.substring(0,Math.max(1,newCardsStr.indexOf(":"))));
			newCardsStr = newCardsStr.substring(newCardsStr.indexOf(":")+1);
		}
		cards[board_size-1] = newCardsStr;
		
		numCards = board_size;
		System.out.println("Adding a total of " + numCards + " new cards to the board.");
		for (int i = 0; i < board_size; i++){
			int j = Character.getNumericValue(cards[i].charAt(0))*27+Character.getNumericValue(cards[i].charAt(1))*9+Character.getNumericValue(cards[i].charAt(2))*3+Character.getNumericValue(cards[i].charAt(3)*1);
			checkBoxes[i].setIcon(new CheckBoxIcon(checkBoxes[i], images[j], SwingConstants.CENTER, SwingConstants.TOP));	
			checkBoxes[i].setEnabled(false);
			checkBoxes[i].setVisible(true);
			boardpanel.add(checkBoxes[i]);
		}
		
		/*
		for (int i = board_size; i < 21; i++){
			checkBoxes[i].setVisible(false);	
		}
		*/
	}
	
	
	/*
	public static void add_player(String player){
		players[num_players][0] = player;
		players[num_players][1] = "0";
		num_players++;
	}
	
	public static void update_score(String player,String score){
		int found_player = 0;
		for (int i = 0; (i<num_players)&&(found_player==0); i++){
			if (players[i][0] == player){
				players[i][1] = score;
				found_player = 1;
			}
		}
		if (found_player == 0){
			add_player(player);
		}
	}
	public static void remove_player(String player){
		int index = -1;
		for (int i = 0; (i < num_players)&&(index == -1); i++){
			if (players[i][0] == player){
				index = i;
			}
		}
		for (int i = index; i < num_players - 1; i++){
			players[i][0] = players[i+1][0];
			players[i][1] = players[i+1][1];
		}
		players[num_players][0] = null;
		players[num_players][1] = null;
		num_players--;
	}
	*/
	
	
	private static class ReleaseSetLock extends TimerTask {
        public void run() {
            lock_set = false;
        	System.out.format("Time's up!%n");
            
            for ( int i = 0; i < 21; i++){
            	checkBoxes[i].setSelected(false);
    			checkBoxes[i].setEnabled(false);
    			checkBoxes[i].setVisible(true);
    		}
            counter = 0;
            setLockTimer.cancel(); //Terminate the timer thread
            set_button.setBackground(Color.YELLOW);
        }
    }
}

/*
- I changed it so that the checkboxes are disabled unless the user declares a set. This should make it less confusing.
- I fixed leave room on the client side, it still needs to be changed on the server side.
 
In the Game room, the Game number at the top is not displaying correctly. I'm not sure what's wrong but I WILL FIX THIS DON'T WORRY ABOUT IT.
 
For updating player info in the playertable:
- When player logins to game, the server needs to update its own player info and then broadcast it to all clients. The client function to be called is add_player(player).
- When player leaves game, the server should update its own player info and then broadcast it to all clients. The client function to be called is remove_player(player).
- When player correctly finds a set, the server should update the score, and broadcast the new score  to all clients. The client should call update_score(player,score).
- When updating the score, the server needs to subtract 1 point for an incorrect set.

For game over:
- go to the server class, search "GAME OVER" and look for my comments and replace them with actual code.

- For the set lock:
- When user Abi declares set (but did not yet select three cards), he sends the request to server, who upon receipt of the request, starts a 5 second timer.
- While the 5 second timer is active, the server's set_lock variable is true, and no other requests will be accepted.
- When the user Abi selects three cards, another request is sent to the server. If this is sent within the 5 second timer, the server processes the set declaration
The server then turns set_lock off and turns its timer off. The server tells all clients whether the set was right or wrong and updates scores etc.
- If the 5 second timer is done and the person who declared did not choose 3 cards,subtract a point from the culprit player, turn set_lock off and inform all clients. 
The players are then free to try again to find and declare a set.
- A different set_lock variable and set_lock timer must be kept for each game. Two different game rooms cannot use the same timer.

- The final testing of the game should do the following:
- Server is hosted and running on VM
- Multiple players register with their own names on their own machines and then login to a game and play a normal game of set. When the game is over, the winner is 
displayed and the game is reset.
- If a player declares a set, make sure the change in score and the cards is visible to all other players.
- If a player leaves the room, make sure the change is visible to all other players.
- If a player leaves the room and then rejoins, make sure the change is visible to all other players, and that the player's score is 0.

-
*MP*
- Need to display winner and restart the game
*/