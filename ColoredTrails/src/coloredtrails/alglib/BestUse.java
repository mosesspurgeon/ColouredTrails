package coloredtrails.alglib;

/*
Colored Trails

Copyright (C) 2006, President and Fellows of Harvard College.  All Rights Reserved.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import coloredtrails.common.Board;
import coloredtrails.common.Boot;
import coloredtrails.common.ChipSet;
import coloredtrails.common.CtColor;
import coloredtrails.common.Exchange;
import coloredtrails.common.Game;
import coloredtrails.common.Goal;
import coloredtrails.common.Player;
import coloredtrails.common.PlayerStatus;
import coloredtrails.common.RowCol;
import coloredtrails.common.Scoring;
import coloredtrails.gui.types.Path;
import coloredtrails.gui.types.TileGoal;
import coloredtrails.ui.ColoredTrailsBoardGui;




/**
Calculates the best use of chips to maximize score
<p>
Currently assumes that there is a single, common goal.  We
can make this class more general by supplying a target location
to the constructor (and this target location needn't even be
a goal square).
<p>
Uses simple DFS to find best paths for now.
<p>
scoring function in CGDR doesn't help us if we want to be able to 
perform client-side calculations without a lot of network traffic

@author Sevan G. Ficici
*/
public class BestUse
{
	/** player's pre-move status (pre- or post-exchange) */
	//private PlayerStatus ps;
	/** the scoring rule we're using */
	Scoring s;
	/** the type of goal we are seeking */
	TileGoal goal_type;
	Goal goal;

	/** (one of) the best post-move state(s) for the player */
	private PlayerStatus beststate;
	
	/** set of best paths for player (working representation) */
	private ArrayList<ArrayList<PlayerStatus>> bestpathset;
	/** set of best paths for player, represented as ArrayList<Path> */
	private ArrayList<Path> paths;
	/** the game board */	
	private static Board board;
	/** the goal's location */
	private RowCol goal_loc;  // currently needed to pass to Scoring
	
	// FIX CONSTRUCTOR to accept a goal type
	// WE CAN IMPROVE base-case of dfs() so that it directly checks the board to
	// see if there is a goal there, rather than comparing against a pre-determined
	// goal location
	// FURTHER, if different goals are worth different amounts, then we might want
	// to traverse one goal square on the way to another goal, in which case we
	// don't want landing on a goal to be a base-case for dfs(); of course, this
	// will make search take longer, since we will then only stop once chips run out
	/**
		Constructor
		
		@param gs         the current game state
		@param ps         the player's current state
		@param s          the scoring rule we're using
	@param goal_type  the type of goal we are seeking
	*/
	public BestUse(Game gs, PlayerStatus ps, Scoring s, TileGoal goal_type)
	{
	        this(gs.getBoard(), ps, s, goal_type);
	}
    
    /**
     * Calls BestUse with goal type = 0
     * @param gs gamestatus
     * @param ps playerstatus
     */
    public BestUse(Game gs, PlayerStatus ps){
        this(gs.getBoard(), ps, gs.getScoring(), Goal.DEFAULT_GOAL_TYPE);
    }

    
    /**
     * Gets a gameStatus, player and goal and calculates the best path to that goal and updates the player 
     * BestPath, chipsUsedInBestPath and ChipsNotUsedInBestPath members
     * @param gs
     * @param p
     * @param g
     */
    public BestUse(Game gs, Goal g) {
    	this.goal = g; 
    	BestUse.board = gs.getBoard();
    	this.s = gs.getScoring();
    	this.goal_loc = new RowCol(g.getX(),g.getY());

    }
    
    /**
     * A method that updates the information received by the player regarding the requested amount of chips of a certain color
     * from an agent
     * @param color
     * @param agent
     * @param amount
     */
    public static void updateHumanRequestChip(int amount, String agentName, String color ) {
    	CtColor newColor = new CtColor(color);
    	System.out.println(newColor.getColorNum());
    	ColoredTrailsBoardGui.game.getHumanPlayers().get(0).addExchangeInitiated(new Exchange(agentName,amount,new CtColor(color)));
    }
    

	public void calcNeededChipsServer(Game game) {
		for(Player p : game.getAllPlayers()) {
			Path improvedPath = improvePath(game, p, game.getGoals().get(0));
			
			if(improvedPath!=null) {
				
				p.setImprovedPath(improvedPath);//set ImprovedPath for the player
				//calculate the needed chips for the improved path that I don't have
				calcChipsNeededFromOtherPlayersServer(p, improvedPath);
			}
			else
				p.setReachedGoal(true);
			
		}
		
	}
	public String calcNeededChipsClient(Game game) {
		String output = new String();
		for(Player p : game.getAllPlayers()) {
			
			
			String playerName = p.getPlayerName();
			output += " Player name : " +((playerName.equals(ColoredTrailsBoardGui.humanClient.getGameBoardPlayerName())) ? "Me"
					: playerName) + "\n";
			if(!p.getReachedGoal()) {
				output += "New Shortest Path\n";
				output += p.getImprovedPath().toUnderstandableString() + "\n";
			}
		
			Boot.logger.info(output);
		}
		
		return output;
	}
    
    
    
/*    @Deprecated
	public String calcNeededChips(Game game) {
		String output = new String();
		for(Player p : game.getAllPlayers()) {
			output += " Player name : " + p.getPlayerName() + "\n";
			Path improvedPath = improvePath(game, p, game.getGoals().get(0));
			
			if(improvedPath!=null) {
				output += "New Shortest Path\n";
				output += improvedPath.toString() + "\n";
				
				p.setImprovedPath(improvedPath);//set ImprovedPath for the player
				//calculate the needed chips for the improved path that I don't have
				calcChipsNeededFromOtherPlayers(p, improvedPath);
			}
			else
				p.setReachedGoal(true);
			
			Boot.logger.info(output);
		}
		
		return output;
	}*/
	
	public void performeExchangesServer(Game game) {

		for(Player p : game.getAllPlayers()) {
			for(Exchange exchange : p.getExchangesInitiated()) {
				if(exchange.isOwnerApproved()) {
					//Chips being offered also needs to be removed and added to the exchange reciever
					//Needs to be decided as to what happens if the offer is invalid
					
					for(CtColor color : exchange.getChipsAsked().keySet()) {
						//current exchange happens only if chips are not used
						//this should be changed to exchange if owner has given approval
						//it should also check if the lender has the requested chips available
						
						boolean chipsGiven=game.getPlayer(exchange.getAgentName()).giveColors(exchange.getChipsAsked().get(color),color);
						//we first take the chips from lender and then add to requestor
						//this will make sure earlier bug is avoided
						if(chipsGiven)
							p.addColors(exchange.getChipsAsked().get(color),color);
						
						//Logic for chip offerings also needs to be added.
					}
				}
			}
			p.emptyExchanges();
		}
	}
	public String performeExchangesClient(Game game) {
		String output = new String();
		output+="These Exchanges were initiated by following players: \n";
		for(Player p : game.getAllPlayers()) {
			
			String playerName = p.getPlayerName();
			output+="Exchange Initiator : "+((playerName.equals(ColoredTrailsBoardGui.humanClient.getGameBoardPlayerName())) ? "Me"
					: playerName)+"\n";
			for(Exchange exchange : p.getExchangesInitiated()) {
				output += "       "+exchange.toString();
				output += "\n";
			}
		}	
		output += "\n";

		output+="These Exchanges  were sent to following players for approval: \n";
		for(Player p : game.getAllPlayers()) {
			
			String playerName = p.getPlayerName();
			output+="Exchange Reciever : "+((playerName.equals(ColoredTrailsBoardGui.humanClient.getGameBoardPlayerName())) ? "Me"
					: playerName)+"\n";
			for(Exchange exchange : p.getExchangesReceived()) {
				output += "       "+exchange.toString();
				output += "\n";
			}
		}	
		output += "\n";
		
		output+="The exchanges will need to be approved/rejected in this phase";
		return output;
	}
/*	*//**
	 * Try to perform the exchanges calculated before. Only exchanges that do not coincide will be performed. 
	 * @param game
	 *//*
    @Deprecated
	public String performeExchanges(Game game) {
		String output = new String();
		output += chooseFirstRequest(game);
		output+="Perform These Exchanges : \n";
		for(Player p : game.getAllPlayers()) {
			for(Exchange exchange : p.getExchangesInitiated()) {
				output += exchange.toString();
				for(CtColor color : exchange.getChipsAsked().keySet()) {
					p.addColors(exchange.getChipsAsked().get(color),color);//we seem to first give the chips to requestor and then try and remove from provider sometimes add is passing subtracting is failing
					game.getPlayer(exchange.getAgentName()).giveColors(exchange.getChipsAsked().get(color),color);
				}
				output += "\n";
			}
			p.emptyExchanges();
		}

		return output;
	}*/
	
	
	/**
	 * A method that checks if an exchange is valid and returns true or false.
	 * @param game
	 * @param exchange
	 * @return
	 */
	boolean checkIfExchangeIsValid(Game game, Player p,Exchange exchange) {
		return true;
	}
    
	/**
	 * In the case of multiple requests that are the same, choose the first request
	 * @param game
	 */
	public String chooseFirstRequest(Game game) {
		String output = new String();
		for(Player p : game.getAllPlayers()) {
			output+="\n" + p.getPlayerName() + " check if there are multiple exchanges :\n";
			for(Exchange exchange : p.getExchangesInitiated()) {
				output += exchange.toString();
				
				HashMap<CtColor,Integer> chipsAsked = exchange.getChipsAsked();
				for(CtColor color : chipsAsked.keySet()) {
					int numChipsAsked = exchange.getChipsAsked().get(color);
					//check how many of these exchanges I can allow
					int numChipsToGive = game.getPlayer(exchange.getAgentName()).getChipsNotUsedInBestPath().getNumChips(color);
					output+= "\n Num of chips to give is "+ Integer.toString(numChipsToGive) + "\n";
					numChipsToGive-=numChipsAsked;
					for(Player p_2 : game.getAllPlayers()) {
						//Go over other exchanges
						if(!p_2.getPlayerName().equals(p.getPlayerName())) {
							for(Exchange exchange_2 : p_2.getExchangesInitiated()) {
								if(exchange_2.asksSameAgentForSameColor(exchange)) {
									//if the agent has no more chips of that color to give don't allow the exchange
									if(numChipsToGive<=0) {
										output += "\n" + p_2.getPlayerName() + " removed exchange\n";
										//remove the exchange from the exchanges arraylist
										p_2.removeExchange(exchange_2);
										break;
									}
									else //reduce the number of chips the agent can allow
									{
										int requestedChipNum = exchange_2.getChipsAsked().get(color);
										if(requestedChipNum<=numChipsToGive)
											numChipsToGive-=requestedChipNum;
										else //update the request to ask for the amount of chips that remains
										{
											exchange_2.adjustNumChipsAsked(color,numChipsToGive);
											numChipsToGive=0;
										}
									}
										
								}
							}
						}
					}
				}
			}
		}
		return output;
		
	}
	
	public void predictExchanges(Game game) {
		for(Player p : game.getAllPlayers()) {
			if(p.isReachedGoal()==false)//If the player has not reached the goal
			{
				//Go over all of the chips I need
				ChipSet neededChips = p.getChipsMissingFromBestPath(); 
				//Go over colors of chips in the needed chips chipset
				for(CtColor color : neededChips.getColors()) {
					int numOfNeededChips = neededChips.getNumChips(color);
					//Look for players to exchange with
					for(Player q : game.getAllPlayers()) {
						//Do not exchange with myself
						if(!q.getPlayerName().equals(p.getPlayerName())) {
							if (q.getChipsNotUsedInBestPath().contains(color)) {
								//how much chips the agent can spare
								int numOfSpareChips = q.getChipsNotUsedInBestPath().getNumChips(color);
								int numChipsAskedFor = 0;
								if(numOfSpareChips >= numOfNeededChips)
									numChipsAskedFor = numOfNeededChips;
								else
									numChipsAskedFor = numOfSpareChips;
								//Reduce the number of chips I need by the number of chips already asked for
								numOfNeededChips-=numChipsAskedFor;
								p.addExchangeInitiated(new Exchange(q.getPlayerName(),numChipsAskedFor,color));
								//If I have found someone to give me all needed chips don't need to ask others
								if(numOfNeededChips==0)
									break;
							}			
						}
					}
				}//End of needed colors loop
			}
		}
		
	}
	
	public String calcExchangesClient(Game game) {
		String output = new String();
		output += "\n---- Calculate Needed Exchanges ---- \n";
		for(Player p : game.getAllPlayers()) {
			if(p.isReachedGoal()==false)//If the player has not reached the goal
			{
				
				String playerName = p.getPlayerName();
				output += "Player " + ((playerName.equals(ColoredTrailsBoardGui.humanClient.getGameBoardPlayerName())) ? "Me"
						: playerName) + " needs to make the following exchange : \n";
				for(Exchange exchange : p.getExchangesInitiated()) {
					String agentName = exchange.getAgentName();
					HashMap<CtColor, Integer> chipsAsked = exchange.getChipsAsked();
					HashMap<CtColor, Integer> chipsOffered = exchange.getChipsOffered();
					
					for (Map.Entry<CtColor, Integer> entry : chipsAsked.entrySet()) {
						CtColor color = entry.getKey();
						Integer numChipsAskedFor = entry.getValue();
						output += "     From " + agentName + "  needs " + numChipsAskedFor + " chip color " + color.getColorName() + "\n";
					}
					
					for (Map.Entry<CtColor, Integer> entry : chipsOffered.entrySet()) {
						CtColor color = entry.getKey();
						Integer numChipsOffered = entry.getValue();
						output += "     To" + agentName + "  offers " + numChipsOffered + " chip color " + color.getColorName() + "\n";
					}
					
				}				

			}
		}

		Boot.logger.info(output);
		return output;
		
	}
	
	
    /**
     * Gets the current game status and calculates the chip exchanges according to the current best calculated path. 
     * Must be calculated before !!!! Returns a string describing the exchanges
     * @param game
     * @return String
     */
	@Deprecated
	public String calcExchanges(Game game) {
		String output = new String();
		output += "---- Calculate Needed Exchanges ---- \n";
		for(Player p : game.getAgentPlayers()) {
			if(p.isReachedGoal()==false)//If the player has not reached the goal
			{
				output += "Player " + p.getPlayerName() + " needs to make the following exchange : \n";
				//Go over all of the chips I need
				ChipSet neededChips = p.getChipsMissingFromBestPath(); 
				//Go over colors of chips in the needed chips chipset
				for(CtColor color : neededChips.getColors()) {
					int numOfNeededChips = neededChips.getNumChips(color);
					//Look for players to exchange with
					for(Player q : game.getAgentPlayers()) {
						//Do not exchange with myself
						if(!q.getPlayerName().equals(p.getPlayerName())) {
							if (q.getChipsNotUsedInBestPath().contains(color)) {
								//how much chips the agent can spare
								int numOfSpareChips = q.getChipsNotUsedInBestPath().getNumChips(color);
								int numChipsAskedFor = 0;
								if(numOfSpareChips >= numOfNeededChips)
									numChipsAskedFor = numOfNeededChips;
								else
									numChipsAskedFor = numOfSpareChips;
								//Reduce the number of chips I need by the number of chips already asked for
								numOfNeededChips-=numChipsAskedFor;
								output += "Needs " + numChipsAskedFor + " chip color " + color.getColorNum() + " from " + q.getPlayerName() + "\n";
								p.addExchangeInitiated(new Exchange(q.getPlayerName(),numChipsAskedFor,color));
								//If I have found someone to give me all needed chips don't need to ask others
								if(numOfNeededChips==0)
									break;
							}			
						}
					}
				}//End of needed colors loop
			}
		}

		Boot.logger.info(output);
		return output;
		
	}
    
    

    
    
    /**
     * Gets the current game Status. Goes over all players and calculates the best path for each one. Updates 
     * their bestPath member
     * Returns a string that describes the best paths 
     * @param game
     * @return String
     */
	
    public void calcBestPathsServer(Game game) {
		for(Player p : game.getAllPlayers()) {
			calcBestPath(p);
		}
    	
    }
    
    public String calcBestPathsClient(Game game) {
		String outputLog = new String();
		
		for(Player p : game.getAllPlayers()) {
			ArrayList<PlayerStatus> bestPath = p.getBestPathToGoal();
			String playerName = p.getPlayerName();
			
			
			outputLog += "Player name : " + ((playerName.equals(ColoredTrailsBoardGui.humanClient.getGameBoardPlayerName())) ? "Me"
					: playerName) + "\n";
			outputLog += "Player position : " + p.getCurrentTile().getLocation().toString()+ "\n";
			ArrayList<CtColor> chipColors = p.getChips().getChips();
			outputLog += "Player chip number " + chipColors.size() + "\n";
			outputLog += "Player chip allocation : \n";
 
			for(CtColor c : chipColors)
				outputLog += c.getColorNum() + " ";


			outputLog += "\nLength of Best path : " + p.getBestPathToGoal().size() + "\n";
			outputLog += "Best path:\n";
			for(PlayerStatus playerstat : bestPath) 
				outputLog += playerstat.getPosition().toUnderstandableString() + " ";

			outputLog += "\n";
		}

		Boot.logger.info(outputLog);
	
		return outputLog;
     	
    }
	
    @Deprecated
    public String calcBestPaths(Game game) {
		String outputLog = new String();
	
		for(Player p : game.getAllPlayers()) {
			calcBestPath(p);
			ArrayList<PlayerStatus> bestPath = p.getBestPathToGoal();
			outputLog += "Player name : " + p.getPlayerName() + "\n";
			outputLog += "Player position : " + p.getCurrentTile().getLocation().toString()+ "\n";
			ArrayList<CtColor> chipColors = p.getChips().getChips();
			outputLog += "Player chip number " + chipColors.size() + "\n";
			outputLog += "Player chip allocation : \n";
 
			for(CtColor c : chipColors)
				outputLog += c.getColorNum() + " ";


			outputLog += "\nLength of Best path : " + p.getBestPathToGoal().size() + "\n";
			outputLog += "Best path:\n";
			for(PlayerStatus playerstat : bestPath) 
				outputLog += playerstat.getPosition().toString() + " ";

			outputLog += "\n";
		}

		Boot.logger.info(outputLog);
	
		return outputLog;
    }

    
    public void calcBestPath(Player p ) {

    	// !!! the system is not calculating player scores as player state changes !!!
    	PlayerStatus psstart = new PlayerStatus(p.getPlayerStatus());// copy constructor
    	psstart.setScore((int)Math.floor(s.score(psstart, goal_loc)));// calculate score of current state

    	ArrayList<ArrayList<PlayerStatus>> foundPaths = new ArrayList<ArrayList<PlayerStatus>>();
    	
    	this.new_dfs(psstart,foundPaths);
    	
    	ArrayList<PlayerStatus> endStatus = new ArrayList<PlayerStatus>();
    	int bestPathIndex = 0;
    	
    	//If it has found direct paths to the goal
    	if(foundPaths.size()>0) {
	    	for( ArrayList<PlayerStatus> path : foundPaths) {
	    		int size = path.size();  		
	    		endStatus.add(path.get(size-1));
	    	}
	    	bestPathIndex = getBestPath(endStatus);
	    	ArrayList<PlayerStatus> bestPath = foundPaths.get(bestPathIndex);
	    	p.setBestPathToGoal(foundPaths.get(bestPathIndex));
	    	//calculate the chips used for best path and chips left over
	    	calcChipsNeededToReachGoal(p,bestPath);
    	}
    	else {
    		p.setBestPathToGoal(null);
    	}
    }
    

    
    
    /**
     * Receives the player and best path to reach the goal and updates the players 
     * chipsUsedInBestPath and ChipsNotUsedInBestPath members
     * @param p
     * @param bestPath
     */
    public static void calcChipsNeededToReachGoal(Player p, ArrayList<PlayerStatus> bestPath) {
    	ChipSet needed = new ChipSet();
    	for(PlayerStatus ps : bestPath) {
    		//Don't need a chip for the current position
    		if(!ps.getPosition().equals(p.getCurrentTile().getCoordinates()))
    			needed.add(new CtColor(board.getTile(ps.getPosition()).getColor()),1);
    	}
    	p.setChipsUsedInBestPath(needed);
    	p.setChipsNotUsedInBestPath();
    	
    }
    
    public int calcNumChipsNeededFromOtherPlayers(Player playerStatus2, Path newPath) {
    	int i = 0;
    	String loggerOutput = new String();
    	loggerOutput += "calcNumChipsNeededFromOtherPlayers\n Player name " + playerStatus2.getPlayerName()+"\n";
    	loggerOutput += "Path " + newPath.toString();
    	ChipSet allNeeded = new ChipSet();
    	LinkedList<RowCol> pathList= newPath.getPoints();
    	for(RowCol pos : pathList) {
    		if(i!=0)//don't need the color of the tile i'm on
    			allNeeded.add(new CtColor(BestUse.board.getTile(pos).getColor()),1);
    		i++;
    	}
    	
    	loggerOutput +="All Needed Chips : ";
    	for(CtColor color : allNeeded.getChips()) {
    		loggerOutput +=  + color.getColorNum() + "; ";
    	}
    	loggerOutput += "\n";
    	
    	loggerOutput +="Chips Used In Best Path : ";
    	for(CtColor color : playerStatus2.getChipsUsedInBestPath().getChips())
    		loggerOutput +="Color index " + color.getColorNum() + "; ";
    	loggerOutput += "\n";
    	
    	loggerOutput +="Chips Not Used In Best Path : ";
    	for(CtColor color : playerStatus2.getChipsNotUsedInBestPath().getChips())
    		loggerOutput +="Color index " + color.getColorNum() + "; ";
    	loggerOutput += "\n";
    	
    	
    	ChipSet actualNeeded = ChipSet.subChipSets(allNeeded, playerStatus2.getChipsNotUsedInBestPath());  	
    	
    	//print the chips I actually need to ask other agents for in order to reach the goal
    	loggerOutput +="Player " + playerStatus2.getPlayerName() + " needs to request the following chips : ";
    	for(CtColor color : actualNeeded.getChips()) {
    		loggerOutput +="Color index "+ color.getColorNum() + "; ";
    	}
    	loggerOutput += "\n";
    	loggerOutput +="NEEDS "  + actualNeeded.getChips().size()+"\n";
    	Boot.logger.info(loggerOutput);
    	return actualNeeded.getChips().size();
    }
    
    public void calcChipsNeededFromOtherPlayersServer(Player p, Path newPath) {
    	int i = 0;
    	String loggerOutput = new String();
    	loggerOutput +="calcChipsNeededFromOtherPlayers function";
    	loggerOutput +=p.getPlayerName();
    	loggerOutput +=newPath.toString();
    	ChipSet allNeeded = new ChipSet();
    	LinkedList<RowCol> pathList= newPath.getPoints();
    	for(RowCol pos : pathList) {
    		if(i!=0)//don't need the color of the tile i'm on
    			allNeeded.add(new CtColor(BestUse.board.getTile(pos).getColor()),1);
    		i++;
    	}
    	
    	loggerOutput += "All Needed Chips : ";
    	for(CtColor color : allNeeded.getChips())
    		loggerOutput +="Color index " + color.getColorNum() + "; ";
    	loggerOutput += "\n";
    	
    	ChipSet actualNeeded = ChipSet.subChipSets(allNeeded, p.getChipsNotUsedInBestPath());
    	
    	loggerOutput +="Actual Needed Chips : ";
    	for(CtColor color : actualNeeded.getChips())
    		loggerOutput +="Color index " + color.getColorNum() + "; ";
    	loggerOutput += "\n";
    	
    	p.setChipsMissingFromBestPath(actualNeeded);
    	
    	//Once I've calculated the paths I will need for the remainder plan
    	//Remove the ones I might need but haven't used yet from the not used chipset
    	ChipSet removeFromNotUsed = ChipSet.subChipSets(allNeeded,actualNeeded);
    	
    	for(CtColor color : removeFromNotUsed.getChips()) {
    		p.moveColorFromNotNeededToNeeded(color);
    	}
    	
    	loggerOutput +="Chips Not Used In Best Path : ";
    	for(CtColor color : p.getChipsNotUsedInBestPath().getChips())
    		loggerOutput +="Color index " + color.getColorNum() + "; ";
    	loggerOutput += "\n";
    	
    		loggerOutput +="Chips Used In Best Path : ";
    	for(CtColor color : p.getChipsUsedInBestPath().getChips())
    		loggerOutput +="Color index " + color.getColorNum() + "; ";
    	loggerOutput += "\n";
    	
    	
    	//print the chips I actually need to ask other agents for in order to reach the goal
    	loggerOutput +="Player " + p.getPlayerName() + " needs to request the following chips : ";
    	for(CtColor color : actualNeeded.getChips()) {
    		loggerOutput +="Color index "+ color.getColorNum() + "; ";
    	}
    	loggerOutput += "\n";
    	
    	Boot.logger.info(loggerOutput);
    	
    }
    
    
    /**
     * Receives the player and the calculated path remainder to the goal. Calculates the chips needed in order 
     * to reach the goal and subtracts the chips I have and haven't used yet. Then update the players ChipsMissingFromBestPathMember.
     * These will be the chips I need to ask from other agents.
     * @param p
     * @param newPath
     */
    @Deprecated
    public void calcChipsNeededFromOtherPlayers(Player p, Path newPath) {
    	int i = 0;
    	String loggerOutput = new String();
    	loggerOutput +="calcChipsNeededFromOtherPlayers function";
    	loggerOutput +=p.getPlayerName();
    	loggerOutput +=newPath.toString();
    	ChipSet allNeeded = new ChipSet();
    	LinkedList<RowCol> pathList= newPath.getPoints();
    	for(RowCol pos : pathList) {
    		if(i!=0)//don't need the color of the tile i'm on
    			allNeeded.add(new CtColor(BestUse.board.getTile(pos).getColor()),1);
    		i++;
    	}
    	
    	loggerOutput += "All Needed Chips : ";
    	for(CtColor color : allNeeded.getChips())
    		loggerOutput +="Color index " + color.getColorNum() + "; ";
    	loggerOutput += "\n";
    	
    	ChipSet actualNeeded = ChipSet.subChipSets(allNeeded, p.getChipsNotUsedInBestPath());
    	
    	loggerOutput +="Actual Needed Chips : ";
    	for(CtColor color : actualNeeded.getChips())
    		loggerOutput +="Color index " + color.getColorNum() + "; ";
    	loggerOutput += "\n";
    	
    	p.setChipsMissingFromBestPath(actualNeeded);
    	
    	//Once I've calculated the paths I will need for the remainder plan
    	//Remove the ones I might need but haven't used yet from the not used chipset
    	ChipSet removeFromNotUsed = ChipSet.subChipSets(allNeeded,actualNeeded);
    	
    	for(CtColor color : removeFromNotUsed.getChips()) {
    		p.moveColorFromNotNeededToNeeded(color);
    	}
    	
    	loggerOutput +="Chips Not Used In Best Path : ";
    	for(CtColor color : p.getChipsNotUsedInBestPath().getChips())
    		loggerOutput +="Color index " + color.getColorNum() + "; ";
    	loggerOutput += "\n";
    	
    		loggerOutput +="Chips Used In Best Path : ";
    	for(CtColor color : p.getChipsUsedInBestPath().getChips())
    		loggerOutput +="Color index " + color.getColorNum() + "; ";
    	loggerOutput += "\n";
    	
    	
    	//print the chips I actually need to ask other agents for in order to reach the goal
    	loggerOutput +="Player " + p.getPlayerName() + " needs to request the following chips : ";
    	for(CtColor color : actualNeeded.getChips()) {
    		loggerOutput +="Color index "+ color.getColorNum() + "; ";
    	}
    	loggerOutput += "\n";
    	
    	Boot.logger.info(loggerOutput);
    	
    }
    
    
    public BestUse(Game gs, PlayerStatus ps, Goal g) {
    	this.goal = g;
    	BestUse.board = gs.getBoard();
    	this.s = gs.getScoring();
    	this.goal_loc = new RowCol(g.getX(),g.getY());
    	// !!! the system is not calculating player scores as player state changes !!!
    	PlayerStatus psstart = new PlayerStatus(ps);// copy constructor
    	psstart.setScore((int)Math.floor(s.score(psstart, goal_loc)));// calculate score of current state
    	ArrayList<ArrayList<PlayerStatus>> foundPaths = new ArrayList<ArrayList<PlayerStatus>>();
    	ArrayList<PlayerStatus> endStatus = new ArrayList<PlayerStatus>();
    	//int bestPathIndex;   	
    	
    	this.new_dfs(psstart,foundPaths);

    	//If it has found direct paths to the goal
    	if(foundPaths.size()>0) {
	    	for( ArrayList<PlayerStatus> path : foundPaths) {
	    		int size = path.size();
	    		endStatus.add(path.get(size-1));
	    	}
	    	//bestPathIndex = getBestPath(endStatus);
	    	//ArrayList<PlayerStatus> bestPath = foundPaths.get(bestPathIndex);
    	}
    }
    
	/**
	 * Receives the best path so far and the goal. Calculates and returns the shortest path
	 * to reach the goal from the last point in the best path. Uses ShortestPaths.calcManhattanDistancePaths
	 * Calculates 5 shortest paths and returns the one whom we need to request as little chips as possible. 
	 * @param bestPath
	 * @param g
	 * @return The remainder of the path to reach the goal
	 */
	public Path improvePath(Game gameStatus,Player p, Goal g) {
		ArrayList<PlayerStatus> bestPath = p.getBestPathToGoal();
		int size = bestPath.size();
		PlayerStatus lastStep = bestPath.get(size-1);
		
		
		RowCol lastStepPosition = lastStep.getPosition();
		Path shortestPath = null;
		
		ArrayList<Path> shortestPaths = null;
		//If the best path has not reached the goal
		if(lastStepPosition.getxCoord()!=g.getX() || lastStepPosition.getyCoord()!=g.getY())
		{
			//find out what is the shortest way to reach the goal.
			//And which chips I will need
			//last value maxPathAmount - how many paths do I want ? Currently one is enough
			shortestPaths = ShortestPaths.calcManhattanDistancePaths(lastStepPosition,g.getPosition(), gameStatus.getBoard(), gameStatus.getScoring(), 30);
			if(shortestPaths.size()>1) {
				return findCheapestPath(p, shortestPaths);
			}
			else if(shortestPaths.size()==1)
				return shortestPaths.get(0);
		}
		return shortestPath;
	}
    

	/**
	 * Receives the players status and all shortest paths and returns the path for whom we will
	 * need to ask for the smallest number of chips
	 * @param playerStatus
	 * @param shortestPaths
	 * @return
	 */
	public Path findCheapestPath(Player playerStatus, ArrayList<Path> shortestPaths) {
		int pathIndex = 0;
		int bestPath = 0;
		int numChipsNeeded = 10;
		
		
		
		for(Path p : shortestPaths) {
			int newNumChipsNeeded = calcNumChipsNeededFromOtherPlayers(playerStatus, p);
			if(numChipsNeeded > newNumChipsNeeded) {
				numChipsNeeded = newNumChipsNeeded;
				bestPath = pathIndex;
			}
			
			pathIndex++;
		}
		
		Path best = shortestPaths.get(bestPath);
		Boot.logger.info("Chosen cheapest remainder path for player " + playerStatus.getPlayerName() + " is : " + best.toString());
		return best;
	}
    

    /**
     * Gets an arrayList of the end states of all found paths and returns the index of the best path
     * according to the cost
     * @param bestPaths
     * @return index of best path according to the Scoring class
     */
    private int getBestPath(ArrayList<PlayerStatus> bestPaths){
    	ArrayList<Integer> costs = new ArrayList<Integer>();
    	for(PlayerStatus p : bestPaths) {
    		costs.add(new Integer((int) s.score(p, this.goal_loc)));
    	}
    	
    	int maxVal = -10000000;
    	int maxInt = 0;
    	for(int i=0;i<costs.size();i++) {
    		if(costs.get(i) > maxVal) {
    			maxVal = costs.get(i);
    			maxInt = i;
    		}
    	}
    	
    	return maxInt;
    }
    
    
	public BestUse(Board b, PlayerStatus ps, Scoring s, TileGoal goal_type)
	{
		this.s = s;
	    this.goal_type = goal_type;
	
		board = b;
	    // assumes nearest goal of desired type is the best for you to seek
	    goal_loc = board.getNearestGoalLocations(ps.getPosition(), goal_type).get(0);
	
		// !!! the system is not calculating player scores as player state changes !!!
		PlayerStatus psstart = new PlayerStatus(ps);                  // copy constructor
		psstart.setScore((int)Math.floor(s.score(psstart, goal_loc)));  // calculate score of current state
		bestpathset = dfs(psstart, 0, 0);                                   // search for best paths
		
		// get a sample best end-state
		beststate = bestpathset.get(0).get(0);
		// convert the paths to Path objects
		convertPaths();
		this.paths.get(0).toString();
	}
	
	
	/**
		Returns the set of best-paths for the player
	*/
	public ArrayList<Path> getPaths()
	{
		return paths;
	}
	
	
	/**
		Returns a sample best end-state
	*/
	public PlayerStatus getBestState()
	{
		return beststate;
	}
	
	
	/**
		Converts the working representation of best paths to
		a collection of Path instances; note that the working
		representation uses necessary PlayerStatus instances,
		which contain score information in addition to position
		<p>
		Note also that the working representation of best paths
		is such that each path is in reverse order, where the
		end-state is at the beginning of the ArrayList representing
		the path; this method converts these reverse-order paths
		to forward-order
	*/
	public void convertPaths()
	{
		paths = new ArrayList<Path>();
		
		for (int i=0; i<bestpathset.size(); ++i)
		{
			ArrayList<PlayerStatus> psl = bestpathset.get(i);
			Path pp = new Path();
			
			for (int j=0; j<psl.size(); ++j)
				// we use insert because working representation is reverse order
				pp.appendPathPoint(new RowCol(psl.get(j).getPosition()));
				
			paths.add(pp);
		}
	}
		
	
	/**
		Depth-first search of move options
		
		@param nomove	player's current state
		@return			a list of best paths, where each path 
						is a sequence of PlayerStats instances
						in reverse order (end-state is first
						element of list)
	*/
	private ArrayList<ArrayList<PlayerStatus>> dfs(PlayerStatus nomove, int prevDeltaRow, int prevDeltaCol)
	{
		// base cases: no more chips left or at goal?
		if (nomove.getChips().isEmpty() ||
	//    board.isGoal(nomove.getPosition(), goal_type))  // would be nice to do this instead
			(nomove.getPosition().getRow() == goal_loc.getRow() && nomove.getPosition().getCol() == goal_loc.getCol()))
		{
			ArrayList<ArrayList<PlayerStatus>> newpathset = new ArrayList<ArrayList<PlayerStatus>>();
			ArrayList<PlayerStatus> newpath = new ArrayList<PlayerStatus>();
			newpath.add(nomove);
			newpathset.add(newpath);
			
			return newpathset;
		}
		
		ArrayList<ArrayList<PlayerStatus>> bestpathset = new ArrayList<ArrayList<PlayerStatus>>();
		ArrayList<PlayerStatus> bestpath = new ArrayList<PlayerStatus>();
		bestpath.add(nomove);
		bestpathset.add(bestpath);
	
		// Before moving to each direction checking that this was not the previous cell.
		// This is done to prevent exceptions if the number of chips is big and allows a lot of returns.
		// If the problem returns, will need to use open and close list.
		
		// decrease row
		if ((prevDeltaRow != 1) && (moveable(-1, 0, nomove)))
		{
			ArrayList<ArrayList<PlayerStatus>> testpathset = dfs(movePlayer(nomove, -1, 0), -1, 0);
			bestpathset = checkMove(nomove, testpathset, bestpathset);
		}
		// increase row
		if ((prevDeltaRow!= -1) && (moveable(1, 0, nomove)))
		{
			ArrayList<ArrayList<PlayerStatus>> testpathset = dfs(movePlayer(nomove, 1, 0), 1, 0);
			bestpathset = checkMove(nomove, testpathset, bestpathset);
		}
	
		// decrease column
		if ((prevDeltaCol != 1) && (moveable(0, -1, nomove)))
		{
			ArrayList<ArrayList<PlayerStatus>> testpathset = dfs(movePlayer(nomove, 0, -1), 0, -1);
			bestpathset = checkMove(nomove, testpathset, bestpathset);
		}
	
		// increase column
		if ((prevDeltaCol != -1) && (moveable(0, 1, nomove)))
		{
			ArrayList<ArrayList<PlayerStatus>> testpathset = dfs(movePlayer(nomove, 0, 1), 0, 1);
			bestpathset = checkMove(nomove, testpathset, bestpathset);
		}
	
		return bestpathset;
	}
	
	
	/**
	 * New DFS implmentation. One that finds all paths from start to goal
	 */
	private void new_dfs(PlayerStatus nomove, ArrayList<ArrayList<PlayerStatus>> foundPaths){
	//https://www.geeksforgeeks.org/find-paths-given-source-destination/
		
		RowCol goalPos = new RowCol(this.goal.getX(),this.goal.getY());
		
		int rows = BestUse.board.getRows();
		int cols = BestUse.board.getColumns();
		boolean visited[][] = new boolean[rows][cols];
		//mark all tiles as not visited
		for(int i=0;i<rows;i++) {
			for(int j=0;j<cols;j++)
				visited[i][j] = false;
		}
	
		ArrayList<PlayerStatus> path= new ArrayList<PlayerStatus>();//create an array to store the path
		int path_index = 0;//initialize the path array as empty
		
		//call the recursive helper function to print all the paths
		findAllPathsUtil(nomove, goalPos, visited,path,path_index,foundPaths);
	}
	
	private void findAllPathsUtil(PlayerStatus startStatus, RowCol goal, boolean[][] visited, ArrayList<PlayerStatus> path,int path_index,ArrayList<ArrayList<PlayerStatus>> foundPaths) {
		RowCol start = startStatus.getPosition();
		visited[start.getRow()][start.getCol()] = true;//mark the current node and store it in path
		path.add(startStatus);
		path_index++;
		
	
		if(!start.equals(goal))//if the current tile is not the destination 
		{
			//Recur for all the vertices adjacent to the current vertex that are reachable
			ArrayList<PlayerStatus> adjacents = new ArrayList<PlayerStatus>();
			int row = start.getRow();
			int col = start.getCol();
			
			if(moveable(1,0,startStatus) && visited[row+1][col]==false) {
				
				PlayerStatus newStatus = new PlayerStatus(startStatus);
				newStatus.setPosition(new RowCol(row+1,col));
				ChipSet currentChips = newStatus.getChips();
				currentChips = ChipSet.subtractColor(currentChips, board.getTile(newStatus.getPosition()).getColor());
				newStatus.setChips(currentChips);
				adjacents.add(newStatus);
			}
			
			if(moveable(-1,0,startStatus) && visited[row-1][col]==false)
			 {
				PlayerStatus newStatus = new PlayerStatus(startStatus);
				newStatus.setPosition(new RowCol(row-1,col));
				ChipSet currentChips = newStatus.getChips();
				currentChips = ChipSet.subtractColor(currentChips, board.getTile(newStatus.getPosition()).getColor());
				newStatus.setChips(currentChips);
				adjacents.add(newStatus);
			}
			
			if(moveable(0,1,startStatus) && visited[row][col+1]==false)
			 {
				PlayerStatus newStatus = new PlayerStatus(startStatus);
				newStatus.setPosition(new RowCol(row,col+1));
				ChipSet currentChips = newStatus.getChips();
				currentChips = ChipSet.subtractColor(currentChips, board.getTile(newStatus.getPosition()).getColor());
				newStatus.setChips(currentChips);
				adjacents.add(newStatus);
			}
			
			if(moveable(0,-1,startStatus) && visited[row][col-1]==false)
			 {
				PlayerStatus newStatus = new PlayerStatus(startStatus);
				newStatus.setPosition(new RowCol(row,col-1));
				ChipSet currentChips = newStatus.getChips();
				currentChips = ChipSet.subtractColor(currentChips, board.getTile(newStatus.getPosition()).getColor());
				newStatus.setChips(currentChips);
				adjacents.add(newStatus);
			}
			
			for (PlayerStatus r : adjacents) {
				findAllPathsUtil(r,goal,visited,path,path_index,foundPaths);
			}
		}
		
		//Saving paths that have not led to a goal 
		ArrayList<PlayerStatus> newPath = new ArrayList<PlayerStatus>();
		for(PlayerStatus p : path) {
			newPath.add(new PlayerStatus(p));
		}
		
		foundPaths.add(newPath);
		
		//remove current tile from path and mark it as unvisited
		path_index--;
		visited[start.getRow()][start.getCol()] = false;
		path.remove(startStatus);
	}
	
	
	
	/**
		Checks that proposed move is within board boundaries and that
		the player has at least one chip of same color as target square;
		NOTE that we assume that deltas are in {-1, 0, 1}
		
		@param deltarow		proposed change in row {-1, 0, 1}
		@param deltacol		proposed change in column {-1, 0, 1}
		@param pscur		current player-state
		@return				true if proposed move is possible
	*/
	private boolean moveable(int deltarow, int deltacol, PlayerStatus pscur)
	{
		RowCol ppos = new RowCol(pscur.getPosition(), deltarow, deltacol);
		int row = ppos.getRow();
		int col = ppos.getCol();
		return row >= 0 && row < board.getRows() &&
			   col >= 0 && col < board.getColumns() &&
			   pscur.getChips().getNumChips(board.getTile(ppos).getColor()) > 0;
	}


/**
	Creates a new PlayerStatus based on current state and
	proposed move; assumes that the proposed move is feasible
	
	@param nomove		the player's current state
	@param deltarow		proposed change in row location
	@param deltacol		proposed change in column location
	@return				a new PlayerStatus instance
*/
private PlayerStatus movePlayer(PlayerStatus nomove, 
								int deltarow, int deltacol)
{
	// create clone first
	PlayerStatus psnew = new PlayerStatus(nomove);
	// change board position
	psnew.setPosition(new RowCol(psnew.getPosition(), deltarow, deltacol));
	// change chipset by subtracting a chip of target-square color
	

	CtColor newChipColor = board.getTile(psnew.getPosition()).getColor();
	ChipSet temp = new ChipSet(newChipColor);
	
	
//	psnew.setChips(
//		ChipSet.subChipSets(psnew.getChips(), 
//					new ChipSet(board.getTile(psnew.getPosition()).getColor())));
	
	
	ChipSet subset = ChipSet.subChipSets(psnew.getChips(), temp);
	
	psnew.setChips( subset);
		
	
	
	
	// calculate player's score in this new hypothetical state
	// NOTE that we do not use psnew.setScore(), as this will cause
	// observers to be notified, and we do not want this for 
	// hypothetical moves
	psnew.setScore((int)Math.floor(s.score(psnew, goal_loc)));  // SHOULD IMPROVE Scoring to not need goal_loc
                                                          // but instead work with GameStatus
	
	return psnew;
}


/**
	Returns the better of the paths we're testing (in 'testpathset', 
	which all have the same score) and the paths that are the best
	so far (in 'bestpathset', which all share identical scores).
	Grows the test paths, by inserting nomove state, if they are better.
	
	@param nomove		current location of player
	@param testpathset	set of paths we're examining to see if they are better
	@param bestpathset	set of paths we're comparing against
	@return				best paths known so far
*/
private ArrayList<ArrayList<PlayerStatus>> checkMove(PlayerStatus nomove,
                            ArrayList<ArrayList<PlayerStatus>> testpathset, 
                            ArrayList<ArrayList<PlayerStatus>> bestpathset)
{
	// get path scores
	double testmovescore = testpathset.get(0).get(0).getScore();
	double bestmovescore = bestpathset.get(0).get(0).getScore();
	
	// best-so-far still best?
	if (testmovescore < bestmovescore)
		return bestpathset;
	
	// test paths are at least as good as best-so-far
	// append state 'nomove' to end of each test path
	for (int i=0; i<testpathset.size(); ++i)
		testpathset.get(i).add(nomove);
	
	// if test paths are better, then they are new best-so-far
	if (testmovescore > bestmovescore)
		bestpathset = testpathset;
	// if test paths are equally good, then add them to best-so-far
	else
		bestpathset.addAll(testpathset);
		
	return bestpathset;
}


public String toString()
{
	StringBuffer sb = new StringBuffer();
	
	for (int i=0; i<bestpathset.size(); ++i)
	{
		sb.append("Path " + i + ":\n");
		
		ArrayList<PlayerStatus> path = bestpathset.get(i);
		for (int j=0; j < path.size(); ++j)
			sb.append(path.get(j));
		sb.append("\n");
	}
	
	return sb.toString();
}

public Scoring getS() {
	return s;
}

public void setS(Scoring s) {
	this.s = s;
}

public TileGoal getGoal_type() {
	return goal_type;
}

public void setGoal_type(TileGoal goal_type) {
	this.goal_type = goal_type;
}

public Goal getGoal() {
	return goal;
}

public void setGoal(Goal goal) {
	this.goal = goal;
}


public PlayerStatus getBeststate() {
	return beststate;
}

public void setBeststate(PlayerStatus beststate) {
	this.beststate = beststate;
}

public ArrayList<ArrayList<PlayerStatus>> getBestpathset() {
	return bestpathset;
}

public void setBestpathset(ArrayList<ArrayList<PlayerStatus>> bestpathset) {
	this.bestpathset = bestpathset;
}

public Board getBoard() {
	return board;
}

public void setBoard(Board board) {
	BestUse.board = board;
}

public RowCol getGoal_loc() {
	return goal_loc;
}

public void setGoal_loc(RowCol goal_loc) {
	this.goal_loc = goal_loc;
}

public void setPaths(ArrayList<Path> paths) {
	this.paths = paths;
}





}