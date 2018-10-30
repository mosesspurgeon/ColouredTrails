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

import coloredtrails.common.Game;
import coloredtrails.common.MessageType;
import coloredtrails.common.UserType;

public class AdminHandler extends Thread {

	private final static Logger LOGGER = Logger.getLogger(AdminHandler.class.getName());

	private Socket clientSocket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private ClientMessageReader messageReader;
	private int clientNum;
	private String adminIdentity;

	private boolean isAlive = true;

	public AdminHandler(Socket clientSocket, int clientNum) {
		try {
			this.clientSocket = clientSocket;
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			this.clientNum = clientNum;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedReader getReader() {
		return reader;
	}

	@Override
	public void run() {

		try {

			LOGGER.log(Level.INFO,
					Thread.currentThread().getName() + " - Processing messages of admin client " + clientNum);

			String adminClientMsg = null;
			while ((adminClientMsg = reader.readLine()) != null) {
				LOGGER.log(Level.FINE,
						Thread.currentThread().getName() + " - Message from client " + clientNum + " received: ");
				LOGGER.log(Level.FINEST,
						Thread.currentThread().getName() + " - Message from client received: " + adminClientMsg);

				Message msg = new Message(MessageType.ADMIN, adminClientMsg);

				handleMessage(msg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			close();
		}
	}

	public void handleMessage(Message msg) {
		JsonObject requestFromClient;
		JsonObject replyToClient = new JsonObject();
		try {
			requestFromClient = (JsonObject) Jsoner.deserialize(msg.getContent());
			String type = (String) requestFromClient.get("type");
			Integer gameId;
			JsonArray playersArray;

			switch (type) {
			case "NewAdmin":
				// (String identity, PlayerHandler managingThread, Socket clientSocket,
				// UserType userType

				String adminIdentity = requestFromClient.getString("identity");
				boolean adminAdded = ServerData.getInstance().addAdmin(adminIdentity);

				replyToClient = ServerMessages.getAdminConnectionResponse(adminAdded, adminIdentity);
				sendMessage(replyToClient.toJson());
				if (adminAdded) {
					this.adminIdentity = adminIdentity;
				} else {
					this.close();
				}

				break;
			case "AvailablePlayerlist":
				List<User> availPlayers = ServerData.getInstance().getAvailablePlayers();
				replyToClient = ServerMessages.getListPlayersResponse(ServerMessages.LISTTYPE_AVAILABLE, availPlayers);
				sendMessage(replyToClient.toJson());
				break;
			case "AllPlayerlist":
				List<User> allPlayers = ServerData.getInstance().getAllPlayers();
				replyToClient = ServerMessages.getListPlayersResponse(ServerMessages.LISTTYPE_ALL, allPlayers);
				sendMessage(replyToClient.toJson());
				break;
			case "GameConfiglist":
				sendMessage(GameConfigManager.getGameConfigsJson().toJson());
				break;
			case "GameConfigRefresh":
				boolean status=GameConfigManager.loadGameConfigs();
				replyToClient = ServerMessages.getRefreshGameConfigsResponse(status);
				sendMessage(replyToClient.toJson());
				break;
			case "Gamelist":
				List<Game> allGames = ServerData.getInstance().getAllGames();
				replyToClient = ServerMessages.getListGamesResponse(allGames);
				sendMessage(replyToClient.toJson());
				break;
			case "CreateGame":
				String gameConfig = (String) requestFromClient.get("gameConfig");
				gameId = ServerData.getInstance().createGame(gameConfig);
				replyToClient = ServerMessages.getCreateGameResponse(gameConfig,gameId!=null,gameId);
				sendMessage(replyToClient.toJson());
				break;
			case "ViewGame":
				gameId =  requestFromClient.getInteger("game");
				Game requestedGame=ServerData.getInstance().getGameById(gameId);
				replyToClient = ServerMessages.getViewGameResponse(gameId,requestedGame);
				sendMessage(replyToClient.toJson());
				break;
			case "AddPlayer":
				List<String> playersAdded = new ArrayList<String> ();

				gameId = requestFromClient.getInteger("game");
				playersArray = (JsonArray) requestFromClient.get("players");
				for(int i=0;i<playersArray.size();i++) {
					String userId=playersArray.getString(i);
					boolean playerAdded=ServerData.getInstance().addUserToGame(gameId, userId);
					if(playerAdded) {
						playersAdded.add(userId);
					}
				}
				replyToClient = ServerMessages.getAddPlayerResponse(gameId,playersAdded);
				sendMessage(replyToClient.toJson());
				break;
			case "RemovePlayer":
				List<String> playersRemoved = new ArrayList<String> ();

				gameId = requestFromClient.getInteger("game");
				playersArray = (JsonArray) requestFromClient.get("players");
				for(int i=0;i<playersArray.size();i++) {
					String userId=playersArray.getString(i);
					boolean playerRemoved=ServerData.getInstance().removeUserFromGame(gameId, userId);
					if(playerRemoved) {
						playersRemoved.add(userId);
					}
				}
				replyToClient = ServerMessages.getAddPlayerResponse(gameId,playersRemoved);
				sendMessage(replyToClient.toJson());
				break;
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

		if (this.adminIdentity != null) {
			LOGGER.log(Level.FINER, Thread.currentThread().getName() + " - Message sent to Admin client:" + clientNum
					+ " Admin User:" + adminIdentity + msg);
		}
		try {
			writer.write((msg + "\n"));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (this.adminIdentity != null)
			ServerData.getInstance().removeAdmin(adminIdentity);
		this.isAlive = false;
		try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.interrupt();
	}

}
