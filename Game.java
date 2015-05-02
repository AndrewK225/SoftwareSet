import java.util.*;

public class Game{
	Deck deck;
	Board board;
	Timer timer;
	HashMap <String, Player> players;
	boolean lock_set = false;
	int gameNum;
	int[] inputs = {-1 -1 -1};
	int num_inputs = 0;
	Player declare_set_player;
	public void addPlayer(String name, Player player){
		players.put(name,player);
	}
	public void removePlayer(String name, Player player){
		players.remove(name);
	}
	public void declare_set(String name){
		lock_set = true;
		declare_set_player = players.get(name);
	}
	public void enter_card(int index){
		inputs[num_inputs] = index;
		num_inputs++;
	}
	public void release_lock(){
		lock_set = false;
	}
	public boolean is_over(){
		return ((deck.num_cards < 3) && (!board.is_set())); 
	}
	public void loop(){
		//this is the function that keeps being called as the game is running
		board.displayBoard();
		while ((!board.is_set())&&(deck.num_cards > 2)&&(!lock_set)){ 
			//System.out.println("Adding cards. Deck size: " + deck.num_cards);
			board.addTriplet(deck);
		}
	}
	public void process_input(String name,int input1,int input2,int input3){
		if (board.is_set(input1,input2,input3)){ 
			if ((board.num_cards) > 12 || (deck.num_cards < 3)){
				//System.out.println("removing 3 cards");
				board.removeTriplet(input1,input2,input3);
			}else if (board.num_cards == 12){
				//System.out.println("replacing 3 cards");
				board.addTriplet(deck,input1,input2,input3);
			}
			System.out.println("You found a set!");
			players.get(name).score++;
		}else{
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


