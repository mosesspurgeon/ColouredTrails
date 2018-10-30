package coloredtrails.admin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import coloredtrails.common.Game;
import coloredtrails.common.Game.Status;
import coloredtrails.common.UserType;

/**
 * Creates an command line console for administering the Colored Trails Game
 * This is typically used by the game administrator to manage the Gaming server
 * Some uses cases like creating games, game configurations are enabled using
 * this
 *
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class AdminClient extends Thread {
    private static final Logger LOGGER = Logger
            .getLogger(AdminClient.class.getName());

    private String serverHostname = "localhost";
    private int serverPort = 6788;
    private UserType userType;
    private String name = "Admin";

    private Socket clientSocket;
    private DataOutputStream out;
    private BufferedReader in;
    private BufferedReader consoleIn;

    public AdminClient() {
        super();
    }

    public AdminClient(String name) {
        super();
        this.name = name;
    }

    public AdminClient(String serverHostname, int serverPort, String name) {
        super();
        this.serverHostname = serverHostname;
        this.serverPort = serverPort;
        this.name = name;
    }

    /**
     * Main deals with starting a thread to run the command line console It
     * expects the admin name presented as a run time argument
     */

    public static void main(String argv[]) throws Exception {
        AdminClient client;

        if (argv.length > 0)
            client = new AdminClient(argv[0]);
        else
            client = new AdminClient();
        client.start();

    }

    /**
     * Implements the command line console thread. Every supported command is
     * handled separately inside the switch statement. Some commands like
     * creategame expect arguments. The arguments could either be suppplied
     * alongwith the command or entered when prompted
     */
    @Override
    public void run() {
        boolean connected = connectToServer();
        if (connected) {
            LOGGER.log(Level.INFO,
                    "Admin Connection to the server has been approved");
            consoleIn = new BufferedReader(new InputStreamReader(System.in));
            String command;
            System.out.print("Admin> ");
            try {
                loop: while ((command = consoleIn.readLine()) != null
                        && !command.equals("exit")) {
                    command = command.trim();
                    String args = null;
                    if (command.contains(" ")) {
                        String[] parts = command.split(" ", 2);
                        command = parts[0];
                        args = parts[1];
                    }
                    // System.out.println("Command:"+command+" args:"+args);
                    try {
                        switch (command.toLowerCase()) {
                        case "idlers":
                            System.out.println("show idle players");
                            getAvailablePlayers();
                            break;
                        case "players":
                            System.out.println("show all players");
                            getAllPlayers();
                            break;
                        case "games":
                            getAllGames();
                            break;
                        case "results":
                            getAllResults();
                            break;
                        case "cfg":
                            getAllGameConfigs();
                            break;
                        case "refreshcfg":
                            refreshGameConfigs();
                            break;
                        case "creategame":
                            String gameConfig;
                            if (args == null) {
                                System.out.print("Admin>Enter game config:  ");
                                gameConfig = consoleIn.readLine();
                                if (gameConfig == null)
                                    break loop;
                            } else {
                                gameConfig = args;
                            }
                            createGame(gameConfig);
                            break;
                        case "addplayer":
                            String addplayer;
                            if (args == null) {
                                System.out.print(
                                        "Admin>Enter gameid playerids:  ");
                                addplayer = consoleIn.readLine();
                                if (addplayer == null)
                                    break loop;
                            } else {
                                addplayer = args;
                            }

                            addPlayer(addplayer);
                            break;
                        case "removeplayer":
                            String removeplayer;
                            if (args == null) {
                                System.out.print(
                                        "Admin>Enter gameid playerids:  ");
                                removeplayer = consoleIn.readLine();
                                if (removeplayer == null)
                                    break loop;
                            } else {
                                removeplayer = args;
                            }

                            removePlayer(removeplayer);
                            break;
                        case "viewgame":
                            String gameId;
                            if (args == null) {
                                System.out.print("Admin>Enter game id:  ");
                                gameId = consoleIn.readLine();
                                if (gameId == null)
                                    break loop;
                            } else {
                                gameId = args;
                            }
                            viewgame(gameId);
                            break;
                        case "":
                            break;
                        case "help":
                        default:
                            System.out.println("Commands supported");
                            System.out.println(
                                    "players        : List all Players");
                            System.out.println(
                                    "idlers         : List all idle Players not in any game");
                            System.out.println(
                                    "games          : List all the games that have been created and status");
                            System.out.println(
                                    "cfg            : List all the games configs that can be used to create games");
                            System.out.println(
                                    "refreshcfg     : Reads all the games configs from disk again");
                            System.out.println(
                                    "creategame     : Create a new game with the config");
                            System.out.println(
                                    "viewgame       : Can view games that have started and in progress");
                            System.out.println(
                                    "restartgame    : Reloads a game config and readds existingplayers");
                            System.out.println(
                                    "deletegame     : Stops an existing game and deletes it");
                            System.out.println(
                                    "addplayer      : Adds a connected player to a game");
                            System.out.println(
                                    "removeplayer   : Removes a connected player from a game");
                            System.out.println(
                                    "exit           : Logout of the system");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("A number was expected");
                    }
                    System.out.print("\n\nAdmin> ");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
                System.exit(0);
            }

        } else {
            LOGGER.log(Level.INFO,
                    "Admin Connection to the server was not approved");
        }

    }

    /**
     * Function to list game configs
     */

    private void getAllGameConfigs() {
        JsonObject allGameConfigs = syncRequestReply(
                AdminClientMessages.getListGameConfigsRequest());
        JsonArray arrayJson = (JsonArray) allGameConfigs.get("gameconfigs");

        List<JsonObject> sortedList = sortJsonArray(arrayJson, "identity");

        // print all the players
        System.out.println("List of Game Configs:\n");
        for (int i = 0; i < sortedList.size(); i++) {
            JsonObject gameConfig = (JsonObject) sortedList.get(i);
            String id = gameConfig.getString("identity");
            String description = gameConfig.getString("description");
            System.out.printf("  %15s  -  %s\n", id, description);
        }
    }

    /**
     * Function to refresh the server after new game configurations have been
     * loaded on the server
     */
    private void refreshGameConfigs() {
        JsonObject allGameConfigs = syncRequestReply(
                AdminClientMessages.getRefreshGameConfigsRequest());
        boolean success = allGameConfigs.getBoolean("status");
        System.out.println(
                "  refresh was " + (success ? "successful" : "unsuccessful"));

    }

    /**
     * Function to make the initial admin connection to the Game server
     */
    public boolean connectToServer() {
        boolean approved = false;
        try {
            clientSocket = new Socket(serverHostname, serverPort);
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream(), "UTF-8"));
            approved = syncRequestReply(
                    AdminClientMessages.connectRequest(name))
                            .getBoolean("approved");

        } catch (UnknownHostException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        }
        return approved;

    }

    /**
     * Function to list all the available or idle players not part of any games
     */
    public void getAvailablePlayers() {
        JsonObject availablePlayers = syncRequestReply(AdminClientMessages
                .getListPlayersRequest(AdminClientMessages.AVAILABLE));
        JsonArray array = (JsonArray) availablePlayers.get("players");

        List<JsonObject> sortedList = sortJsonArray(array, "identity");
        // print available players
        System.out.println("List of Players:\n");
        for (int i = 0; i < sortedList.size(); i++) {
            JsonObject player = (JsonObject) sortedList.get(i);
            String id = player.getString("identity");
            UserType usertype = UserType.valueOf(player.getString("userType"));
            System.out.println(" " + id + ":" + usertype);
        }

    }

    /**
     * Function to list all the players currently connected to the system
     */
    public void getAllPlayers() {
        JsonObject availablePlayers = syncRequestReply(AdminClientMessages
                .getListPlayersRequest(AdminClientMessages.ALL));
        JsonArray array = (JsonArray) availablePlayers.get("players");

        List<JsonObject> sortedList = sortJsonArray(array, "identity");

        // print all the players
        System.out.println("List of Players:\n");
        for (int i = 0; i < sortedList.size(); i++) {
            JsonObject player = (JsonObject) sortedList.get(i);
            String id = player.getString("identity");
            UserType usertype = UserType.valueOf(player.getString("userType"));
            System.out.println(" " + id + ":" + usertype);
        }
    }

    /**
     * Function to get a list of all the games
     */
    public void getAllGames() {
        JsonObject allGames = syncRequestReply(
                AdminClientMessages.getListGamesRequest());
        JsonArray array = (JsonArray) allGames.get("games");

        List<JsonObject> sortedList = sortJsonArray(array, "identity");

        // print all the games
        System.out.printf("  %10s  %15s  %15s  %s\n", "id", "gameConfig",
                "status", "description");
        for (int i = 0; i < sortedList.size(); i++) {
            JsonObject game = (JsonObject) sortedList.get(i);
            String id = game.getString("identity");
            String gameConfig = game.getString("gameConfig");
            String status = game.getString("status");
            String description = game.getString("description");
            System.out.printf("  %10s  %15s  %15s  %s\n", id, gameConfig,
                    status, description);
        }
    }

    /**
     * Function to print a dashboard of all the games that are over 
     * (Completed successfully, Failed or Maxed out)
     */
    public void getAllResults() {
        JsonObject allGames = syncRequestReply(
                AdminClientMessages.getListGamesRequest());
        JsonArray array = (JsonArray) allGames.get("games");

        List<JsonObject> sortedList = sortJsonArray(array, "identity");

        // print all the results of games that are finished
        System.out.printf("  %10s  %10s  %9s  %s\n", "id", "gameConfig",
                "status", "Result");
        for (int i = 0; i < sortedList.size(); i++) {
            JsonObject game = (JsonObject) sortedList.get(i);
            Status gameStatus = Status.getStatus(game.getString("status"));
            if (gameStatus != Status.CREATED
                    && gameStatus != Status.INPROGRESS) {
                String id = game.getString("identity");
                String gameConfig = game.getString("gameConfig");
                String result = game.getString("result");
                System.out.printf("  %10s  %10s  %9s  %s\n", id, gameConfig,
                        gameStatus.name(), result);
            }
        }
    }

    /**
     * Function to create a new game out of a game config
     */
    public void createGame(String gameConfig) {
        JsonObject createGame = syncRequestReply(
                AdminClientMessages.getCreateGameRequest(gameConfig));
        boolean success = createGame.getBoolean("status");
        System.out.println("  Game creation was " + (success
                ? "successful. Game created: " + createGame.getInteger("game")
                : "unsuccessful"));
    }

    /**
     * Function to add a player to a newly created game
     */
    private void addPlayer(String addplayer) {
        String[] args = addplayer.trim().split("\\s+");
        if (args.length < 2) {
            System.out.println(
                    "  Player addition requires atleast 2 arguments - [gameid] [playerid]");
        } else {
            List<String> playerIds = new ArrayList<String>();
            for (int i = 1; i < args.length; i++) {
                playerIds.add(args[i]);
            }
            JsonObject addPlayerResponse = syncRequestReply(AdminClientMessages
                    .getAddPlayerRequest(Integer.parseInt(args[0]), playerIds));
            Integer game = addPlayerResponse.getInteger("game");
            JsonArray players = (JsonArray) addPlayerResponse.get("players");
            String playersAdded = "";
            for (int i = 0; i < players.size(); i++)
                playersAdded += players.getString(i) + " ";
            System.out.println("  Players added to game " + game + " are :"
                    + playersAdded);
        }
    }

    /**
     * Function to remove Player from a newly created game or one in progress
     */
    private void removePlayer(String removePlayer) {
        String[] args = removePlayer.trim().split("\\s+");
        if (args.length < 2) {
            System.out.println(
                    "  Player removal requires atleast 2 arguments - [gameid] [playerid]");
        } else {
            List<String> playerIds = new ArrayList<String>();
            for (int i = 1; i < args.length; i++) {
                playerIds.add(args[i]);
            }
            JsonObject removePlayerResponse = syncRequestReply(
                    AdminClientMessages.getRemovePlayerRequest(
                            Integer.parseInt(args[0]), playerIds));
            Integer game = removePlayerResponse.getInteger("game");
            JsonArray players = (JsonArray) removePlayerResponse.get("players");
            String playersRemoved = "";
            for (int i = 0; i < players.size(); i++)
                playersRemoved += players.getString(i) + " ";
            System.out.println("  Players Removed from game " + game + " are -"
                    + playersRemoved);
        }
    }

    /**
     * View the complete picture of a single game
     * Creates a GUI to display it
     */
    private void viewgame(String gameId) {

        Game selectedGame = getGameStatus(Integer.parseInt(gameId));
        if (selectedGame == null) {
            System.out.println("Invalid Game Id");
            return;
        }

        AdminGameBoard cb = new AdminGameBoard(this, selectedGame);
        // This will run the GUI in the same thread as the console;
        cb.run();

    }

    /**
     * Function to retrieve Game Object
     */

    public Game getGameStatus(int gameId) {
        JsonObject viewGame = syncRequestReply(
                AdminClientMessages.getViewGameRequest(gameId));
        // System.out.println(viewGame.toJson());
        JsonObject gameStatus = ((JsonObject) viewGame.get("gameStatus"));
        return (gameStatus == null ? null : new Game(gameStatus));
    }

    /*
     * Helper Functions
     */
    
    /**
     * Function for establishing a sync communication
     * JSON request responses
     */
    private JsonObject syncRequestReply(JsonObject request) {
        // send Request
        JsonObject response = null;
        try {
            send(request);
            response = getServerJson();
        } catch (IOException | DeserializationException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        }
        return response;
    }

    /**
     * Sends a JsonObject out 
     */
    private void send(JsonObject obj) throws IOException {
        send(obj.toJson());
    }

    private void send(String message) throws IOException {
        LOGGER.log(Level.FINE,
                Thread.currentThread().getName() + "Sending: " + message);
        out.write((message + "\n").getBytes("UTF-8"));
        out.flush();

    }

    /**
     * Gets a JsonObject 
     */
    public JsonObject getServerJson() throws DeserializationException {
        String serverMsg = getServerMessage();
        JsonObject serverJson = (JsonObject) Jsoner.deserialize(serverMsg);
        return serverJson;
    }

    public String getServerMessage() {
        String msg = null;
        try {
            msg = in.readLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            this.close();
            System.exit(0);
        }
        if (msg == null) {
            LOGGER.log(Level.INFO, "Server closed admin client connection");
            this.close();
            System.exit(0);
        }
        return msg;

    }

    /**
     * Sorts an array of JSON objects based on a specific field
     */
    public List<JsonObject> sortJsonArray(JsonArray arrayJson,
            String sortKeyField) {

        List<JsonObject> arrayList = new ArrayList<JsonObject>();
        for (int i = 0; i < arrayJson.size(); i++) {
            arrayList.add((JsonObject) arrayJson.get(i));
        }

        Collections.sort(arrayList, new Comparator<JsonObject>() {
            @Override
            public int compare(final JsonObject object1,
                    final JsonObject object2) {
                return object1.getString(sortKeyField)
                        .compareTo(object2.getString(sortKeyField));
            }
        });
        return arrayList;
    }

    private void close() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

}
