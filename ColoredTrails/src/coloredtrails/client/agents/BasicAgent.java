package coloredtrails.client.agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import coloredtrails.client.AgentClient;
import coloredtrails.common.ChipSet;
import coloredtrails.common.CtColor;
import coloredtrails.common.Exchange;
import coloredtrails.common.Player;

/**
 * This implements basic agent behaviour; looks at chips required by current
 * player and chips not required by other players
 * 
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class BasicAgent extends AgentClient {

    /**
     * @param clientName
     */
    public BasicAgent(String clientName) {
        super(clientName);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param serverHostname
     * @param serverPort
     * @param clientName
     */
    public BasicAgent(String serverHostname, int serverPort,
            String clientName) {
        super(serverHostname, serverPort, clientName);
        // TODO Auto-generated constructor stub
    }

    /*
     * This does a basic check to see chips i need and then looks at the chips
     * not needed by other players and puts in exchanges appropriately
     */
    @Override
    public List<Exchange> selectExchanges() {
        Player me = this.getGamePlayed().getPlayer(getGameBoardPlayerName());
        List<Exchange> exchanges = new ArrayList<Exchange>();
        if (!me.isReachedGoal())// If the player has not reached the goal
        {
            // Go over all of the chips I need
            ChipSet neededChips = me.getChipsMissingFromBestPath();
            // Go over colors of chips in the needed chips chipset
            for (CtColor color : neededChips.getColors()) {
                int numOfNeededChips = neededChips.getNumChips(color);
                // Look for players to exchange with
                for (Player q : getGamePlayed().getAgentPlayers()) {
                    // Do not exchange with myself
                    if (!q.getPlayerName().equals(me.getPlayerName())) {
                        if (q.getChipsNotUsedInBestPath().contains(color)) {
                            // how much chips the agent can spare
                            int numOfSpareChips = q.getChipsNotUsedInBestPath()
                                    .getNumChips(color);
                            int numChipsAskedFor = 0;
                            if (numOfSpareChips >= numOfNeededChips)
                                numChipsAskedFor = numOfNeededChips;
                            else
                                numChipsAskedFor = numOfSpareChips;
                            // Reduce the number of chips I need by the number
                            // of chips already asked for
                            numOfNeededChips -= numChipsAskedFor;
                            exchanges.add(new Exchange(q.getPlayerName(),
                                    numChipsAskedFor, color));
                            // If I have found someone to give me all needed
                            // chips don't need to ask others
                            if (numOfNeededChips == 0)
                                break;
                        }
                    }
                }
            } // End of needed colors loop
        }
        return exchanges;
    }

    /*
     * @see coloredtrails.client.AgentClient#approveExchanges
     */
    @Override
    public List<Exchange> approveExchanges(List<Exchange> exchanges) {
        Player me = this.getGamePlayed().getPlayer(getGameBoardPlayerName());
        ChipSet myChips = new ChipSet(me.getChipsNotUsedInBestPath());

        for (Exchange exchange : exchanges) {
            Map<CtColor, Integer> chipsAsked = exchange.getChipsAsked();
            boolean exchangeApproval = true;

            for (Map.Entry<CtColor, Integer> entry : chipsAsked.entrySet()) {
                CtColor color = entry.getKey();
                Integer numChips = entry.getValue();
                if (myChips.getNumChips(color) < numChips) {
                    exchangeApproval = false;
                    break;
                } else {
                    for (int i = 0; i < numChips; i++) {
                        if (myChips.contains(color)) {
                            ChipSet.subtractColor(myChips, color);
                        }

                    }
                }

            }
            exchange.setOwnerApproval(exchangeApproval);
        }
        return exchanges;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        int noOfAgents = 0;
        if (args.length == 0) {
            System.out.println("Enter number of agents to be started: ");
            BufferedReader consoleIn = new BufferedReader(
                    new InputStreamReader(System.in));
            try {
                noOfAgents = Integer.parseInt(consoleIn.readLine());
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            noOfAgents = Integer.parseInt(args[0]);
        }
        for (int i = 1; i <= noOfAgents; i++) {
            BasicAgent client = new BasicAgent("BasicClient" + i);
            client.start();
        }

    }

}
