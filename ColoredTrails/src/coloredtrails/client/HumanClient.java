package coloredtrails.client;

import java.util.List;

import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import coloredtrails.common.Exchange;
import coloredtrails.common.Game;
import coloredtrails.common.Player;
import coloredtrails.common.UserType;
import coloredtrails.ui.ColoredTrailsBoardGui;
import coloredtrails.ui.StartGameWindow;

public class HumanClient extends ClientImpl {
	private Thread board;
	private ColoredTrailsBoardGui cb;

	public HumanClient(String clientName) {
		super(clientName, UserType.HUMAN);
	}

	public HumanClient(String serverHostname, int serverPort, String clientName) {
		super(serverHostname, serverPort, clientName, UserType.HUMAN);
	}

	public static void main(String argv[]) throws Exception {
		/*
		 * String sentence; String modifiedSentence; BufferedReader inFromUser = new
		 * BufferedReader(new InputStreamReader(System.in));
		 */
		StartGameWindow s = new StartGameWindow();// updates the loadFile
		s.open();

	}

	@Override
	public void run() {
		connectToServer();

	}

/*	@Override
	public void startGame(String gameStatus) {
		JsonObject gameStatusJson;
		try {
			gameStatusJson = (JsonObject) Jsoner.deserialize(gameStatus);
			Game game2 = new Game(gameStatusJson);
			this.setGamePlayed(game2);


		} catch (DeserializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

	@Override
	public void updateGame(String gameStatus) {
			JsonObject gameStatusJson;
			try {
				gameStatusJson = (JsonObject) Jsoner.deserialize(gameStatus);
				Game updatedGame = new Game(gameStatusJson);

				//check if player has been added to new Game
				//if new game shutdown old game window
				if (this.getGamePlayed()!=null && this.getGamePlayed().getGameId() !=  updatedGame.getGameId() && cb!=null) {
					cb.shutdown();
					ColoredTrailsBoardGui.game=null;
				}
				
				//Set current Human Player icon to Me for better intuition on the GUI
				updatedGame.getPlayer(this.getGameBoardPlayerName()).setPlayerIcon("Me");
				this.setGamePlayed(updatedGame);
				
				if (ColoredTrailsBoardGui.game == null) {
					cb = new ColoredTrailsBoardGui(this, this.getGamePlayed());
					// update players and goals icon images on the board
					cb.updateGuiBoard();
					board = new Thread(cb);
					board.start();					
				}
				else {
					ColoredTrailsBoardGui.game = this.getGamePlayed();
					ColoredTrailsBoardGui.getIcd().updatePhaseClient();
				}
			} catch (DeserializationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}

	public void handleExchangePhase(boolean approval) {
		Player me=this.getGamePlayed().getPlayer(getGameBoardPlayerName());		
		List<Exchange> exchanges = approveExchanges(me.getExchangesReceived(), approval);
		this.sendExchangeApprovals(exchanges);		
	}
	public List<Exchange> approveExchanges(List<Exchange> exchanges, boolean approval){
		for(Exchange exchange: exchanges) {
			exchange.setOwnerApproval(approval);
		}
		return exchanges;
	}
	
	public boolean hasExchangestoApprove() {
		Player me=this.getGamePlayed().getPlayer(getGameBoardPlayerName());			
		return (me.getExchangesReceived().size()!=0);
	}
	
	
}
