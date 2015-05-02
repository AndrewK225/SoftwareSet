import java.util.*;

public class Game{
		Deck deck;
		Board board;
		HashMap players = new HashMap();
		int gameNum;
		 public Game(int num){
			gameNum = num;
	        deck = new Deck();
	        // shuffle after this actually works - will see cards in order then, theoretically
	         deck.shuffle();
	        board = new Board(deck);
		
	 //int score = 0; //initialize the player's score to 0
	 Scanner user_input = new Scanner( System.in );
     while(deck.num_cards > 2 || (board.is_set())){ //while the deck still has at least 3 cards
		System.out.println("start of while loop. checking for sets. deck size: " + deck.num_cards + " board size: " + board.num_cards);
		if ((!board.is_set())&&(deck.num_cards > 2)){ //if there are no sets on the board or if the board has under 12 cards, 
			//add 3 cards from the deck to the board
			System.out.println("Adding cards. Deck size: " + deck.num_cards);
			board.addTriplet(deck);
		}else{ //if there is at least one set on the board
			board.displayBoard(); //display the board
			int index1 = user_input.nextInt( ); //get three inputs from user
			int index2 = user_input.nextInt( );
			int index3 = user_input.nextInt( );
			System.out.println("user input provided. checking if valid set"); 
			if (board.is_set(index1,index2,index3)){ 
				if ((board.num_cards) > 12 || (deck.num_cards < 3)){
					System.out.println("removing 3 cards");
					board.removeTriplet(index1,index2,index3);
				}else if (board.num_cards == 12){
					System.out.println("replacing 3 cards");
					board.addTriplet(deck,index1,index2,index3);
				}
				System.out.println("You found a set!");
				//score++;
			}else{
				System.out.println("Not a set!");
			}
		}
     }
    user_input.close();
		 }
}
   /*
    * while game is still going:
    * 	if the deck has >3 cards and board < 12 cards:
    * 		add 3 cards 
  */


