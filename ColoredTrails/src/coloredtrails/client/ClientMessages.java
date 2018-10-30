package coloredtrails.client;

import java.util.List;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;

import coloredtrails.common.Exchange;
import coloredtrails.common.UserType;

/**
 * Class documents all the JSON messages that the client could send out
 *
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class ClientMessages {

    public static JsonObject getNewUserRequest(UserType userType,
            String identity) {
        JsonObject newIdentity = new JsonObject();
        newIdentity.put("type", "NewUser");
        newIdentity.put("userType", userType.name());
        newIdentity.put("identity", identity);
        return newIdentity;
    }

    public static JsonObject getExchangeRequest(List<Exchange> exchanges,
            String phase) {
        JsonObject newIdentity = new JsonObject();
        newIdentity.put("type", phase);
        JsonArray exchangeArray = new JsonArray();
        for (Exchange exchange : exchanges)
            exchangeArray.add(exchange.toJsonObject());
        newIdentity.put("exchanges", exchangeArray);
        return newIdentity;
    }

    public static JsonObject updateGamePhaseRequest() {
        JsonObject newIdentity = new JsonObject();
        newIdentity.put("type", "UpdateGamePhase");
        return newIdentity;
    }

    public static JsonObject quitGameRequest(Integer gameId) {
        JsonObject quitGame = new JsonObject();
        quitGame.put("type", "QuitGame");
        quitGame.put("gameId", gameId);
        return quitGame;
    }

}
