package coloredtrails.server;

import coloredtrails.common.MessageType;


/**
 * This is used to encapsulate messages being communicated on the server.
 * This adds meta attributes to a message
 * 
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */

public class Message {
	private MessageType messageType;
	private String content;

	public Message(MessageType messageType, String clientMsg) {
		this.messageType = messageType;
		this.content = clientMsg;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
