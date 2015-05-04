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
		addPlayer("0");
	}

	public boolean addPlayer(String playerName) {
		Player newPlayer = new Player(playerName);
		
		System.out.println("LobbyClass: Player obj created = " + newPlayer);
		System.out.println("LobbyClass: Before players size = " + players.size());
		
		players.put(playerName, newPlayer);
		System.out.println(players);
		System.out.println("LobbyClass: After players size = " + players.size());
		return true;
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
	}
	
	public void removePlayer(Player p) {
		System.out.println("Removing player: " + p.name);
		players.remove(p.name);
		p = null;
	}
	
	public void removePlayer(String playerName) {
		System.out.println("Removing player: " + playerName);
		Player p = players.get(playerName);
		players.remove(p.name);
		p = null;
	}
	
	public void movePlayer(String playerName, int destination) {
		System.out.println("In movePlayer");
		Player p = players.get(playerName);
		p.movePlayer(destination);
		System.out.println("Leaving movePlayer");
	}
	
	public String showPlayers() {
		System.err.println("LobbyClass: SHOWING PLAYERS");
		String active = "LBYACTIVE:";
	    Iterator it = players.entrySet().iterator();
	    
	    System.out.println("Size of players = " + players.size());
	    
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String playerName = (String) pair.getKey();
	        Player p = (Player) pair.getValue();
	        int playerLocation = p.getLocation();
	        
	        active = active + playerName + " is in";

	        // Add player's location; 0 means Lobby, 1-3 mean specific game room 
			switch (playerLocation) {
				case -1:
					; // Removed player
					break;
				case 0:
					active = active + " the Lobby.";
					break;
				case 1:
					active = active + " Game 1.";
					break;
				case 2:
					active = active + " Game 2.";
					break;
				case 3:
					active = active + " Game 3.";
					break;
				default:
					active = active + " an unknown location.";
					break;
			}
	        
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
		
	    
	    /*
		for (String key:players.keySet()) {
			// Append player's name
			active = active + key;
			
			Player p = players.get(key);
			int playerLocation = p.getLocation();
			
			active = active + " is in";
			// Add player's location; 0 means Lobby, 1-3 mean specific game room 
			switch (playerLocation) {
				case 0:
					active = active + " the Lobby.";
					break;
				case 1:
					active = active + " Game 1.";
					break;
				case 2:
					active = active + " Game 2.";
					break;
				case 3:
					active = active + " Game 3.";
					break;
				default:
					active = active + " an unknown location.";
					break;
			}
			
			active = active + "\n";
		}
		*/
	    
		System.out.println("Active string = " + active);
		return active;
	}
}
