package coloredtrails.common;

import org.json.simple.JsonObject;

import coloredtrails.gui.types.TileGoal;

public class Goal implements JsonSerializable {
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
		this.goalIcon = "spaceshipTexture";// default goal icon
	}

	/**
	 * Constructor with initial coordinates, width and height 1, 1
	 * 
	 * @param xCoord
	 * @param yCoord
	 */
	public Goal(int xCoord, int yCoord) {
		super();
		this.currentTile = new Tile(xCoord, yCoord);
		this.x = xCoord;
		this.y = yCoord;
		this.goalIcon = "spaceshipTexture";// default goal icon
	}

	public Goal(int xCoord, int yCoord, String goalicon) {
		super();
		this.currentTile = new Tile(xCoord, yCoord);
		this.x = xCoord;
		this.y = yCoord;
		this.goalIcon = goalicon;// default goal icon
	}

	public Goal(int xCoord, int yCoord, TileGoal tileGoal) {
		this(xCoord, yCoord);
		this.tileGoal = tileGoal;
		this.x = xCoord;
		this.y = yCoord;
		this.goalIcon = "spaceshipTexture";// default goal icon
	}

	public Goal(Tile startTile) {
		this.currentTile = startTile;
		this.x = startTile.getCoordinates().getxCoord();
		this.y = startTile.getCoordinates().getyCoord();

		this.goalIcon = "spaceshipTexture";// default goal icon
	}

	public Goal(Tile startTile, TileGoal tileGoal) {
		this.currentTile = startTile;
		this.tileGoal = tileGoal;
		this.x = startTile.getCoordinates().getxCoord();
		this.y = startTile.getCoordinates().getyCoord();
		this.goalIcon = "spaceshipTexture";// default goal icon
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
		return new RowCol(this.x, this.y);
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject goal = new JsonObject();
		goal.put("currentTile", currentTile == null ? null : currentTile.toJsonObject());
		goal.put("x", x);
		goal.put("y", y);
		goal.put("tileGoal", tileGoal == null ? null : tileGoal.toJsonObject());
		goal.put("goalIcon", goalIcon);
		goal.put("DEFAULT_GOAL_TYPE", DEFAULT_GOAL_TYPE == null ? null : DEFAULT_GOAL_TYPE.toJsonObject());
		return goal;
	}

	public Goal(JsonObject goal) {
		this.currentTile = goal.get("currentTile") == null ? null : new Tile((JsonObject) goal.get("currentTile"));
		this.x = goal.getInteger("x");
		this.y = goal.getInteger("y");
		this.tileGoal = goal.get("tileGoal") == null ? null : TileGoal.getTileGoal((JsonObject) goal.get("tileGoal"));
		this.goalIcon = goal.getString("goalIcon");
		this.DEFAULT_GOAL_TYPE = goal.get("DEFAULT_GOAL_TYPE") == null ? null
				: TileGoal.getTileGoal((JsonObject) goal.get("DEFAULT_GOAL_TYPE"));

	}

}
