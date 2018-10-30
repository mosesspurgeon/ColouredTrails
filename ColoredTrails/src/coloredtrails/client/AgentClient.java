package coloredtrails.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import coloredtrails.common.Exchange;
import coloredtrails.common.Game;
import coloredtrails.common.Player;
import coloredtrails.common.UserType;

/**
 * This provides the template for all computer agent clients
 * 
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class AgentClient extends ClientImpl {
    private static final Logger LOGGER = Logger
            .getLogger(AgentClient.class.getName());

    public AgentClient(String clientName) {
        super(clientName, UserType.COMPUTER);
    }

    public AgentClient(String serverHostname, int serverPort,
            String clientName) {
        super(serverHostname, serverPort, clientName, UserType.COMPUTER);
    }

    @Override
    public void run() {
        connectToServer();

    }

    /**
     * Function that is a call back by server every time the game state changes
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

    /**
     * Function that is called to handle a particular game phase
     */

    public final void handleGamePhase() {

        String phaseName = this.getGamePlayed().getPhases().getCurrent()
                .getName();
        LOGGER.log(Level.INFO,
                this.getClientName() + ": Current phase" + phaseName);

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

    /**
     * Function to retrieve exchange offers in the "Communication phase"
     */
    public List<Exchange> selectExchanges() {
        List<Exchange> exchanges = new ArrayList<Exchange>();
        return exchanges;
    }

    /**
     * Function to approve exchange offers in the "Exchange phase"
     */
    public List<Exchange> approveExchanges(List<Exchange> exchanges) {
        return exchanges;
    }

}
