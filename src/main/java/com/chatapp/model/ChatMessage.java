package com.chatapp.model;

public class ChatMessage {
	private MessageType type;
	private String content;
	private String sender;
	private String adminCommand;
	private String errorMessage;

	public enum MessageType {
		CHAT, JOIN, LEAVE, KICK
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getAdminCommand() {
		return adminCommand;
	}

	public void setAdminCommand(String adminCommand) {
		this.adminCommand = adminCommand;
	}

	@Override
	public String toString() {
		return "ChatMessage [type=" + type + ", content=" + content + ", sender=" + sender + ", adminCommand="
				+ adminCommand + ", errorMessage=" + errorMessage + "]";
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adminCommand == null) ? 0 : adminCommand.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatMessage other = (ChatMessage) obj;
		if (adminCommand == null) {
			if (other.adminCommand != null)
				return false;
		} else if (!adminCommand.equals(other.adminCommand))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		} else if (!sender.equals(other.sender))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
