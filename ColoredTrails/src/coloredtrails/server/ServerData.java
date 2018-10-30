package coloredtrails.server;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import coloredtrails.common.Game;

public class ServerData {

	
	public static final String gamesDirectory = "src/coloredtrails/games/";
	
	/** the singleton instance */
	private static ServerData INSTANCE = null;


	private Map<String, User> userMap = Collections.synchronizedMap(new HashMap<String, User>());

	private Set<String> adminSet = Collections.synchronizedSet(new HashSet<String>());

	private Map<Integer, Game> gamesMap = Collections.synchronizedMap(new HashMap<Integer, Game>());
	
	private volatile int gameIdCounter=0; 
	
	private volatile int exchangeIdCounter=0;


	private ServerData() {

	}

	private synchronized static void createInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ServerData();
		}
	}

	public static ServerData getInstance() {
		if (INSTANCE == null)
			createInstance();
		return INSTANCE;
	}

	/**
	 * Add the given player connection object to the list of players which the
	 * server knows about.
	 *
	 * @param p
	 *            The player connection object to be added.
	 */
	public boolean addPlayer(User user) {
		if (userMap.get(user.getIdentity()) == null) {
			userMap.put(user.getIdentity(), user);
			return true;
		}
		return false;
	}

	public boolean addAdmin(String identity) {
		return adminSet.add(identity);
	}	
	public boolean removeAdmin(String identity) {
		return adminSet.remove(identity);
	}		
	public List<User> getAvailablePlayers() {
		List<User> allPlayers = getAllPlayers();
		List<User> availablePlayers = new ArrayList<User>();
		for (User user : allPlayers) {
			if (user.isIdle()) {
				availablePlayers.add(user);
			}
		}
		return availablePlayers;
	}

	public List<User> getAllPlayers() {
		return new ArrayList<User>(userMap.values());
	}

	public List<Game> getAllGames() {
		return new ArrayList<Game>(gamesMap.values());
	}

	public boolean removePlayer(User user) {
		return userMap.remove(user.getIdentity()) != null ? true : false;
	}

	public synchronized Integer createGame(String gameConfig) {
		Integer gameId=null;
		Game game=null;
		try {
			game = new Game(GameConfigManager.getGameConfig(gameConfig).getGameConfigId());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {//bad gameConfig id
			e.printStackTrace();
		}
		if(game!=null) {
			gameIdCounter++;
			game.setGameId(gameIdCounter);
			gameId=game.getGameId();
			gamesMap.put(gameId, game);
		}
		return gameId;
	}
	
	public boolean addUserToGame (Integer gameId,String userId) {
		Game game;
		User networkUser;
		if((game=gamesMap.get(gameId))==null) {
			return false;
		}
		if((networkUser=userMap.get(userId))==null) {
			return false;
		}
		
		//if user was part of another game unload him from there
		if(networkUser.getCurrentGame()!=null && networkUser.getCurrentGame()>0)
			removeUserFromGame(networkUser.getCurrentGame(),userId);
		
		return game.loadNetworkUser(networkUser);
		
	}


	public boolean removeUserFromGame(Integer gameId, String userId) {
		Game game;
		User networkUser;
		if((game=gamesMap.get(gameId))==null) {
			return false;
		}
		if((networkUser=userMap.get(userId))==null) {
			return false;
		}
		return game.unloadNetworkUser(networkUser);		
	}	
	
	public Game getGameById(Integer gameId) {
		return gamesMap.get(gameId);
	}
	
	public synchronized int getNextExchangeId() {
		exchangeIdCounter++;
		return exchangeIdCounter;
		
	}


	/**
	 * Get the IP of a player given that player's PIN.
	 *
	 * @param pin
	 *            The player's pin whose host ip needs to be looked up.
	 * @return The host ip of the player looked up by pin.
	 *//*
		 * public String getHostByPin(int pin) { for (PlayerConnection p : players) { if
		 * (p.getPin() == pin) { return p.getHostip(); } } return "NO_SUCH_PIN"; }
		 */
}
