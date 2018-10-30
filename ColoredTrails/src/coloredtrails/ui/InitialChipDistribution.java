package coloredtrails.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import coloredtrails.admin.AdminGameBoard;
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
	private JLabel lblPhaseAndTime;
	private JTextField textField_1;
	private JScrollPane scrollPane;
	private JPanel panelInitialChip;
	private Map<Integer, ArrayList<JTextField>> numColorsPerAgent;// this is the textfields indicating the num of colors
																	// on the ICD pane
	private JButton btnProposeExchange;
	private JButton btnNext;
	private JButton btnReject;
	private int numAllPlayers;
	private int numChips;
	private int numColors;
	private int numCombos;
	ArrayList<JComboBox<String>> comboBoxAgentChoiceArray;
	ArrayList<JComboBox<String>> comboBoxAskChipNumberArray;
	ArrayList<JComboBox<String>> comboBoxAskChipColorArray;
	ArrayList<JComboBox<String>> comboBoxGiveChipNumberArray;
	ArrayList<JComboBox<String>> comboBoxGiveChipColorArray;

	private Timer phaseUpdateTimer;

	/**
	 * Create the panel.
	 */
	public InitialChipDistribution() {

		numAllPlayers = ColoredTrailsBoardGui.game.getNumAllPlayers();
		// numChips =
		// ColoredTrailsBoardGui.game.getAgentPlayers().get(0).getChips().getNumChips();
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
			String playerName = ColoredTrailsBoardGui.game.getAllPlayers().get(index).getPlayerName();
			labelString = ((playerName.equals(ColoredTrailsBoardGui.humanClient.getGameBoardPlayerName())) ? "Me"
					: playerName) + " : Initial Chip Distribution :";

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
		scrollPane.setBounds(12, 377, 870, 385);
		add(scrollPane);

		communicationPhaseTextPane = new JTextPane();
		scrollPane.setViewportView(communicationPhaseTextPane);
		communicationPhaseTextPane.setEnabled(false);

		// Get the first phase
		// GamePhase nextPhase
		// =AdminBoardGui.game.getPhases().getPhaseSequence().get(0);
		lblPhaseAndTime = new JLabel("Initial Phase");
		// lblCommAndTime = new JLabel(nextPhase.getName());
		lblPhaseAndTime.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPhaseAndTime.setBounds(45, 332, 606, 33);
		add(lblPhaseAndTime);

		/**
		 * The Next button moves the phase to the next phase
		 */
		btnNext = new JButton("NEXT");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnProposeExchange.setEnabled(false);
				btnNext.setEnabled(false);
				btnReject.setEnabled(false);

				phaseName = ColoredTrailsBoardGui.game.getPhases().getCurrent().getName();
				if (phaseName.equals("Exchange")) {
					ColoredTrailsBoardGui.humanClient.handleExchangePhase(true);
				}
				// Move phase
				else if (phaseName.equals("Movement")) {
					ColoredTrailsBoardGui.humanClient.updateGamePhase();
				}
			}
		});

		btnNext.setBounds(700, 338, 80, 25);
		add(btnNext);

		/**
		 * The Next button moves the phase to the next phase
		 */
		btnReject = new JButton("REJECT");
		btnReject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnProposeExchange.setEnabled(false);
				btnReject.setEnabled(false);
				btnNext.setEnabled(false);

				phaseName = ColoredTrailsBoardGui.game.getPhases().getCurrent().getName();
				if (phaseName.equals("Exchange")) {
					ColoredTrailsBoardGui.humanClient.handleExchangePhase(false);
				}

			}
		});
		btnReject.setVisible(false);

		btnReject.setBounds(785, 338, 80, 25);
		add(btnReject);

		JPanel panelExchange = new JPanel();
		panelExchange.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panelExchange.setBounds(12, 772, 870, 249);
		add(panelExchange);
		panelExchange.setLayout(null);

		comboBoxAgentChoiceArray = new ArrayList<JComboBox<String>>();
		comboBoxAskChipNumberArray = new ArrayList<JComboBox<String>>();
		comboBoxAskChipColorArray = new ArrayList<JComboBox<String>>();
		comboBoxGiveChipNumberArray = new ArrayList<JComboBox<String>>();
		comboBoxGiveChipColorArray = new ArrayList<JComboBox<String>>();
		ArrayList<JLabel> askLabelArray = new ArrayList<JLabel>();
		ArrayList<JLabel> forLabelArray = new ArrayList<JLabel>();
		ArrayList<JLabel> chipLabelArray = new ArrayList<JLabel>();

		askLabelArray.add(new JLabel("Ask "));
		askLabelArray.get(0).setFont(new Font("Dialog", Font.BOLD, 16));
		askLabelArray.get(0).setBounds(8, 33 + 33, 70, 15);
		panelExchange.add(askLabelArray.get(0));

		comboBoxAgentChoiceArray.add(new JComboBox<String>());
		comboBoxAgentChoiceArray.get(0).setFont(new Font("Dialog", Font.BOLD, 16));

		for (int i = 0; i < numAllPlayers; i++) {
			String playerName = ColoredTrailsBoardGui.game.getAllPlayers().get(i).getPlayerName();
			if (playerName.equals(ColoredTrailsBoardGui.humanClient.getGameBoardPlayerName()))
				continue;
			comboBoxAgentChoiceArray.get(0).addItem(playerName);
		}
		comboBoxAgentChoiceArray.get(0).setEditable(false);
		comboBoxAgentChoiceArray.get(0).setBounds(60, 28 + 33, 104, 24);
		panelExchange.add(comboBoxAgentChoiceArray.get(0));

		forLabelArray.add(new JLabel("for"));
		forLabelArray.get(0).setFont(new Font("Dialog", Font.BOLD, 16));
		forLabelArray.get(0).setBounds(175, 33 + 33, 70, 15);
		panelExchange.add(forLabelArray.get(0));
		numCombos = 0;

		for (int playerIndex = 0; playerIndex < numAllPlayers; playerIndex++) {

			Player player = ColoredTrailsBoardGui.game.getAllPlayers().get(playerIndex);
			if (player.getPlayerName().equals(ColoredTrailsBoardGui.humanClient.getGameBoardPlayerName()))
				continue;

			numChips = player.getChips().getNumChips();

			comboBoxAskChipNumberArray.add(new JComboBox<String>());
			comboBoxAskChipNumberArray.get(numCombos).setFont(new Font("Dialog", Font.BOLD, 16));

			for (int i = 0; i < numChips + 1; i++) {
				String agentName = Integer.toString(i);
				comboBoxAskChipNumberArray.get(numCombos).addItem(agentName);
			}
			comboBoxAskChipNumberArray.get(numCombos).setEditable(false);
			comboBoxAskChipNumberArray.get(numCombos).setBounds(220, 28 + 33 * numCombos, 58, 24);
			panelExchange.add(comboBoxAskChipNumberArray.get(numCombos));

			comboBoxAskChipColorArray.add(new JComboBox<String>());
			comboBoxAskChipColorArray.get(numCombos).setFont(new Font("Dialog", Font.BOLD, 16));
			comboBoxAskChipColorArray.get(numCombos).setModel(new DefaultComboBoxModel(new String[] { "Blue", "Red",
					"White", "Yellow", "Green", "Orange", "Gray", "Brown", "Purple", "Light Blue" }));
			comboBoxAskChipColorArray.get(numCombos).setBounds(290, 28 + 33 * numCombos, 111, 24);
			panelExchange.add(comboBoxAskChipColorArray.get(numCombos));

			chipLabelArray.add(new JLabel("chips in exchange for "));
			chipLabelArray.get(numCombos).setFont(new Font("Dialog", Font.BOLD, 16));
			chipLabelArray.get(numCombos).setBounds(410, 33 + 33 * numCombos, 220, 15);
			panelExchange.add(chipLabelArray.get(numCombos));

			comboBoxGiveChipNumberArray.add(new JComboBox<String>());
			comboBoxGiveChipNumberArray.get(numCombos).setFont(new Font("Dialog", Font.BOLD, 16));

			for (int i = 0; i < numChips + 1; i++) {
				String agentName = Integer.toString(i);
				comboBoxGiveChipNumberArray.get(numCombos).addItem(agentName);
			}
			comboBoxGiveChipNumberArray.get(numCombos).setEditable(false);
			comboBoxGiveChipNumberArray.get(numCombos).setBounds(620, 28 + 33 * numCombos, 58, 24);
			panelExchange.add(comboBoxGiveChipNumberArray.get(numCombos));

			comboBoxGiveChipColorArray.add(new JComboBox<String>());
			comboBoxGiveChipColorArray.get(numCombos).setFont(new Font("Dialog", Font.BOLD, 16));
			comboBoxGiveChipColorArray.get(numCombos).setModel(new DefaultComboBoxModel(new String[] { "Blue", "Red",
					"White", "Yellow", "Green", "Orange", "Gray", "Brown", "Purple", "Light Blue" }));
			comboBoxGiveChipColorArray.get(numCombos).setBounds(690, 28 + 33 * numCombos, 111, 24);
			panelExchange.add(comboBoxGiveChipColorArray.get(numCombos));

			numCombos++;

		}

		btnProposeExchange = new JButton("<html>Propose Exchange </html>");// new JButton("<html>Propose <br> Exchange
																			// </html>");
		btnProposeExchange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnProposeExchange.setEnabled(false);
				btnNext.setEnabled(false);

				// Collect exchange information from combo boxes
				String agentName = comboBoxAgentChoiceArray.get(0).getSelectedItem().toString();
				HashMap<CtColor, Integer> chipRequests = new HashMap<CtColor, Integer>();
				HashMap<CtColor, Integer> chipOfferes = new HashMap<CtColor, Integer>();
				for (int i = 0; i < numCombos; i++) { // This is no more numAgents its just single agent which we got
														// outside already. We have option of choosing multiple
														// exchanges for one agent
					if (comboBoxAskChipNumberArray.get(i).getSelectedItem() != null) {
						int chipNumberAsked = Integer
								.parseInt(comboBoxAskChipNumberArray.get(i).getSelectedItem().toString());
						// If I want to exchange with anyone
						if (chipNumberAsked != 0) {
							String color = comboBoxAskChipColorArray.get(i).getSelectedItem().toString();
							System.out.println(agentName + " wants " + Integer.toString(chipNumberAsked) + " " + color
									+ " chips.");
							// BestUse.updateHumanRequestChip(chipNumber,agentName,color);
							chipRequests.put(new CtColor(color), chipNumberAsked);
						}

						int chipNumberOffered = Integer
								.parseInt(comboBoxGiveChipNumberArray.get(i).getSelectedItem().toString());
						if (chipNumberOffered != 0) {
							String color = comboBoxGiveChipColorArray.get(i).getSelectedItem().toString();
							System.out.println(agentName + " wants " + Integer.toString(chipNumberAsked) + " " + color
									+ " chips.");
							chipOfferes.put(new CtColor(color), chipNumberOffered);
						}
					}
				}

				// Create and send a exchange msg to the server
				Exchange newExchange = new Exchange(agentName, chipRequests, chipOfferes);
				List<Exchange> exchanges = new ArrayList<Exchange>();
				exchanges.add(newExchange);
				ColoredTrailsBoardGui.humanClient.sendExchanges(exchanges);
			}
		});
		btnProposeExchange.setFont(new Font("Dialog", Font.BOLD, 16));
		btnProposeExchange.setBounds(641, 208, 217, 29);
		btnProposeExchange.setEnabled(false);
		panelExchange.add(btnProposeExchange);

		JButton btnAskAI = new JButton("Ask AI");
		btnAskAI.setFont(new Font("Dialog", Font.BOLD, 16));
		btnAskAI.setBounds(23, 211, 117, 25);
		panelExchange.add(btnAskAI);

	}

	/**
	 * Initialize the gui exchange chip values to be "Agent1" "0" and "blue"
	 */
	public void initChipExchangeValues() {
		// currently able to exchange with only one agent
		comboBoxAgentChoiceArray.get(0).setSelectedIndex(0);
		for (int i = 0; i < numCombos; i++) {
			comboBoxAskChipNumberArray.get(i).setSelectedIndex(0);
			comboBoxAskChipColorArray.get(i).setSelectedIndex(0);
			comboBoxGiveChipNumberArray.get(i).setSelectedIndex(0);
			comboBoxGiveChipColorArray.get(i).setSelectedIndex(0);
		}
	}

	public void updatePhaseClient() {

		Status gameStatus = ColoredTrailsBoardGui.game.getGameStatus();
		updatePhaseAndTimeDetails();

		if (gameStatus == Status.INPROGRESS) {
			phaseName = ColoredTrailsBoardGui.game.getPhases().getCurrent().getName();

			// Communication phase
			if (phaseName.equals("Communication")) {
				this.btnProposeExchange.setEnabled(true);
				// I'added comment
				ColoredTrailsBoardGui.calcNeededChipsClient();
				this.btnNext.setEnabled(false);
				this.btnReject.setVisible(false);
			}
			// Exchange phase
			else if (phaseName.equals("Exchange")) {
				ColoredTrailsBoardGui.exchangeChipsClient(); // updates GUI only
				initChipExchangeValues();// jus resets the drop downs in the exchange panel back to 0
				this.btnProposeExchange.setEnabled(false);
				this.btnNext.setText("APPROVE");
				this.btnReject.setVisible(true);
				this.btnNext.setEnabled(true);
				this.btnReject.setEnabled(true);
				
				
				// if the game is finite game and player has no exchanges to approve
				// the approve and reject buttons can be disabled

				if (!ColoredTrailsBoardGui.game.getPhases().getCurrent().isIndefinite()
						&& !ColoredTrailsBoardGui.humanClient.hasExchangestoApprove()) {
					this.btnNext.setEnabled(false);
					this.btnReject.setEnabled(false);
				}
			}
			// Move phase
			else if (phaseName.equals("Movement")) {
				ColoredTrailsBoardGui.updatePlayerChipDistribution();// updates the chip counts on the GUI
				ColoredTrailsBoardGui.movePlayersClient(); // updates GUI
				String output = "Exchange Approval communications obtained and processed\n Players movement completed accordingly";
				this.btnNext.setText("NEXT");
				this.setExchangeAndPathInfo(output); // Not returning anything now

				this.btnProposeExchange.setEnabled(false);
				this.btnNext.setEnabled(true);
				this.btnReject.setVisible(false);
				
				//if the game is finite game, button can be disabled
				if (!ColoredTrailsBoardGui.game.getPhases().getCurrent().isIndefinite())
					this.btnNext.setEnabled(false);
					
				
			}
		} else if (ColoredTrailsBoardGui.game.isFinished()) {
			this.btnProposeExchange.setEnabled(false);
			this.btnNext.setEnabled(false);
			this.btnReject.setVisible(false);

			String text = gameStatus.getDescription();

			// displayer players that left if game failed
			if (ColoredTrailsBoardGui.game.getGameStatus() == Status.FAILED) {
				text += " Players left:";
				for (Player p : ColoredTrailsBoardGui.game.getAllPlayers()) {
					if (p.isLeftGame()) {
						text += " " + p.getPlayerName();
					}
				}
			}

			this.setExchangeAndPathInfo(text);
			stopTimer();
		}
	}

	public void startTimer() {
		phaseUpdateTimer = new Timer(1000, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (ColoredTrailsBoardGui.game.getGameStatus() == Status.INPROGRESS
						&& !ColoredTrailsBoardGui.game.getPhases().getCurrent().isIndefinite()) {
					updatePhaseAndTimeDetails();
					int timeLeft = ColoredTrailsBoardGui.game.getPhases().getCurrentSecsLeft();
					ColoredTrailsBoardGui.game.getPhases().setCurrentleft(timeLeft - 1);

				}
			}
		});
		phaseUpdateTimer.start();

	}

	public void stopTimer() {
		if (phaseUpdateTimer != null && phaseUpdateTimer.isRunning())
			phaseUpdateTimer.stop();
	}

	public void updatePhaseAndTimeDetails() {

		Status currGameStatus = ColoredTrailsBoardGui.game.getGameStatus();
		String text = "Status: " + currGameStatus.name().toUpperCase();
		if (currGameStatus == Status.INPROGRESS) {

			String phaseName = ColoredTrailsBoardGui.game.getPhases().getCurrent().getName();
			text += " - " + phaseName.toUpperCase() + " Phase";
			if (!ColoredTrailsBoardGui.game.getPhases().getCurrent().isIndefinite()) {
				int timeLeft = ColoredTrailsBoardGui.game.getPhases().getCurrentSecsLeft();
				text = text + "  Time Left : " + timeLeft;
			}
		}

		lblPhaseAndTime.setText(text);

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

}
