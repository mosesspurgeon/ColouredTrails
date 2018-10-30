package coloredtrails.server;

import java.net.Socket;

import org.json.simple.JsonObject;

import coloredtrails.common.JsonSerializable;
import coloredtrails.common.UserType;

/**
 * This represents one client player on the network. This could either be a
 * human or a computer agent
 *
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class User implements JsonSerializable {

    private String identity;
    private Integer currentGame;
    private PlayerHandler managingThread;
    private Socket clientSocket;
    private UserType userType;

    public User(String identity, PlayerHandler managingThread,
            Socket clientSocket, UserType userType) {
        super();
        this.identity = identity;
        this.managingThread = managingThread;
        this.clientSocket = clientSocket;
        this.userType = userType;
    }

    public User(JsonObject user) {
        this.identity = user.getString("identity");
        this.currentGame = user.getInteger("currentGame");
        this.userType = UserType.getUserType(user.getString("userType"));
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject user = new JsonObject();
        user.put("identity", identity);
        user.put("currentGame", currentGame);
        user.put("userType", userType.name());
        return user;
    }

    // copy constructor used only to get a copy of identity info
    // used generally on game completion or when user disconnects
    public User(User user) {
        this.identity = user.identity;
        this.currentGame = user.currentGame;
        this.userType = user.userType;
    }

    public String getIdentity() {
        return identity;
    }

    public Integer getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Integer currentGame) {
        this.currentGame = currentGame;
    }

    public PlayerHandler getManagingThread() {
        return managingThread;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public UserType getUserType() {
        return userType;
    }

    public void disconnectGame() {
        setCurrentGame(null);
    }

    public boolean isIdle() {
        if (getCurrentGame() == null)
            return true;
        return false;
    }

}
