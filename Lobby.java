import java.util.*;
public class Lobby {
	
	private static Game games[] = new Game[3];
	private static HashMap<String,Player> players = new HashMap<String,Player>();
	
	//Constructor
	public Lobby (int number_of_games) {
		for(int i = 0; i < number_of_games; i++) {
			//create games
			games[i] = new Game(i);
		} 
	}

	public void addPlayer(Player p) {
		players.put(p.name,p);
	}
	
	public void removePlayer(Player p) {
		players.remove(p.name);
	}
	
	public String showPlayers() {
		String active = "LBYACTIVE:";
		for(String key:players.keySet()) {
			active = active + key +"\n";
		}
		return active;
		
	}
}
