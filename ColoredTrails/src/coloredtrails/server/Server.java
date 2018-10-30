package coloredtrails.server;

/**
 * This starts the two server threads - AdminServer and PlayerServer for
 * handling inbound admin and player connections
 * 
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class Server {
    public static void main(String args[]) {
        AdminServer adminServer = new AdminServer();
        adminServer.start();

        PlayerServer playerServer = new PlayerServer();
        playerServer.start();
    }

}
