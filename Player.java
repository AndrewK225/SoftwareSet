
public class Player {
	private int score;
	public int location;
	public String name;
	
	public Player (String name){
		score = 0;
		location = 0;
		this.name = name;
	}
	
	public void score_point(){
		this.score++;
	}
	
	public void movePlayer(int destination) {
		System.out.println("Wrong");
		this.location = destination;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getLocation() {
		return location;
	}
	
	public void print_score(){
		System.out.println(name + " has " + score + " points.");
	}
	
	public void setLocation(int destination) {
		location = destination; 
	}
	
}
