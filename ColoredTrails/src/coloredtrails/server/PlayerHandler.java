package coloredtrails.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import coloredtrails.common.Exchange;
import coloredtrails.common.Game;
import coloredtrails.common.MessageType;
import coloredtrails.common.UserType;
import coloredtrails.ui.ColoredTrailsBoardGui;

public class PlayerHandler extends Thread {

	private final static Logger LOGGER = Logger.getLogger(PlayerHandler.class.getName());

	private Socket clientSocket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private BlockingQueue<Message> messageQueue;
	private ClientMessageReader messageReader;
	private int clientNum;
	private User user;

	private boolean isAlive = true;

	public PlayerHandler(Socket clientSocket, int clientNum) {
		try {
			this.clientSocket = clientSocket;
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			messageQueue = new LinkedBlockingQueue<Message>();
			this.clientNum = clientNum;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedReader getReader() {
		return reader;
	}

	public BlockingQueue<Message> getMessageQueue() {
		return messageQueue;
	}

	@Override
	public void run() {

		try {

			messageReader = new ClientMessageReader(this);
			messageReader.setName(this.getName() + "Reader");
			messageReader.start();

			LOGGER.log(Level.INFO, Thread.currentThread().getName() + " - Processing messages of client " + clientNum);

			while (isAlive) {
				Message msg = messageQueue.take();
				LOGGER.log(Level.FINE,
						Thread.currentThread().getName() + " - Processing Message of client " + clientNum);
				LOGGER.log(Level.FINEST,
						Thread.currentThread().getName() + " - Message of client is " + msg.getContent());
				if (msg.getMessageType() == MessageType.CLIENT)
					handleMessage(msg);
				else if (msg.getMessageType() == MessageType.SERVER)
					sendMessage(msg.getContent());

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void handleMessage(Message msg) {
		JsonObject requestFromClient;
		Game playerGame;
		JsonObject replyToClient = new JsonObject();
		try {
			requestFromClient = (JsonObject) Jsoner.deserialize(msg.getContent());
			System.out.println("Got request from "+(this.user==null?"Client"+clientNum:this.user.getIdentity())+" : " + requestFromClient);
			String type = (String) requestFromClient.get("type");

			switch (type) {
			case "NewUser":
				// (String identity, PlayerHandler managingThread, Socket clientSocket,
				// UserType userType
				User newUser = new User(requestFromClient.getString("identity"), this, this.clientSocket,
						UserType.valueOf(requestFromClient.getString("userType")));
				boolean playerAdded = ServerData.getInstance().addPlayer(newUser);

				replyToClient = ServerMessages.getNewIdentityResponse(playerAdded, newUser.getUserType(),
						newUser.getIdentity());
				sendMessage(replyToClient.toJson());
				if (playerAdded) {
					this.user = newUser;
				} else {
					this.close();
				}

				break;
			case "Exchange":
				JsonArray exchangeArray=(JsonArray)requestFromClient.get("exchanges");
				List<Exchange> exchanges = new ArrayList<Exchange> ();
				for(int i=0; i<exchangeArray.size(); i++) {
					Exchange newExchange = new Exchange((JsonObject) exchangeArray.get(i));
					exchanges.add(newExchange);
				}
				playerGame = ServerData.getInstance().getGameById(this.user.getCurrentGame());
				if(this.user.getCurrentGame() != null && playerGame!= null) {
					playerGame.addExchangesByUser(exchanges, this.user);
					playerGame.commsReceived(this.user);
					playerGame.updatePhase();
				}
				break;
			case "ExchangeApproval":
				JsonArray exchangeApprovalArray=(JsonArray)requestFromClient.get("exchanges");
				List<Exchange> exchangeApprovals = new ArrayList<Exchange> ();
				for(int i=0; i<exchangeApprovalArray.size(); i++) {
					Exchange newExchange = new Exchange((JsonObject) exchangeApprovalArray.get(i));
					exchangeApprovals.add(newExchange);
				}
				playerGame = ServerData.getInstance().getGameById(this.user.getCurrentGame());
				if(this.user.getCurrentGame() != null && playerGame!=null) {
					playerGame.addExchangeApprovalsByUser(exchangeApprovals, this.user);
					playerGame.commsReceived(this.user);
					playerGame.updatePhase();
				}
				break;
			case "UpdateGamePhase":
				playerGame = ServerData.getInstance().getGameById(this.user.getCurrentGame());
				if(this.user.getCurrentGame() != null && playerGame!=null ) {
					playerGame.commsReceived(this.user);				
					playerGame.updatePhase();
				}
			case "QuitGame":
				Integer gameId=requestFromClient.getInteger("gameId");
				if(gameId!=null && this.user.getCurrentGame() != null && gameId.equals(this.user.getCurrentGame())) {
					ServerData.getInstance().removeUserFromGame(gameId, this.user.getIdentity());
				}
			default:

			}
		} catch (ArithmeticException e) {

		} catch (DeserializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendMessage(Message msg) {
		sendMessage(msg.getContent());
	}

	public void sendMessage(String msg) {

		if (this.user != null) {
			LOGGER.log(Level.INFO, Thread.currentThread().getName() + " - Message sent to client:" + clientNum
					+ " User:" + user.getIdentity() + msg);
		}
		try {
			writer.write((msg + "\n"));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (this.user != null) {
			if(this.user.getCurrentGame() !=null && this.user.getCurrentGame()>0) {
				//Game playerGame = ServerData.getInstance().getGameById(this.user.getCurrentGame());
				ServerData.getInstance().removeUserFromGame(this.user.getCurrentGame(), this.user.getIdentity());
							
			}
			ServerData.getInstance().removePlayer(user);
		}
		this.isAlive = false;
		this.interrupt();
	}

}
