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
		
		// When making a new game, create a new deck, and pass it over to the Board (which displays+draws the cards)
		deck = new Deck();
		deck.shuffle();
		board = new Board(deck);
		players = listofplayers;
	}
	
	/*
	public void addPlayer(String name, Player player){
		players.put(name,player);
	}
	
	public void removePlayer(String name, Player player){
		players.remove(name);
	}
	*/
	
	public String showScores() {
		System.out.println("GameClass: SHOWING SCORES");
		String active = "SCORES:" + gameNum;
		
		// SCORES:1:someplayer1:5:someplayer2:10  -- player1 has 5 points, and player 2 has 10 points
	    Iterator it = players.entrySet().iterator();
	    
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String playerName = (String) pair.getKey();
	        Player p = (Player) pair.getValue();
	        int playerLocation = p.getLocation();

	        // Add player's location; 0 means Lobby, 1-3 mean specific game room 
			if (playerLocation == gameNum)
				active = active + ":" + playerName + ":" + p.getScore();
	    }
		
		System.out.println("SCORE String for game #" + gameNum + ": " + active);
		return active;
	}
	
	public boolean getSetLock(String name) {
		if (lock_set == false) {
			timer = new Timer();
			timer.schedule(new Release_lock(),5500);
			declare_set_player = players.get(name);
			lock_set = true;
			return true;
		}
		else
			return false;
	}
	
	class Release_lock extends TimerTask {
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
			// If an actual set: award a point
			players.get(name).score_point();
			
			// If too many cards on the board, or if no cards left in the deck, just remove the cards
			if ((board.num_cards) > 12 || (deck.num_cards < 3)){
				System.out.println("removing 3 cards");
				board.removeTriplet(input1,input2,input3);
			}
			else if (board.num_cards == 12){
				System.out.println("replacing 3 cards");
				board.replaceTriplet(deck,input1,input2,input3);
			}
			while ((!board.is_set())&&(deck.num_cards > 2)){ 
				//System.out.println("Adding cards. Deck size: " + deck.num_cards);
				board.addTriplet(deck);
			}
			System.out.println("Player " + name + " found a set!");
			System.out.println("players.getname = " + players.get(name));
			
			String cardUpdateStr;
			
			if ((!board.is_set())&&(deck.num_cards < 3)){
				//if game is over
				cardUpdateStr = "GAME OVER";
			} else {
				//else
				cardUpdateStr = board.displayBoard();
			}
			return cardUpdateStr;
		}
		
		else {
			System.out.println("Not a set!");
			players.get(name).deduct_point();
			return null;
		}
	}
	
	public int getDeckSize() {
		return deck.num_cards;
	}
}


