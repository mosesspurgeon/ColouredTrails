package coloredtrails.common;



import java.io.Serializable;
import java.util.LinkedHashSet;

import org.json.simple.JsonObject;

public class RowCol implements Serializable,  JsonSerializable{
	private int xCoord;
	//private int row;
	//private int col;
	private int yCoord;
	private int width;
	private int height;
	
	/**
	 * Default Ctor. Initial coordinates at 0,0, width and height 1, 1,
	 */
	public RowCol() {
		this.xCoord = 0;
		this.yCoord = 0;
		this.width = 1;
		this.height = 1;
		//this.row = yCoord;
		//this.col = xCoord;
	}
	
	public RowCol(RowCol c) {
		this.xCoord = c.getxCoord();
		this.yCoord = c.getyCoord();
		this.width = c.getWidth();
		this.height = c.getHeight();
		//this.row = yCoord;
		//this.col = xCoord;
	}
	
	/**
	 * Ctor, default width and height is 1, 1
	 * @param x
	 * @param y
	 */
	public RowCol(int x, int y) {
		this.xCoord = x;
		this.yCoord = y;
		this.width = 1;
		this.height = 1;
		//this.row = yCoord;
		//this.col = xCoord;
	}
	
	public RowCol(int x, int y, int width, int height) {
		this.xCoord = x;
		this.yCoord = y;
		this.width = width;
		this.height = height;
		//this.row = yCoord;
		//this.col = xCoord;
	}

	public int getxCoord() {
		return xCoord;
	}

	public void setxCoord(int xCoord) {
		if( xCoord >=0 ) {//&& xCoord < gameBoard.getColumns()) {
			this.xCoord = xCoord;
			//this.row = xCoord;
		}
	}

	public int getyCoord() {
		return yCoord;
	}

	public void setyCoord(int yCoord) {
		if ( yCoord >= 0 ) {//&& yCoord < gameBoard.getRows()) {
			this.yCoord = yCoord;
			//this.col = yCoord;
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		if( width > 0 ) //&& width < gameBoard.getColumns())
			this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		if( height > 0 )//&& height < gameBoard.getRows())
			this.height = height;
	}
	
	
	public RowCol(RowCol rc, int deltarow, int deltacol)
	{
		//this.row = rc.row + deltarow;
		this.xCoord = rc.getxCoord() + deltarow;
		//this.col = rc.col + deltacol;
		this.yCoord = rc.getyCoord() + deltacol;
	}
    
    


    
    public String toString() {
        //return "(R:" + row + ",C:" + col + ")";
    	return "(R:" + this.xCoord + ",C:" + this.yCoord + ")";
    }

    public String toUnderstandableString() {
        //return "(R:" + row + ",C:" + col + ")";
    	return "(R:" + (this.xCoord+1) + ",C:" + (this.yCoord+1) + ")";
    }

    
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + yCoord;
		result = prime * result + xCoord;
		return result;
	}

    /**
     * Determine if this position equals another.
     * @param other The other position to compare to.
     * @return Whether the two RowCol objects refer to the same position.
     */
    public boolean equals(Object o) {
    	RowCol other = (RowCol) o;
        return (xCoord == other.xCoord && yCoord == other.yCoord);
    }
    
    /**
        Calculate distance from here to some other location (SGF)
    */
    public int dist(RowCol other)
    {
        return Math.abs(this.xCoord - other.xCoord) + Math.abs(this.yCoord - other.yCoord);
    }

    /**
     * Determine whether two (row,col) positions are neighbors.
     * @param rc1 The first position.
     * @param rc2 The second position.
     * @return Whether the positions are in fact neighbors.
     */
    public static boolean areNeighbors(RowCol rc1, RowCol rc2) {
        return ((Math.abs(rc1.xCoord - rc2.xCoord) +
                Math.abs(rc1.yCoord - rc2.yCoord)) == 1);
    }
    
    /**
     * Gets the neighbor position of a position
     * @param board Board of the game
     * @return Neighbor positions
     * @author ilke
     */
    public LinkedHashSet<RowCol> getNeighbors(Board board) {
    	LinkedHashSet<RowCol> neighbors = new LinkedHashSet<RowCol>();
    	
    	if( yCoord-1 >= 0 )
    		neighbors.add(new RowCol(xCoord, yCoord-1));
    	if( xCoord-1 >= 0 )
    		neighbors.add(new RowCol(xCoord-1, yCoord));
    	if( yCoord + 1 < board.getColumns())
    		neighbors.add(new RowCol(xCoord, yCoord+1));
    	if( xCoord + 1 < board.getRows())
    		neighbors.add(new RowCol(xCoord+1, yCoord));
    	
    	return neighbors;
    }

	public int getRow() {
		return getxCoord();
	}

	public void setRow(int row) {
		this.setxCoord(row);
		
	}

	public int getCol() {
		return getyCoord();
	}

	public void setCol(int col) {
		this.setyCoord(col);
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject rowCol= new JsonObject();
		rowCol.put("xCoord", xCoord);
		rowCol.put("yCoord", yCoord);
		rowCol.put("width", width);
		rowCol.put("height", height);
		return rowCol;
	}
	public RowCol(JsonObject rowCol) {
		this.xCoord = rowCol.getInteger("xCoord");
		this.yCoord = rowCol.getInteger("yCoord");
		this.width = rowCol.getInteger("width");
		this.height = rowCol.getInteger("height");		
	}
	
    
    
    
}
