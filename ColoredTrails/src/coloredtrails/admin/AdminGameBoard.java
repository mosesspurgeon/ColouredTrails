package coloredtrails.admin;

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
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import coloredtrails.common.CtColor;
import coloredtrails.common.Game;
import coloredtrails.common.Goal;
import coloredtrails.common.Player;
import coloredtrails.common.RowCol;
import coloredtrails.common.Tile;

/**
 * Creates a GUI for an admin to look a game in detail This GUI has two main
 * vertical panels. The left panel shows the game board and is created here The
 * right panel is created using the InitialChipDistribution The whole GUI is
 * refreshed with a timer by polling game status
 * 
 *
 * @author Mor Vered -This is based from coloredtrails.ui.ColoredTrailsBoardGui
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class AdminGameBoard implements Runnable {
    // https://stackoverflow.com/questions/21077322/create-a-chess-board-with-jpanel

    public static final String RESOURCES = "../resources/";

    private JFrame mainFrame;

    private final JPanel container = new JPanel(new GridLayout(1, 2));
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private static JButton[][] ctBoardSquares;// = new JButton[8][8];
    private JPanel ctBoard;
    private JPanel ctInfo;
    private final JLabel message = new JLabel(
            "New Colored Trails game is about to begin !");
    private static int rows;
    private static int columns;
    int[][] map; // colour map of each square in the board
    private JButton QuitPhase;

    protected static Object lock = new Object();

    public static Game game;

    public static final int REFRESH_INTERVAL_SECS = 3;

    private static InitialChipDistribution icd;
    public static AdminClient adminClient;

    //Timer to poll game status 
    public static Timer gameStatusRefresher;

    public AdminGameBoard(AdminClient adminClient, Game game) {
        AdminGameBoard.adminClient = adminClient;
        AdminGameBoard.game = game;
        AdminGameBoard.rows = AdminGameBoard.game.getBoard().getRows();
        AdminGameBoard.columns = AdminGameBoard.game.getBoard().getColumns();
        AdminGameBoard.ctBoardSquares = 
                new JButton[AdminGameBoard.rows][AdminGameBoard.columns];
        this.map = AdminGameBoard.game.getBoard().getIntMap();
        initializeGui();

        icd.updatePhaseClient();
        // update players and goals icon images on the board
        updateGuiBoard();

    }

    /**
     * Draw on board all player icons
     */
    public static void updatePlayerLocationsOnBoard() {
        ArrayList<Player> players = game.getAllPlayers();
        for (Player player : players) {
            Tile currentTile = game.getBoard()
                    .getTile(player.getCurrentTile().getCoordinates());

            setMultipleImages(
                    player.getCurrentTile().getCoordinates().getxCoord(),
                    player.getCurrentTile().getCoordinates().getyCoord(),
                    player, currentTile.getPlayers());

        }
    }

    /**
     * Clear the board from all icons
     */
    public static void clearBoard() {
        // first of all set all tiles to have no goals
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                game.getBoard().getTile(new RowCol(i, j)).setHasGoal(false);
                removeImage(i, j);
            }
        }

    }

    /**
     * Draw on board all goal icons
     */
    public static void updateGoalLocationsOnBoard() {

        ArrayList<Goal> goals = game.getGoals();
        for (Goal g : goals) {
            // set that tile to have a goal on it
            game.getBoard().getTile(g.getCurrentTile().getCoordinates())
                    .setHasGoal(true);
            setImage(g.getCurrentTile().getCoordinates().getxCoord(),
                    g.getCurrentTile().getCoordinates().getyCoord(),
                    g.getGoalIcon());
        }

    }

    public static void updatePlayerChipDistribution() {
        icd.initChipDistribution();
        ArrayList<Player> allPlayers = game.getAllPlayers();
        icd.setPlayerChipDistribution(allPlayers);

    }

    // Code to initialize GUI
    // Mor
    public final void initializeGui() {

        icd = new InitialChipDistribution();
        updatePlayerChipDistribution(); // updates the count of players chip
                                        // distribution

        this.QuitPhase = new JButton("Quit");
        this.QuitPhase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        tools.add(this.QuitPhase); // TODO - add functionality!
        tools.addSeparator();
        tools.add(message);

        gui.add(new JLabel("?"), BorderLayout.LINE_START);

        ctBoard = new JPanel(new GridLayout(0, AdminGameBoard.rows + 1));
        ctInfo = new JPanel();
        ctBoard.setBorder(new LineBorder(Color.BLACK));
        ctInfo.setBorder(new LineBorder(Color.BLACK));
        gui.add(ctBoard);

        // create the chess board squares
        Insets buttonMargin = new Insets(0, 0, 0, 0);
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

        // fill the chess board
        ctBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < AdminGameBoard.rows; ii++) {
            ctBoard.add(new JLabel(Integer.toString(ii + 1),
                    SwingConstants.CENTER));
            // new JLabel(COLS.substring(ii, ii + 1),
            // SwingConstants.CENTER));
        }
        // fill the black non-pawn piece row
        for (int ii = 0; ii < AdminGameBoard.rows; ii++) {
            for (int jj = 0; jj < AdminGameBoard.columns; jj++) {
                switch (jj) {
                case 0:
                    ctBoard.add(
                            new JLabel("" + (ii + 1), SwingConstants.CENTER));
                default:
                    ctBoard.add(ctBoardSquares[ii][jj]);
                }
            }
        }

        container.add(gui);
        container.add(icd);

        mainFrame = new JFrame("ColoredTrails");
        JScrollPane scrPane = new JScrollPane();

        // only a configuration to the jScrollPane...
        scrPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mainFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent arg0) {
                synchronized (lock) {
                    mainFrame.setVisible(false);
                    lock.notify();
                    if (gameStatusRefresher != null
                            && gameStatusRefresher.isRunning())
                        gameStatusRefresher.stop();
                    icd.stopTimer();
                }
            }

        });

        // Then, add the jScrollPane to your frame
        mainFrame.getContentPane().add(scrPane);

        scrPane.setViewportView(getContainer());
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setLocationByPlatform(true);

        // ensures the frame is the minimum size it needs to be
        // in order display the components within it
        mainFrame.pack();
        // ensures the minimum size is enforced.
        mainFrame.setMinimumSize(mainFrame.getSize());
    }

    public static void removeImage(int i, int j) {

        ctBoardSquares[i][j].setIcon(null);

    }

    // https://stackoverflow.com/questions/16299517/setting-multiple-icons-on-jbutton
    public static BufferedImage getCombinedImage(BufferedImage i1,
            BufferedImage i2) {
        if (i1.getHeight() != i2.getHeight()
                || i1.getWidth() != i2.getWidth()) {
            throw new IllegalArgumentException("Images are not the same size!");
        }
        BufferedImage bi = new BufferedImage(i1.getHeight(), i1.getWidth(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(i1, 0, 0, null);
        g.drawImage(i2, 0, 0, null);
        g.dispose();

        return bi;
    }

    // Set an icon image for a player or a goal
    public static void setImage(int i, int j, String playerImage) {
        String resource = RESOURCES + playerImage + ".png";

        try {
            ctBoardSquares[i][j].setIcon(new ImageIcon(
                    ImageIO.read(AdminGameBoard.class.getResource(resource))));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void setMultipleImages(int i, int j, Player newPlayer,
            ArrayList<Player> players) {
        String playerImage = newPlayer.getPlayerIcon();
        String resource = "../resources/" + playerImage + ".png";
        if (players.size() == 1)
            setImage(i, j, playerImage);
        else// More than one player on one tile
        {
            try {
                BufferedImage bi1 = ImageIO
                        .read(AdminGameBoard.class.getResource(resource));
                for (Player p : players) {
                    // Check for new players
                    if (!p.getPlayerName().equals(newPlayer.getPlayerName())) {
                        playerImage = p.getPlayerIcon();
                        resource = "../resources/" + playerImage + ".png";
                        BufferedImage bi2 = ImageIO.read(
                                AdminGameBoard.class.getResource(resource));
                        BufferedImage combined = getCombinedImage(bi1, bi2);
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

    public static InitialChipDistribution getIcd() {
        return icd;
    }

    @Override
    public void run() {
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setVisible(true);
        icd.startTimer();
        this.startGuiRefreshTimer();
        block();

    }

    /**
     * Function to keep blocking till the Admin closes GUI
     */

    public void block() {
        synchronized (lock) {
            while (mainFrame.isVisible())
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * Function to update complete Admin GUI
     */

    public static void update() {
        icd.updatePhaseDetails();
        updatePlayerChipDistribution();
        clearBoard();
        updatePlayerLocationsOnBoard();
        updateGoalLocationsOnBoard();
        icd.updateCommunicationPane();
        icd.updateGameStatusDetails();
    }

    /**
     * Function to update players and goals on board
     */
    public void updateGuiBoard() {
        ArrayList<Player> players = game.getHumanPlayers();
        for (Player player : players) {
            AdminGameBoard.setImage(
                    player.getCurrentTile().getCoordinates().getxCoord(),
                    player.getCurrentTile().getCoordinates().getyCoord(),
                    player.getPlayerIcon());
        }

        players = game.getAgentPlayers();
        for (Player player : players) {
            AdminGameBoard.setImage(
                    player.getCurrentTile().getCoordinates().getxCoord(),
                    player.getCurrentTile().getCoordinates().getyCoord(),
                    player.getPlayerIcon());
        }

        ArrayList<Goal> goals = game.getGoals();
        for (Goal g : goals) {
            AdminGameBoard.setImage(
                    g.getCurrentTile().getCoordinates().getxCoord(),
                    g.getCurrentTile().getCoordinates().getyCoord(),
                    g.getGoalIcon());
        }

    }

    /**
     * Function to start Timer to refresh GUI
     */
    void startGuiRefreshTimer() {
        gameStatusRefresher = new Timer(REFRESH_INTERVAL_SECS * 1000,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        refreshGame();
                    }
                });
        gameStatusRefresher.start();

    }

    /**
     * Function to make a call to the server to get the latest game status and
     * update GUI
     */

    public static synchronized void refreshGame() {
        Game newGameStatus = adminClient.getGameStatus(game.getGameId());
        if (game != null) {
            game = newGameStatus;
            update();
        }

    }

}
