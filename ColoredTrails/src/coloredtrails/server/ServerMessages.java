package coloredtrails.server;

import java.util.List;
import java.util.Set;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;

import coloredtrails.common.Game;
import coloredtrails.common.UserType;
/**
 * Class documents all the JSON messages that the server could send out
 *
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class ServerMessages {
	/*
	 *  Player Client Messages
	 */
	@SuppressWarnings("unchecked")
	public static JsonObject getNewIdentityResponse(boolean approval, UserType userType, String identity) {
		JsonObject newIdentityRes = new JsonObject();
		newIdentityRes.put("type", "NewUser");
		newIdentityRes.put("userType", userType.name());
		newIdentityRes.put("approved", Boolean.toString(approval));
		newIdentityRes.put("identity", identity);
		return newIdentityRes;
	}
	
	public static JsonObject startGameNotification(Game gamestatus) {
		JsonObject newIdentityRes = new JsonObject();
		newIdentityRes.put("type", "StartGame");
		newIdentityRes.put("gameStatus", gamestatus.toJsonObject());
		return newIdentityRes;
	}

	public static JsonObject gameUpdateNotification(Game gameStatus) {
		JsonObject updateNotify = new JsonObject();
		updateNotify.put("type", "UpdateGame");
		updateNotify.put("gameStatus", gameStatus.toJsonObject());
		return updateNotify;
	}	
	

    /*
     *  Admin Client Messages
     */	
	@SuppressWarnings("unchecked")
	public static JsonObject getAdminConnectionResponse(boolean approval, String identity) {
		JsonObject newIdentityRes = new JsonObject();
		newIdentityRes.put("type", "NewAdmin");
		newIdentityRes.put("approved", Boolean.toString(approval));
		newIdentityRes.put("identity", identity);
		return newIdentityRes;
	}

	@SuppressWarnings("unchecked")
	public static final String LISTTYPE_AVAILABLE = "Available";
	public static final String LISTTYPE_ALL = "All";

	public static JsonObject getListPlayersResponse(String listType, List<User> users) {
		if (!(listType.equals(LISTTYPE_ALL) || listType.equals(LISTTYPE_AVAILABLE)))
			return null;
		JsonObject listAvailablePlayersResponse = new JsonObject();

		listAvailablePlayersResponse.put("type", listType + "Playerlist");
		JsonArray playerList = new JsonArray();
		for (User user : users) {
			JsonObject availablePlayer = new JsonObject();
			availablePlayer.put("userType", user.getUserType().name());
			availablePlayer.put("identity", user.getIdentity());
			playerList.add(availablePlayer);
		}
		listAvailablePlayersResponse.put("players", playerList);
		return listAvailablePlayersResponse;

	}

	public static JsonObject getListGameConfigsResponse(List<GameConfig> gameConfigs) {
		JsonObject listGameConfigResponse = new JsonObject();

		listGameConfigResponse.put("type", "GameConfiglist");
		JsonArray gameConfigList = new JsonArray();
		for (GameConfig gameConf : gameConfigs) {
			JsonObject gameConfig = new JsonObject();
			gameConfig.put("identity", gameConf.getGameConfigId());
			gameConfig.put("description", gameConf.getDescription());
			gameConfigList.add(gameConfig);
		}
		listGameConfigResponse.put("gameconfigs", gameConfigList);
		System.out.println(listGameConfigResponse.toJson());
		return listGameConfigResponse;
	}
	
	public static JsonObject getRefreshGameConfigsResponse(boolean status) {
		JsonObject listGameConfigResponse = new JsonObject();
		listGameConfigResponse.put("type", "GameConfigRefresh");
		listGameConfigResponse.put("status", status);
		return listGameConfigResponse;
	}	

	public static JsonObject getListGamesResponse(List<Game> games) {
		JsonObject listGameConfigResponse = new JsonObject();

		listGameConfigResponse.put("type", "Gamelist");
		JsonArray gameConfigList = new JsonArray();
		for (Game game : games) {
			JsonObject gameObj = new JsonObject();
			gameObj.put("identity", game.getGameId());
			gameObj.put("gameConfig", game.getGameConfigid());
			gameObj.put("status", game.getGameStatus().name());
			gameObj.put("description", game.getDescriptionText());
			gameObj.put("result", game.getResult());
			gameConfigList.add(gameObj);
		}
		listGameConfigResponse.put("games", gameConfigList);
		return listGameConfigResponse;
	}
	public static JsonObject getCreateGameResponse(String gameConfigId,boolean status,Integer gameId) {
		JsonObject createGame = new JsonObject();
		createGame.put("type", "CreateGame");
		createGame.put("gameConfig", gameConfigId);
		createGame.put("status", status);
		createGame.put("game", gameId);
		return createGame;
	}
	public static JsonObject getAddPlayerResponse(Integer gameId,List<String> playerIds) {
		JsonObject addPlayer = new JsonObject();
		addPlayer.put("type", "AddPlayer");
		addPlayer.put("game", gameId);
		JsonArray players = new JsonArray();
		for(String playerId:playerIds)
			players.add(playerId);
		addPlayer.put("players", players);
		return addPlayer;
	}

	public static JsonObject getRemovePlayerResponse(Integer gameId,List<String> playerIds) {
		JsonObject addPlayer = new JsonObject();
		addPlayer.put("type", "RemovePlayer");
		addPlayer.put("game", gameId);
		JsonArray players = new JsonArray();
		for(String playerId:playerIds)
			players.add(playerId);
		addPlayer.put("players", players);
		return addPlayer;
	}

	public static JsonObject getViewGameResponse(Integer gameId, Game requestedGame) {
		JsonObject viewGame = new JsonObject();
		viewGame.put("type", "ViewGame");
		viewGame.put("game", gameId);
		viewGame.put("gameStatus", requestedGame==null?null:requestedGame.toJsonObject());		
		return viewGame;
	}

}
