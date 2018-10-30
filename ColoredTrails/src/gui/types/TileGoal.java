package gui.types;

public enum TileGoal {
	
	//Grass("grassTexture",true),
	//Water("waterTexture",false),
	//Dirt("dirtTexture",true),
	Goal("goalTexture",false);
	/*so your 200 pixel 2D sprite becomes a 256 pixel texture.
	It is a requirement, at least on old graphics hardware, that all textures had 
	"two to the power something" width and height and Slick enforces this for you.
	 */
	
	public String goalName;
	public boolean buildable;
	
	TileGoal(String goalName, boolean buildable){
		this.goalName = goalName;
		this.buildable = buildable;
	}
}
