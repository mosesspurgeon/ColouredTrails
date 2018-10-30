package coloredtrails.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;

import coloredtrails.alglib.BestUse;
import coloredtrails.common.Game.Status;
import coloredtrails.gui.types.GamePalette;
import coloredtrails.gui.types.HistoryLog;
import coloredtrails.gui.types.TileGoal;
import coloredtrails.gui.types.TilePlayer;
import coloredtrails.server.Message;
import coloredtrails.server.ServerData;
import coloredtrails.server.ServerMessages;
import coloredtrails.server.User;
import coloredtrails.ui.ColoredTrailsBoardGui;

public class Game implements Serializable, Cloneable {

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
	private int rounds = 0;
	public static final int MAX_ROUNDS = 30;

	private String gameConfigid;
	private volatile Status gameStatus = Status.CREATED;

	public enum Status {
		CREATED("Game is just created; players are being loaded"), INPROGRESS(
				"Game is underway; all players have been loaded"), FAILED(
						"The Game failed because a player left the game. "), MAXOUT(
								"Maximum number of rounds has been reached"), COMPLETED(
										"All Players have now reached their goals");

		private String description;

		private Status(String description) {
			this.description = description;
		}

		public String getDescription() {
			return this.description;
		}

		public static Status getStatus(String status) {
			for (Status st : Status.values()) {
				if (st.name().equals(status))
					return st;
			}
			return null;
		}
	}

	public JsonObject toJsonObject() {
		JsonObject gameStatus = new JsonObject();
		gameStatus.put("board", board == null ? null : board.toJsonObject());
		/*
		 * gameStatus.put("humanPlayers", humanPlayers == null ? null :
		 * Util.toJsonList(humanPlayers)); gameStatus.put("agentPlayers", agentPlayers
		 * == null ? null : Util.toJsonList(agentPlayers));
		 */
		gameStatus.put("allPlayers", allPlayers == null ? null : Util.toJsonList(allPlayers));
		gameStatus.put("goals", goals == null ? null : Util.toJsonList(goals));

		gameStatus.put("phases", phases == null ? null : phases.toJsonObject());
		gameStatus.put("numComputerPlayers", numComputerPlayers);
		gameStatus.put("numHumanPlayers", numHumanPlayers);
		gameStatus.put("numAllPlayers", numAllPlayers);
		gameStatus.put("numGoals", numGoals);
		gameStatus.put("gameId", gameId);
		gameStatus.put("isStarted", isStarted);
		gameStatus.put("isInitialized", isInitialized);
		gameStatus.put("score", score == null ? null : score.toJsonObject());

		gameStatus.put("rounds", rounds);
		gameStatus.put("gameConfigid", gameConfigid);
		gameStatus.put("gameStatus", this.gameStatus.name());

		return gameStatus;
	}

	public Game(JsonObject gameStatus) {

		this.board = new Board((JsonObject) gameStatus.get("board"));
		
		if(this.humanPlayers ==null) 
			this.humanPlayers = new ArrayList<Player>();
		if(this.agentPlayers ==null) 
			this.agentPlayers = new ArrayList<Player>();
		
		JsonArray array;
		array = (JsonArray) gameStatus.get("allPlayers");
		if (array != null) {
			// this.allPlayers = new ArrayList<Player>();
			for (int i = 0; i < array.size(); i++) {
				JsonObject allPlayer = (JsonObject) array.get(i);
				this.addPlayer(allPlayer == null ? null : new Player(allPlayer, board));
			}
		}

		array = (JsonArray) gameStatus.get("goals");
		if (array != null) {

			this.goals = new ArrayList<Goal>();
			for (int i = 0; i < array.size(); i++) {
				JsonObject goal = (JsonObject) array.get(i);
				goals.add(goal == null ? null : new Goal(goal));
			}
		}

		this.phases = new Phases((JsonObject) gameStatus.get("phases"));
		this.numComputerPlayers = (int) gameStatus.getInteger("numComputerPlayers");
		this.numHumanPlayers = (int) gameStatus.getInteger("numHumanPlayers");
		this.numAllPlayers = (int) gameStatus.getInteger("numAllPlayers");
		this.numGoals = (int) gameStatus.getInteger("numGoals");
		this.gameId = (int) gameStatus.getInteger("gameId");
		this.isStarted = (boolean) gameStatus.getBoolean("isStarted");
		this.isInitialized = (boolean) gameStatus.getBoolean("isInitialized");
		this.score = new Scoring((JsonObject) gameStatus.get("score"));

		this.rounds = (int) gameStatus.getInteger("rounds");
		this.gameConfigid = (String) gameStatus.getString("gameConfigid");
		this.gameStatus = gameStatus.getString("gameStatus") == null ? null
				: Status.getStatus((String) gameStatus.getString("gameStatus"));
	}

	/**
	 * Default constructor creates 6*6 grid with 2 human players with spaceship
	 * textures and one goal with Goal texture
	 */
	public Game() {
		this.gameId = -1;
		this.numAllPlayers = 0;
		this.numComputerPlayers = 0;
		this.numHumanPlayers = 0;
		this.board = new Board();
		Player newPlayer = new Player(this.board, 3, 3, true, TilePlayer.SpaceshipTexture, "Me");
		Player newPlayer2 = new Player(this.board, 2, 2, true, TilePlayer.SpaceshipTexture, "Agent1");
		Goal newGoal = new Goal(5, 5, TileGoal.Goal);
		this.addPlayer(newPlayer);
		this.addPlayer(newPlayer2);
		goals = new ArrayList<Goal>();
		goals.add(newGoal);
		this.isStarted = false;
		this.isInitialized = false;
		this.log = new HistoryLog();
		this.gamePalette = new GamePalette();
		this.score = new Scoring(100, -5, 1); // points earned for reaching the goal, penalty for each unit distance
												// away from goal (<0), points earned for each chip in posession
	}

	public Game(Board map) {
		this.board = new Board(map);
		this.numAllPlayers = 0;
		this.numComputerPlayers = 0;
		this.numHumanPlayers = 0;
		Player newPlayer = new Player(this.board, 3, 3, true, TilePlayer.SpaceshipTexture, "Me");
		Player newPlayer2 = new Player(this.board, 2, 2, true, TilePlayer.SpaceshipTexture, "Agent1");
		Goal newGoal = new Goal(5, 5, TileGoal.Goal);

		this.isStarted = false;
		this.isInitialized = false;
		this.log = new HistoryLog();
		this.gamePalette = new GamePalette();
		this.score = new Scoring(10, 5, 10);
		this.addPlayer(newPlayer);
		this.addPlayer(newPlayer2);
		goals = new ArrayList<Goal>();
		goals.add(newGoal);

		this.score = new Scoring(100, -5, 1); // points earned for reaching the goal, penalty for each unit distance
												// away from goal (<0), points earned for each chip in posession

	}

	/**
	 * Constructor that initializes the game via input file
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public Game(String path) throws FileNotFoundException {

		loadGameFromFile(path);// load map,scoring,players,colors and goals

		loadPhasesFromFile(path);// load phases and times
		this.gameConfigid = path;

	}

	public void loadPhasesFromFile(String path) throws FileNotFoundException {
		String filePath = ServerData.gamesDirectory + path + "/" + "game" + path + "Phases.txt";

		// each phase name, duration, indefinite
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filePath));
			int numOfPhases = scanner.nextInt();
			ArrayList<GamePhase> phases = new ArrayList<GamePhase>();

			for (int i = 0; i < numOfPhases; i++) {
				boolean indefinite = false;
				String phaseName = scanner.next();
				int phaseLength = scanner.nextInt();
				String indefiniteString = scanner.next();
				if (indefiniteString.equals("True"))
					indefinite = true;
				else
					indefinite = false;

				GamePhase p = new GamePhase(phaseName, phaseLength, indefinite);
				phases.add(p);

			}

			this.phases = new Phases(this);
			this.phases.setPhaseSequence(phases);

		} finally {
			if (scanner != null)
				scanner.close();
		}

	}

	public void loadGameFromFile(String path) throws FileNotFoundException {
		String filePath = ServerData.gamesDirectory + path + "/" + "game" + path + ".txt";
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filePath));

			int rows = scanner.nextInt();
			int columns = scanner.nextInt();

			int goalweight = scanner.nextInt();
			int distweight = scanner.nextInt();
			int chipweight = scanner.nextInt();
			this.score = new Scoring(goalweight, distweight, chipweight);

			int numColors = scanner.nextInt();
			int[][] map = new int[rows][columns];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++)
					map[i][j] = scanner.nextInt();

			}

			this.board = new Board(numColors, map);
			this.board.setRows(rows);
			this.board.setColumns(columns);

			int nHumanPlayers = scanner.nextInt();
			for (int i = 0; i < nHumanPlayers; i++) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				String tex = scanner.next();
				String name = "Human" + Integer.toString(i + 1);
				// Board myGame, int xCoord, int yCoord, boolean human, String tileTypeString
				Player newPlayer = new Player(this.board, x, y, true, tex, name);
				this.addPlayer(newPlayer);
			}

			int nComputerPlayers = scanner.nextInt();
			for (int i = 0; i < nComputerPlayers; i++) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				String tex = scanner.next();
				String name = "Agent" + Integer.toString(i + 1);
				// Board myGame, int xCoord, int yCoord, boolean human, String tileTypeString
				Player newPlayer = new Player(this.board, x, y, false, tex, name);
				this.addPlayer(newPlayer);
			}

			numGoals = scanner.nextInt();
			for (int i = 0; i < numGoals; i++) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				String tex = scanner.next();
				// Board myGame, int xCoord, int yCoord, boolean human, String tileTypeString
				Goal newGoal = new Goal(x, y, tex);
				this.addGoal(newGoal);
			}
			for (int i = 0; i < nHumanPlayers; i++) {
				int numChips = scanner.nextInt();// read an int for a colored chip
				ChipSet chips = new ChipSet();
				for (int j = 0; j < numChips; j++) {
					int chipColor = scanner.nextInt();
					chips.add(new CtColor(chipColor), 1);
				}
				this.humanPlayers.get(i).setChips(chips);
			}

			for (int i = 0; i < nComputerPlayers; i++) {
				int numChips = scanner.nextInt();// read an int for a colored chip
				ChipSet chips = new ChipSet();
				for (int j = 0; j < numChips; j++) {
					int chipColor = scanner.nextInt();
					chips.add(new CtColor(chipColor), 1);
				}
				this.agentPlayers.get(i).setChips(chips);
			}
			
			if(this.humanPlayers ==null) 
				this.humanPlayers = new ArrayList<Player>();
			if(this.agentPlayers ==null) 
				this.agentPlayers = new ArrayList<Player>();
			
		} finally {
			if (scanner != null)
				scanner.close();
		}

	}

	/**
	 * Method that moves player p to new position as in newPlayerStatus while
	 * updating the board tiles
	 * 
	 * @param p
	 * @param newPlayerStatus
	 */
	public void moveNextStep(Player p, PlayerStatus newPlayerStatus) {
        //:TODO to be modified to check if chip is available first
	    //before moving by one step

	    // leave the current tile
		Tile initialTile = p.getCurrentTile();
		initialTile.removePlayer(p);

		p.setPlayerStatus(newPlayerStatus);
		// The first position is the current position, we want to go to the next one
		RowCol movePos = newPlayerStatus.getPosition();
		if (this.goals.get(0).getPosition().equals(movePos))
			p.setReachedGoal(true);

		Tile moveTile = board.getTile(movePos);
		CtColor moveColor = moveTile.getColor();

		// set the current Tile
		p.setCurrentTile(moveTile);
		moveTile.addPlayer(p);

		// remove the used color
		p.useColor(moveColor);
		// remove the first step from the best path
		p.getBestPathToGoal().remove(0);

	}

	/**
	 * A method in charge of moving all the players one step along their best path
	 */
	public void moveOneStep() {
		for (Player p : agentPlayers) {
			if (p.getBestPathToGoal().size() > 1) {
				PlayerStatus newPlayerStatus = p.getBestPathToGoal().get(1);
				moveNextStep(p, newPlayerStatus);
			}
		}

		for (Player p : humanPlayers) {
			if (p.getBestPathToGoal().size() > 1) {
				PlayerStatus newPlayerStatus = p.getBestPathToGoal().get(1);
				moveNextStep(p, newPlayerStatus);
			}
		}
	}

	/**
	 * A method in charge of moving all the players all steps along their best path
	 */
	public void moveAllSteps() {
		for (Player p : agentPlayers) {
			for (PlayerStatus nextStep : p.getBestPathToGoal()) {
				moveNextStep(p, nextStep);
			}
			// initialize the best path to a new empty path
			p.setBestPathToGoal(new ArrayList<PlayerStatus>());
		}

		for (Player p : humanPlayers) {
			for (PlayerStatus nextStep : p.getBestPathToGoal()) {
				moveNextStep(p, nextStep);
			}
			// initialize the best path to a new empty path
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

	public ArrayList<Player> getAllPlayers() {
		return allPlayers;
	}

	public List<Double> getAllPlayersScore() {
		List<Double> scores = new ArrayList<Double>();
		Goal g = this.getGoals().get(0);

		for (Player p : allPlayers) {
			Double playerScore = score.score(p.getPlayerStatus(), new RowCol(g.getX(), g.getY()));
			scores.add(playerScore);
		}

		return scores;
	}

	public ArrayList<Player> getAgentPlayers() {
		return agentPlayers;
	}

	public Player getPlayer(String playerName) {
		for (Player p : agentPlayers) {
			if (p.getPlayerName().equals(playerName))
				return p;
		}

		for (Player p : humanPlayers) {
			if (p.getPlayerName().equals(playerName))
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
		
		if(this.humanPlayers ==null) 
			this.humanPlayers = new ArrayList<Player>();
		if(this.agentPlayers ==null) 
			this.agentPlayers = new ArrayList<Player>();
		
		if (player.isHuman()) {
			if (this.humanPlayers == null)
				this.humanPlayers = new ArrayList<Player>();
			this.humanPlayers.add(player);
			this.numHumanPlayers++;
		} else {
			if (this.agentPlayers == null)
				this.agentPlayers = new ArrayList<Player>();
			this.agentPlayers.add(player);
			this.numComputerPlayers++;
		}

		if (this.allPlayers == null)
			this.allPlayers = new ArrayList<Player>();
		this.allPlayers.add(player);

		this.numAllPlayers++;
	}

	public void addGoal(Goal goal) {
		if (this.goals == null)
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

	public String getGameConfigid() {
		return gameConfigid;
	}

	public Status getGameStatus() {
		return gameStatus;
	}

	private synchronized void setGameStatus(Status gameStatus) {
		this.gameStatus = gameStatus;
	}

	public int getRounds() {
		return rounds;
	}

	public void calcNeededChips() {
		BestUse bestUse = new BestUse(this, this.getGoals().get(0));
		bestUse.calcBestPathsServer(this); // sets the best path in the player object
		bestUse.calcNeededChipsServer(this); // sets the needed chips

		// exchange will be added by agents
		// bestUse.calcExchangesServer(this); // adds the exchange for all the agents in
		// communication phase
	}

	public void exchangeChipsServer() {
		BestUse bestUse = new BestUse(this, this.getGoals().get(0));
		bestUse.performeExchangesServer(this); // performExchanges(game) performs the actual exchanges and returns a
												// string
	}

	public void movePlayersServer() {
		this.moveOneStep();
		// game.moveAllSteps();
	}

	public void failGame() {
		this.setGameStatus(Status.FAILED);
		publishGameStatus();
		killAllPlayers();
	}

	public void completeGame() {
		this.setGameStatus(Status.COMPLETED);
		publishGameStatus();
		killAllPlayers();
	}

	public void maxOutGame() {
		this.setGameStatus(Status.MAXOUT);
		publishGameStatus();
		killAllPlayers();
	}

	public void killAllPlayers() {
		// remains of players are stored for archiving games
		for (Player p : allPlayers) {
			User remains = new User(p.getNetworkUser());
			p.getNetworkUser().disconnectGame();
			p.setNetworkUser(remains);
		}
	}

	public boolean loadNetworkUser(User networkUser) {

		if (gameStatus != Status.CREATED)
			return false;

		List<Player> players = null;
		if (networkUser.getUserType() == UserType.HUMAN) {
			players = humanPlayers;

		} else if (networkUser.getUserType() == UserType.COMPUTER) {
			players = agentPlayers;
		}

		for (Player player : players) {
			if (player.getNetworkUser() == null) {
				player.setNetworkUser(networkUser);
				networkUser.setCurrentGame(gameId);

				// Possible Side Effect when adding users to a game
				// When game has required no of users it has to be started
				updatePhase();
				return true;
			}
		}
		return false;

	}

	public boolean unloadNetworkUser(User networkUser) {

		if (this.isFinished())
			return false;

		List<Player> players = null;
		if (networkUser.getUserType() == UserType.HUMAN) {
			players = humanPlayers;

		} else if (networkUser.getUserType() == UserType.COMPUTER) {
			players = agentPlayers;
		}

		for (Player player : players) {
			if (player.getNetworkUser() == networkUser) {
				// if game was just created, new user could fill in the vacant spot
				// otherwise game is marked as failed
				if (gameStatus == Status.CREATED) {
					player.getNetworkUser().disconnectGame();
					player.setNetworkUser(null);
					return true;
				} else if (gameStatus == Status.INPROGRESS) {
					player.setLeftGame(true);
					failGame();
					return true;
				}
				break;
			}
		}
		return false;
	}

	public synchronized void updatePhase() {
		// :TODO update phase needs to check if the current phase has finished its
		// pre-requisities
		// Communication phase - check if time has run out

		GamePhase nextPhase = this.getPhases().getNextPhase();
		// could be handled with phases isLoop but is being handled here currently
		if (nextPhase == null) {
			nextPhase = this.getPhases().getPhaseSequence().get(0);
		}

		if (metPrerequisites(nextPhase)) {
			advancePhase(nextPhase);
		}

	}

	public synchronized void advancePhase(GamePhase nextPhase) {
		if (!isFinished()) {
			// Set new Phase
			this.setGameStatus(Status.INPROGRESS);
			this.getPhases().setCurrent(nextPhase);
			this.getPhases().setCurrentleft(nextPhase.getDuration());

			// Everytime first phase is encountered increase rounds
			// check if all players reached goals
			if (nextPhase.getName().equals(this.getPhases().getPhaseSequence().get(0).getName())) {

				int playersReachedGoals = 0;
				for (Player player : allPlayers) {
					if (!hasPlayerReachedGoalTile(player))
						break;
					else {
						playersReachedGoals++;
					}
				}
				if (playersReachedGoals == allPlayers.size()) {
					this.completeGame();
					return;
				}

				rounds++;
			}

			// if maximum rounds reached then stop
			if (rounds > MAX_ROUNDS) {
				this.maxOutGame();
				rounds--;
				return;
			}

			String phaseName = this.getPhases().getCurrent().getName();

			// Communication phase
			if (phaseName.equals("Communication")) {

				calcNeededChips();
			}
			// Exchange phase
			else if (phaseName.equals("Exchange")) {
				// Server Does not need to do actual exchange here
				// but get exchange approvals from clients
			}

			// Move phase
			else if (phaseName.equals("Movement")) {
				exchangeChipsServer();
				movePlayersServer();
			}

			clearCommsReceived();
			publishGameStatus();
		}

	}

	public boolean isFinished() {
		if (gameStatus == Status.FAILED || gameStatus == Status.COMPLETED || gameStatus == Status.MAXOUT)
			return true;
		return false;
	}

	private boolean metPrerequisites(GamePhase phase) {
		String phaseName = phase.getName();
		// Communication phase
		if (phaseName.equals("Communication")) {
			return this.allNetworkPlayersLoaded();

		}
		// Exchange phase
		else if (phaseName.equals("Exchange")) {
			return this.allPlayerCommsReceived();
		}

		// Move phase
		else if (phaseName.equals("Movement")) {
			return this.allPlayerCommsReceived();

		}
		return false;
	}

	public synchronized void publishGameStatus() {
		// notify all players of game updates
		for (Player player : allPlayers) {
			JsonObject gameStatus = ServerMessages.gameUpdateNotification(this);
			Message msg = new Message(MessageType.SERVER, gameStatus.toJson());
			player.sendMessageNetworkUser(msg);
		}

	}

	public boolean allNetworkPlayersLoaded() {
		for (Player player : allPlayers) {
			if (player.getNetworkUser() == null) {
				return false;
			}
		}
		return true;
	}

	public boolean allPlayerCommsReceived() {
		for (Player player : allPlayers) {
			if (!player.isCommsRecieved()) {
				return false;
			}
		}
		return true;
	}

	private void clearCommsReceived() {
		for (Player player : allPlayers) {
			player.setCommsRecieved(false);
		}
	}
	/*
	 * public void start() { // assumes caller has check isStartable before calling
	 * this
	 * 
	 * this.gameStatus = Status.INPROGRESS; this.updatePhase();
	 * 
	 * // notify human players of game start for (Player player : humanPlayers) {
	 * User networkuser = player.getNetworkUser(); JsonObject gameStart =
	 * ServerMessages.startGameNotification(this); Message msg = new
	 * Message(MessageType.SERVER, gameStart.toJson());
	 * networkuser.getManagingThread().getMessageQueue().add(msg); }
	 * 
	 * }
	 */

	public Player getPlayerByNetworkUser(User user) {
		for (Player player : allPlayers) {
			if (player.getNetworkUser() == user) {
				return player;
			}
		}
		return null;
	}

	public void addExchangeByUser(Exchange newExchange, User user) {
		String phaseName = this.getPhases().getCurrent().getName();
		if (gameStatus == Status.INPROGRESS && phaseName.equals("Communication")) {

			// empty exchange
			if (newExchange.getChipsAsked().size() == 0 && newExchange.getChipsOffered().size() == 0)
				return;

			// invalid player
			Player player = getPlayerByNetworkUser(user);
			if (player == null)
				return;

			// Check validity of player at the receiving end of the exchange
			String targetExchanger = newExchange.getAgentName();
			if (targetExchanger == null)
				return;
			Player targetPlayer = getPlayer(targetExchanger);
			if (targetPlayer == null)
				return;

			// generate exchange id
			int exchangeId = ServerData.getInstance().getNextExchangeId();
			newExchange.setExchangeId(exchangeId);
			newExchange.setRound(rounds);

			// add an exchange to the receiving player
			Exchange receivingExchange = new Exchange(newExchange);
			receivingExchange.setAgentName(player.getPlayerName());
			targetPlayer.addExchangeReceived(receivingExchange);

			// add an exchange to the initiator player
			player.addExchangeInitiated(newExchange);
		}

	}

	public void addExchangeApprovalByUser(Exchange exchangeApproval, User user) {
		String phaseName = this.getPhases().getCurrent().getName();
		if (gameStatus == Status.INPROGRESS && phaseName.equals("Exchange")) {
			Player approvingPlayer = getPlayerByNetworkUser(user);
			if (approvingPlayer == null)
				return;

			// Check validity of player at the receiving end of the exchange approval
			String exchangeInitiator = exchangeApproval.getAgentName();
			if (exchangeInitiator == null)
				return;
			Player initiatorPlayer = getPlayer(exchangeInitiator);
			if (initiatorPlayer == null)
				return;

			initiatorPlayer.approveExchange(exchangeApproval, approvingPlayer.getPlayerName());
		}

	}

	public void addExchangesByUser(List<Exchange> exchanges, User user) {
		for (Exchange exchange : exchanges) {
			addExchangeByUser(exchange, user);
		}

	}

	public void addExchangeApprovalsByUser(List<Exchange> exchanges, User user) {
		for (Exchange exchange : exchanges) {
			addExchangeApprovalByUser(exchange, user);
		}

	}

	public String getDescriptionText() {
		String description = "";

		if (this.gameStatus == Status.CREATED) {
			int noHumanSpots = 0, noAgentSpots = 0;
			for (Player player : humanPlayers) {
				if (player.getNetworkUser() == null) {
					noHumanSpots++;
				}
			}

			for (Player player : agentPlayers) {
				if (player.getNetworkUser() == null) {
					noAgentSpots++;
				}
			}
			description = "Player spots Available - Humans:" + noHumanSpots + " Agents:" + noAgentSpots;

		}
		if (this.gameStatus == Status.INPROGRESS || this.isFinished()) {
			int noHumanSpots = 0, noAgentSpots = 0;
			for (Player player : humanPlayers) {
				if (hasPlayerReachedGoalTile(player)) {
					noHumanSpots++;
				}
			}

			for (Player player : agentPlayers) {
				if (hasPlayerReachedGoalTile(player)) {
					noAgentSpots++;
				}
			}
			description = "Rounds completed: " + this.rounds + " Players reached goal - Humans:" + noHumanSpots
					+ " Agents:" + noAgentSpots;
		}

		return description;
	}

	public boolean hasPlayerReachedGoalTile(Player p) {
		Goal g = this.getGoals().get(0);
		RowCol gpos = new RowCol(g.getX(), g.getY());

		if (score.getManhattanDist(p.getPlayerStatus(), gpos) == 0) {
			return true;
		}
		return false;
	}

	public void commsReceived(User user) {
		this.getPlayerByNetworkUser(user).setCommsRecieved(true);
	}

	public String getResult() {
		String result = "";

		if (this.isFinished()) {
			List<Double> scores = this.getAllPlayersScore();
			
			int player = 0;
			Double scoreSum = 0.0;

			String playerResult="";
			for (Player p : this.getAllPlayers()) {
				String boardPlayer = p.getPlayerName();
				String networkUser = p.getNetworkUser() == null ? "Unloaded" : p.getNetworkUser().getIdentity();
				String playerType = p.getNetworkUser() == null ? "-" : (p.getNetworkUser().getUserType()==UserType.COMPUTER)?"C":"H";
				Double score = (this.getGameStatus() == Status.CREATED) ? 0
						: p.getNetworkUser() == null ? 0 : scores.get(player++);
				String goalReached = p.getNetworkUser() == null ? ""
						: (this.hasPlayerReachedGoalTile(p) ? " done" : "");
				String leftGame = p.isLeftGame() ? " left" : "";

				scoreSum += score;
				playerResult +=  String.format(" %s[%s %s]  %s%s%s,",
						 boardPlayer, networkUser, playerType, (new DecimalFormat("#.##")).format(score), goalReached,leftGame);


			}
			result = "Rounds: "+this.rounds+"; Score: "+scoreSum+"; "+ playerResult;
		}

		return result;
	}
}

