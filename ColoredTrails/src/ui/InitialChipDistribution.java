package ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;

import data.Board;
import data.Exchange;
import data.GamePhase;
import data.Phases;
import data.Player;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.JTextPane;
import javax.swing.Timer;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.LineBorder;

import alglib.BestUse;

public class InitialChipDistribution extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String phaseName;

	private JTextPane communicationPhaseTextPane;
	private JLabel lblCommAndTime;
	private JTextField textField_1;
	private JScrollPane scrollPane;
	private JPanel panelInitialChip;
	private Map<Integer, ArrayList<JTextField>> numColorsPerAgent;
	private JButton btnProposeExchange;
	private JButton btnNext;
	private int numAgents;
	private int numAllPlayers;
	private int numChips;
	private int numColors;
	ArrayList<JComboBox<String>> comboBoxAgentChoiceArray;
	ArrayList<JComboBox<String>> comboBoxAskChipNumberArray;
	ArrayList<JComboBox<String>> comboBoxAskChipColorArray;
	ArrayList<JComboBox<String>> comboBoxGiveChipNumberArray;
	ArrayList<JComboBox<String>> comboBoxGiveChipColorArray;

	/**
	 * Create the panel.
	 */
	public InitialChipDistribution() {
		
		numAgents = ColoredTrailsBoardGui.game.getNumComputerPlayers();
		numAllPlayers = ColoredTrailsBoardGui.game.getNumAllPlayers();
		numChips = ColoredTrailsBoardGui.game.getAgentPlayers().get(0).getChips().getNumChips();
		numColors = Board.NUM_COLORS;
		
		ArrayList<JLabel> initialChipDistribution = new ArrayList<JLabel>();
		ArrayList<JTextField> colorsChipText = new ArrayList<JTextField>();
		//An arrayList that maintains how many chip colors for each agent
		numColorsPerAgent = new HashMap<Integer,ArrayList<JTextField>>();
		this.setLayout(null);

		
		panelInitialChip = new JPanel();
		panelInitialChip.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panelInitialChip.setBounds(12, 12, 870, 313);
		panelInitialChip.setLayout(null);
		add(panelInitialChip);
		
		int spaceBetweenInitChipRows = 35;
		int firstInitChipRowPosition = 25;
		int spaceBetweenInitChipColumns = 50;
		
		
		for(int index = 0; index < numAllPlayers;index++) {
			int colorIndex;
			String labelString;
			if(index==0)//the human player
				labelString = new String("Me : Initial Chip Distribution :");
			else
				labelString = new String("Agent" + Integer.toString(index) + " : Initial Chip Distribution :");
			
			initialChipDistribution.add(new JLabel(labelString));
			initialChipDistribution.get(index).setBounds(12, firstInitChipRowPosition+spaceBetweenInitChipRows*index, 249, 15);
			panelInitialChip.add(initialChipDistribution.get(index));
			
			
			ArrayList<JTextField>  numChipsOfEachColor = new ArrayList<JTextField>();
			for(int i = 0 ; i < numColors; i ++ ) {
				//The order is : Blue,Red, White, Yellow, Green, Orange, Gray, Brown, Purple, Light blue
				numChipsOfEachColor.add(new JTextField());
				numChipsOfEachColor.get(i).setBounds(264+spaceBetweenInitChipColumns*i, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
				numChipsOfEachColor.get(i).setBackground(UIManager.getColor("Button.background"));
				numChipsOfEachColor.get(i).setText("0");
				numChipsOfEachColor.get(i).setColumns(10);
				numChipsOfEachColor.get(i).setBorder(null);
				panelInitialChip.add(numChipsOfEachColor.get(i));
			}
			numColorsPerAgent.put(index, numChipsOfEachColor);
			
			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size()-1;
			colorsChipText.get(colorIndex).setBounds(279, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(0, 0, 255));
			panelInitialChip.add(colorsChipText.get(colorIndex));
			
			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size()-1;
			colorsChipText.get(colorIndex).setBounds(279+spaceBetweenInitChipColumns, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(255,0,0));
			panelInitialChip.add(colorsChipText.get(colorIndex));
								
			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size()-1;
			colorsChipText.get(colorIndex).setBounds(279+spaceBetweenInitChipColumns*2, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(255,255,255));
			panelInitialChip.add(colorsChipText.get(colorIndex));
			
			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size()-1;
			colorsChipText.get(colorIndex).setBounds(279+spaceBetweenInitChipColumns*3, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(255,255,0));
			panelInitialChip.add(colorsChipText.get(colorIndex));
			
			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size()-1;
			colorsChipText.get(colorIndex).setBounds(279+spaceBetweenInitChipColumns*4, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(0,255,0));
			panelInitialChip.add(colorsChipText.get(colorIndex));
			
			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size()-1;
			colorsChipText.get(colorIndex).setBounds(279+spaceBetweenInitChipColumns*5, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(255,140,0));
			panelInitialChip.add(colorsChipText.get(colorIndex));
			
			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size()-1;
			colorsChipText.get(colorIndex).setBounds(279+spaceBetweenInitChipColumns*6, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(128,128,128));
			panelInitialChip.add(colorsChipText.get(colorIndex));
			
			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size()-1;
			colorsChipText.get(colorIndex).setBounds(279+spaceBetweenInitChipColumns*7, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(139,69,19));
			panelInitialChip.add(colorsChipText.get(colorIndex));
			
			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size()-1;
			colorsChipText.get(colorIndex).setBounds(279+spaceBetweenInitChipColumns*8, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(128,0,128));
			panelInitialChip.add(colorsChipText.get(colorIndex));
			
			colorsChipText.add(new JTextField());
			colorIndex = colorsChipText.size()-1;
			colorsChipText.get(colorIndex).setBounds(279+spaceBetweenInitChipColumns*9, firstInitChipRowPosition+index*spaceBetweenInitChipRows, 19, 15);
			colorsChipText.get(colorIndex).setEditable(false);
			colorsChipText.get(colorIndex).setColumns(10);
			colorsChipText.get(colorIndex).setBorder(null);
			colorsChipText.get(colorIndex).setBackground(new Color(0,255,255));
			panelInitialChip.add(colorsChipText.get(colorIndex));

		}


		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 377, 870, 385);
		add(scrollPane);
		
		communicationPhaseTextPane = new JTextPane();
		scrollPane.setViewportView(communicationPhaseTextPane);
		communicationPhaseTextPane.setEnabled(false);
		
		//Get the first phase
		//GamePhase nextPhase =ColoredTrailsBoardGui.game.getPhases().getPhaseSequence().get(0); 
		lblCommAndTime = new JLabel("Initial Phase");
		//lblCommAndTime = new JLabel(nextPhase.getName());
		lblCommAndTime.setFont(new Font("Dialog", Font.BOLD, 20));
		lblCommAndTime.setBounds(115, 332, 606, 33);
		add(lblCommAndTime);
		
		
		/**
		 * The Next button moves the phase to the next phase
		 */
		btnNext = new JButton("NEXT PHASE");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updatePhase();
				
				
			}
		});
		
		btnNext.setBounds(719, 338, 149, 25);
		add(btnNext);
		
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
		askLabelArray.get(0).setBounds(8, 33+33, 70, 15);
		panelExchange.add(askLabelArray.get(0));
		

		comboBoxAgentChoiceArray.add(new JComboBox<String>());
		comboBoxAgentChoiceArray.get(0).setFont(new Font("Dialog", Font.BOLD, 16));
		
		for(int i=1 ; i  < numAgents+1; i++) {
			String agentName = "Agent"+Integer.toString(i);
			comboBoxAgentChoiceArray.get(0).addItem(agentName);
		}
		comboBoxAgentChoiceArray.get(0).setEditable(false);
		comboBoxAgentChoiceArray.get(0).setBounds(60, 28+33, 104, 24);
		panelExchange.add(comboBoxAgentChoiceArray.get(0));
		
		forLabelArray.add(new JLabel("for"));
		forLabelArray.get(0).setFont(new Font("Dialog", Font.BOLD, 16));
		forLabelArray.get(0).setBounds(175, 33+33, 70, 15);
		panelExchange.add(forLabelArray.get(0));
		
	
		

		for(int index = 0 ; index < numAgents; index++) {
			
			comboBoxAskChipNumberArray.add(new JComboBox<String>());
			comboBoxAskChipNumberArray.get(index).setFont(new Font("Dialog", Font.BOLD, 16));
			
			for(int i=0 ; i  < numChips+1; i++) {
				String agentName = Integer.toString(i);
				comboBoxAskChipNumberArray.get(index).addItem(agentName);
			}
			comboBoxAskChipNumberArray.get(index).setEditable(false);
			comboBoxAskChipNumberArray.get(index).setBounds(220, 28+33*index, 58, 24);
			panelExchange.add(comboBoxAskChipNumberArray.get(index));
			
			comboBoxAskChipColorArray.add(new JComboBox<String>());
			comboBoxAskChipColorArray.get(index).setFont(new Font("Dialog", Font.BOLD, 16));
			comboBoxAskChipColorArray.get(index).setModel(new DefaultComboBoxModel(new String[] {"Blue", "Red", "White", "Yellow", "Green", "Orange", "Gray", "Brown", "Purple", "Light Blue"}));
			comboBoxAskChipColorArray.get(index).setBounds(290, 28+33*index, 111, 24);
			panelExchange.add(comboBoxAskChipColorArray.get(index));
			
			chipLabelArray.add(new JLabel("chips in exchange for "));
			chipLabelArray.get(index).setFont(new Font("Dialog", Font.BOLD, 16));
			chipLabelArray.get(index).setBounds(410, 33+33*index, 220, 15);
			panelExchange.add(chipLabelArray.get(index));
		
			
			comboBoxGiveChipNumberArray.add(new JComboBox<String>());
			comboBoxGiveChipNumberArray.get(index).setFont(new Font("Dialog", Font.BOLD, 16));
			
			for(int i=0 ; i  < numChips+1; i++) {
				String agentName = Integer.toString(i);
				comboBoxGiveChipNumberArray.get(index).addItem(agentName);
			}
			comboBoxGiveChipNumberArray.get(index).setEditable(false);
			comboBoxGiveChipNumberArray.get(index).setBounds(620, 28+33*index, 58, 24);
			panelExchange.add(comboBoxGiveChipNumberArray.get(index));
			
			comboBoxGiveChipColorArray.add(new JComboBox<String>());
			comboBoxGiveChipColorArray.get(index).setFont(new Font("Dialog", Font.BOLD, 16));
			comboBoxGiveChipColorArray.get(index).setModel(new DefaultComboBoxModel(new String[] {"Blue", "Red", "White", "Yellow", "Green", "Orange", "Gray", "Brown", "Purple", "Light Blue"}));
			comboBoxGiveChipColorArray.get(index).setBounds(690, 28+33*index, 111, 24);
			panelExchange.add(comboBoxGiveChipColorArray.get(index));

		}
		
		btnProposeExchange = new JButton("<html>Propose Exchange </html>");//new JButton("<html>Propose <br> Exchange </html>");
		btnProposeExchange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Get the agent name
				String agentName = comboBoxAgentChoiceArray.get(0).getSelectedItem().toString();
				HashMap<CtColor,Integer> chipRequests = new HashMap<CtColor,Integer>();
				HashMap<CtColor,Integer> chipOfferes = new HashMap<CtColor,Integer>();
				for(int i=0;i<numAgents;i++) {
					if(comboBoxAskChipNumberArray.get(i).getSelectedItem()!=null) {
						int chipNumberAsked = Integer.parseInt(comboBoxAskChipNumberArray.get(i).getSelectedItem().toString()); 
						//If I want to exchange with anyone
						if(chipNumberAsked!=0) {
							String color = comboBoxAskChipColorArray.get(i).getSelectedItem().toString();
							System.out.println(agentName + " wants " + Integer.toString(chipNumberAsked) + " " + color + " chips.");
							//BestUse.updateHumanRequestChip(chipNumber,agentName,color);
							chipRequests.put(new CtColor(color),chipNumberAsked);
						}
						
						int chipNumberOffered = Integer.parseInt(comboBoxGiveChipNumberArray.get(i).getSelectedItem().toString());
						if(chipNumberOffered!=0) {	
							String color = comboBoxGiveChipColorArray.get(i).getSelectedItem().toString();
							System.out.println(agentName + " wants " + Integer.toString(chipNumberAsked) + " " + color + " chips.");
							chipOfferes.put(new CtColor(color),chipNumberOffered);
						}
					}
				}
				Exchange newExchange = new Exchange(agentName,chipRequests,chipOfferes);
				ColoredTrailsBoardGui.game.getHumanPlayers().get(0).addExchange(newExchange);
				
				updatePhase();	
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
		//currently able to exchange with only one agent
		comboBoxAgentChoiceArray.get(0).setSelectedIndex(0);
		for(int i=0;i<numAgents;i++) {
			comboBoxAskChipNumberArray.get(i).setSelectedIndex(0);
			comboBoxAskChipColorArray.get(i).setSelectedIndex(0);
			comboBoxGiveChipNumberArray.get(i).setSelectedIndex(0);
			comboBoxGiveChipColorArray.get(i).setSelectedIndex(0);
		}
	}
	
	
		

	
	
	public  void updatePhase() {
		
		GamePhase nextPhase =ColoredTrailsBoardGui.game.getPhases().getNextPhase(); 
		
		if(nextPhase!=null) {
			ColoredTrailsBoardGui.game.getPhases().setCurrent(nextPhase);
			phaseName = ColoredTrailsBoardGui.game.getPhases().getCurrent().getName();
			String text = phaseName + " Phase"; 
			lblCommAndTime.setText(text);
		}
		else//startOver 
		{
			GamePhase newPhase = ColoredTrailsBoardGui.game.getPhases().getPhaseSequence().get(0);
			ColoredTrailsBoardGui.game.getPhases().setCurrent(newPhase);
			phaseName = ColoredTrailsBoardGui.game.getPhases().getCurrent().getName();
			String text = phaseName + " Phase"; 
			lblCommAndTime.setText(text);
			
		}

		
		//Communication phase
		if(phaseName.equals("Communication")) {
			this.btnProposeExchange.setEnabled(true);
			ColoredTrailsBoardGui.calcNeededChips();
			this.btnNext.setEnabled(false);
		}
		//Exchange phase
		else if(phaseName.equals("Exchange")) {
			ColoredTrailsBoardGui.exchangeChips();
			initChipExchangeValues();
			this.btnProposeExchange.setEnabled(false);
			this.btnNext.setEnabled(true);		
		}
		//Move phase
		else if(phaseName.equals("Movement")) {
			this.btnProposeExchange.setEnabled(false);
			ColoredTrailsBoardGui.movePlayers();
			this.btnNext.setEnabled(true);
		}
	}
	
	
	
	void startTimer(Phases phases) {
		Timer t = new Timer(1000, new ActionListener() {
			ArrayList<GamePhase> gamePhases = (ArrayList<GamePhase>) phases.getPhaseSequence();
			
			int numOfPhases = gamePhases.size();
			int phaseIndex = 0;
			int start=0;
			GamePhase currentPhase = gamePhases.get(phaseIndex);
			int phaseTimeLeft = currentPhase.getDuration();
			String phaseName = currentPhase.getName();
			public void actionPerformed(ActionEvent e) {
				int timeLeft = phaseTimeLeft-start;
				String text = "" + phaseName + " Phase                       	 Time Left : " + timeLeft;
				lblCommAndTime.setText(text);
				start++;
				
				if(timeLeft==0) {
					start = 0;
					phaseIndex++;
					if(phaseIndex<numOfPhases) {
						phaseTimeLeft = gamePhases.get(phaseIndex).getDuration();
						phaseName = gamePhases.get(phaseIndex).getName();
					}
					else {
						phaseName = "DONE";
					}
				}
				
				//SimpleDateFormat sdf = new 
				//lblCommAndTime.setText(new java.util.Date().toString());
			}
		});
		t.start();
		
	}
	
	void setExchangeAndPathInfo(String info) {
		this.communicationPhaseTextPane.setEnabled(true);
		this.communicationPhaseTextPane.setText(info);
	}
	
	
	String getExchangeAndPathInfo() {
		return this.communicationPhaseTextPane.getText();
	}
	

	
	void initChipDistribution() {
		for(int i = 0 ; i < numAllPlayers; i++) {
			ArrayList<JTextField> playerColors = (ArrayList<JTextField>) this.numColorsPerAgent.get(i);
			initColorList(playerColors);
		}
	}
	
	
	void setPlayerChips(int playerIndex, ArrayList<CtColor> chips) {
		ArrayList<JTextField> playerColors = (ArrayList<JTextField>) this.numColorsPerAgent.get(playerIndex);
		//First of all init all of the colors
		initColorList(playerColors);
		
		//The order is : Blue,Red, White, Yellow, Green, Orange, Gray, Brown, Purple, Light blue
		for(CtColor color : chips) {
			int colorNum = color.getColorNum();
			int chipNumber = Integer.parseInt(playerColors.get(colorNum-1).getText());
			chipNumber++;
			playerColors.get(colorNum-1).setText(Integer.toString(chipNumber));
		}
	}
	
	void initColorList(ArrayList<JTextField> playerColors) {
		for(JTextField text : playerColors) {
			text.setText("0");
		}
	}
	
	

	
	void setPlayerChipDistribution(ArrayList<Player> players) {
		int pIndex = 0;
		for(Player p : players) {
			setPlayerChips(pIndex,p.getChips().getChips());
			pIndex++;
			
		}
	}
}
