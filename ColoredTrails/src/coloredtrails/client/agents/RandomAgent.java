package coloredtrails.client.agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import coloredtrails.client.AgentClient;
import coloredtrails.common.ChipSet;
import coloredtrails.common.CtColor;
import coloredtrails.common.Exchange;
import coloredtrails.common.Player;

public class RandomAgent extends AgentClient {

	public RandomAgent(String clientName) {
		super(clientName);
		// TODO Auto-generated constructor stub
	}

	public RandomAgent(String serverHostname, int serverPort, String clientName) {
		super(serverHostname, serverPort, clientName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Exchange> selectExchanges() {
		Player me = this.getGamePlayed().getPlayer(getGameBoardPlayerName());
		List<Exchange> exchanges = new ArrayList<Exchange>();

		if (!me.isReachedGoal())// If the player has not reached the goal
		{
			Player randomPlayer;
			Random random = new Random();

			// chose random player which is not myself
			do {
				int randomPlayerIndex = random.nextInt(this.getGamePlayed().getNumAllPlayers());
				// could be changed to Agent if exchange needs to happen only with agents
				randomPlayer = this.getGamePlayed().getAllPlayers().get(randomPlayerIndex);
			} while (this.getGameBoardPlayerName().equals(randomPlayer.getPlayerName()));

			// pick a random color from player's not used chipset
			List<CtColor> notUsedChips = randomPlayer.getChipsNotUsedInBestPath().getChips();
			if (notUsedChips.size() > 0) {
				int randomChipIndex = random.nextInt(notUsedChips.size());
				int numChipsAskedFor = 1;
				CtColor chipColor = notUsedChips.get(randomChipIndex);
				exchanges.add(new Exchange(randomPlayer.getPlayerName(), numChipsAskedFor, chipColor));
			}
		}
		return exchanges;
	}

	@Override
	public List<Exchange> approveExchanges(List<Exchange> exchanges) {
		Random random = new Random();
		int randomApproval = random.nextInt(2);	//0-approve 1-reject	
		
		for (Exchange exchange : exchanges) {
			exchange.setOwnerApproval(randomApproval==0);
		}
		return exchanges;
	}

	public static void main(String[] args) {

		int noOfAgents = 0;
		if (args.length == 0) {
			System.out.println("Enter number of agents to be started: ");
			BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
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
			RandomAgent client = new RandomAgent("RandomClient" + i);
			client.start();
		}

	}

}
