package coloredtrails.server;

public class Server {
	public static void main(String args[]) {
		AdminServer adminServer=new AdminServer();
		adminServer.start();
		
		PlayerServer playerServer=new PlayerServer();
		playerServer.start();
	}

}
