package coloredtrails.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import coloredtrails.common.MessageType;

public class ClientMessageReader extends Thread {
	private final static Logger LOGGER = Logger.getLogger(ClientMessageReader.class.getName());
	
	private PlayerHandler playerHandler;

	public ClientMessageReader(PlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
	
	@Override
	// This thread reads messages from the client's socket input stream
	public void run() {
		try {

			LOGGER.log(Level.INFO, Thread.currentThread().getName() + " - Reading messages from client connection");

			String clientMsg = null;
			// JSONParser parser = new JSONParser();
			while ((clientMsg = playerHandler.getReader().readLine()) != null) {
				LOGGER.log(Level.FINE, Thread.currentThread().getName() + " - Message from client received: ");
				LOGGER.log(Level.FINEST, Thread.currentThread().getName() + " - Message from client received: " + clientMsg);


				// place the message in the queue for the client connection
				// thread to process
				Message msg = new Message(MessageType.CLIENT, clientMsg);
				playerHandler.getMessageQueue().add(msg);

			}
/*
			Message exit = new Message(MessageType.ADMIN, "exit");
			messageQueue.add(exit);*/

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			LOGGER.log(Level.INFO, Thread.currentThread().getName()
					+ " - Closing thread - no more messages will be read for client connection");

		}
		playerHandler.close();
	}
}
