package coloredtrails.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import coloredtrails.common.Exchange;
import coloredtrails.common.Game;
import coloredtrails.common.UserType;

/**
 * This provides all the generic client implementation required both by a human
 * player and a computer agent. This handles establishing connections It creates
 * two threads to deliver the asynchronous communication model
 * 
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class ClientImpl extends Thread implements Client {
    private static final Logger LOGGER = Logger
            .getLogger(ClientImpl.class.getName());

    public static final int SERVER_PORT = 6789;
    public static final String SERVER_HOST = "localhost";

    private String serverHostname = SERVER_HOST;
    private int serverPort = SERVER_PORT;
    private String clientName;
    private UserType userType;

    private Game gamePlayed;
    private String gameBoardPlayerName;

    private Socket clientSocket;
    private DataOutputStream out;
    private ClientImplReceiver myReceiver;

    public ClientImpl(String clientName, UserType userType) {
        super();
        this.clientName = clientName;
        this.userType = userType;
    }

    public ClientImpl(String serverHostname, int serverPort, String clientName,
            UserType userType) {
        super();
        this.serverHostname = serverHostname;
        this.serverPort = serverPort;
        this.clientName = clientName;
        this.userType = userType;
    }

    public static void main(String argv[]) throws Exception {
        ClientImpl client = new ClientImpl("HumanClient1234", UserType.HUMAN);
        client.start();

    }

    @Override
    public void run() {
        this.connectToServer();

    }

    /**
     * Function called every time Server sends a message Currently only one
     * message type is pushed by the server It is the "UpdateGame"
     */

    public void onMessage(String msg) {

        LOGGER.log(Level.INFO,
                Thread.currentThread().getName() + "Recieved: " + msg);

        JsonObject msgFromServer;
        try {
            msgFromServer = (JsonObject) Jsoner.deserialize(msg);
            String type = (String) msgFromServer.get("type");
            String gamestatus;

            switch (type) {
            case "UpdateGame":
                gamestatus = ((JsonObject) msgFromServer.get("gameStatus"))
                        .toJson();
                this.setGameBoardPlayerName(
                        msgFromServer.getString("playerName"));
                updateGame(gamestatus);
                break;
            default:

            }

        } catch (DeserializationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void connectToServer() {
        try {
            clientSocket = new Socket(serverHostname, serverPort);
            out = new DataOutputStream(clientSocket.getOutputStream());
            myReceiver = new ClientImplReceiver(this, clientSocket);
            myReceiver.start();
            send(ClientMessages.getNewUserRequest(userType, clientName));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void send(JsonObject obj) throws IOException {
        send(obj.toJson());
    }

    private void send(String message) throws IOException {
        LOGGER.log(Level.INFO,
                Thread.currentThread().getName() + "Sending: " + message);
        out.write((message + "\n").getBytes("UTF-8"));
        out.flush();

    }

    @Override
    public String getServerHostname() {
        return this.serverHostname;
    }

    @Override
    public void setServerHostname(String hostname) {
        this.serverHostname = hostname;
    }

    @Override
    public int getServerPort() {
        // TODO Auto-generated method stub
        return this.serverPort;
    }

    @Override
    public void setServerPort(int port) {
        this.serverPort = port;
    }

    @Override
    public String getClientName() {
        return this.clientName;
    }

    @Override
    public void setClientName(String name) {
        this.clientName = name;
    }

    public String getGameBoardPlayerName() {
        return gameBoardPlayerName;
    }

    public void setGameBoardPlayerName(String gameBoardPlayerName) {
        this.gameBoardPlayerName = gameBoardPlayerName;
    }

    public Game getGamePlayed() {
        return gamePlayed;
    }

    public void setGamePlayed(Game gamePlayed) {
        this.gamePlayed = gamePlayed;
    }

    /**
     * Function to send exchange offers by the client Used in the
     * "Communication" phase
     */
    @Override
    public void sendExchanges(List<Exchange> exchanges) {
        try {
            send(ClientMessages.getExchangeRequest(exchanges, "Exchange"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        }

    }

    /**
     * Function to send exchange approvals by the client Used in the "Exchange"
     * phase
     */
    @Override
    public void sendExchangeApprovals(List<Exchange> exchanges) {
        try {
            send(ClientMessages.getExchangeRequest(exchanges,
                    "ExchangeApproval"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        }

    }

    /**
     * Function to send a quite game request by a client Used to discontinue
     * playing a game
     */

    @Override
    public void sendQuitGame() {
        try {
            send(ClientMessages.quitGameRequest(this.gamePlayed.getGameId()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        }
    }

    /**
     * Function to send the UpdateGamePhase Json Object to the game This is
     * generally used in an indefinite game phase by a human player
     */

    public void updateGamePhase() {
        try {
            send(ClientMessages.updateGamePhaseRequest());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        }
    }

    /*
     * CALL BACKS FROM SERVER
     */
    /**
     * Call back from server communicating updating login connection status
     * handled differently by different types of clients
     */
    @Override
    public void connectionStatus(boolean status) {
        // TODO Auto-generated method stub

    }

    /**
     * Call back from server communicating an updated game status handled
     * differently by different types of clients
     */
    @Override
    public void updateGame(String gamestatus) {
        // TODO Auto-generated method stub

    }

}
