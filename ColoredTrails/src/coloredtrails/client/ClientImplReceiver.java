package coloredtrails.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This thread helps listen to any server messages asynchronously Provides
 * required decoupling to listen to both a player's moves or a servers commands
 * Helps create the callbacks from the game server
 * 
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class ClientImplReceiver extends Thread {

    private static final Logger LOGGER = Logger
            .getLogger(ClientImpl.class.getName());

    private ClientImpl clientImpl;
    private Socket clientSocket;
    private BufferedReader in;

    public ClientImplReceiver(ClientImpl clientImpl, Socket clientSocket) {
        super();
        this.clientImpl = clientImpl;
        this.clientSocket = clientSocket;
        try {
            this.in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        }
    }

    @Override
    public void run() {
        try {
            String msg = null;
            while ((msg = in.readLine()) != null) {
                clientImpl.onMessage(msg);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }

    }
}
