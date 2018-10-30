package data;




import java.util.ArrayList;

import gui.types.TileGoal;
import ui.CtColor;



/**
 * Tile class, contains x, y, width, height, 
 * @author morvered
 *
 */
public  class Tile {
	
	private RowCol location;
	private boolean hasGoal;
	private Goal goal;
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
		if(tile.goal!=null)
			this.goal = new Goal(tile.goal);
		else
			this.goal = null;
		if(tile.getColor()!=null)
			this.color = new CtColor(tile.getColor());
		else
			this.color = null;
		this.hasPlayer = tile.hasPlayer;
		this.players = new ArrayList<Player>();
		for(Player p : tile.getPlayers()) {
			this.players.add(new Player(p));
		}
	}
	
	
	public Tile(int x, int y, CtColor color) {
		this(x,y);
		this.setColor(color);
		this.hasPlayer = false;
		this.hasGoal = false;
		this.goal = null;
		this.players = new ArrayList<Player>();
	}
	
	

	
	public Tile(int x, int y) {
		this.location = new RowCol(x,y);
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
		if( this.goal.getTileGoal() == g)
			return true;
		return false;
	}

	public boolean isHasPlayer() {
		return hasPlayer;
	}

//	public void setHasPlayer(boolean hasPlayer) {
//		this.hasPlayer = hasPlayer;
//	}

	public ArrayList<Player> getPlayers() {
		return players;
	}


	public void addPlayer(Player player) {
		boolean exists = false;
		for(Player p : players) {
			if(p.getPlayerName().equals(player.getPlayerName()))
				exists = true;
		}
		if(!exists) {
			players.add(player);
			hasPlayer  = true;
		}
		
	}
	
	public  void removePlayer(Player player) {
		int index = 0;
		for(Player p : players) {
			if(p.getPlayerName().equals(player.getPlayerName())) {
				players.remove(index);
				break;
			}
			index++;
		}
		if(players.size()==0)
			hasPlayer = false;
	}
	
	
	
	
}
