package coloredtrails.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import coloredtrails.common.Board;
import coloredtrails.common.CtColor;
import coloredtrails.common.Exchange;
import coloredtrails.common.GamePhase;
import coloredtrails.common.Game;
import coloredtrails.common.Phases;
import coloredtrails.common.Player;
import coloredtrails.common.Game.Status;
import coloredtrails.ui.ColoredTrailsBoardGui;

public class InitialChipDistribution extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String phaseName;

	private JTextPane communicationPhaseTextPane;
	private JLabel lblCommAndTime;
	private JLabel labelGameStatus;
	private JTextField textField_1;
	private JScrollPane scrollPane;
	private JPanel panelInitialChip;
	private Map<Integer, ArrayList<JTextField>> numColorsPerAgent;// this is the textfields indicating the num of colors
																	// on the ICD pane
	// private JButton btnProposeExchange;
	private JButton btnRefresh;

	private int numAgents;
	private int numAllPlayers;
	private int numColors;

	private Timer phaseDurationUpdateTimer;

	/**
	 * Create the panel.
	 */
	public InitialChipDistribution() {

		numAgents = AdminGameBoard.game.getNumComputerPlayers();
		numAllPlayers = AdminGameBoard.game.getNumAllPlayers();
		numColors = Board.NUM_COLORS;

		ArrayList<JLabel> initialChipDistribution = new ArrayList<JLabel>();
		ArrayList<JTextField> colorsChipText = new ArrayList<JTextField>();
		// An arrayList that maintains how many chip colors for each agent
		numColorsPerAgent = new HashMap<Integer, ArrayList<JTextField>>();
		this.setLayout(null);

		panelInitialChip = new JPanel();
		panelInitialChip.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panelInitialChip.setBounds(12, 12, 870, 313);
		panelInitialChip.setLayout(null);
		add(panelInitialChip);

		int spaceBetweenInitChipRows = 35;
		int firstInitChipRowPosition = 25;
		int spaceBetweenInitChipColumns = 50;

		for (int index = 0; index < numAllPlayers; index++) {
			int colorIndex;
			String labelString;
			String playerName = AdminGameBoard.game.getAllPlayers().get(index).getPlayerName();
			labelString = playerName + " : Initial Chip Distribution :";

			/*
			 * if(index==0)//the human player labelString = new
			 * String("Me : Initial Chip Distribution :"); else labelString = new
			 * String("Agent" + Integer.toString(index) + " : Initial Chip Distribution :");
			 */

			initialChipDistribution.add(new JLabel(labelString));
			initialChipDistribution.get(index).setBounds(12,
					firstInitChipRowPosition + spaceBetweenInitChipRows * index, 249, 15);
			panelInitialChip.add(initialChipDistribution.get(index));

			ArrayList<JTextField> numChipsOfEachColor = new ArrayList<JTextField>();
			for (int i = 0; i < numColors; i++) {
				// The order is : Blue,Red, White, Yellow, Green, Orange, Gray, Brown, Purple,
				// Light blue
				numChipsOfEachColor.add(new JTextField());
				numChipsOfEachColor.get(i).setBounds(264 + spaceBetweenInitChipColumns * i,
						firstInitChipRowPosition + index * spaceBetweenInitChipRows, 19, 15);
				numChipsOfEachColor.get(i).setBackground(UIManager.getColor("Button.background"));
				numChipsOfEachColor.get(i).setText("0");
				numChipsOfEachColor.get(i).setColumns(10);
				numChipsOfEachColor.get(i).setBorder(null);
				panelInitialChip.add(numChipsOfEachColor.get(i));
			}
			numColorsPerAgent.put(index, numChipsOfEachColor);

			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size() - 1;
			colorsChipText.get(colorIndex).setBounds(279, firstInitChipRowPosition + index * spaceBetweenInitChipRows,
					19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(0, 0, 255));
			panelInitialChip.add(colorsChipText.get(colorIndex));

			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size() - 1;
			colorsChipText.get(colorIndex).setBounds(279 + spaceBetweenInitChipColumns,
					firstInitChipRowPosition + index * spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(255, 0, 0));
			panelInitialChip.add(colorsChipText.get(colorIndex));

			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size() - 1;
			colorsChipText.get(colorIndex).setBounds(279 + spaceBetweenInitChipColumns * 2,
					firstInitChipRowPosition + index * spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(255, 255, 255));
			panelInitialChip.add(colorsChipText.get(colorIndex));

			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size() - 1;
			colorsChipText.get(colorIndex).setBounds(279 + spaceBetweenInitChipColumns * 3,
					firstInitChipRowPosition + index * spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(255, 255, 0));
			panelInitialChip.add(colorsChipText.get(colorIndex));

			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size() - 1;
			colorsChipText.get(colorIndex).setBounds(279 + spaceBetweenInitChipColumns * 4,
					firstInitChipRowPosition + index * spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(0, 255, 0));
			panelInitialChip.add(colorsChipText.get(colorIndex));

			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size() - 1;
			colorsChipText.get(colorIndex).setBounds(279 + spaceBetweenInitChipColumns * 5,
					firstInitChipRowPosition + index * spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(255, 140, 0));
			panelInitialChip.add(colorsChipText.get(colorIndex));

			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size() - 1;
			colorsChipText.get(colorIndex).setBounds(279 + spaceBetweenInitChipColumns * 6,
					firstInitChipRowPosition + index * spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(128, 128, 128));
			panelInitialChip.add(colorsChipText.get(colorIndex));

			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size() - 1;
			colorsChipText.get(colorIndex).setBounds(279 + spaceBetweenInitChipColumns * 7,
					firstInitChipRowPosition + index * spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(139, 69, 19));
			panelInitialChip.add(colorsChipText.get(colorIndex));

			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size() - 1;
			colorsChipText.get(colorIndex).setBounds(279 + spaceBetweenInitChipColumns * 8,
					firstInitChipRowPosition + index * spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(128, 0, 128));
			panelInitialChip.add(colorsChipText.get(colorIndex));

			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size() - 1;
			colorsChipText.get(colorIndex).setBounds(279 + spaceBetweenInitChipColumns * 9,
					firstInitChipRowPosition + index * spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(0, 255, 255));
			panelInitialChip.add(colorsChipText.get(colorIndex));

		}

		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 377, 870, 285);
		add(scrollPane);

		communicationPhaseTextPane = new JTextPane();
		scrollPane.setViewportView(communicationPhaseTextPane);
		communicationPhaseTextPane.setEnabled(false);
		updateCommunicationPane();

		// Get the first phase
		// GamePhase nextPhase
		// =AdminGameBoard.game.getPhases().getPhaseSequence().get(0);
		lblCommAndTime = new JLabel("Initial Phase");
		// lblCommAndTime = new JLabel(nextPhase.getName());
		lblCommAndTime.setFont(new Font("Dialog", Font.BOLD, 20));
		lblCommAndTime.setBounds(45, 332, 606, 33);
		updatePhaseDetails();
		add(lblCommAndTime);

		/**
		 * The Refresh button to get the latest status on the game
		 */
		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AdminGameBoard.refreshGame();
			}
		});

		btnRefresh.setBounds(719, 338, 149, 25);
		add(btnRefresh);

		JPanel panelGameStatus = new JPanel();
		panelGameStatus.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panelGameStatus.setBounds(12, 672, 870, 329);
		add(panelGameStatus);
		panelGameStatus.setLayout(null);

		labelGameStatus = new JLabel("",SwingConstants.CENTER);
		labelGameStatus.setFont(new Font("Dialog", Font.PLAIN, 12));
		labelGameStatus.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		labelGameStatus.setBounds(8, 8, 855, 316);
		updateGameStatusDetails();
		panelGameStatus.add(labelGameStatus);
		
	}

	// This method updates GUI alone
	public void updatePhaseClient() {

	}

	/*
	 * @Deprecated public void updatePhase() {
	 * 
	 * GamePhase nextPhase =AdminGameBoard.game.getPhases().getNextPhase();
	 * 
	 * if(nextPhase!=null) { AdminGameBoard.game.getPhases().setCurrent(nextPhase);
	 * phaseName = AdminGameBoard.game.getPhases().getCurrent().getName(); String
	 * text = phaseName + " Phase"; lblCommAndTime.setText(text); } else//startOver
	 * { GamePhase newPhase =
	 * AdminGameBoard.game.getPhases().getPhaseSequence().get(0);
	 * AdminGameBoard.game.getPhases().setCurrent(newPhase); phaseName =
	 * AdminGameBoard.game.getPhases().getCurrent().getName(); String text =
	 * phaseName + " Phase"; lblCommAndTime.setText(text);
	 * 
	 * }
	 * 
	 * 
	 * //Communication phase if(phaseName.equals("Communication")) {
	 * this.btnProposeExchange.setEnabled(true); AdminGameBoard.calcNeededChips();
	 * this.btnNext.setEnabled(false); } //Exchange phase else
	 * if(phaseName.equals("Exchange")) { AdminGameBoard.exchangeChips();
	 * initChipExchangeValues(); this.btnProposeExchange.setEnabled(false);
	 * this.btnNext.setEnabled(true); } //Move phase else
	 * if(phaseName.equals("Movement")) { this.btnProposeExchange.setEnabled(false);
	 * AdminGameBoard.movePlayers(); this.btnNext.setEnabled(true); } }
	 */

	void startTimer() {
		phaseDurationUpdateTimer = new Timer(1000, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (AdminGameBoard.game.getGameStatus() == Status.INPROGRESS
						&& !AdminGameBoard.game.getPhases().getCurrent().isIndefinite()) {
					updatePhaseDetails();
					Phases gamePhases = AdminGameBoard.game.getPhases();
					gamePhases.setCurrentleft(gamePhases.getCurrentSecsLeft() - 1);
				}
			}
		});
		phaseDurationUpdateTimer.start();

	}

	void stopTimer() {
		if (phaseDurationUpdateTimer != null && phaseDurationUpdateTimer.isRunning())
			phaseDurationUpdateTimer.stop();
	}

	void setExchangeAndPathInfo(String info) {
		this.communicationPhaseTextPane.setEnabled(true);
		this.communicationPhaseTextPane.setText(info);
	}

	String getExchangeAndPathInfo() {
		return this.communicationPhaseTextPane.getText();
	}

	void initChipDistribution() {
		for (int i = 0; i < numAllPlayers; i++) {
			ArrayList<JTextField> playerColors = (ArrayList<JTextField>) this.numColorsPerAgent.get(i);
			initColorList(playerColors);
		}
	}

	void setPlayerChips(int playerIndex, ArrayList<CtColor> chips) {
		//
		ArrayList<JTextField> playerColors = (ArrayList<JTextField>) this.numColorsPerAgent.get(playerIndex);
		// First of all init all of the colors
		initColorList(playerColors);

		// The order is : Blue,Red, White, Yellow, Green, Orange, Gray, Brown, Purple,
		// Light blue
		for (CtColor color : chips) {
			int colorNum = color.getColorNum();
			int chipNumber = Integer.parseInt(playerColors.get(colorNum - 1).getText());
			chipNumber++;
			playerColors.get(colorNum - 1).setText(Integer.toString(chipNumber));
		}
	}

	void initColorList(ArrayList<JTextField> playerColors) {
		for (JTextField text : playerColors) {
			text.setText("0");
		}
	}

	void setPlayerChipDistribution(ArrayList<Player> players) {
		int pIndex = 0;
		for (Player p : players) {
			setPlayerChips(pIndex, p.getChips().getChips());
			pIndex++;

		}
	}

	public void updatePhaseDetails() {

		Status currGameStatus = AdminGameBoard.game.getGameStatus();
		String text = "Status: " + currGameStatus.name().toUpperCase();
		if (currGameStatus == Status.INPROGRESS) {

			String phaseName = AdminGameBoard.game.getPhases().getCurrent().getName();
			text += "  - " + phaseName.toUpperCase() + " Phase";
			if (!AdminGameBoard.game.getPhases().getCurrent().isIndefinite()) {
				int timeLeft = AdminGameBoard.game.getPhases().getCurrentSecsLeft();
				text = text + "  Time Left : " + timeLeft;
			}
		}

		lblCommAndTime.setText(text);

	}

	public String getExchangeInformation() {
		String exchangeInfo = "";
		Map<Integer, Map<String, String>> exchangeHistory = sortExchangesByRounds();

		for (Map.Entry<Integer, Map<String, String>> entry : exchangeHistory.entrySet()) {
			int round = entry.getKey();
			exchangeInfo += "Round " + round + ":\n";
			Map<String, String> playerExchanges = entry.getValue();

			for (Map.Entry<String, String> playerExchange : playerExchanges.entrySet()) {
				String playerName = playerExchange.getKey();
				String exchanges = playerExchange.getValue();

				exchangeInfo += "       Player " + playerName + ":\n";
				exchangeInfo += exchanges;
			}
			exchangeInfo += "\n";
			
		}
		return exchangeInfo;
	}

	public Map<Integer, Map<String, String>> sortExchangesByRounds() {
		// Sort by rounds, then by players
		Map<Integer, Map<String, String>> allRoundsHistory = new TreeMap<Integer, Map<String, String>>();

		for (Player p : AdminGameBoard.game.getAllPlayers()) {
			String playerName = p.getPlayerName();
			for (Exchange e : p.getExchangeInitiatedArchive()) {
				int round = e.getRound();

				Map<String, String> roundHistory = allRoundsHistory.get(round);
				if (roundHistory == null) {
					roundHistory = new TreeMap<String, String>();
					allRoundsHistory.put(round, roundHistory);
				}
				String playerExchangesInRound = roundHistory.get(playerName);
				if (playerExchangesInRound == null) {
					playerExchangesInRound = "";
				}
				String updatedExchange=playerExchangesInRound.concat( "                    " + exchangeDescription(e) + "\n");
				roundHistory.put(playerName, updatedExchange);

			}
		}

		return allRoundsHistory;
	}

	public String exchangeDescription(Exchange e) {
		String output = new String();
		output += "To " + e.getAgentName() + ":         Asked --";
		for (CtColor color : e.getChipsAsked().keySet())
			output += e.getChipsAsked().get(color) + " chips of " + color.getColorName() + "; ";
		output += ":         Offered -- ";
		for (CtColor color : e.getChipsOffered().keySet())
			output += e.getChipsOffered().get(color) + " chips of " + color.getColorName() + "; ";
		output += ":         " + (e.isOwnerApproved() ? "Approved" : "Rejected");
		return output;
	}

	public String getGameStatusDetails() {
		Game game = AdminGameBoard.game;
		String gameStatusDetails = "<html><body><h3 align='center'>Game Status</h3>";
		gameStatusDetails += "GameId: " + game.getGameId()  ;
		gameStatusDetails += "&nbsp;&nbsp;&nbsp;";
		gameStatusDetails += "GameConfig: " + game.getGameConfigid() + "<br>";
		gameStatusDetails += "Round: " + game.getRounds() + "<br>";
		
		List<Double> scores = game.getAllPlayersScore();
		int player = 0;
		Double scoreSum = 0.0;
		String table = "<table style=\"width:100%\">  <tr>";
		table += "<th>Boardplayer</th><th>Networkusername</th><th>PlayerType</th><th>Score</th><th>Goal Reached?</th><th>Left?</th></tr>";


		for (Player p : game.getAllPlayers()) {
			String boardPlayer = p.getPlayerName();
			String networkUser = p.getNetworkUser() == null ? "Unloaded" : p.getNetworkUser().getIdentity();
			String playerType = p.getNetworkUser() == null ? "-" : p.getNetworkUser().getUserType().name();
			Double score = (game.getGameStatus()==Status.CREATED)?0:p.getNetworkUser() == null ? 0 : scores.get(player++);
			String goalReached = p.getNetworkUser() == null ? "-" : (game.hasPlayerReachedGoalTile(p) ? "yes" : "no");
			String leftGame = p.isLeftGame()?"yes":"";
			
			scoreSum += score;
			
			table += String.format("<tr><td>%s</td><td>%s</td><td align='center'>%s</td><td align='center'>%f</td><td align='center'>%s</td><td align='center'>%s</td></tr>",
					boardPlayer, networkUser, playerType, score, goalReached,leftGame);
			
		}
		gameStatusDetails += "</table>";

		
		gameStatusDetails += "Total Game Score: " + scoreSum + "<br><br>";
		gameStatusDetails += table+"</body></html>";

		return gameStatusDetails;
	}

	public void updateGameStatusDetails() {
		String text = getGameStatusDetails();
		//System.out.println(text);

		//System.out.println(text);
		labelGameStatus.setText(text);
		
	}

	public void updateCommunicationPane() {
		String output = getExchangeInformation();
		this.setExchangeAndPathInfo(output); // Not returning anything now

	}
}
