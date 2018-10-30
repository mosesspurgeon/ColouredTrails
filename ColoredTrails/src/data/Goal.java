package data;

import gui.types.TileGoal;

public class Goal {
	Tile currentTile;
	private int x;
	private int y;
	private TileGoal tileGoal;
	private String goalIcon;
	
	public static TileGoal DEFAULT_GOAL_TYPE;
	public Goal() {
		super();
		DEFAULT_GOAL_TYPE = TileGoal.valueOf("goalTexture");
		this.currentTile = new Tile();
		this.goalIcon = "spaceshipTexture";//default goal icon
	}
	
	/**
	 * Constructor with initial coordinates, width and height 1, 1
	 * @param xCoord
	 * @param yCoord
	 */
	public Goal(int xCoord, int yCoord) {
		super();
		this.currentTile = new Tile(xCoord,yCoord);
		this.x = xCoord;
		this.y = yCoord;
		this.goalIcon = "spaceshipTexture";//default goal icon
	}
	
	public Goal(int xCoord, int yCoord, String goalicon) {
		super();
		this.currentTile = new Tile(xCoord,yCoord);
		this.x = xCoord;
		this.y = yCoord;
		this.goalIcon = goalicon;//default goal icon
	}
	
	public Goal(int xCoord, int yCoord,TileGoal tileGoal) {
		this(xCoord,yCoord);
		this.tileGoal = tileGoal;
		this.x = xCoord;
		this.y = yCoord;
		this.goalIcon = "spaceshipTexture";//default goal icon
	}
	

	

	
	public Goal(Tile startTile) {
		this.currentTile = startTile;
		this.x = startTile.getCoordinates().getxCoord();
		this.y = startTile.getCoordinates().getyCoord();
		
		this.goalIcon = "spaceshipTexture";//default goal icon
	}

	public Goal(Tile startTile, TileGoal tileGoal) {
		this.currentTile = startTile;
		this.tileGoal = tileGoal;
		this.x = startTile.getCoordinates().getxCoord();
		this.y = startTile.getCoordinates().getyCoord();
		this.goalIcon = "spaceshipTexture";//default goal icon
	}

	public Goal(Goal goal) {
		this.x = goal.getX();
		this.y = goal.getY();
		this.goalIcon = new String(goal.getGoalIcon());
		this.currentTile = new Tile(goal.getCurrentTile());
		this.tileGoal = goal.getTileGoal();
	}

	public Tile getCurrentTile() {
		return currentTile;
	}

	public void setCurrentTile(Tile currentTile) {
		this.currentTile = currentTile;
	}

	public TileGoal getTileGoal() {
		return tileGoal;
	}

	public void setTileGoal(TileGoal tileGoal) {
		this.tileGoal = tileGoal;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public static TileGoal getDEFAULT_GOAL_TYPE() {
		return DEFAULT_GOAL_TYPE;
	}

	public static void setDEFAULT_GOAL_TYPE(TileGoal dEFAULT_GOAL_TYPE) {
		DEFAULT_GOAL_TYPE = dEFAULT_GOAL_TYPE;
	}

	public String getGoalIcon() {
		return goalIcon;
	}

	public void setGoalIcon(String goalIcon) {
		this.goalIcon = goalIcon;
	}
	
	public RowCol getPosition() {
		return new RowCol(this.x,this.y);
	}
	
	
	

}
