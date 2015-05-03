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
	}

	public boolean addPlayer(String playerName) {
		Player newPlayer = new Player(playerName);
		if (players.put(newPlayer.name, newPlayer) != null) {
			System.out.println("LobbyClass: Added player with name: " + newPlayer.name);
			return true;
		}
		
		else {
			System.err.println("LobbyClass: Error creating a new player for username '" + newPlayer.name + "'");
			return false;
		}
	}
	
	public void removePlayer(Player p) {
		players.remove(p.name);
	}
	
	public void removePlayer(String playerName) {
		Player p = players.get(playerName);
		players.remove(p.name);
		p = null;
	}
	
	public void movePlayer(String playerName, int destination) {
		Player p = players.get(playerName);
		p.movePlayer(destination);
	}
	
	public String showPlayers() {
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
	        
	        
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
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
