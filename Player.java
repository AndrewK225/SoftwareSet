
public class Player {
	int score;
	String name;
	public Player (String name){
		score = 0;
		this.name = name;
	}
	public void score_point(){
		this.score++;
	}
	public void print_score(){
		System.out.println(name + " has " + score + " points.");
	}
}
