package coloredtrails.server;

import coloredtrails.common.MessageType;

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
