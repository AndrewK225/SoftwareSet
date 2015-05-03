import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;

public class GameGUI {
	static Dimension screenSize;
	static int screenWidth;
	static int screenHeight;
	static int gameNum;
	static JPanel gamepanel;
	static JPanel playerpanel;
	static JPanel chatbox;
	static JPanel set_button;
	static JPanel deck_size_indicator;
	static JPanel timer;
	static JPanel leave_button;
	static int chatBoxWidth;
	static int chatBoxHeight;
	static int playersBoxHeight;
	static boolean lock_set = false;
	public static void main(String[] args) {
		setup();
		
		String board = "70:18:0020:0101:0201:0000:2201:0000:0000:0000:0000:0000:0000:0000:0020:0101:0201:0000:2201:0000";
		displayBoard(board);
		epilogue();
	}
	public static void declare_set(){
		
	}
	public static void displayBoard(String s){
		//displays the cards on the board, given the board in the format deck_size:board_size:card1:card2:... and it will display.
		String temp = s;
		int deck_size = Integer.parseInt((temp.substring(0,temp.indexOf(":"))));
		temp = temp.substring(temp.indexOf(":")+1);
		int board_size = Integer.parseInt((temp.substring(0,temp.indexOf(":"))));
		temp = temp.substring(temp.indexOf(":")+1);
		String[] cards = new String[board_size];
		for (int i = 0; i < board_size-1; i++){
			cards[i] = (temp.substring(0,Math.max(1,temp.indexOf(":"))));
			temp = temp.substring(temp.indexOf(":")+1);
		}
		cards[board_size-1] = temp;
		JCheckBox[] checkBoxes = new JCheckBox[board_size];
		Icon[] customIcons = new ImageIcon[board_size];
		for (int i = 0; i < board_size; i++){
			checkBoxes[i] = new JCheckBox();
			customIcons[i] = new ImageIcon(".//src//setpics//" + cards[i] + ".gif");
			checkBoxes[i].setIcon(new CheckBoxIcon(checkBoxes[i], customIcons[i], SwingConstants.CENTER, SwingConstants.TOP));	
			//checkBoxes[i].addChangeListener(changeListener);
			gamepanel.add(checkBoxes[i]);
		} 
	}
	public static void playerpanel(String s){
		//the input is the string representing the names of all players and their score
	}
	public static void chatbox(String s){
		
	}
	public static void setup(){
		//screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//screenHeight = screenSize.height;
		//screenWidth = screenSize.width;
		screenHeight = 300;
		screenWidth = 500;	
		//chatBoxWidth = screenWidth;
		//chatBoxHeight = 200;
		//playersBoxHeight = screenHeight - chatBoxHeight - 30;	
		gamepanel = new JPanel();
		playerpanel = new JPanel();  
		chatbox = new JPanel();;
		set_button = new JPanel();;
		deck_size_indicator = new JPanel();
		timer = new JPanel();
		leave_button = new JPanel();
		//JScrollPane players;
		gamepanel.setMaximumSize(gamepanel.getPreferredSize());
		gamepanel.setLayout(new GridLayout(3,7,5,5));
		playerpanel.setMaximumSize(playerpanel.getPreferredSize());
		chatbox.setMaximumSize(chatbox.getPreferredSize());
		set_button.setMaximumSize(set_button.getPreferredSize());
		deck_size_indicator.setMaximumSize(deck_size_indicator.getPreferredSize());
		timer.setMaximumSize(timer.getPreferredSize());
		leave_button.setMaximumSize(leave_button.getPreferredSize());
	}
	public static void epilogue(){
		gamepanel.setBackground(Color.BLACK);
		playerpanel.setBackground(Color.BLACK);
		chatbox.setBackground(Color.BLACK);
		set_button.setBackground(Color.BLACK);
		deck_size_indicator.setBackground(Color.BLACK);
		timer.setBackground(Color.BLACK);
		leave_button.setBackground(Color.BLACK);
		JFrame frame = new JFrame("SET GAME ROOM");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(gamepanel);
	
		frame.setSize(new Dimension(900, 600)); 
		//frame.setBackground(Color.YELLOW);
		//frame.pack();
		frame.setVisible(true);
	}
}
