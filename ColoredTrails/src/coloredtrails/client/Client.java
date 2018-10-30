package coloredtrails.client;

import java.util.List;

import coloredtrails.common.Exchange;

public interface Client {
	
	//auxillary functions

	public String getServerHostname();

	public int getServerPort();

	public String getClientName();

	public void setServerHostname(String hostname);

	public void setServerPort(int port);

	public void setClientName(String name);
	
	//calls to server

	public void connectToServer();
	
	public void sendExchanges(List<Exchange> exchanges);
	
	public void sendExchangeApprovals(List<Exchange> exchanges);
	
	public void sendQuitGame();
	
	
	//call backs from server
	
	public void connectionStatus(boolean status);
	
	public void updateGame(String gamestatus);
	
}
