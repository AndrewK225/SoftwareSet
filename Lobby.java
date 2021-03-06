import java.util.*;
public class Lobby {
	
	public static Game games[] = new Game[3];
	public Hashtable<String,Player> players = null;
	public int game1Pop = 0;
	public int game2Pop = 0;
	public int game3Pop = 0;
	
	
	//Constructor
	public Lobby (int number_of_games, Hashtable<String, Player> playerList) {
		players = playerList;
		for(int i = 0; i < number_of_games; i++) {
			//create games
			games[i] = new Game(i+1, players);
		}
		//addPlayer("0");
	}

	public String addPlayer(Player newPlayer) {
		//Player newPlayer = new Player(playerName);
		String playerName = newPlayer.name;
		players.put(playerName, newPlayer);
		String returnStr = showPlayers();
		return returnStr;
	}
	
	public String removePlayer(String playerName) {
		System.out.println("Removing player: " + playerName);
		Player p = players.get(playerName);
		if (p != null) {
			if (p.location == 1) {
				game1Pop--;
			}
			else if (p.location == 1) {
				game2Pop--;
			}
			else if (p.location == 3) {
				game3Pop--;
			}
			
			players.remove(p.name);
			p = null;
		}
		
		String playerList = showPlayers();
		return playerList;
	}
	
	public String movePlayer(String playerName, int destination) {
		Player p = players.get(playerName);
		
		// If player is moving into a game from the lobby
		if (destination > 0) {
			if (destination == 1)
				game1Pop++;		
			else if (destination == 2)
				game2Pop++;
			else if (destination == 3)
				game3Pop++;
		}
		// Else the player is returning to lobby from a game
		else {
			if (p.location == 1)
				game1Pop--;
			
			else if (p.location == 2)
				game2Pop--;
				
			else if (p.location == 3)
				game3Pop--;
		}

		p.location = destination;
		String playerList = showPlayers();
		
		return playerList;
	}
	
	public String showLobbyInfo() {
		String roomDetails = "LOBBYINFO:";
		int game1decksize = games[0].getDeckSize();
		int game1numCards = games[0].board.num_cards;
		int game2decksize = games[1].getDeckSize();
		int game2numCards = games[1].board.num_cards;
		int game3decksize = games[2].getDeckSize();
		int game3numCards = games[2].board.num_cards;
		roomDetails = roomDetails + "Game1:" + game1Pop + " players inside. " + game1numCards + " cards currently in play, with " + game1decksize + " cards left in the deck.:";
		roomDetails = roomDetails + "Game2:" + game2Pop + " players inside. " + game2numCards + " cards currently in play, with " + game2decksize + " cards left in the deck.:";
		roomDetails = roomDetails + "Game3:" + game3Pop + " players inside. " + game3numCards + " cards currently in play, with " + game3decksize + " cards left in the deck.";
		
		return roomDetails;
	}
	
	public String showPlayers() {
		System.err.println("LobbyClass: SHOWING PLAYERS");
		String active = "LBYACTIVE:";
	    Iterator it = players.entrySet().iterator();
	    
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String playerName = (String) pair.getKey();
	        Player p = (Player) pair.getValue();
	        int playerLocation = p.getLocation();

	        // Add player's location; 0 means Lobby, 1-3 mean specific game room 
			switch (playerLocation) {
				case -1:
					; // Removed player
					break;
				case 0:
					active = active + playerName + " is in" + " the Lobby\\n";
					break;
				case 1:
					active = active + playerName + " is in" + " Game 1\\n";
					break;
				case 2:
					active = active + playerName + " is in" + " Game 2\\n";
					break;
				case 3:
					active = active + playerName + " is in" + " Game 3\\n";
					break;
				default:
					active = active + playerName + " is in" + " an unknown location\\n";
					break;
			}
	        
	    }
		
		System.out.println("Active string = " + active);
		return active;
	}
	
	public static String check_set(String player, int game_number, int card1, int card2, int card3) {
		String updateStr = games[game_number-1].evaluateSetClaim(player, card1, card2, card3);
		return updateStr;
	}
}
