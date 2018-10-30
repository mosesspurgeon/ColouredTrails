package gui.types;

/**
 * Enum describing different textures, different tile tyes
 * @author morvered
 *
 */
public enum TilePlayer {
	
	//Grass("grassTexture",true),
	//Water("waterTexture",false),
	//Dirt("dirtTexture",true),
	SpaceshipTexture("spaceshipTexture"),
	Player("player"),
	GoalTexture("goalTexture");
	/*so your 200 pixel 2D sprite becomes a 256 pixel texture.
	It is a requirement, at least on old graphics hardware, that all textures had 
	"two to the power something" width and height and Slick enforces this for you.
	 */
	
	public String playerName;
	//public boolean buildable;
	
	TilePlayer(String playerName){//, boolean buildable){
		this.playerName = playerName;
		//this.buildable = buildable;
	}

}
