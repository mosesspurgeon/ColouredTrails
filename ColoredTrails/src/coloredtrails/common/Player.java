package coloredtrails.common;

//import static gui.Translator.COLUMNS;
//import static gui.Translator.ROWS;


import static coloredtrails.common.Board.getNum_colors_used;

import java.util.ArrayList;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import coloredtrails.gui.types.Path;
import coloredtrails.gui.types.TilePlayer;
import coloredtrails.server.Message;
import coloredtrails.server.ServerMessages;
import coloredtrails.server.User;


/**
 * Player class. 
 * @author morvered
 *
 */
public  class Player implements JsonSerializable{
	private Tile startTile;
	private Tile currentTile;
	private boolean human;
	private ChipSet chips;
	private TilePlayer tilePlayer;
	private Board board;
	private String playerIcon;
	private String playerName;
	private PlayerStatus playerStatus;
	private int score;
	private ArrayList<PlayerStatus> bestPathToGoal;
	private Path improvedPath; 
	private ChipSet chipsUsedInBestPath;
	private ChipSet chipsNotUsedInBestPath;
	private ChipSet chipsMissingFromBestPath;
	private boolean reachedGoal;
	private int id;
	private ArrayList<Exchange> exchangesInitiated;
	private ArrayList<Exchange> exchangesReceived;
	private ArrayList<Exchange> exchangeInitiatedArchive;
	public static int playerId = 0;

	private User networkUser;
	private boolean commsRecieved;
	private boolean leftGame=false;

	@Override
	public JsonObject toJsonObject() {

		JsonObject player = new JsonObject();


		player.put("startTile", startTile==null?null:startTile.getLocation().toJsonObject());
		player.put("currentTile", currentTile==null?null:currentTile.getLocation().toJsonObject());
		player.put("human", human);
		player.put("chips", chips==null?null:chips.toJsonObject());
		player.put("tilePlayer", tilePlayer==null?null:tilePlayer.toJsonObject());
		player.put("playerIcon", playerIcon);
		player.put("playerName", playerName);
		player.put("playerStatus", playerStatus==null?null:playerStatus.toJsonObject());
		player.put("score", score);
		player.put("bestPathToGoal", bestPathToGoal==null?null:Util.toJsonList(bestPathToGoal));
		player.put("improvedPath", improvedPath==null?null:improvedPath.toJsonObject());
		player.put("chipsUsedInBestPath", chipsUsedInBestPath==null?null:chipsUsedInBestPath.toJsonObject());
		player.put("chipsNotUsedInBestPath", chipsNotUsedInBestPath==null?null:chipsNotUsedInBestPath.toJsonObject());
		player.put("chipsMissingFromBestPath", chipsMissingFromBestPath==null?null:chipsMissingFromBestPath.toJsonObject());
		player.put("reachedGoal", reachedGoal);
		player.put("id", id);
		player.put("exchangesInitiated", exchangesInitiated==null?null:Util.toJsonList(exchangesInitiated));
		player.put("exchangesReceived", exchangesReceived==null?null:Util.toJsonList(exchangesReceived));
		player.put("playerId", playerId);
		player.put("networkUser", networkUser==null?null:networkUser.toJsonObject());
		player.put("exchangeInitiatedArchive", exchangeInitiatedArchive==null?null:Util.toJsonList(exchangeInitiatedArchive));
		player.put("leftGame", leftGame);

		return player;
	}
	public Player (JsonObject player,Board board) {

		
		this.board=board;
		this.startTile=player.get("startTile")==null?null:board.getTile(new RowCol((JsonObject)player.get("startTile")));
		this.currentTile=player.get("currentTile")==null?null:board.getTile(new RowCol((JsonObject)player.get("currentTile")));
		this.human=player.getBoolean("human");
		this.chips=player.get("chips")==null?null:new ChipSet((JsonObject)player.get("chips"));
		this.tilePlayer=player.get("tilePlayer")==null?null:TilePlayer.getTilePlayer((JsonObject)player.get("tilePlayer"));
		this.playerIcon=player.getString("playerIcon");
		this.playerName=player.getString("playerName");
		this.playerStatus=player.get("playerStatus")==null?null:new PlayerStatus((JsonObject)player.get("playerStatus"));
		this.score=player.getInteger("score");
		
		JsonArray array = (JsonArray) player.get("bestPathToGoal");
		if(array!=null) {
			this.bestPathToGoal = new ArrayList<PlayerStatus>();
			for (int i = 0; i < array.size(); i++) {
				JsonObject status = (JsonObject) array.get(i);
				bestPathToGoal.add(status == null ? null : new PlayerStatus(status));
			}
		}
		this.improvedPath = new Path();

		this.improvedPath=player.get("improvedPath")==null?improvedPath:new Path((JsonObject)player.get("improvedPath"));

		this.chipsUsedInBestPath=player.get("chipsUsedInBestPath")==null?null:new ChipSet((JsonObject)player.get("chipsUsedInBestPath"));
		this.chipsNotUsedInBestPath=player.get("chipsNotUsedInBestPath")==null?null:new ChipSet((JsonObject)player.get("chipsNotUsedInBestPath"));
		this.chipsMissingFromBestPath=player.get("chipsMissingFromBestPath")==null?null:new ChipSet((JsonObject)player.get("chipsMissingFromBestPath"));
		this.reachedGoal=player.getBoolean("reachedGoal");
		this.id=player.getInteger("id");
		this.playerId=player.getInteger("playerId");
		JsonArray exchangesarray = (JsonArray) player.get("exchangesInitiated");
		if(exchangesarray!=null) {
			this.exchangesInitiated = new ArrayList<Exchange>();
			for (int i = 0; i < exchangesarray.size(); i++) {
				JsonObject exchange = (JsonObject) exchangesarray.get(i);
				exchangesInitiated.add(exchange == null ? null : new Exchange(exchange));
				
			}		
		}
		JsonArray exchangesRecvArray = (JsonArray) player.get("exchangesReceived");
		if(exchangesRecvArray!=null) {
			this.exchangesReceived = new ArrayList<Exchange>();
			for (int i = 0; i < exchangesRecvArray.size(); i++) {
				JsonObject exchange = (JsonObject) exchangesRecvArray.get(i);
				exchangesReceived.add(exchange == null ? null : new Exchange(exchange));
				
			}		
		}		
		JsonArray exchangesArchiveArray = (JsonArray) player.get("exchangeInitiatedArchive");
		if(exchangesArchiveArray!=null) {
			this.exchangeInitiatedArchive = new ArrayList<Exchange>();
			for (int i = 0; i < exchangesArchiveArray.size(); i++) {
				JsonObject exchange = (JsonObject) exchangesArchiveArray.get(i);
				exchangeInitiatedArchive.add(exchange == null ? null : new Exchange(exchange));
				
			}		
		}		
		if(this.currentTile!=null)
			this.currentTile.addPlayer(this);
		this.networkUser=player.get("networkUser")==null?null:new User((JsonObject)player.get("networkUser"));
		this.leftGame = player.getBoolean("leftGame");

	}
	

	/**
	 * Default Constructor. Initial coordinates at 0,0, width and height 1, 1, player is human
	 * Generate random chipset of the same size as the number of colors used
	 */
	public Player(String name) {
		super();
		this.human = true;
		this.chips = new ChipSet();
		this.chipsMissingFromBestPath = new ChipSet();
		this.chipsNotUsedInBestPath = new ChipSet();
		this.chipsUsedInBestPath = new ChipSet();
		this.improvedPath = new Path();
		this.playerName = name;
		this.reachedGoal = false;
		this.playerStatus = new PlayerStatus(playerId);
		this.id = this.playerId;
		this.playerId++;
		this.score = 0;
		this.playerStatus.setScore(score);
		this.exchangesInitiated = new ArrayList<Exchange>();
		this.exchangesReceived = new ArrayList<Exchange>();
		this.exchangeInitiatedArchive = new ArrayList<Exchange>();
		
	}
	
	/**
	 * Constructor with initial coordinates, width and height 1, 1
	 * @param xCoord
	 * @param yCoord
	 */
	public Player(Board board, int xCoord, int yCoord, boolean human, String name) {
		super();
		this.board = board;
		this.startTile = null;
		this.currentTile = null;
		this.reachedGoal = false;
		this.id = this.playerId;
		this.playerId++;
		
		this.setStartTile(board.getTile(xCoord,yCoord));//new Tile(xCoord,yCoord));
		this.setCurrentTile(board.getTile(xCoord,yCoord));//initializes playerStatus
		this.human = human;
		this.chips = new ChipSet();
		this.chipsMissingFromBestPath = new ChipSet();
		this.chipsNotUsedInBestPath = new ChipSet();
		this.chipsUsedInBestPath = new ChipSet();
		this.improvedPath = new Path();
		this.playerIcon = "player";//default player icon
		this.playerName = name;		
		this.setScore(0);
		this.playerId++;
		this.exchangesInitiated = new ArrayList<Exchange>();
		this.exchangesReceived = new ArrayList<Exchange>();
		this.exchangeInitiatedArchive = new ArrayList<Exchange>();
	}
	
	public Player(Board board, int xCoord, int yCoord, String icon, String name) {
		super();
		this.board = board;
		this.startTile = null;
		this.currentTile = null;
		this.setStartTile(board.getTile(xCoord,yCoord));//new Tile(xCoord,yCoord));
		this.setCurrentTile(board.getTile(xCoord,yCoord));//initializes playerStatus
		this.playerIcon = icon;
		this.chips = new ChipSet();
		this.chipsMissingFromBestPath = new ChipSet();
		this.chipsNotUsedInBestPath = new ChipSet();
		this.chipsUsedInBestPath = new ChipSet();
		this.improvedPath = new Path();
		this.playerName = name;
		this.setScore(0);
		this.reachedGoal = false;
		this.setScore(0);
		this.playerId++;
		this.exchangesInitiated = new ArrayList<Exchange>();
		this.exchangesReceived = new ArrayList<Exchange>();
		this.exchangeInitiatedArchive = new ArrayList<Exchange>();
	}
	
	/**
	 * Constructor that accepts all values
	 * @param xCoord
	 * @param yCoord
	 * @param width
	 * @param height
	 */
	public Player(Board board, int xCoord, int yCoord, int width, int height, boolean human, String name) {
		super();
		this.board = board;
		this.startTile = null;
		this.currentTile = null;
		this.setStartTile(board.getTile(xCoord,yCoord));
		this.setCurrentTile(board.getTile(xCoord,yCoord));//initializes playerStatus
		this.human = human;
		this.chips = new ChipSet();
		this.chipsMissingFromBestPath = new ChipSet();
		this.chipsNotUsedInBestPath = new ChipSet();
		this.chipsUsedInBestPath = new ChipSet();
		this.improvedPath = new Path();
		this.playerIcon = "player";//default player icon
		this.playerName = name;
		this.setScore(0);
		this.reachedGoal = false;
		this.setScore(0);
		this.playerId++;
		this.exchangesInitiated = new ArrayList<Exchange>();
		this.exchangesReceived = new ArrayList<Exchange>();
		this.exchangeInitiatedArchive = new ArrayList<Exchange>();
	}
	
	


	public Player(Board myGame, int xCoord, int yCoord, boolean human, TilePlayer tileType, String name) {
		this(myGame, xCoord,yCoord,human, name);
		this.tilePlayer = tileType;
		this.playerIcon = "player";//default player icon
	}
	
	public Player(Board myGame, int xCoord, int yCoord, boolean human, String tileTypeString, String name) {
		this(myGame,xCoord,yCoord,human, name);
		//this.tilePlayer = TilePlayer.valueOf(tileTypeString);
		this.playerIcon = tileTypeString;//default player icon
	}
	
	
	public Player(Player temp) {
		this.board = temp.board;
		this.startTile = null;
		this.currentTile = null;
		int xCoord = temp.getCurrentTile().getCoordinates().getxCoord();
		int yCoord = temp.getCurrentTile().getCoordinates().getyCoord();
		this.setStartTile(board.getTile(xCoord,yCoord));
		this.setCurrentTile(board.getTile(xCoord,yCoord));//initializes playerStatus
		this.human = temp.isHuman();
		this.setChips(temp.getChips());
		this.setChipsUsedInBestPath(temp.getChipsUsedInBestPath());
		this.setChipsMissingFromBestPath(temp.getChipsMissingFromBestPath());
		this.setChipsNotUsedInBestPath();
		this.setImprovedPath(temp.getImprovedPath());	
		this.playerIcon = temp.getPlayerIcon();//default player icon
		this.playerName = temp.getPlayerName();
		this.setScore(temp.getScore());
		this.reachedGoal = false;
		this.setScore(0);
		this.playerId++;
		this.exchangesInitiated = new ArrayList<Exchange>();
		for(Exchange exchange : temp.exchangesInitiated)
			this.exchangesInitiated.add(exchange);
		this.exchangesReceived = new ArrayList<Exchange>();
		for(Exchange exchange : temp.exchangesReceived)
			this.exchangesReceived.add(exchange);
		this.exchangeInitiatedArchive = new ArrayList<Exchange>();
		for(Exchange exchange : temp.exchangeInitiatedArchive)
			this.exchangeInitiatedArchive.add(exchange);

	}
	
	
	public void addExchangeInitiated(Exchange ex) {
		this.exchangesInitiated.add(new Exchange(ex));
	}
	
	public void addExchangeReceived(Exchange ex) {
		this.exchangesReceived.add(new Exchange(ex));
	}

	public void addExchangeInitiatedArchive(Exchange ex) {
		this.exchangeInitiatedArchive.add(new Exchange(ex));
	}
	
	public void emptyExchanges() {
		//archive exchanges before cleared for historic reasons 
		this.archiveExchangesInitiated();

		this.exchangesInitiated.clear();
		this.exchangesReceived.clear();
	}
	
	public void archiveExchangesInitiated() {
		this.exchangeInitiatedArchive.addAll(exchangesInitiated);
	}
	
	public void removeExchange(int i) {
		this.exchangesInitiated.remove(i);
	}
	
	public void removeExchange(Exchange exchange) {
		this.exchangesInitiated.remove(exchange);
	}
	
	

	public boolean isReachedGoal() {
		return reachedGoal;
	}

	public void setReachedGoal(boolean reachedGoal) {
		this.reachedGoal = reachedGoal;
	}

	public boolean getReachedGoal() {
		return this.reachedGoal;
	}	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public ChipSet getChips() {
		return chips;
	}

	public void setChips(ChipSet chips) {
		this.chips = new ChipSet(chips);
		this.playerStatus.setChips(chips);
	}

	public Tile getStartTile() {
		return startTile;
	}

	public void setStartTile(Tile startTile) {
		//if(this.startTile!=null) {
		//	this.board.setPlayer(this.startTile.getLocation().getxCoord(), this.startTile.getLocation().getyCoord(),this);
		//}
		this.startTile = startTile;
		//this.board.setPlayer(this.startTile.getLocation().getxCoord(), this.startTile.getLocation().getyCoord(),this);
	}

	public Tile getCurrentTile() {
		return currentTile;
	}

	public void setCurrentTile(Tile currentTile) {
		//if(this.currentTile!=null)
		//	this.board.setPlayer(this.currentTile.getLocation().getxCoord(), this.currentTile.getLocation().getyCoord(),this);
			//this.board.setHasPlayer(this.currentTile.getLocation().getxCoord(), this.currentTile.getLocation().getyCoord(),false);
		this.currentTile = currentTile;
		//this.board.setPlayer(this.currentTile.getLocation().getxCoord(), this.currentTile.getLocation().getyCoord(),this);
		
		if(this.playerStatus!=null)//if this is not through the constructor
			this.playerStatus.setPosition(new RowCol(currentTile.getCoordinates().getxCoord(),currentTile.getCoordinates().getyCoord()));
		else {
			this.playerStatus = new PlayerStatus(playerId);
			this.playerId++;
			this.playerStatus.setPosition(new RowCol(this.currentTile.getCoordinates().getxCoord(),this.currentTile.getCoordinates().getyCoord()));
		}
		}
	
	

	public boolean isHuman() {
		return human;
	}

	public void setHuman(boolean human) {
		this.human = human;
	}

	public TilePlayer getTilePlayer() {
		return tilePlayer;
	}

	public void setTilePlayer(TilePlayer tilePlayer) {
		this.tilePlayer = tilePlayer;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board myGame) {
		this.board = myGame;
	}

	public String getPlayerIcon() {
		return playerIcon;
	}

	public void setPlayerIcon(String playerIcon) {
		this.playerIcon = playerIcon;
	}

	public void setMovesAllowed(boolean movesAllowed)
	{
		this.playerStatus.setMovesAllowed(movesAllowed);
	}
	
	 
	 public void setCommunicationAllowed(boolean communicationAllowed)
	 {
	 	this.playerStatus.setCommunicationAllowed(communicationAllowed);
	 }
	
	 
	 public void setTransfersAllowed(boolean transfersAllowed)
	 {
	 	this.playerStatus.setTransfersAllowed(transfersAllowed);
	 }
	 
	public boolean areMovesAllowed()
	{
		return this.playerStatus.areMovesAllowed();
	}
	
	 
	 public boolean isCommunicationAllowed()
	 {
		 return this.playerStatus.isCommunicationAllowed();
	 }
	
	 
	 public boolean areTransfersAllowed()
	 {
		 return this.playerStatus.areTransfersAllowed();
	 }

	public PlayerStatus getPlayerStatus() {
		return playerStatus;
	}

	public void setPlayerStatus(PlayerStatus playerStatus) {
		this.playerStatus = playerStatus;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
		this.playerStatus.setScore(score);
	}

	public static int getPlayerId() {
		return playerId;
	}

	public static void setPlayerId(int playerId) {
		Player.playerId = playerId;
	}

	public ArrayList<PlayerStatus> getBestPathToGoal() {
		return bestPathToGoal;
	}

	public void setBestPathToGoal(ArrayList<PlayerStatus> bestPathToGoal) {
		this.bestPathToGoal = new ArrayList<PlayerStatus>();
		for(PlayerStatus p : bestPathToGoal)
			this.bestPathToGoal.add(new PlayerStatus(p));
	}

	public ChipSet getChipsUsedInBestPath() {
		return chipsUsedInBestPath;
	}

	public void setChipsUsedInBestPath(ChipSet chipsUsedInBestPath) {
		this.chipsUsedInBestPath = new ChipSet(chipsUsedInBestPath);
	}

	public ChipSet getChipsMissingFromBestPath() {
		return chipsMissingFromBestPath;
	}

	public void setChipsMissingFromBestPath(ChipSet chipsMissingFromBestPath) {
		this.chipsMissingFromBestPath = new ChipSet(chipsMissingFromBestPath);
				
	}

	public ChipSet getChipsNotUsedInBestPath() {
		return chipsNotUsedInBestPath;
	}

	public void setChipsNotUsedInBestPath() {
		if(this.chips!=null && this.chipsUsedInBestPath!=null)
			this.chipsNotUsedInBestPath = new ChipSet(ChipSet.subChipSets(this.chips, this.chipsUsedInBestPath));
	}

	public Path getImprovedPath() {
		return improvedPath;
	}

	public void setImprovedPath(Path improvedPath) {
		this.improvedPath = new Path(improvedPath);
	}
	
	public User getNetworkUser() {
		return networkUser;
	}

	public void setNetworkUser(User networkUser) {
		this.networkUser = networkUser;
	}

	public boolean isCommsRecieved() {
		return commsRecieved;
	}

	public void setCommsRecieved(boolean commsRecieved) {
		this.commsRecieved = commsRecieved;
	}

	public boolean isLeftGame() {
		return leftGame;
	}
	public void setLeftGame(boolean leftGame) {
		this.leftGame = leftGame;
	}
	public ArrayList<Exchange> getExchangeInitiatedArchive() {
		return exchangeInitiatedArchive;
	}
	/**
	 * Receives a color and removes it from the not needed ChipSet into the Needed chipset. This is used for projected paths,
	 * so other agents will not be able to take needed chips.
	 * @param color
	 */
	public void moveColorFromNotNeededToNeeded(CtColor color) {
		ChipSet.addColor(this.chipsUsedInBestPath, color);
		ChipSet.subtractColor(this.chipsMissingFromBestPath, color);
		ChipSet.subtractColor(this.chipsNotUsedInBestPath, color);
	}
	
	public void addColorAndRemoveFromNeeded(CtColor color) {
		ChipSet.addColor(this.chipsUsedInBestPath, color);
		ChipSet.subtractColor(this.chipsMissingFromBestPath, color);	
	}
	
	

	
	/**
	 * Adds the colors to the chips missing from best path ChipSet and also to the overall ChipSet
	 * Otherwise it should not be an option
	 * @param color
	 */
	public void addColors(int numChips, CtColor color) {
		for(int i=0;i<numChips;i++) {
			if(this.chipsMissingFromBestPath.contains(color))
			{
				this.addColorAndRemoveFromNeeded(color);
				this.chips.add(color, 1);
			}
			else {
				//System.out.println("ERROR !!!! This should not be an option, I should not receive a chip I don't need");
				ChipSet.addColor(this.chipsNotUsedInBestPath, color);
				this.chips.add(color,1);
			}
			
			updatePlayerStatusChips();
		}
	}
	 
	 
	/**
	 * Removes a color due to the player giving the color away. Removes it from the chips not used in best path chipSet and the overall chipset
	 * Otherwise should not be an option 
	 * @param color
	 */
	public boolean giveColors(int numChips, CtColor color) {
	    
	    //TODO: this should be modified to check just chips
		if(this.chipsNotUsedInBestPath.getNumChips(color)>=numChips) {
			for(int i=0;i<numChips;i++) {
				if(this.chips.contains(color)) 
				{
					ChipSet.subtractColor(this.chips, color);
					ChipSet.subtractColor(this.chipsNotUsedInBestPath, color);
				}
	
			}
			updatePlayerStatusChips();
			return true;
		} 
		System.out.println("ERROR !!!! This should not be an option, I should not give away chips I don't have");
		return false;
		
	}
	
	public void updatePlayerStatusChips() {
		this.playerStatus.setChips(this.chips);
	}
	 
	/**
	 * Removes a color due to the player using it to move. Removes it from the used chips and the overall chipset. 
	 * @param color
	 */
	public void useColor(CtColor color) {
		if(this.chipsUsedInBestPath.contains(color)) {
			ChipSet.subtractColor(this.chips, color);
			ChipSet.subtractColor(this.chipsUsedInBestPath, color);		
		}
		else
			System.out.println("ERROR !!!! This should not be an option, I should not use chips I  don't have");
		
		updatePlayerStatusChips();
	}

	public ArrayList<Exchange> getExchangesInitiated() {
		return exchangesInitiated;
	}

	public ArrayList<Exchange> getExchangesReceived() {
		return exchangesReceived;
	}	
	
	public void sendMessageNetworkUser(Message msg) {
		User networkUser = this.getNetworkUser();
			try {
				if(networkUser != null) {
				JsonObject messageJson=  (JsonObject) Jsoner.deserialize(msg.getContent());
				messageJson.put("playerName", this.getPlayerName());
				msg.setContent(messageJson.toJson());
				networkUser.getManagingThread().getMessageQueue().add(msg);
				}
			} catch (DeserializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void approveExchange(Exchange exchangeApproval, String approverName) {
		for(Exchange exchange:exchangesInitiated) {
			if(exchange.getExchangeId().equals(exchangeApproval.getExchangeId()) && exchange.getAgentName().equals(approverName) ) {
				exchange.setOwnerApproval(exchangeApproval.isOwnerApproved());
				return;
			}
		}
		
	}

	
	


}
