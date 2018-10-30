package coloredtrails.common;

import java.awt.Color;

import org.json.simple.JsonObject;


public class CtColor implements JsonSerializable{
	
	public int colorNum;
	public String colorName;
	public Color color;
	
	@Override
	public JsonObject toJsonObject() {
		JsonObject ctColor = new JsonObject();
		ctColor.put("colorNum", colorNum);
		return ctColor;
	}
	
	public CtColor (JsonObject ctColor) {
		this (ctColor.getInteger("colorNum")); 
	}
	
	public CtColor(int colorNum) {
		super();
		this.colorNum = colorNum;
		setNameAndColor();
	}
	
	/**
	 * Default C'tor. Sets color to black. 
	 */
	public CtColor() {
		super();
		this.colorNum = 0;
		setNameAndColor();
	}

	

	private void setNameAndColor() {
		//"Blue", "Red", "White", "Yellow", "Green", "Orange", "Gray", "Brown", "Purple", "Light Blue"
		switch(colorNum) {
		case 1 :
			this.color = Color.blue;//0 0 255
			this.colorName = "Blue";
			break;
		case 2 :
			this.color = Color.red;//255 0 0
			this.colorName = "Red";
			break;
		case 3 :
			this.color = Color.white;//255 255 255
			this.colorName = "White";
			break;
		case 4 :
			this.color = Color.yellow;//255 255 0
			this.colorName = "Yellow";
			break;
		case 5 :
			this.color = Color.green;//0 204 0
			this.colorName = "Green";
			break;
		case 6 :
			this.color = Color.orange;// 255 102 0
			this.colorName = "Orange";
			break;
		case 7 :
			this.color = Color.gray;//153 153 153
			this.colorName = "Gray";
			break;
		case 8 ://light brown
			this.color = new Color(153,102,0);
			this.colorName = "Brown";
			break;
		case 9 ://Purple
			this.color = new Color(102,0,153);
			this.colorName = "Purple";
			break;
		case 10 ://light blue
			this.color = new Color(51,204,255);
			this.colorName = "Light Blue";
			break;
		//case 11 :
		//	this.color = Color.cyan;
		//	this.colorName = this.color.toString();
		//	break;
		default:
			this.color = Color.black;
			this.colorName = this.color.toString();
			break;
			
		}
	}
	
	public CtColor(String colorString) {
		super();
		this.colorName = colorString;
		setNumberByColor();
	}
	
	
	public CtColor(CtColor color2) {
		this.colorName = new String(color2.getColorName());
		this.colorNum = color2.getColorNum();
		this.color = color2.getColor();
	}

	@Override
	public boolean equals(Object o) {
		 if(o==this)
			 return true;
		 if(!(o instanceof CtColor))
			 return false;
		 
		 CtColor c = (CtColor)o;
		 if(this.colorNum == c.getColorNum() && this.colorName.equals(c.getColorName()) && this.color.equals(c.getColor()))
			 return true;
		 return false;
	}

	private void setNumberByColor() {
		//"Blue", "Red", "White", "Yellow", "Green", "Orange", "Gray", "Brown", "Purple", "Light Blue"
		if (this.colorName.equals("Blue")) {
			this.color = Color.blue;
			this.colorNum = 1;
		} else if (this.colorName.equals("Red")) {
			this.color = Color.red;
			this.colorNum = 2;
		} else if (this.colorName.equals("White")) {
			this.color = Color.white;
			this.colorNum = 3;
		} else if (this.colorName.equals("Yellow")) {
			this.color = Color.yellow;
			this.colorNum = 4;
		} else if (this.colorName.equals("Green")) {
			this.color = Color.green;
			this.colorNum = 5;
		} else if (this.colorName.equals("Orange")) {
			this.color = Color.orange;
			this.colorNum = 6;
		} else if (this.colorName.equals("Gray")) {
			this.color = Color.gray;
			this.colorNum = 7;
		} else if (this.colorName.equals("Brown")) {
			this.color = new Color(153,102,0);
			this.colorNum = 8;
		} else if (this.colorName.equals("Purple")) {
			this.color = new Color(102,0,153);
			this.colorNum = 9;
		} else if (this.colorName.equals("Light Blue")) {
			this.color = new Color(51,204,255);
			this.colorNum = 10;
		//} else if (Color.cyan.toString().equals(colorName)) {
		//	this.color = Color.cyan;
		//	this.colorNum = 11;
		} else {
			this.color = Color.black;
			this.colorName = Color.black.toString();
			this.colorNum = 0;
		}
	}

	

	public int getColorNum() {
		return colorNum;
	}


	public void setColorNum(int colorNum) {
		this.colorNum = colorNum;
	}


	public String getColorName() {
		return colorName;
	}


	public void setColorName(String colorName) {
		this.colorName = colorName;
	}


	public Color getColor() {
		return color;
	}


	public void setColor(Color color) {
		this.color = color;
	}


	
	

}
