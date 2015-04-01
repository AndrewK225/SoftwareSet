import java.util.Random;

public class Deck {
	int num_cards; 
	Card[] cards = new Card[81]; 
	public Deck(){ //the deck constructor populates a new deck with all the cards in order
		num_cards = 0;
		String color = "";
		String shape = "";
		String number = "";
		String shade = "";
		for (int i = 0; i < 3; i++){
			switch (i){
			case 0:
				color = "purple";
				break;
			case 1:
				color = "green";
				break;
			case 2:
				color = "red";
				break;
			}
			for (int j = 0; j < 3; j++){
				switch (j){
				case 0:
					shape = "oval";
					break;
				case 1:
					shape = "diamond";
					break;
				case 2:
					shape = "squiggle";
					break;
				}
				for (int k = 0; k < 3; k++){
					switch (k){
					case 0:
						number = "1";
						break;
					case 1:
						number = "2";
						break;
					case 2:
						number = "3";
						break;
					}
					for (int l = 0; l < 3; l++){
						switch (l){
						case 0:
							shade = "clear";
							break;
						case 1:
							shade = "striped";
							break;
						case 2:
							shade = "solid";
							break;
						}
						Card card = new Card(color,shape,number,shade);
						this.addCard(card);
					}
				}
			}
		}
	}
	public void addCard(Card card){
		//adds a given card to the deck;
		cards[num_cards] = card;
		num_cards++;
	}
	public void shuffle(){
		//shuffles the cards of the current deck, the way it works is each index of the deck is swapped with another random index in the deck; all arrangements are equally likely;
		Random rand = new Random();
		for (int i = 0; i < num_cards; i++){
			Card temp_card = cards[i];
			int randomNum = rand.nextInt(num_cards);
			cards[i] = cards[randomNum];
			cards[randomNum] = temp_card;
		}
	}
	public void printDeck(){
		System.out.println("This deck contains " + num_cards + " cards.");
		for (int i = 0; i < num_cards; i++){
			cards[i].printCard();
			
		}
	}
	public Card drawCard(){
		//draw a card from the top of the deck, removing it from the deck, and returning the card removed.
		num_cards--;
		return cards[num_cards];
	}
}
