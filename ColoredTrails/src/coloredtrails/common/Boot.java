package coloredtrails.common;





import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;


import coloredtrails.ui.ColoredTrailsBoardGui;





public class Boot {
	
	public static String gamesDirectory = "src/games/";
	public static String gameLoadFile = "1";//Default value
	public static String playerID;
	public static String playerGender;
	public static String playerBirthDate;
	
	private int rows = 0;
	private int columns = 0;
	Game game;
	ColoredTrailsBoardGui cb;
	

	/**
	 * Generates a new board map. 
	 * @throws DeserializationException 
	 */
	public Boot() throws DeserializationException {

		

//		StartGameWindow s = new StartGameWindow();//updates the loadFile
//		s.open();
		
		
		try {
			game = new Game(gameLoadFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        game.updatePhase();
		
		//serialize to json
/*		Gson gson = new Gson();
		String gameStatus=gson.toJson(game);
		System.out.println(gameStatus);
*/		
		//deserialize to pojo
/*		GameStatus game2 = gson.fromJson(gameStatus, GameStatus.class);
*/		

		String gameStatus=game.toJsonObject().toJson();
		JsonObject gameStatusJson = (JsonObject) Jsoner.deserialize(gameStatus);
		Game game2 = new Game(gameStatusJson);
		
		
//    	cb = new AdminBoardGui(game2);
    	
    	//update players and goals icon images on the board
    	updateGuiBoard();
		
		cb.run();
		
		
	}
	

	
	
	
	public void updateGuiBoard() {
		ArrayList<Player> players  = game.getHumanPlayers();
		for( Player player : players) {
			ColoredTrailsBoardGui.setImage(player.getCurrentTile().getCoordinates().getxCoord(), player.getCurrentTile().getCoordinates().getyCoord(), player.getPlayerIcon());
		}
		
		players  = game.getAgentPlayers();
		for( Player player : players) {
			ColoredTrailsBoardGui.setImage(player.getCurrentTile().getCoordinates().getxCoord(), player.getCurrentTile().getCoordinates().getyCoord(), player.getPlayerIcon());
		}
	
		
		ArrayList<Goal> goals = game.getGoals();
		for(Goal g : goals) {
			ColoredTrailsBoardGui.setImage(g.getCurrentTile().getCoordinates().getxCoord(), g.getCurrentTile().getCoordinates().getyCoord(), g.getGoalIcon());
		}
			
	}
	
	public static Logger logger=Logger.getLogger("MyLog");// = Logger.getLogger(Boot.class.getName());
	
	/**
	 * Main method. Calls boot. 
	 * @param args
	 * @throws DeserializationException 
	 */
	public static void main(String[] args) throws DeserializationException {
		// TODO Auto-generated method stub
		
		
		logger = Logger.getLogger("MyLog");  
	    FileHandler fh;  
	    
	    try {  

	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("MyLogFile.log");
	        		//"C:/temp/test/MyLogFile.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

	        // the following statement is used to log any messages  
	        //logger.info("My first log");  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  

	    //logger.info("Hi How r u?");  


	    new Boot();
	}
	

	
	/**
	 * Loads a map from an input file. The input file will always be at directory "maps/" and will always have a ".txt" ending.
	 * @param path
	 * @return
	 */
	public int[][] readColorsFromFile(String path){
		
		int [][] map;
		int count = 0;
		try {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(new File("maps/" + path + ".txt"));
			while(scanner.hasNextInt()) {
				if(count==0)
					rows = scanner.nextInt();
				else if(count==1) 
					columns = scanner.nextInt();
					
				
				else {
					map = new int[rows][columns];
					for(int i=0 ; i < rows; i++) {
						for( int j=0; j< columns; j++) {
								map[i][j] = scanner.nextInt();
						}
					}
					return map;
					
				}
				count++;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (@SuppressWarnings("hiding") IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
