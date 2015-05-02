//this class contains the Board that is currently active in game. It starts empty, but cards can be added from a given deck, or removed when the player chooses to remove a particular card.
import java.util.*;

public class Game {
	
    public Board gameBoard;
    public  Deck deck;
    public HashMap players = new HashMap();
    public int gameNum;
    public Game(int num) {
		gameNum = num;
        deck = new Deck();
        // shuffle after this actually works - will see cards in order then, theoretically
        // deck.shuffle();
        gameBoard = new Board(deck);
        // gameBoard has 12 cards now, need to display them to all players in game.
    }
    
    public void addPlayer(String playerName, Player p) {
		players.put(playerName, p);
	}
    
    public void judgeSet(int i, int j, int k) {
        // i, j, and k need to be offset by 1 to properly index into the array that starts indexing at 0
        boolean ret = gameBoard.is_set(i, j, k);
        
        if (ret == true) {
            // If valid set, either replace the 3 cards, or simply remove the 3 cards
            if (gameBoard.num_cards < 13) {
                // Make sure there are still cards left in the deck
                if (deck.num_cards > 0) {
                    gameBoard.replaceTriplet(deck, i, j, k);
                }
            }
            else {
                // If more than 12 cards (like 15) in play, remove the 3 cards used to make the set from the array
                int tmp;
                // Sort i, j, and k from least to greatest
				if (j < i) {
                    tmp = i;
                    i = j;
                    j = tmp;
                }
                if (k < j) {
                    tmp = j;
                    j = k;
                    k = tmp;
                }
				
                /*
				gameBoard.cards = ArrayUtils.removeElement(gameBoard.cards, i);
				j = j-1;
				gameBoard.cards = ArrayUtils.removeElement(gameBoard.cards, j);
				k = k-2;
				gameBoard.cards = ArrayUtils.removeElement(gameBoard.cards, k);
                */
            }
			
			// After the sizing is in check, see if the game board has a valid set present for players to find
			ret = gameBoard.is_set();
			while (ret == false) {
				gameBoard.addTriplet(deck);
				ret = gameBoard.is_set();
			}
				
			// Update the players' (in this game) view of this game with the cards on the board
			// SOME FUNCTION HERE
			String cardUpdateStr = gameBoard.displayBoard();
        }
		
		// Release the set lock
    }
	
}
