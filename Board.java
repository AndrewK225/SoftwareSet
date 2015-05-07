//this class contains the Board that is currently active in game. It starts empty, but cards can be added from a given deck, or removed when the player chooses to remove a particular card.

public class Board {
	int num_cards;
	Card[] cards = new Card[21]; //board is capped at 21 cards
	
	public Board(Deck deck){ //create a new board, drawing the top 12 cards from a given deck
		for (num_cards = 0; num_cards<12;num_cards++){
			cards[num_cards] = deck.drawCard();
		}
		System.out.println("deck size is " + deck.num_cards);
		while ((!is_set())&&(deck.num_cards > 2)){ 
			//System.out.println("Adding cards. Deck size: " + deck.num_cards);
			addTriplet(deck);
			System.out.println("in while loop, deck size is: " + deck.num_cards);
		}
	}
	
	public String displayBoard(){ // display, in order, the contents of the board
		String retStr = "";
        System.out.println("The board contains " + num_cards + " cards.");
		for (int i = 0; i < num_cards; i++){
			System.out.print(i + "\t");
			retStr += cards[i].printCard();

            if (i != (num_cards-1))
                retStr += ":";
		}
		int deckSize = 81-num_cards;
		retStr = deckSize + ":" + num_cards + ":" + retStr;
		System.out.println("Displaying board: " + retStr);
        return retStr;
	}
	
	public void addTriplet(Deck deck){  //take the top three cards of the deck and put them on the board
		for (int i = 0; i < 3; i++){
			cards[num_cards+i] = deck.drawCard();
		}
		num_cards += 3;
	}
	public void replaceTriplet(Deck deck, int c1, int c2, int c3){  //take the top three cards of the deck and put them on the board
		cards[c1] = deck.drawCard();
		cards[c2] = deck.drawCard();
		cards[c3] = deck.drawCard();
	}
    public void removeTriplet(int c1, int c2, int c3){ //removes the card from the board, assuming the board isn't empty.
    	int[] indices = {c1, c2 ,c3};
    	for (int i = 0; i < 3; i++){
    		for (int j = 0; j < 2; j++){
    			if (indices[j] < indices[j+1]){
    				int temp = indices[j];
    				indices[j] = indices[j+1];
    				indices[j+1] = temp;
    			} 
    		}
    	}
    	for (int i =0; i < 3; i++){
    		for (int j = indices[i]; j < num_cards-1; j++){
        		cards[j] = cards[j+1];
        	}
        	num_cards--;
    	}
    }
	public boolean is_set(int i, int j, int k){ //given the indices of three cards on the board, do they form a set?
		//System.out.println("Calling is_set for particular triplet " + i + "\t" + j + "\t" + k);
		if ((cards[i]).color != (cards[j]).color){
			if ((cards[j].color == cards[k].color)||(cards[i].color == cards[k].color)){
				return false;
			}
		}else{
			if (cards[j].color != cards[k].color){
				return false;
			}
		}
		//System.out.println("colors form a set");
		if ((cards[i]).shape != (cards[j]).shape){
			if ((cards[j].shape == cards[k].shape)||(cards[i].shape == cards[k].shape)){
				return false;
			}
		}else{
			if (cards[j].shape != cards[k].shape){
				return false;
			}
		}
		//System.out.println("shapes form a set");
		if ((cards[i]).number != (cards[j]).number){
			if ((cards[j].number == cards[k].number)||(cards[i].number == cards[k].number)){
				return false;
			}
		}else{
			if (cards[j].number != cards[k].number){
				return false;
			}
		}
		//System.out.println("numbers form a set");
		if ((cards[i]).shade != (cards[j]).shade){
			if ((cards[j].shade == cards[k].shade)||(cards[i].shade == cards[k].shade)){
				return false;
			}
		}else{
			if (cards[j].shade != cards[k].shade){
				return false;
			}
		}
		//System.out.println("shades form a set");
		return true;
	}
	public boolean is_set(){ //given the current board, is there a set on the board?
		for (int i = 0; i < num_cards-1; i++){
			for (int j = i+1; j < num_cards-1; j++){
				for (int k = j+1; k < num_cards-1; k++){
					if (is_set(i,j,k)){
						System.out.println("Found a set in the board: " + i + "\t" + j + "\t" + k);
						return true;
					}
				}
			}
		}
		return false;
	} 
}
