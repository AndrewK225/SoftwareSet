import java.util.*;

public class Game{
	Deck deck;
	Board board;
	HashMap <String, Player> players;
	Timer timer;
	boolean lock_set = false;
	int gameNum;
	Player declare_set_player;
	int[] inputs;
	int num_inputs = 0;
	public void addPlayer(String name, Player player){
		players.put(name,player);
	}
	public void removePlayer(String name, Player player){
		players.remove(name);
	}
	public void declare_set(String name){
		lock_set = true;
		timer = new Timer();
		timer.schedule(new Release_lock(),5000);
		declare_set_player = players.get(name);
	}
	
	class Release_lock extends TimerTask{
		public void run(){
			lock_set = false;
			num_inputs = 0;
		}
	}
	public boolean is_over(){
		return ((deck.num_cards < 3) && (!board.is_set())); 
	}
	public void enter_input(int x){
		if (num_inputs < 3){
			inputs[num_inputs] = x;
			num_inputs++;
		}
	}
	public void loop(){
		//this is the function that keeps being called as the game is running
		board.displayBoard();
		while ((!board.is_set())&&(deck.num_cards > 2)&&(!lock_set)){ 
			//System.out.println("Adding cards. Deck size: " + deck.num_cards);
			board.addTriplet(deck);
		}
		if((lock_set) && (num_inputs == 3)){
			process_input(declare_set_player.name,inputs[0],inputs[1],inputs[2]);
			new Release_lock();
		}
	}
	
	public void process_input(String name, int input1,int input2,int input3){
		if (board.is_set(input1,input2,input3)){ 
			if ((board.num_cards) > 12 || (deck.num_cards < 3)){
				//System.out.println("removing 3 cards");
				board.removeTriplet(input1,input2,input3);
			}else if (board.num_cards == 12){
				//System.out.println("replacing 3 cards");
				board.addTriplet(deck,input1,input2,input3);
			}
			System.out.println("You found a set!");
			players.get(name).score_point();
		}
		else{
			System.out.println("Not a set!");
		}
	}
	
	public Game(int num){
		gameNum = num;
		deck = new Deck();
		deck.shuffle();
		board = new Board(deck);
	}
}


