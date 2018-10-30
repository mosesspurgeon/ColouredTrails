package coloredtrails.admin;

import java.util.List;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;

import coloredtrails.common.UserType;
import coloredtrails.server.User;

public class AdminClientMessages {

	public static final String AVAILABLE= "Available";
	public static final String ALL= "All";

	public static JsonObject getListPlayersRequest(String listType) {
			JsonObject listAvailablePlayersRequest = new JsonObject();
			if (listType.equals(AVAILABLE) || listType.equals(ALL)) {
				listAvailablePlayersRequest.put("type", listType + "Playerlist");
			}
			return listAvailablePlayersRequest;


	}
	public static JsonObject getListGameConfigsRequest() {
		JsonObject listRequest = new JsonObject();
		listRequest.put("type", "GameConfiglist");
		return listRequest;
	}

	public static JsonObject getRefreshGameConfigsRequest() {
		JsonObject listRequest = new JsonObject();
		listRequest.put("type", "GameConfigRefresh");
		return listRequest;
	}

	public static JsonObject getListGamesRequest() {
		JsonObject listRequest = new JsonObject();
		listRequest.put("type", "Gamelist");
		return listRequest;
	}	
	
	public static JsonObject createGamesRequest(String gameConfigId) {
		JsonObject gameRequest = new JsonObject();
		gameRequest.put("type", "CreateGame");
		gameRequest.put("gameConfigIdentity",gameConfigId);
		return gameRequest;
	}	

	public static JsonObject connectRequest(String identity) {
		JsonObject newIdentity = new JsonObject();
		newIdentity.put("type", "NewAdmin");
		newIdentity.put("identity", identity);
		return newIdentity;
	}
	public static JsonObject getCreateGameRequest(String gameConfigId) {
		JsonObject createGame = new JsonObject();
		createGame.put("type", "CreateGame");
		createGame.put("gameConfig", gameConfigId);
		return createGame;
	}
	public static JsonObject getAddPlayerRequest(Integer gameId,List<String> playerIds) {
		JsonObject addPlayer = new JsonObject();
		addPlayer.put("type", "AddPlayer");
		addPlayer.put("game", gameId);
		JsonArray players = new JsonArray();
		for(String playerId:playerIds)
			players.add(playerId);
		addPlayer.put("players", players);
		return addPlayer;
	}
	public static JsonObject getRemovePlayerRequest(Integer gameId,List<String> playerIds) {
		JsonObject removePlayer = new JsonObject();
		removePlayer.put("type", "RemovePlayer");
		removePlayer.put("game", gameId);
		JsonArray players = new JsonArray();
		for(String playerId:playerIds)
			players.add(playerId);
		removePlayer.put("players", players);
		return removePlayer;
	}

	public static JsonObject getViewGameRequest(int gameId) {
		JsonObject viewGame = new JsonObject();
		viewGame.put("type", "ViewGame");
		viewGame.put("game", gameId);
		return viewGame;
	}
}
