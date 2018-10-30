package coloredtrails.client;

import java.util.List;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;

import coloredtrails.common.Exchange;
import coloredtrails.common.UserType;

public class ClientMessages {

    @SuppressWarnings("unchecked")
    public static JsonObject getNewUserRequest(UserType userType,String identity) {
    	JsonObject newIdentity = new JsonObject();
    	newIdentity.put("type", "NewUser");
    	newIdentity.put("userType", userType.name());
        newIdentity.put("identity", identity);
        return newIdentity;
    }
    public static JsonObject getExchangeRequest(List<Exchange> exchanges,String phase) {
    	JsonObject newIdentity = new JsonObject();
    	newIdentity.put("type", phase);
    	JsonArray exchangeArray = new JsonArray();
    	for(Exchange exchange:exchanges)
    		exchangeArray.add(exchange.toJsonObject());
    	newIdentity.put("exchanges", exchangeArray);
        return newIdentity;
    }
    //temporary
	public static JsonObject updateGamePhaseRequest() {
    	JsonObject newIdentity = new JsonObject();
    	newIdentity.put("type", "UpdateGamePhase");
        return newIdentity;
	}
	
    //temporary
	public static JsonObject quitGameRequest(Integer gameId) {
    	JsonObject quitGame = new JsonObject();
    	quitGame.put("type", "QuitGame");
    	quitGame.put("gameId", gameId);
        return quitGame;
	}
	
}
