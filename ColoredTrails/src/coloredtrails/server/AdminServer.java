package coloredtrails.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This starts the thread listening for inbound connections from admin clients
 * It listens on port 6788
 * 
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class AdminServer extends Thread {
    private final static Logger LOGGER = Logger
            .getLogger(AdminServer.class.getName());

    public static final int SERVER_PORT = 6788;
    private static int adminClientNum = 0;
    private static List<AdminHandler> adminClients;
    public static final long hbInterval = 500;

    @Override
    public void run() {

        ServerSocket serverSocket = null;

        // Load Game Configs from disk
        GameConfigManager.loadGameConfigs();

        try {
            // Create a server socket listening on given port
            Thread.currentThread().setName("Admin Server");

            serverSocket = new ServerSocket(SERVER_PORT);

            LOGGER.log(Level.INFO,
                    Thread.currentThread().getName()
                            + " - Server listening on port " + SERVER_PORT
                            + " for an admin connection");
            adminClients = new ArrayList<AdminHandler>();

            // Listen for incoming connections for ever
            while (true) {

                // Accept an incoming client connection request
                Socket clientSocket = serverSocket.accept();
                LOGGER.log(Level.INFO, Thread.currentThread().getName()
                        + " - Administration Client conection accepted");

                adminClientNum++;

                // Create a client connection to listen for and process all the
                // messages sent by the client
                AdminHandler adminHandler = new AdminHandler(clientSocket,
                        adminClientNum);
                adminHandler.setName("Admin Client Thread " + adminClientNum);
                adminClients.add(adminHandler);
                adminHandler.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
