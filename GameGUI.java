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
	static JPanel boardpanel  = new JPanel();
	static JPanel sidepanel = new JPanel();
	//static JPanel playerpanel  = new JPanel();;
	static JPanel chatpanel  = new JPanel();;
	//static JPanel setbuttonpanel  = new JPanel();;
	//static JPanel deckpanel  = new JPanel();;
	static JPanel timer  = new JPanel();
	//static JPanel leavebuttonpanel  = new JPanel();
	static JPanel chat_enterpanel = new JPanel();
	static JPanel chat_textbox = new JPanel();
	static JPanel chat_history = new JPanel();
	//panel components
	static Icon[] images = new ImageIcon[81];
	static JCheckBox[] checkBoxes = new JCheckBox[21];
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
	//set_lock variables
	static boolean lock_set = false;
	static String setChain = "S:";
	static int counter = 0;
	//players
	String playername;
	static String[] players = {};
	static int num_players;
	static HashMap < String, Integer > scoreboard;
	//board
	static String board = "";
	public static void main(String[] args) {
		gameNum = 11;
		setup();
		create_components();
		update_board("45:21:0020:0101:0201:0000:2201:0000:1000:0111:0000:0000:1100:0011:0000:0000:0000:0000:0000:0000:0000:0000:0000");
		displayBoard(board);
		displayChat();
		epilogue();
	}
	public static void declare_set(){
		System.out.println("SET!");
	}
	public static void leave_room(){
		System.out.println("LEAVING ROOM");
	}
	public static void enter_chat(){
		System.out.println("ENTER CHAT");
	}
	public static void update_score(String player, String score){
		scoreboard.put(player, Integer.parseInt(score));
	}
	public static void add_player(String player){
		scoreboard.put(player, 0);
	}
	public static void remove_player(String player){
		scoreboard.remove(player);
	}
	public static void update_board(String s){
		board = s;
	}
	
	public static void create_components(){
		create_images_checkboxes();
		create_game_number();
		create_set_button();
		displayPlayers();
		create_leave_button();
		//create_chat();
	}

	public static void create_images_checkboxes(){
		deck.setPreferredSize(new Dimension(200, 100));
		for (int i = 0; i < 21; i++){
			checkBoxes[i] = new JCheckBox(Integer.toString(i+1));
			checkBoxes[i].setEnabled(true);
			checkBoxes[i].setPreferredSize(new Dimension(200,200));
			blanks[i] = new JLabel();
			checkBoxes[i].addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent e){
					AbstractButton abstractButton = (AbstractButton) e.getSource();
					boolean selected = abstractButton.isSelected();
					System.out.println("Is selected = " + selected);
					System.out.println(e.getActionCommand() + " was pressed");
					setChain = setChain + e.getActionCommand() + ":";
					//kind of a kluge to detect if the box was being checked or unchecked
					if("S:"!=setChain) {  //if something has already been selected it would be in the string
						//System.out.println("in the if");
						String[] parts = setChain.split(":"); //so know which cards have been selected
						if(e.getActionCommand().equals(parts[1])|| e.getActionCommand().equals(parts[parts.length-1])) { //if the card if pressed again then user wanted it unchecked
							//System.out.println(setChain);
							setChain = setChain.replace(e.getActionCommand() + ":", "");
							System.out.println(e.getActionCommand()+" was unchecked");
						}
					}
					System.out.println(e.getSource());
					counter++;
					if(counter == 3){
						check_set();						
					}
					System.out.println(setChain);
				}
			});
		}
		for (int i = 0; i < 81; i++){
			String four_digits = "";
			int temp = i;
			for (int j = 0; j < 4; j++){
				four_digits = (temp%3) + four_digits;
				temp = i/3;
			}
			images[i] = new ImageIcon(".//src//setpics//" + four_digits + ".gif");
		}
	}
	
	
	public static void create_game_number() {
		JTextField gameID = new JTextField("Game " + gameNum);
		sidepanel.add(gameID, "1, 1, center, center");
	}
	
	public static void create_set_button(){
		JButton set_button = new JButton("SET!");
		set_button.setPreferredSize(new Dimension(75, 25));
		set_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				declare_set();
			}
		});
		//setbuttonpanel.add(set_button);
		
		//GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.HORIZONTAL;
		//c.ipady = 0;      //make this component tall
		//c.weightx = 0.0;
		//c.gridwidth = 0;
		//c.gridx = 0;
		//c.gridy = 1;
		sidepanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("200px"),},
			new RowSpec[] {
				RowSpec.decode("100px"),
				RowSpec.decode("100px"),
				RowSpec.decode("100px"),
				RowSpec.decode("100px"),}));
		
		sidepanel.add(set_button, "1, 1, center, center");
	}
	
	public static void displayPlayers(){
		String columnNames[] = { "Player", "Score" };
		String dataValues[][] = { {"Miraj","10"},{"Abi","45"}};
		playertable = new JTable(dataValues,columnNames);
		
		//GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.HORIZONTAL;
		//c.gridy = GridBagConstraints.RELATIVE; 
		
		//sidepanel.add(playertable, "1, 2, center, center");
		sidepanel.add(new JScrollPane(playertable), "1, 2, center, center");
		//playerpanel.add(scrollPane);
	}
	
	public static void create_leave_button(){
		JButton leave_button = new JButton("<html>Leave<br>room</html>");
		leave_button.setPreferredSize(new Dimension(75, 50));
		leave_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				leave_room();
			}
		});
		//leavebuttonpanel.add(leave_button);
		sidepanel.add(leave_button, "1, 3, center, center");
	}
	public static void create_chat(){
		JButton chat_button = new JButton("<html>Enter</html>");
		chat_button.setPreferredSize(new Dimension(100, 100));
		chat_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enter_chat();
			}
		});
		JTextField chat_text = new JTextField();
		JLabel chat_history = new JLabel();
		chatpanel.add(chat_button);
		chatpanel.add(chat_text);
		chatpanel.add(chat_history);
	}
	public static void check_set() {
		//reset the global counter
		counter = 0;
		//pass the setChain to the game logic
		setChain = "S:";	
	}
	public static void displayBoard(String s){
		//displays the cards on the board, given the board in the format deck_size:board_size:card1:card2:... and it will display.
		String temp = s;
		int deck_size = Integer.parseInt((temp.substring(0,temp.indexOf(":"))));
		deck.setText("Cards left in deck: " + deck_size);
		//deckpanel.add(deck);
		sidepanel.add(deck, "1, 4, center, center");
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

	public static void displayChat(){
	}
	
	public static void setup(){
		//mainpanel.setLayout(new BorderLayout(mainpanel, BoxLayout.PAGE_AXIS));
		//mainpanel.setBounds(0,0,screenWidth,screenHeight);
		boardpanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		boardpanel.setPreferredSize(new Dimension(screenWidth,screenHeight));
		//setbuttonpanel.setBounds(0,0,500,500);
		//leavebuttonpanel.setBounds(0,0,600,600);
		GridLayout cardGridLayout = new GridLayout(3, 7, 3, 3);
		//cardGridLayout.layoutContainer(cardGridLayout);
		//boardpanel.setLayout(new GridLayout(3,7,1,5).);
		boardpanel.setLayout(cardGridLayout);
		//deckpanel.setBounds(0,0,500,100);
		//deckpanel.setBounds(100,100,400,300);
	}
	public static void epilogue(){
		//boardpanel.setBounds(0, 0, 900, 500);
		mainpanel.add(boardpanel, BorderLayout.CENTER);
		//sidepanel.add(deckpanel);
		//sidepanel.add(setbuttonpanel);
		//sidepanel.add(playerpanel);
		//sidepanel.add(leavebuttonpanel);
		//sidepanel.add(Box.createRigidArea(new Dimension(200,450)));
		mainpanel.add(sidepanel, BorderLayout.CENTER);
		mainpanel.add(chatpanel, BorderLayout.PAGE_END);
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



*/
