package coloredtrails.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerServer extends Thread{
	private final static Logger LOGGER = Logger.getLogger(PlayerServer.class.getName());

	public static final int SERVER_PORT = 6789;
	private static int clientNum = 0;
	private static List<PlayerHandler> clients;

    @Override
	public void run () {
		ServerSocket serverSocket =null;

		try {

			// Create a server socket listening on given port
			Thread.currentThread().setName("Player Server");

			serverSocket = new ServerSocket(SERVER_PORT);
			
			LOGGER.log(Level.INFO, Thread.currentThread().getName() + " - Server listening on port " + SERVER_PORT
					+ " for a client connection");
			clients = new ArrayList<PlayerHandler>();

			// Listen for incoming connections for ever
			while (true) {

				// Accept an incoming client connection request
				Socket clientSocket = serverSocket.accept();
				LOGGER.log(Level.INFO, Thread.currentThread().getName() + " - Client conection accepted");

				clientNum++;

				// Create a client connection to listen for and process all the
				// messages sent by the client
				PlayerHandler playerHandler = new PlayerHandler(clientSocket, clientNum);
				playerHandler.setName("Client Thread " + clientNum);
				clients.add(playerHandler);
				playerHandler.start();


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
