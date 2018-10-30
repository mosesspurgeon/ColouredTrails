package coloredtrails.common;

import java.util.ArrayList;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;

import coloredtrails.gui.types.TileGoal;

/**
 * Tile class, contains x, y, width, height,
 * 
 * @author morvered
 *
 */
public class Tile implements JsonSerializable{

	private RowCol location;
	private boolean hasGoal;
	private Goal goal;//this is never used or set
	private CtColor color;
	private TileGoal tileGoal; 
	private boolean hasPlayer;
	private ArrayList<Player> players;

	public Tile() {
		location = new RowCol();
		this.hasGoal = false;
		this.goal = null;
		this.color = null;
		this.hasPlayer = false;
		this.players = new ArrayList<Player>();
	}

	public Tile(Tile tile) {
		this.setLocation(tile.getLocation());
		this.hasGoal = tile.hasGoal;
		if (tile.goal != null)
			this.goal = new Goal(tile.goal);
		else
			this.goal = null;
		if (tile.getColor() != null)
			this.color = new CtColor(tile.getColor());
		else
			this.color = null;
		this.hasPlayer = tile.hasPlayer;
		this.players = new ArrayList<Player>();
		for (Player p : tile.getPlayers()) {
			this.players.add(new Player(p));
		}
	}

	public Tile(int x, int y, CtColor color) {
		this(x, y);
		this.setColor(color);
		this.hasPlayer = false;
		this.hasGoal = false;
		this.goal = null;
		this.players = new ArrayList<Player>();
	}

	public Tile(int x, int y) {
		this.location = new RowCol(x, y);
		this.hasPlayer = false;
		this.hasGoal = false;
		this.goal = null;
		this.color = null;
		this.players = new ArrayList<Player>();
	}

	public RowCol getCoordinates() {
		return location;
	}

	public void setCoordinates(RowCol location) {
		this.location = new RowCol(location);
	}

	public RowCol getLocation() {
		return location;
	}

	public void setLocation(RowCol location) {
		this.location = new RowCol(location);
	}

	public boolean isHasGoal() {
		return hasGoal;
	}

	public void setHasGoal(boolean hasGoal) {
		this.hasGoal = hasGoal;
	}

	public void addGoal(Goal g) {
		this.hasGoal = true;
		this.goal = g;
	}

	public Goal getGoal() {
		if (hasGoal)
			return goal;
		else
			return null;
	}

	public void removeGoal() {
		hasGoal = false;
		goal = null;
	}

	public CtColor getColor() {
		return color;
	}

	public void setColor(CtColor color) {
		this.color = color;
	}

	public TileGoal getTileGoal() {
		return tileGoal;
	}

	public void setTileGoal(TileGoal tileGoal) {
		this.tileGoal = tileGoal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
	}

	public boolean hasGoal(TileGoal g) {
		if (this.goal.getTileGoal() == g)
			return true;
		return false;
	}

	public boolean isHasPlayer() {
		return hasPlayer;
	}

	// public void setHasPlayer(boolean hasPlayer) {
	// this.hasPlayer = hasPlayer;
	// }

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player player) {
		boolean exists = false;
		for (Player p : players) {
			if (p.getPlayerName().equals(player.getPlayerName()))
				exists = true;
		}
		if (!exists) {
			players.add(player);
			hasPlayer = true;
		}

	}

	public void removePlayer(Player player) {
		int index = 0;
		for (Player p : players) {
			if (p.getPlayerName().equals(player.getPlayerName())) {
				players.remove(index);
				break;
			}
			index++;
		}
		if (players.size() == 0)
			hasPlayer = false;
	}

	public JsonObject toJsonObject() {
		JsonObject tile = new JsonObject();
		tile.put("location", location == null ? null : location.toJsonObject());
		tile.put("hasGoal", hasGoal);
		tile.put("goal", goal == null ? null : goal.toJsonObject());
		tile.put("color", color == null ? null : color.toJsonObject());
		tile.put("tileGoal", tileGoal == null ? null : tileGoal.toJsonObject());
		tile.put("hasPlayer", hasPlayer);


		return tile;

	}

	public Tile(JsonObject tile) {
		this.location = (tile.get("location") == null ? null : (new RowCol((JsonObject) tile.get("location"))));
		this.hasGoal = tile.getBoolean("hasGoal");
		this.goal = (tile.get("goal") == null ? null : (new Goal((JsonObject) tile.get("goal"))));
		this.color = (tile.get("color") == null ? null : (new CtColor((JsonObject) tile.get("color"))));
		this.tileGoal = (tile.get("tileGoal") == null ? null : TileGoal.getTileGoal((JsonObject) tile.get("tileGoal")));
		this.hasPlayer = tile.getBoolean("hasPlayer");
		this.players = new ArrayList<Player>();


	}

}
