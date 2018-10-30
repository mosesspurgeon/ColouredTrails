package data;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import gui.types.*;
import ui.CtColor;




public class GameStatus implements Serializable, Cloneable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Board board;
	ArrayList<Player> humanPlayers;
	ArrayList<Player> agentPlayers;
	ArrayList<Player> allPlayers;
	ArrayList<Goal> goals;
	Phases phases;
	int numComputerPlayers;
	int numHumanPlayers;
	int numAllPlayers;
	int numGoals;
	int gameId;
	boolean isStarted;
	boolean isInitialized;
	HistoryLog log;
	GamePalette gamePalette;
	Scoring score;


	
	/**
	 * Default constructor creates 6*6 grid with 2 human players with spaceship textures and 
	 * one goal with Goal texture
	 */
	public GameStatus() {
		this.gameId = -1;
		this.numAllPlayers = 0;
		this.numComputerPlayers = 0;
		this.numHumanPlayers = 0;
		this.board = new Board();
		Player newPlayer = new Player(this.board,3,3,true,TilePlayer.SpaceshipTexture, "Me");
		Player newPlayer2 = new Player(this.board,2,2,true,TilePlayer.SpaceshipTexture, "Agent1");
		Goal newGoal = new Goal(5,5,TileGoal.Goal);
		this.addPlayer(newPlayer);
		this.addPlayer(newPlayer2);
		goals = new ArrayList<Goal>();
		goals.add(newGoal);
		this.isStarted = false;
		this.isInitialized = false;
		this.log = new HistoryLog();
		this.gamePalette = new GamePalette();
		this.score = new Scoring(100,-5,1); //points earned for reaching the goal, penalty for each unit distance away from goal (<0), points earned for each chip in posession
	}
	
	public GameStatus(Board map) {
		this.board = new Board(map);
		this.numAllPlayers = 0;
		this.numComputerPlayers = 0;
		this.numHumanPlayers = 0;
		Player newPlayer = new Player(this.board,3,3,true,TilePlayer.SpaceshipTexture, "Me");
		Player newPlayer2 = new Player(this.board,2,2,true,TilePlayer.SpaceshipTexture, "Agent1");
		Goal newGoal = new Goal(5,5,TileGoal.Goal);

		
		this.isStarted = false;
		this.isInitialized = false;
		this.log = new HistoryLog();
		this.gamePalette = new GamePalette();
		this.score = new Scoring(10,5,10);
		this.addPlayer(newPlayer);
		this.addPlayer(newPlayer2);
		goals = new ArrayList<Goal>();
		goals.add(newGoal);
		
		this.score = new Scoring(100,-5,1); //points earned for reaching the goal, penalty for each unit distance away from goal (<0), points earned for each chip in posession

	}
	
	
	/**
	 * Constructor that initializes the game via input file
	 * @param path
	 */
	public GameStatus(String path) {
		
		loadGameFromFile(path);//load map,scoring,players,colors and goals
		
		loadPhasesFromFile(path);//load phases and times
		
		
	}
	
	

	public void loadPhasesFromFile(String path) {
		String filePath = Boot.gamesDirectory + path + "/" + "game"+path+"Phases.txt";
		
		//each phase name, duration, indefinite
		try {
			Scanner scanner = new Scanner( new File(filePath));
			int numOfPhases = scanner.nextInt();
			ArrayList<GamePhase> phases = new ArrayList<GamePhase>();
			
			for(int i=0;i<numOfPhases;i++) {
				boolean indefinite = false;
				String phaseName = scanner.next();
				int phaseLength = scanner.nextInt();
				String indefiniteString = scanner.next();
				if(indefiniteString.equals("True"))
					indefinite = true;
				else
					indefinite = false;
				
				GamePhase p = new GamePhase(phaseName,phaseLength,indefinite);
				phases.add(p);
				
			}
			
			this.phases = new Phases();
			this.phases.setPhaseSequence(phases);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	public void loadGameFromFile(String path) {
		String filePath = Boot.gamesDirectory + path + "/" + "game"+path+".txt";
		
		try {
			Scanner scanner = new Scanner( new File(filePath));
			
			int rows = scanner.nextInt();
			int columns = scanner.nextInt();
			
			
			int goalweight = scanner.nextInt();
			int distweight = scanner.nextInt();
			int chipweight = scanner.nextInt();
			this.score = new Scoring(goalweight,distweight,chipweight);
			
			int numColors = scanner.nextInt();
			int [][]map = new int[rows][columns];
			for(int i=0;i<rows;i++) {
				for(int j=0;j<columns;j++) 
					map[i][j] = scanner.nextInt();
				
			}
			
			this.board = new Board(numColors,map);
			this.board.setRows(rows);
			this.board.setColumns(columns);
			
			int nHumanPlayers = scanner.nextInt();
			for(int i=0;i<nHumanPlayers;i++) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				String tex = scanner.next();
				String name = "Human" + Integer.toString(i+1);
				//Board myGame, int xCoord, int yCoord, boolean human, String tileTypeString
				Player newPlayer = new Player(this.board,x,y,true,tex,name);
				this.addPlayer(newPlayer);
			}
			
			int nComputerPlayers = scanner.nextInt();
			for(int i=0;i<nComputerPlayers;i++) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				String tex = scanner.next();
				String name = "Agent" + Integer.toString(i+1);
				//Board myGame, int xCoord, int yCoord, boolean human, String tileTypeString
				Player newPlayer = new Player(this.board,x,y,false,tex,name);
				this.addPlayer(newPlayer);
			}
			
			numGoals = scanner.nextInt();
			for(int i=0;i<numGoals;i++) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				String tex = scanner.next();
				//Board myGame, int xCoord, int yCoord, boolean human, String tileTypeString
				Goal newGoal = new Goal(x,y,tex);
				this.addGoal(newGoal);
			}
			for(int i=0;i<nHumanPlayers;i++) {
				int numChips = scanner.nextInt();//read an int for a colored chip
				ChipSet chips = new ChipSet();
				for(int j=0;j<numChips;j++) {
					int chipColor = scanner.nextInt();
					chips.add(new CtColor(chipColor), 1);
				}
				this.humanPlayers.get(i).setChips(chips);
			}
			
			for(int i=0;i<nComputerPlayers;i++) {
				int numChips = scanner.nextInt();//read an int for a colored chip
				ChipSet chips = new ChipSet();
				for(int j=0;j<numChips;j++) {
					int chipColor = scanner.nextInt();
					chips.add(new CtColor(chipColor), 1);
				}
				this.agentPlayers.get(i).setChips(chips);
			}
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Method that moves player p to new position as in newPlayerStatus while updating the board tiles
	 * @param p
	 * @param newPlayerStatus
	 */
	public void moveNextStep(Player p, PlayerStatus newPlayerStatus) {
		//leave the current tile
		Tile initialTile = p.getCurrentTile();
		initialTile.removePlayer(p);
		
		
		p.setPlayerStatus(newPlayerStatus);
		//The first position is the current position, we want to go to the next one
		RowCol movePos = newPlayerStatus.getPosition();
		if(this.goals.get(0).getPosition().equals(movePos))
			p.setReachedGoal(true);
		
		Tile moveTile = board.getTile(movePos);
		CtColor moveColor = moveTile.getColor();
		
		
		//set the current Tile
		p.setCurrentTile(moveTile);
		moveTile.addPlayer(p);
		
		//remove the used color
		p.useColor(moveColor);
		//remove the first step from the best path
		p.getBestPathToGoal().remove(0);
		
	}


	/**
	 * A method in charge of moving all the players one step along their best path 
	 */
	public void moveOneStep() {
		for(Player p : agentPlayers) {
			if(p.getBestPathToGoal().size()>1) {
				PlayerStatus newPlayerStatus = p.getBestPathToGoal().get(1);
				moveNextStep(p,newPlayerStatus);
			}
		}
		
		for(Player p : humanPlayers) {
			if(p.getBestPathToGoal().size()>1) {
				PlayerStatus newPlayerStatus = p.getBestPathToGoal().get(1);
				moveNextStep(p,newPlayerStatus);
			}
		}
	}
	
	
	/**
	 * A method in charge of moving all the players all steps along their best path
	 */
	public void moveAllSteps() {
		for(Player p : agentPlayers) {
			for( PlayerStatus nextStep: p.getBestPathToGoal()) {
				moveNextStep(p,nextStep);
			}
			//initialize the best path to a new empty path
			p.setBestPathToGoal(new ArrayList<PlayerStatus>());
		}
		
		for(Player p : humanPlayers) {
			for( PlayerStatus nextStep: p.getBestPathToGoal()) {
				moveNextStep(p,nextStep);
			}
			//initialize the best path to a new empty path
			p.setBestPathToGoal(new ArrayList<PlayerStatus>());
		}
	}



	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}


	
	
	public ArrayList<Player> getHumanPlayers() {
		return humanPlayers;
	}
	
	public ArrayList<Player> getAllPlayers(){
		return allPlayers;
	}



	public ArrayList<Player> getAgentPlayers() {
		return agentPlayers;
	}
 

	public Player getPlayer(String playerName) {
		for(Player p : agentPlayers) {
			if(p.getPlayerName().equals(playerName))
				return p;
		}
		
		for(Player p : humanPlayers) {
			if(p.getPlayerName().equals(playerName))
				return p;
		}
		return null;
	}

	public Scoring getScore() {
		return score;
	}

	public void setScore(Scoring score) {
		this.score = score;
	}

	public void addPlayer(Player player) {
		if(player.isHuman()) {
			if(this.humanPlayers==null)
				this.humanPlayers = new ArrayList<Player>();
			this.humanPlayers.add(player);
			this.numHumanPlayers++;
		}
		else {
			if(this.agentPlayers==null)
				this.agentPlayers = new ArrayList<Player>();
			this.agentPlayers.add(player);
			this.numComputerPlayers++;
		}
		
		if(this.allPlayers==null)
			this.allPlayers = new ArrayList<Player>();
		this.allPlayers.add(player);
		this.numAllPlayers++;
	}
	
	public void addGoal(Goal goal) {
		if(this.goals==null)
			this.goals = new ArrayList<Goal>();
		this.goals.add(goal);
	}




	public ArrayList<Goal> getGoals() {
		return goals;
	}

	public void setGoals(ArrayList<Goal> goals) {
		this.goals = goals;
	}

	public Phases getPhases() {
		return phases;
	}

	public void setPhases(Phases phases) {
		this.phases = phases;
	}

	public int getNumComputerPlayers() {
		return numComputerPlayers;
	}


	
	public int getNumHumanPlayers() {
		return numHumanPlayers;
	}


	public int getNumAllPlayers() {
		return numAllPlayers;
	}

	public int getNumGoals() {
		return numGoals;
	}

	public void setNumGoals(int numGoals) {
		this.numGoals = numGoals;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	public HistoryLog getLog() {
		return log;
	}

	public void setLog(HistoryLog log) {
		this.log = log;
	}

	public GamePalette getGamePalette() {
		return gamePalette;
	}

	public void setGamePalette(GamePalette gamePalette) {
		this.gamePalette = gamePalette;
	}

	public Scoring getScoring() {
		return score;
	}

	public void setScoring(Scoring score) {
		this.score = score;
	}
	
	

}
