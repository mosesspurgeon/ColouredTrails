package coloredtrails.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;

import coloredtrails.alglib.BestUse;
import coloredtrails.common.CtColor;
import coloredtrails.common.GamePhase;
import coloredtrails.common.Phases;
import coloredtrails.common.Scoring;
import coloredtrails.gui.types.GamePalette;
import coloredtrails.gui.types.HistoryLog;
import coloredtrails.gui.types.TileGoal;
import coloredtrails.gui.types.TilePlayer;
import coloredtrails.server.ServerData;
import coloredtrails.ui.ColoredTrailsBoardGui;

public class GameConfig implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	List<GamePhase> phases;
	private int boardRows;
	private int boardCols;
	private int boardColors;
	private int numComputerPlayers;
	private int numHumanPlayers;
	private int numGoals;
	private String gameConfigId;
	private Scoring score;
	private String gameConfigFilepath;
	private String phasesConfigFilepath;
	private String description;

	public String getGameConfigId() {
		return gameConfigId;
	}

	public String getGameConfigFilepath() {
		return gameConfigFilepath;
	}

	public String getPhasesConfigFilepath() {
		return phasesConfigFilepath;
	}

	public String getDescription() {
		return description;
	}

	public GameConfig(String gameConfigId) {
		try {
		loadGameConfigFromFile(gameConfigId);// load map,scoring,players,colors and goals
		loadPhasesConfigFromFile(gameConfigId);// load phases and times
		this.gameConfigId = gameConfigId;
		setDescription();
		}
		catch (InputMismatchException ex) {
			System.out.println("Invalid Game config for game " + gameConfigId);
			throw ex;
			// e.printStackTrace();
		}

	}

	private void setDescription() {
		description = String.format(
				"%d x %d board; %d humans %d agents %d goals; Scoring (%d %d %d); colors %d; phases %d", boardRows,
				boardCols, numHumanPlayers, numComputerPlayers,numGoals,score.goalweight,score.distweight,score.chipweight,boardColors,phases.size());

	}

	public void loadGameConfigFromFile(String gameConfigId) {
		String filePath = ServerData.gamesDirectory + gameConfigId + "/" + "game" + gameConfigId + ".txt";

		try {
			Scanner scanner = new Scanner(new File(filePath));
			this.gameConfigFilepath = filePath;

			boardRows = scanner.nextInt();
			boardCols = scanner.nextInt();

			int goalweight = scanner.nextInt();
			int distweight = scanner.nextInt();
			int chipweight = scanner.nextInt();
			this.score = new Scoring(goalweight, distweight, chipweight);

			boardColors = scanner.nextInt();
			int[][] map = new int[boardRows][boardCols];
			for (int i = 0; i < boardRows; i++) {
				for (int j = 0; j < boardCols; j++)
					map[i][j] = scanner.nextInt();

			}

			numHumanPlayers = scanner.nextInt();
			for (int i = 0; i < numHumanPlayers; i++) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				String tex = scanner.next();
			}

			numComputerPlayers = scanner.nextInt();
			for (int i = 0; i < numComputerPlayers; i++) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				String tex = scanner.next();
			}

			numGoals = scanner.nextInt();
			for (int i = 0; i < numGoals; i++) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				String tex = scanner.next();

			}
			for (int i = 0; i < numHumanPlayers; i++) {
				int numChips = scanner.nextInt();// read an int for a colored chip
				for (int j = 0; j < numChips; j++) {
					int chipColor = scanner.nextInt();
				}
			}

			for (int i = 0; i < numComputerPlayers; i++) {
				int numChips = scanner.nextInt();// read an int for a colored chip
				for (int j = 0; j < numChips; j++) {
					int chipColor = scanner.nextInt();
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("No Game config at " + filePath);
			// e.printStackTrace();
		} 

	}

	public void loadPhasesConfigFromFile(String gameConfigId) {
		String filePath = ServerData.gamesDirectory + gameConfigId + "/" + "game" + gameConfigId + "Phases.txt";

		// each phase name, duration, indefinite
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filePath));
			this.phasesConfigFilepath = filePath;
			int numOfPhases = scanner.nextInt();
			phases = new ArrayList<GamePhase>();

			for (int i = 0; i < numOfPhases; i++) {
				boolean indefinite = false;
				String phaseName = scanner.next();
				int phaseLength = scanner.nextInt();
				String indefiniteString = scanner.next();
				if (indefiniteString.equals("True"))
					indefinite = true;
				else
					indefinite = false;

				GamePhase p = new GamePhase(phaseName, phaseLength, indefinite);
				phases.add(p);

			}


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

	}

}
