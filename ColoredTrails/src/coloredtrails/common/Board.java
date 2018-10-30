package coloredtrails.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;

import coloredtrails.gui.types.TileGoal;

public class Board implements Serializable, Cloneable, JsonSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * Set all these variables to non final so I can load them from a properties
	 * configuration file
	 * 
	 */
	public static final int NUM_COLORS = 10;

	private static int num_colors_used = 5;

	private int rows = 8;// 10;
	private int columns = 8;// 7;

	public Tile[][] map;

	public Board() {
		super();
		// TODO Auto-generated constructor stub
		map = new Tile[getRows()][getColumns()];
		Random randomGenerator = new Random();
		int randomNum;

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				randomNum = randomGenerator.nextInt(num_colors_used);
				map[i][j] = new Tile(i, j, new CtColor(randomNum));

			}
		}
	}

	/**
	 * Ctopy Ctor
	 * 
	 * @param board
	 */
	public Board(Board board) {
		this.rows = board.getRows();
		this.columns = board.getColumns();
		this.num_colors_used = board.getNum_colors_used();

		map = new Tile[rows][columns];

		for (int i = 0; i < columns; i++) {// map.length; i++) {
			for (int j = 0; j < rows; j++) {// map[i].length; j++) {
				map[i][j] = new Tile(board.getTile(i, j));

			}
		}
	}

	public Board(int rows, int columns, int numColorsUsed) {
		this.num_colors_used = numColorsUsed;
		this.rows = rows;
		this.columns = columns;

		map = new Tile[rows][columns];
		Random randomGenerator = new Random();
		int randomNum;

		for (int i = 0; i < columns; i++) {// map.length; i++) {
			for (int j = 0; j < rows; j++) {// map[i].length; j++) {
				randomNum = randomGenerator.nextInt(num_colors_used);
				map[i][j] = new Tile(i, j, new CtColor(randomNum));
			}
		}
	}

	/**
	 * Constructor. Initializes board with existing map and number of colores used
	 * 
	 * @param numColorsUsed
	 * @param newMap
	 */
	public Board(int numColorsUsed, int[][] newMap) {
		setRows(newMap.length);
		setColumns(newMap[0].length);
		this.num_colors_used = numColorsUsed;
		map = new Tile[getRows()][getColumns()];

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new Tile(i, j, new CtColor(newMap[i][j]));

			}
		}

	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	/**
	 * Sets a new colored tile in requested coordinate
	 * 
	 * @param xCoord
	 * @param yCoord
	 * @param color
	 */
	public void setTileColor(int xCoord, int yCoord, CtColor color) {

		map[xCoord][yCoord] = new Tile(xCoord, yCoord, color);

	}

	/**
	 * Sets a new colored tile in requested coordinate according to colorIndex
	 * 
	 * @param xCoord
	 * @param yCoord
	 * @param colorIndex
	 */
	public void setTileColor(int xCoord, int yCoord, int colorIndex) {

		map[xCoord][yCoord] = new Tile(xCoord, yCoord, new CtColor(colorIndex));

	}

	// /**
	// * Sets a new textured tile in requested coordinate according to texture
	// defined by TilePlayer
	// * @param xCoord
	// * @param yCoord
	// * @param type
	// */
	// public void setTilePlayer(int xCoord, int yCoord, TilePlayer type) {
	//
	// map[xCoord][yCoord] = new
	// Tile(xCoord,yCoord,TILE_WIDTH_SIZE,TILE_HEIGHT_SIZE,type);
	//
	// }

	/**
	 * 
	 * @param xCoord
	 * @param yCoord
	 * @return The tile located in the x,y indexs
	 */
	public Tile getTile(int xCoord, int yCoord) {
		return map[xCoord][yCoord];
	}

	public Tile getTile(RowCol pos) {
		return map[pos.getxCoord()][pos.getyCoord()];
	}

	public static int getNum_colors_used() {
		return num_colors_used;
	}

	public static void setNum_colors_used(int numColorsUsed) {
		num_colors_used = numColorsUsed;
	}

	public Tile[][] getMap() {
		return map;
	}

	public void setMap(Tile[][] map) {
		this.map = map;
	}

	/**
	 * Get all of the colors present on the board.
	 * 
	 * @return A list of all of the colors on the board.
	 */
	public Set<CtColor> getColors() {
		HashSet<CtColor> colors = new HashSet<CtColor>();

		// for (String k : getFeatures())
		// if (k.startsWith("Square."))
		// colors.add(((Square)get(k)).getColor());

		for (int i = 0; i < this.getNum_colors_used(); i++)
			colors.add(new CtColor(i));

		return colors;
	}

	/**
	 * Returns list of all square locations that contain a goal
	 * 
	 * @author SGF
	 */
	public ArrayList<RowCol> getGoalLocations() {
		ArrayList<RowCol> glist = new ArrayList<RowCol>();

		for (int i = 0; i < getRows(); ++i)
			for (int j = 0; j < getColumns(); ++j)
				if (getTile(i, j).isHasGoal())
					glist.add(new RowCol(i, j));

		return glist;
	}

	public ArrayList<RowCol> getGoalLocations(TileGoal goalType) {
		ArrayList<RowCol> glist = new ArrayList<RowCol>();
		// Add all specific goals
		for (int i = 0; i < getRows(); ++i)
			for (int j = 0; j < getColumns(); ++j)
				if (getTile(i, j).hasGoal(goalType))
					glist.add(new RowCol(i, j));
		// Add all default goals
		for (int i = 0; i < getRows(); ++i)
			for (int j = 0; j < getColumns(); ++j)
				if (getTile(i, j).hasGoal(Goal.DEFAULT_GOAL_TYPE) && !glist.contains(new RowCol(i, j)))
					glist.add(new RowCol(i, j));

		return glist;
	}

	// CAN BE MADE MORE EFFICIENT!
	// ALSO, Square class not so efficient?
	// the automated movement is quite slow for some reason
	/**
	 * Gets the nearest goal positions
	 * 
	 * //@deprecated needs further info or should be deleted (BestUse uses it)
	 */
	public ArrayList<RowCol> getNearestGoalLocations(RowCol pos, TileGoal goal_type) {
		int mind = Integer.MAX_VALUE;
		ArrayList<RowCol> nearest = new ArrayList<RowCol>();

		ArrayList<RowCol> goal_list = getGoalLocations(goal_type);
		for (RowCol rc : goal_list)
			if (pos.dist(rc) < mind) {
				mind = pos.dist(rc);
				nearest.clear();
				nearest.add(rc);
			} else if (pos.dist(rc) == mind)
				nearest.add(rc);

		return nearest;
	}

	/*
	 * public void setPlayer(int x, int y, Player player) {
	 * this.getMap()[x][y].addPlayer(player); }
	 */

	public int[][] getIntMap() {
		int[][] intMap = new int[this.rows][this.columns];
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.columns; j++)
				intMap[i][j] = this.map[i][j].getColor().getColorNum();
		}

		return intMap;
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject board = new JsonObject();
		board.put("num_colors_used", num_colors_used);
		board.put("rows", rows);
		board.put("columns", columns);

		JsonArray rowArray = new JsonArray();
		for (int i = 0; i < map.length; i++) {
			JsonArray colArray = new JsonArray();
			for (int j = 0; j < map[i].length; j++) {
				colArray.add(map[i][j]==null?null:map[i][j].toJsonObject());
			}
			rowArray.add(colArray);
		}
		board.put("map", rowArray);
		return board;
	}

	public Board(JsonObject board) {
		this.num_colors_used = board.getInteger("num_colors_used");
		this.rows = board.getInteger("rows");
		this.columns = board.getInteger("columns");
		this.map = new Tile[rows][columns];

		JsonArray rowArray= (JsonArray) board.get("map");
		for (int i = 0; i < rowArray.size(); i++) {
			JsonArray colArray = (JsonArray) rowArray.get(i);
			for (int j = 0; j < colArray.size(); j++) {
				this.map[i][j] = colArray.get(j) == null? null:new Tile((JsonObject)colArray.get(j));
			}
		}
		
		
		
	}
}
