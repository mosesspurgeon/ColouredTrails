package coloredtrails.alglib;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;

import coloredtrails.common.Board;
import coloredtrails.common.ChipSet;
import coloredtrails.common.CtColor;
import coloredtrails.common.RowCol;
import coloredtrails.common.Scoring;
import coloredtrails.common.Tile;
import coloredtrails.gui.types.Path;
import coloredtrails.gui.types.PathAndChips;




/**
 * Returns the shortest paths between a start and an end point while taking the weights
 * of the square colors into account. The path with the smallest weight is the best. The
 * algorithm however, does not terminate after getting the best one possible, but continues
 * to get other options with same or more weights.
 *<p>
 * !!! Will be modified to work more efficient !!!
 * @author ilke
 *
 */
public class ShortestPaths {
	/**
	 * Returns an ArrayList of shortest paths between two points, according to 
	 * the weights of the squares
	 * @param start Starting point
	 * @param end Ending point
	 * @param board Board that the path is on
	 * @param scoring Scoring function of the game
	 * @param maxPathAmount Max no. of paths returned. Set to Integer.MAX_VALUE for all paths
	 * @return An ArrayList of shortest paths
	 * @author ilke
	 */
	   static public int NUM_PATHS_RELEVANT = 30 ;
	   
	   
	   
	   static ArrayList<RowCol> getNeighbours(RowCol startPosition, RowCol endPosition, Board board){
		   ArrayList<RowCol> neighbours = new ArrayList<RowCol>();
		   
		   int xCoord = startPosition.getxCoord();
		   int yCoord = startPosition.getyCoord();
		   int goalXCoord = endPosition.getxCoord();
		   int goalYCoord = endPosition.getyCoord();
		   
		   int distY = Math.abs(goalYCoord - yCoord);
		   int distX = Math.abs(goalXCoord - xCoord);
		   
		 	if( yCoord-1 >= 0 ) {
		 		//make sure it's heading in the goals direction
		 		if(Math.abs(goalYCoord - (yCoord-1)) <= distY)
		 			neighbours.add(new RowCol(xCoord, yCoord-1));
		 	}
	    	if( xCoord-1 >= 0 ) {
		 		//make sure it's heading in the goals direction
		 		if(Math.abs(goalXCoord - (xCoord-1)) <= distX)
		 			neighbours.add(new RowCol(xCoord-1, yCoord));
	    	}
	    	if( yCoord + 1 < board.getColumns()) {
		 		//make sure it's heading in the goals direction
		 		if(Math.abs(goalYCoord - (yCoord+1)) <= distY)
		 			neighbours.add(new RowCol(xCoord, yCoord+1));
	    	}
	    	if( xCoord + 1 < board.getRows()) {
		 		//make sure it's heading in the goals direction
		 		if(Math.abs(goalXCoord - (xCoord+1)) <= distX)
		 			neighbours.add(new RowCol(xCoord+1, yCoord));
	    	}
	    	
	    	return neighbours;
		   
		   
	   }
	   
	   
		/**
		 * Calculates all Manhatten paths from initial coordinate to goal. 
		 * Returns the shortest one which the player has the most chips for already. 
		 * @param start
		 * @param g
		 * @return
		 */
		public static ArrayList<Path> calcManhattanDistancePaths(RowCol startPosition,RowCol goalPosition, Board board, Scoring scoring, int maxPathAmount){
			ArrayList<Path> paths = new ArrayList<Path>();
			PriorityQueue<Path> queue = new PriorityQueue<Path>();
			
			Path path = new Path(board, scoring);
			path.addPathPoint(startPosition);
			queue.offer(path);
			Path p;
			ArrayList<RowCol> neighbors;
			int counter = 0;
			
			while(!queue.isEmpty() && counter<maxPathAmount) {
				p = queue.poll();
				
				if(p.getEndPoint().equals(goalPosition)) {
					paths.add(p);
					counter++;
				}
				else
				{
					neighbors = (ArrayList<RowCol>) getNeighbours(p.getEndPoint(),goalPosition,board);
					
					for(RowCol rc:neighbors) {
						if(!p.contains(rc)) {
							
							Path newpath = new Path(p);
							newpath.addPathPoint(rc);			
							queue.offer(newpath);			
						}
					}
				}
			}
			return paths;  
		}
	   
	   
	   
	   public static ArrayList<Path> getShortestPathsNew(RowCol startPosition,RowCol goalPosition, Board board, Scoring scoring, int maxPathAmount) {
			ArrayList<Path> paths = new ArrayList<Path>();
			PriorityQueue<Path> queue = new PriorityQueue<Path>();
			
			Path path = new Path(board, scoring);
			path.addPathPoint(startPosition);
			queue.offer(path);
			Path p;
			LinkedHashSet<RowCol> neighbors;
			int counter = 0;
			
			while(!queue.isEmpty() && counter<maxPathAmount) {
				p = queue.poll();
				
				if(p.getEndPoint().equals(goalPosition)) {
					paths.add(p);
					counter++;
				}
				else
				{
					neighbors = (LinkedHashSet<RowCol>) p.getEndPoint().getNeighbors(board);
					
					for(RowCol rc:neighbors) {
						if(!p.contains(rc)) {
							
							Path newpath = new Path(p);
							newpath.addPathPoint(rc);			
							queue.offer(newpath);			
						}
					}
				}
			}
			return paths;  
	   }
	   
	   
	    public static ArrayList<Path> getShortestPaths(Tile start,Tile end, Board board, Scoring scoring, int maxPathAmount)
		{
			ArrayList<Path> paths = new ArrayList<Path>();
			PriorityQueue<Path> queue = new PriorityQueue<Path>();
			
			Path path = new Path(board, scoring);
			path.addPathPoint(start.getCoordinates());
			queue.offer(path);
			Path p;
			LinkedHashSet<RowCol> neighbors;
			int counter = 0;
			
			while(!queue.isEmpty() && counter<maxPathAmount) {
				p = queue.poll();
				
				if(p.getEndPoint().equals(end)) {
					paths.add(p);
					counter++;
				}
				else
				{
					//if(p.getNumPoints()<10) {
						neighbors = (LinkedHashSet<RowCol>) p.getEndPoint().getNeighbors(board);
						
						for(RowCol rc:neighbors) {
							if(!p.contains(rc)) {
								
								Path newpath = new Path(p);
								newpath.addPathPoint(rc);			
								queue.offer(newpath);			
							}
						}
					//}
				}
			}
			return paths;
		}
	    
	    
	    public static ArrayList<Path> getShortestPaths(RowCol start,RowCol end, Board board, Scoring scoring, int maxPathAmount)
		{
			ArrayList<Path> paths = new ArrayList<Path>();
			PriorityQueue<Path> queue = new PriorityQueue<Path>();
			
			Path path = new Path(board, scoring);
			path.addPathPoint(start);
			queue.offer(path);
			Path p;
			LinkedHashSet<RowCol> neighbors;
			int counter = 0;
			
			while(!queue.isEmpty() && counter<maxPathAmount) {
				p = queue.poll();
				
				if(p.getEndPoint().equals(end)) {
					paths.add(p);
					counter++;
				}
				else
				{
					neighbors = (LinkedHashSet<RowCol>) p.getEndPoint().getNeighbors(board);
					
					for(RowCol rc:neighbors) {
						if(!p.contains(rc)) {
							
							Path newpath = new Path(p);
							newpath.addPathPoint(rc);			
							queue.offer(newpath);			
						}
					}
				}
			}
			return paths;
		}
	    
	    
		public static ArrayList<Path> getShortestPaths(RowCol start, RowCol end, Board board, Scoring scoring, int maxPathAmount, ChipSet myChips, ChipSet opChips)
		{
			ArrayList<Path> paths = new ArrayList<Path>();

			PriorityQueue<PathAndChips> queue = new PriorityQueue<PathAndChips>();
			
			ChipSet unionCs = ChipSet .addChipSets(myChips, opChips);
			Path path = new Path(board, scoring);
			path.addPathPoint(start);
			PathAndChips pathAndCs = new PathAndChips(path, unionCs);
			queue.offer(pathAndCs);

			PathAndChips pCs;
			
			LinkedHashSet<RowCol> neighbors;
			//FileLogger.getInstance("Agent").writeln("mychips are: " + myChips);
			//FileLogger.getInstance("Agent").writeln("opchips are: " + opChips);
			int counter = 0;
			while(!queue.isEmpty() && counter<maxPathAmount) {
				pCs = queue.poll();
				
				if(pCs.p.getEndPoint().equals(end)) {
					paths.add(pCs.p);
					counter++;
				}
				else
				{
					neighbors = (LinkedHashSet<RowCol>) pCs.p.getEndPoint().getNeighbors(board);
					//FileLogger.getInstance("Agent").writeln("In getShortestPaths, looking at path: " + pCs.p);
					//FileLogger.getInstance("Agent").writeln("neighbors are: " + neighbors);
					for(RowCol rc:neighbors) {
						if(!pCs.p.contains(rc)) {
							CtColor color = board.getTile(rc).getColor();
							int unionChipsNum = pCs.cs.getNumChips(color);
							//FileLogger.getInstance("Agent").writeln("Color is: " + color);

							//FileLogger.getInstance("Agent").writeln("unionChips.getNumChips(color)= " + unionChipsNum);
							//We consider the path p + rc if and only if at least one of the players has a chip of this color
							
							//if(myChipsColorNum > 0 || opChipsColorNum > 0) {
							if(unionChipsNum > 0) {
								Path newpath = new Path(pCs.p);
								newpath.addPathPoint(rc);
								ChipSet newCs = new ChipSet(pCs.cs);
								newCs.setNumChips(color, unionChipsNum - 1);
								PathAndChips newpathCs = new PathAndChips(newpath, newCs);
								queue.offer(newpathCs);
								//if(myChipsColorNum > 0)
								//	myChips.setNumChips(color, myChipsColorNum -1);
								//else
								//	opChips.setNumChips(color, opChipsColorNum-1);
							}
						}
					}
				}
			}
			return paths;
		}
		

		public static ArrayList<Path> getShortestPathsForMyChips(RowCol start, RowCol end, Board board, Scoring scoring, int maxPathAmount, ChipSet myChips) {
			ChipSet emptyCS = new ChipSet();
			return getShortestPaths( start,  end,  board,  scoring, maxPathAmount,  myChips, emptyCS);
		}

		public static ArrayList<Path> getShortestPathsForOpChips(RowCol start, RowCol end, Board board, Scoring scoring, int maxPathAmount, ChipSet opChips) {
			ChipSet emptyCS = new ChipSet();
			return getShortestPaths( start,  end,  board,  scoring, maxPathAmount, emptyCS, opChips);
		}
	    /**
		 * Returns an array list of first 10 paths from the starting point to the first goal on the board
		 * Used in PhaseLoopPlayer class to simplify the usage of the getShortestPaths method
		 * @param start Starting position
		 * @param board Board of the game
		 * @param scoring Scoring of the game
		 * @return 10 best paths from the starting point to the first goal
		 */
		public static ArrayList<Path> getShortestPathsToFirstGoal(RowCol start, Board board, Scoring scoring) {
			//return getShortestPaths(start, board.getGoalLocations().get(0), board, scoring, 10);
			return getShortestPaths(start, board.getGoalLocations().get(0), board, scoring, 10);
		}
		
		/**
		 * Returns all paths (w/o backtracking - circling) to the first goal
		 * Paths are ordered according to their weights
		 * @param start Starting position
		 * @param board Board of the game
		 * @param scoring Scoring of the game
		 * @return All paths from the starting point to the first goal
		 */
		public static ArrayList<Path> getPathsToFirstGoal(RowCol start, Board board, Scoring scoring) {
			return getShortestPaths(start, board.getGoalLocations().get(0), board, scoring, Integer.MAX_VALUE);
		}
}
