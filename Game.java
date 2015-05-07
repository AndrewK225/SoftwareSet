import java.util.*;

public class Game{
	Deck deck;
	Board board;
	Hashtable <String, Player> players;
	Timer timer;
	boolean lock_set = false;
	int gameNum;
	Player declare_set_player;
	int[] inputs;
	int num_inputs = 0;
	
	public Game(int num, Hashtable <String, Player> listofplayers){
		System.out.println("Making game number " + num);
		gameNum = num;
		deck = new Deck();
		deck.shuffle();
		board = new Board(deck);
		players = listofplayers;
	}
	
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
	public void enter_input(int x){
		if (num_inputs < 3){
			inputs[num_inputs] = x;
			num_inputs++;
		}
	}
	
	public String evaluateSetClaim(String name, int input1,int input2,int input3){
		System.out.println("evaluateSetClaim");
		if (board.is_set(input1,input2,input3)) {
			
			// If too many cards on the board, or if no cards left in the deck, just remove the cards
			if ((board.num_cards) > 12 || (deck.num_cards < 3)){
				System.out.println("removing 3 cards");
				board.removeTriplet(input1,input2,input3);
			}
			
			else if (board.num_cards == 12){
				System.out.println("replacing 3 cards");
				board.replaceTriplet(deck,input1,input2,input3);
			}
			
			System.out.println("Player " + name + " found a set!");
			System.out.println("players.getname = " + players.get(name));
			players.get(name).score_point();
			String cardUpdateStr = board.displayBoard();
			return cardUpdateStr;
		}
		
		else {
			System.out.println("Not a set!");
			players.get(name).deduct_point();
			return null;
		}
	}
}


