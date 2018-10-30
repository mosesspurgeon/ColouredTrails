package coloredtrails.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import coloredtrails.common.ChipSet;
import coloredtrails.common.CtColor;
import coloredtrails.common.Exchange;
import coloredtrails.common.Game;
import coloredtrails.common.GamePhase;
import coloredtrails.common.Player;
import coloredtrails.common.UserType;
import coloredtrails.ui.ColoredTrailsBoardGui;
import coloredtrails.ui.StartGameWindow;

public class AgentClient extends ClientImpl {
	private static final Logger LOGGER = Logger.getLogger(AgentClient.class.getName());

	public AgentClient(String clientName) {
		super(clientName, UserType.COMPUTER);
	}

	public AgentClient(String serverHostname, int serverPort, String clientName) {
		super(serverHostname, serverPort, clientName, UserType.COMPUTER);
	}


	@Override
	public void run() {
		connectToServer();

	}

	/*
	 * @Override public void startGame(String gameStatus) { JsonObject
	 * gameStatusJson; try { gameStatusJson = (JsonObject)
	 * Jsoner.deserialize(gameStatus); this.setGamePlayed(new Game(gameStatusJson));
	 * handleGamePhase();
	 * 
	 * } catch (DeserializationException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

	@Override
	public void updateGame(String gameStatus) {
		LOGGER.log(Level.INFO, "update game called in " + this.getClientName());
		LOGGER.log(Level.INFO, this.getClientName() + " : " + gameStatus);

		JsonObject gameStatusJson;
		try {
			gameStatusJson = (JsonObject) Jsoner.deserialize(gameStatus);
			Game game2 = new Game(gameStatusJson);
			this.setGamePlayed(game2);
			handleGamePhase();
		} catch (DeserializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleGamePhase() {

		String phaseName = this.getGamePlayed().getPhases().getCurrent().getName();
		LOGGER.log(Level.INFO, this.getClientName() + ": Current phase" + phaseName);

		// Communication phase
		if (phaseName.equals("Communication")) {
			handleCommunicationPhase();
		}
		// Exchange phase
		else if (phaseName.equals("Exchange")) {
			handleExchangePhase();
		}
		// Move phase
		else if (phaseName.equals("Movement")) {
			handleMovementPhase();
		}
	}

	private void handleMovementPhase() {
		// TODO Auto-generated method stub

	}

	public void handleExchangePhase() {
		Player me = this.getGamePlayed().getPlayer(getGameBoardPlayerName());
		List<Exchange> exchanges = approveExchanges(me.getExchangesReceived());
		this.sendExchangeApprovals(exchanges);
	}

	public void handleCommunicationPhase() {
		List<Exchange> exchanges = selectExchanges();
		this.sendExchanges(exchanges);
	}

	public List<Exchange> selectExchanges() {
		List<Exchange> exchanges = new ArrayList<Exchange>();
		return exchanges;
	}

	public List<Exchange> approveExchanges(List<Exchange> exchanges) {
		return exchanges;
	}

}
