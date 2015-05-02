//this class contains the representation of a card, and a way to print the card

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
 	public String printCard(){
 		String retStr = "";
        System.out.println("Color = " + color + " Shape = " + shape + " Number = " + number + " Shade = " + shade);

    if ("purple".equals(color))
        retStr += "0";
    else if ("green".equals(color))
        retStr += "1";
    else if ("red".equals(color))
        retStr += "2";
    else
        retStr += "-COLORUNKNOWN-";

    if ("oval".equals(shape))
        retStr += "0";
    else if ("diamond".equals(shape))
        retStr += "1";
    else if ("squiggle".equals(shape))
        retStr += "2";
    else
        retStr += "-SHAPEUNKNOWN-";

    if ("1".equals(number))
        retStr += "0";
    else if ("2".equals(number))
        retStr += "1";
    else if ("3".equals(number))
        retStr += "2";
    else
        retStr += "-NUMBERUNKNOWN-";

    if ("clear".equals(shade))
        retStr += "0";
    else if ("striped".equals(shade))
        retStr += "1";
    else if ("solid".equals(shade))
        retStr += "2";
    else
        retStr += "-SHADEUNKNOWN-";

    return retStr;
 }
 	
}
