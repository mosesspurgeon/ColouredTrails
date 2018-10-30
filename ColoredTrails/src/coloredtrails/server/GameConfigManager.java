package coloredtrails.server;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JsonObject;

/**
 * This represents the manager class to manage all Game configs. It stores all
 * configs on the disk in a map with the id of the game config as the key
 * 
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class GameConfigManager {


    private static Map<String, GameConfig> gameConfigs = Collections
            .synchronizedMap(new HashMap<String, GameConfig>());

    public static boolean loadGameConfigs() {
        gameConfigs.clear();
        File file = new File(ServerData.gamesDirectory);
        String[] gameIds = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        boolean success = true;
        for (String gameid : gameIds) {
            try {
                GameConfig newGameConfig = new GameConfig(gameid);
                gameConfigs.put(gameid, newGameConfig);
            } catch (Exception e) {
                success = false;
                e.printStackTrace();
            }
        }
        return success;

    }

    public static JsonObject getGameConfigsJson() {
        return ServerMessages.getListGameConfigsResponse(
                new ArrayList<GameConfig>(gameConfigs.values()));
    }

    public static void main(String args[]) {
        GameConfigManager.loadGameConfigs();
    }

    public static GameConfig getGameConfig(String gameConfigId) {
        return gameConfigs.get(gameConfigId);
    }

}
