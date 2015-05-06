import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import java.util.*;
import javax.swing.border.BevelBorder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

public class GameGUI {
	//panels
	static JPanel mainpanel = new JPanel();
	static JPanel toppanel = new JPanel();
	static JPanel boardpanel  = new JPanel();
	static JPanel sidepanel = new JPanel();
	static JPanel chatpanel  = new JPanel();
	//panel components
	static Icon[] images = new ImageIcon[81];
	static JCheckBox[] checkBoxes = new JCheckBox[21];
	static int[] checked = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	static JLabel[] blanks = new JLabel[21];
	static JTable playertable = new JTable();
	static JLabel deck = new JLabel();
	//global dimensions
	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();;
	//static int screenWidth = screenSize.width;
	//static int screenHeight = screenSize.height;
	static int screenWidth = 1000;
    static int screenHeight = 500;
	//game number
	static int gameNum;
	static int decksize;
	//set_lock variables
	static boolean lock_set = false;
	static String setChain = "S:";
	static int counter = 0;
	//players
	String playername;
	static String[] players = new String[20];
	static int num_players;
	static HashMap < String, String > scoreboard = new HashMap< String, String>();
	//board
	static String board = "";
	static int k = 0;
	public GameGUI(int gameID){
		gameNum = gameID;
		add_player("Miraj");
		add_player("Abi");
		setup_toppanel();
		setup_boardpanel();
		setup_sidepanel();
		setup_chatpanel();
		update_board("45:18:0020:2201:0000:0000:1011:0000:0000:1100:0011:0000:0000:0000:0000:0000:0000:0000:0000:0000");
		displayBoard(board);
		displayScoreBoard();
		displayChat();
		epilogue();
	}
	//Server to GUI communication
	public static void set_acknowledged(){
		//the server calls this to indicate to the game clients that someone has declared set.
		lock_set = true;
	}
	public static void time_out(){
		//the server calls this to indicate that whoever called SET did not select three cards in time. 
		lock_set = false;
	}
	//GUI to server communication
	public static void declare_set(){
		//this is called when the set button is pressed
		if (!lock_set){ 
			//send server the SET request.
			System.out.println("SET!");
			lock_set = true;
		}
	}
	public static void check_the_box(int x){
		checked[x] = 1-checked[x];
	}
	public static void choose_three(){
		//this is called when the user has selected three cards after having clicked SET and before time-out.
		//send server the three cards.
		System.out.println("THREE CHOSEN");
	}
	public static void leave_room(){
		System.out.println("LEAVING ROOM");
	}
	public static void enter_chat(){
		System.out.println("ENTER CHAT");
	}
	public static void update_score(String player, String score){
		scoreboard.put(player, score);
	}
	public static void add_player(String player){
		players[num_players] = player;
		num_players++;
		scoreboard.put(player, "0");
	}
	public static void remove_player(String player){
		scoreboard.remove(player);
		int index = -1; 
		for (int i = 0; i < num_players; i++){
			if ((players[i] == player)){ 
				index = i;
			}
		}
		for (int i = index; i < num_players-1; i++){
			players[i] = players[i+1];
		}
		num_players--;
	}
	public static void update_board(String s){
		board = s;
	}
	public static void displayBoard(String s){
		//displays the cards on the board, given the board in the format deck_size:board_size:card1:card2:... and it will display.
		String temp = s;
		decksize = Integer.parseInt((temp.substring(0,temp.indexOf(":"))));
		deck.setText("Cards left in deck: " + decksize);
		sidepanel.add(deck, BorderLayout.NORTH);
		temp = temp.substring(temp.indexOf(":")+1);
		int board_size = Integer.parseInt((temp.substring(0,temp.indexOf(":"))));
		temp = temp.substring(temp.indexOf(":")+1);
		String[] cards = new String[board_size];
		for (int i = 0; i < board_size-1; i++){
			cards[i] = (temp.substring(0,Math.max(1,temp.indexOf(":"))));
			temp = temp.substring(temp.indexOf(":")+1);
		}
		cards[board_size-1] = temp;
		for (int i = 0; i < board_size; i++){
			int j = Character.getNumericValue(cards[i].charAt(0))*27+Character.getNumericValue(cards[i].charAt(1))*9+Character.getNumericValue(cards[i].charAt(2))*3+Character.getNumericValue(cards[i].charAt(3)*1);
			checkBoxes[i].setIcon(new CheckBoxIcon(checkBoxes[i], images[j], SwingConstants.CENTER, SwingConstants.TOP));	
			checkBoxes[i].setText("");
			boardpanel.add(checkBoxes[i]);
		}
		for (int i = board_size; i < 21; i++){
			//boardpanel.add(blanks[i]);
			boardpanel.add(blanks[i]);
		}
	}
	public static void displayScoreBoard(){
		sidepanel.add(new JScrollPane(playertable), BorderLayout.CENTER);
	}
	//GUI components
	//EVENTS: Section end
	public static void setup_toppanel(){
		JLabel gameID = new JLabel("Welcome to SET! You are in game room  " + gameNum);
		toppanel.add(gameID);
	}
	public static void setup_boardpanel(){
		boardpanel.setLayout(new GridLayout(3,7));
		deck.setPreferredSize(new Dimension(200, 100));
		for (k = 0; k < 21; k++){
			checked[k] = 0;
			checkBoxes[k] = new JCheckBox(Integer.toString(k+1));
			checkBoxes[k].setEnabled(true);
			checkBoxes[k].setPreferredSize(new Dimension(200,200));
			blanks[k] = new JLabel();
			checkBoxes[k].addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent e){
					int index = boardpanel.getComponentZOrder((Component) e.getSource());
					if (checkBoxes[index].isSelected()){
						checked[index] = 1;
					}else{
						checked[index] = 0;
					}
					int num_selected = 0;
					for (int m = 0; m < 21; m++){
						if (checked[m]==1){
							num_selected++;
						}
					}
					if ((num_selected == 3)&&(lock_set)){
						choose_three();
						lock_set = false;
					}
				}
			});
		}
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
	public static void setup_sidepanel(){
		sidepanel.setLayout(new BorderLayout());
		String columnNames[] = { "Player", "Score" };
		String dataValues[][] = new String[20][2];
		for (int i = 0; i < num_players; i++){
			String player = players[i];
			String score = scoreboard.get(player);
			dataValues[i][0] = player;
			dataValues[i][1] = score;
		}
		playertable = new JTable(dataValues,columnNames);
		JButton set_button = new JButton("SET!");
		set_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				declare_set();
			}
		});
		sidepanel.add(set_button, BorderLayout.SOUTH);
	}
	public static void setup_chatpanel(){
		JButton chat_button = new JButton("<html>Enter</html>");
		chat_button.setPreferredSize(new Dimension(75, 30));
		chat_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enter_chat();
			}
		});
		JButton leave_button = new JButton("<html>Leave<br>room</html>");
		leave_button.setPreferredSize(new Dimension(75,30));
		leave_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				leave_room();
			}
		});
		JTextField chat_text = new JTextField();
		JLabel chat_history = new JLabel("Chat history. BLAHBLAH BLAH. Chat history. BLAHBLAH BLAH.Chat history. BLAHBLAH BLAH.Chat history. BLAHBLAH BLAH.Chat history. BLAHBLAH BLAH. ");
		chat_history.setPreferredSize(new Dimension(screenWidth,100));
		chatpanel.setLayout(new BorderLayout());
		chatpanel.add(chat_button, BorderLayout.WEST);
		chatpanel.add(chat_text, BorderLayout.CENTER);
		chatpanel.add(new JScrollPane(chat_history), BorderLayout.NORTH );
		chatpanel.add(leave_button, BorderLayout.EAST);
	}
	public static void displayChat(){
	}
	public static void epilogue(){
		mainpanel.setLayout(new BorderLayout());
		mainpanel.add(toppanel,BorderLayout.NORTH);
		mainpanel.add(boardpanel, BorderLayout.WEST);
		mainpanel.add(sidepanel, BorderLayout.EAST);
		mainpanel.add(chatpanel, BorderLayout.SOUTH);
		mainpanel.setBackground(Color.WHITE);
		JFrame frame = new JFrame("SET GAME ROOM");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainpanel);
		frame.setSize(new Dimension(screenWidth, screenHeight)); 
		//frame.setBackground(Color.YELLOW);
		frame.pack();
		//frame.setResizable(false);
		frame.setVisible(true);
	}
}

/*
GameGUI to Server:
- Declaring SET
- Leaving room
- Entering chat

Server to GameGUI:
- Chat
- Start game?
- Player entered room
- Player left room
- Update board
- Player declared SET

When the user presses SET, the command is send to the server. As soon as the server receives the command, the server starts its timer. While
this server timer is not up, the user has to lock in a set. When the user has selected three cards, the user sends a query to the server.
If the server receives this query before time-out, it processes a valid declare-set request and replies accordingly. If the server doesn't receive
a reply before timeout, it sends an error. 


*/
