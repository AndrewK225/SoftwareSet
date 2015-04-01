import java.util.Scanner;

public class Set {
	public static void main(String[] args){
		int score = 0;
		Scanner user_input = new Scanner( System.in );
		Deck A = new Deck();
		A.shuffle();
		Board B = new Board(A);
		//B.displayBoard();
		System.out.println("initialized board. checking for sets");
		while (!B.is_set()){
			B.addTriplet(A);
		}
		while(A.num_cards > 2){
			System.out.println("start of while loop. checking for sets. deck size: " + A.num_cards + " board size: " + B.num_cards);
			if (!B.is_set()){
				System.out.println("Adding cards. Deck size: " + A.num_cards);
				B.addTriplet(A);
				System.out.println("Added cards. Deck size: " + A.num_cards);
			}else{
				B.displayBoard();
				int s1 = user_input.nextInt( );
				int s2 = user_input.nextInt( );
				int s3 = user_input.nextInt( );
				System.out.println("user input provided. checking if valid set");
				if (B.is_set(s1,s2,s3)){
					B.addTriplet(A,s1,s2,s3);
					System.out.println("You found a set!");
					score++;
				}else{
					System.out.println("Not a set!");
				}
			}
		
		}
		user_input.close();
	}
}





 

/*
public class Deck{
	Card[] cards;
}

public class Player{
	string name;
	int score;
}
*/


/*

Software Engineering:
- Game of set
- Main menu: 1 player, 2 player, Options, Exit
- Options menu: Volume, Lose point for incorrect call, return to main menu 
- End game menu: Play again (if 1 player), Propose rematch (if 2 player), return to main menu
- In-game: Board will be displayed, buttons for calling set, surrendering.
- card Class with four attributes: color, number, shade, shape. Each attribute has 3 possible values. Deck has 81 cards.
- Deck Class consisting of cards. Deck can be shuffled. Cards can be dealt to the board. Deck starts with 81 cards.
- Deal function, which deals 3 cards to the board. 
- Board Class: Responsible for displaying cards currently on the board.
- When cards are dealt to the board, the board must determine where on the board to display the cards.
- Under each card, a check button must be displayed.
- 

*/