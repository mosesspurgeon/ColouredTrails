package coloredtrails.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import coloredtrails.alglib.BestUse;
import coloredtrails.client.HumanClient;
import coloredtrails.common.CtColor;
import coloredtrails.common.Game;
import coloredtrails.common.Goal;
import coloredtrails.common.Player;
import coloredtrails.common.RowCol;
import coloredtrails.common.Tile;


public class ColoredTrailsBoardGui implements Runnable{
	//https://stackoverflow.com/questions/21077322/create-a-chess-board-with-jpanel
	
	public static final String RESOURCES = "../resources/";
	
	private final JPanel container = new JPanel(new GridLayout(1,2));
	private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private static JButton[][] ctBoardSquares;// = new JButton[8][8];
    private JPanel ctBoard;
    private JPanel ctInfo;
    private final JLabel message = new JLabel(
            "New Colored Trails game is about to begin !");
    //private static final String COLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static int rows;
	private static int columns;
    int[][] map; // colour map of each square in the board
    //private JButton makeExchangesButton;
    //private JButton calcNeededChipsButton;
    //private JButton calcBestPathButton;
    private JButton QuitPhase;
    
    public static Game game;
    
    private static BestUse bestUse;
    
    private static InitialChipDistribution icd;
    public static HumanClient humanClient;
    
    private JFrame mainFrame ;

    
    
    public ColoredTrailsBoardGui(HumanClient humanClient, Game game) {
    	ColoredTrailsBoardGui.humanClient = humanClient;
    	ColoredTrailsBoardGui.game = game;
    	ColoredTrailsBoardGui.rows = ColoredTrailsBoardGui.game.getBoard().getRows();
    	ColoredTrailsBoardGui.columns = ColoredTrailsBoardGui.game.getBoard().getColumns();
    	ColoredTrailsBoardGui.ctBoardSquares = new JButton[ColoredTrailsBoardGui.rows][ColoredTrailsBoardGui.columns];
    	this.map = ColoredTrailsBoardGui.game.getBoard().getIntMap();
    	bestUse = new BestUse(game,game.getGoals().get(0));//Only one Possible goal at this point
        initializeGui();

        this.icd.updatePhaseClient();
        
    }
    
    
    /**
     * Draw on board all player icons
     */
    public static void updatePlayerLocationsOnBoard() {
		ArrayList<Player> players  = game.getAllPlayers();
		for( Player player : players) {
			//set that tile to have a player on it
			Tile currentTile = game.getBoard().getTile(player.getCurrentTile().getCoordinates());
			
			setMultipleImages(player.getCurrentTile().getCoordinates().getxCoord(), player.getCurrentTile().getCoordinates().getyCoord(), player,currentTile.getPlayers());
			//setImage(player.getCurrentTile().getCoordinates().getxCoord(), player.getCurrentTile().getCoordinates().getyCoord(), player.getPlayerIcon());
		}
    }
    
    /**
     * Clear the board from all icons
     */
	public static void clearBoard() {
    	//first of all set all tiles to have no goals
    	for( int i=0; i < rows; i++) {
    		for( int j=0 ; j < columns; j ++ ) {
    			game.getBoard().getTile(new RowCol(i,j)).setHasGoal(false);
    			removeImage(i,j);
    		}
    	}
		
	}
    
	
	/**
	 * Draw on board all goal icons
	 */
    public static void updateGoalLocationsOnBoard() {

		ArrayList<Goal> goals = game.getGoals();
		for(Goal g : goals) {
			//set that tile to have a goal on it
			game.getBoard().getTile(g.getCurrentTile().getCoordinates()).setHasGoal(true);
			setImage(g.getCurrentTile().getCoordinates().getxCoord(), g.getCurrentTile().getCoordinates().getyCoord(), g.getGoalIcon());
		}
    	
    	
    }
    
    public static void updatePlayerChipDistribution( ) {
    	icd.initChipDistribution();
        ArrayList<Player> allPlayers = game.getAllPlayers();
        icd.setPlayerChipDistribution(allPlayers);

    }
    


    
    public static void exchangeChipsClient() {
    	icd.setExchangeAndPathInfo(bestUse.performeExchangesClient(game)); //Not returning anything now 
    }
    
/*    @Deprecated
    public static void exchangeChips() {
    	icd.setExchangeAndPathInfo(bestUse.performeExchanges(game)); //performExchanges(game) performs the actual exchanges and returns a string 
		updatePlayerChipDistribution();//updates the chip counts on the GUI 
    }*/
    

/*    public static void calcNeededChipsServer() {
    	bestUse.calcBestPathsServer(game); //sets the best path in the player object
    	bestUse.calcNeededChipsServer(game); //sets the needed chips
    	bestUse.calcExchangesServer(game); // adds the exchange for all the agents in communication phase
    }*/
    
    public static void calcNeededChipsClient() {
    	String outputOne = bestUse.calcBestPathsClient(game); //sets the best path in the player object
    	String outputTwo = bestUse.calcNeededChipsClient(game); //sets the needed chips
    	
    	//predict exchange offers that other agents may put forward based on the best paths and chips needed from them
    	bestUse.predictExchanges(game);
    	String outputThree = bestUse.calcExchangesClient(game); // adds the exchange for all the agents in communication phase
    	icd.setExchangeAndPathInfo(outputOne + outputTwo + outputThree);
    }
        
/*    @Deprecated
    public static void calcNeededChips() {
    	String outputOne = bestUse.calcBestPaths(game); //sets the best path in the player object
    	String outputTwo = bestUse.calcNeededChips(game); //sets the needed chips
    	String outputThree = bestUse.calcExchanges(game); // adds the exchange for all the agents in communication phase
    	icd.setExchangeAndPathInfo(outputOne + outputTwo + outputThree);
    }*/
    

    public static void movePlayersClient() {
    	//game.moveAllSteps();
    	update(); //updates GUI
    }
/*    @Deprecated
    public static void movePlayers() {
    	game.moveOneStep();
    	//game.moveAllSteps();
    	update();
    }
*/    
    //MOR
    public final void initializeGui() {
    	
        icd = new InitialChipDistribution();
        updatePlayerChipDistribution(); //updates the count of players chip distribution
        
        
    	//this.makeExchangesButton = new JButton("Make Exchanges - 3");
    	//this.calcNeededChipsButton = new JButton("Calc Needed Chips - 2");
    	//this.calcBestPathButton = new JButton("Best Path - 1");
    	this.QuitPhase = new JButton("Quit");
    	this.QuitPhase.addActionListener(new ActionListener(){ 
  		  public void actionPerformed(ActionEvent e) { 
			    System.exit(0);
		  } } );
    	
//    	this.makeExchangesButton.addActionListener(new ActionListener() {
//    		public void actionPerformed(ActionEvent e) {
//    			exchangeChips();
//    			icd.updatePhase();
//    		}
//    	});
//    	
//    	this.calcBestPathButton.addActionListener(new ActionListener() {
//    		public void actionPerformed(ActionEvent e) {
//    			calcBestPaths();
//    		}
//    	});
//    	
//    	this.calcNeededChipsButton.addActionListener(new ActionListener() {
//    		public void actionPerformed(ActionEvent e) {
//    			calcNeededChips();
//    			icd.updatePhase();
//    		}
//    	});
    	
    	
        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        //tools.add(this.makeExchangesButton); // TODO - add functionality!
        //tools.add(this.calcNeededChipsButton); // TODO - add functionality!
        //tools.add(this.calcBestPathButton); // TODO - add functionality!
        //tools.addSeparator();
        tools.add(this.QuitPhase); // TODO - add functionality!
        tools.addSeparator();
        tools.add(message);

        gui.add(new JLabel("?"), BorderLayout.LINE_START);

        ctBoard = new JPanel(new GridLayout(0, ColoredTrailsBoardGui.rows+1));
        ctInfo = new JPanel();
        ctBoard.setBorder(new LineBorder(Color.BLACK));
        ctInfo.setBorder(new LineBorder(Color.BLACK));
        gui.add(ctBoard);
        

        // create the chess board squares
        Insets buttonMargin = new Insets(0,0,0,0);
        for (int ii = 0; ii < ctBoardSquares.length; ii++) {
            for (int jj = 0; jj < ctBoardSquares[ii].length; jj++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);
                // our chess pieces are 64x64 px in size, so we'll
                // 'fill this in' using a transparent icon..
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                b.setIcon(icon);
                CtColor tempColor = new CtColor(map[ii][jj]);
                b.setBackground(tempColor.getColor());
                ctBoardSquares[ii][jj] = b;
            }
        }

        
        //fill the chess board
        ctBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < ColoredTrailsBoardGui.rows; ii++) {
            ctBoard.add(
            		new JLabel(Integer.toString(ii+1),
                            SwingConstants.CENTER));
                    //new JLabel(COLS.substring(ii, ii + 1),
                    //SwingConstants.CENTER));
        }
        // fill the black non-pawn piece row
        for (int ii = 0; ii < ColoredTrailsBoardGui.rows; ii++) {
            for (int jj = 0; jj < ColoredTrailsBoardGui.columns; jj++) {
                switch (jj) {
                    case 0:
                        ctBoard.add(new JLabel("" + (ii + 1),
                                SwingConstants.CENTER));
                    default:
                        ctBoard.add(ctBoardSquares[ii][jj]);
                }
            }
        }
        

        
        
        container.add(gui);
        container.add(icd);
        
        
    }
    
    public static void removeImage(int i, int j) {

		ctBoardSquares[i][j].setIcon(null);

    }
    
    //https://stackoverflow.com/questions/16299517/setting-multiple-icons-on-jbutton
    public static BufferedImage getCombinedImage(BufferedImage i1, BufferedImage i2) {
        if (i1.getHeight() != i2.getHeight()
                || i1.getWidth() != i2.getWidth()) {
            throw new IllegalArgumentException("Images are not the same size!");
        }
        BufferedImage bi = new BufferedImage(
                i1.getHeight(), 
                i1.getWidth(), 
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(i1,0,0,null);
        g.drawImage(i2,0,0,null);
        g.dispose();

        return bi;
    }
    
    
    //Set an icon image for a player or a goal
    public static void setImage(int i, int j, String playerImage) {
    	String resource = RESOURCES + playerImage + ".png";
       
        try {
        	//URL url = new URL(resource);
			//ctBoardSquares[i][j].setIcon(new ImageIcon(ImageIO.read(url)));
			//ctBoardSquares[i][j].setIcon(new ImageIcon(ImageIO.read(getClass().getResource(resource))));
			ctBoardSquares[i][j].setIcon(new ImageIcon(ImageIO.read(ColoredTrailsBoardGui.class.getResource(resource))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    
    public static void setMultipleImages(int i, int j,Player newPlayer, ArrayList<Player> players) {
    	String playerImage = newPlayer.getPlayerIcon();
    	String resource = "../resources/" + playerImage + ".png";
    	if(players.size()==1) 
    		setImage(i,j,playerImage);
    	else//More than one player on one tile 
    	{
    		try {
    			BufferedImage bi1 = ImageIO.read(ColoredTrailsBoardGui.class.getResource(resource));
    			for(Player p : players) {
    				//Check for new players
    				if(!p.getPlayerName().equals(newPlayer.getPlayerName())) {
    					playerImage = p.getPlayerIcon();
    					resource = "../resources/" + playerImage + ".png";
    					BufferedImage bi2 = ImageIO.read(ColoredTrailsBoardGui.class.getResource(resource));
    					BufferedImage combined = getCombinedImage(bi1,bi2);
    					bi1 = combined;
    				}
    			}
    			
    			ctBoardSquares[i][j].setIcon(new ImageIcon(bi1));
    		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public final JComponent getBoard() {
        return ctBoard;
    }

    public final JComponent getGui() {
        return gui;
    }
    
    public final JComponent getContainer() {
    	return container;
    }

//    public static void main(String[] args) {
 //       Runnable r = new Runnable() {

    public static InitialChipDistribution getIcd() {
		return icd;
	}


	@Override
    public void run() {
    	
        mainFrame = new JFrame("ColoredTrails");
        JScrollPane scrPane = new JScrollPane();

     // only a configuration to the jScrollPane...
        scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Then, add the jScrollPane to your frame
        mainFrame.getContentPane().add(scrPane);       
        
        //f.add(cb.getGui());
   //     f.add(scrPane);
        scrPane.setViewportView(getContainer());
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setLocationByPlatform(true);

        // ensures the frame is the minimum size it needs to be
        // in order display the components within it
        mainFrame.pack();
        // ensures the minimum size is enforced.
        mainFrame.setMinimumSize(mainFrame.getSize());
        

        
		mainFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				shutdown();
			}

		});
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        //mainFrame.setUndecorated(true);
        mainFrame.setVisible(true);
        		
        icd.startTimer();
    }

            
    public static void update() {
    	updatePlayerChipDistribution();
    	clearBoard();
    	updatePlayerLocationsOnBoard();
    	updateGoalLocationsOnBoard();
    }
    

    //This is redundant copied from Boot
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

	public void shutdown() {
		ColoredTrailsBoardGui.humanClient.sendQuitGame();		
		mainFrame.setVisible(false);
		icd.stopTimer();
	}
}
