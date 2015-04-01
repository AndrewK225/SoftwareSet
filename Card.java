
public class Card{
 	public String color; //color = "purple" OR "green" OR "red"
 	public String shape; //shape = "oval" OR "diamond" OR "squiggle"
 	public String number; //number = "1" or "2" or "3"
 	public String shade; //shade = "clear" or "striped" or "solid"
 	public Card(String color,String shape, String number, String shade){
 		this.color = color;
 		this.shape = shape;
 		this.number = number;
 		this.shade = shade;
 	}
 	public void printCard(){
 		System.out.println("Color = " + color + " Shape = " + shape + " Number = " + number + " Shade = " + shade);
 	}
 } 