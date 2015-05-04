import java.util.*;
public class Lobby {
	
	private static Game games[] = new Game[3];
	private static Hashtable<String,Player> players = null;
	
	//Constructor
	public Lobby (int number_of_games, Hashtable<String, Player> playerList) {
		players = playerList;
		for(int i = 0; i < number_of_games; i++) {
			//create games
			games[i] = new Game(i);
		}
		//addPlayer("0");
	}

	public String addPlayer(String playerName) {
		Player newPlayer = new Player(playerName);
		
		System.out.println("LobbyClass: Player obj created = " + newPlayer);
		
		players.put(playerName, newPlayer);
		System.out.println(players);
		
		/*
		if (players.put(playerName, newPlayer) != null) {
			System.out.println("LobbyClass: Added player with name: " + newPlayer.name);
			return true;
		}
		
		else {
			System.err.println("LobbyClass: Error creating a new player for username '" + newPlayer.name + "'");
			return false;
		}
		*/
		String returnStr = showPlayers();
		return returnStr;
	}
	
	public String removePlayer(Player p) {
		System.out.println("Removing player: " + p.name);
		players.remove(p.name);
		p = null;
		String playerList = showPlayers();
		return playerList;
	}
	
	public String removePlayer(String playerName) {
		System.out.println("Removing player: " + playerName);
		Player p = players.get(playerName);
		players.remove(p.name);
		p = null;
		String playerList = showPlayers();
		return playerList;
	}
	
	public String movePlayer(String playerName, int destination) {
		System.out.println("In movePlayer");
		//Player p = players.get(playerName);
		//p.location = destination;
		//System.out.println("Leaving movePlayer");
		String playerList = "hi";
		//playerList = showPlayers();
		
		return playerList;
	}
	
	/*
	public String movePlayer(String playerName, String destination) {
		System.out.println("Move player with string");
		/*
		int dest = Integer.parseInt(destination);
		System.out.println("In movePlayer");
		Player p = players.get(playerName);
		System.out.println("Move player with string");
		p.movePlayer(dest);
		System.out.println("Leaving movePlayer");
		String playerList = showPlayers();
		return playerList;
		
		return destination;
	}
	*/
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
}
