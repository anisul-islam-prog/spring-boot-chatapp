package com.chatapp.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.chatapp.model.ChatMessage;

@Component
public class WebSocketEventListener {

	public static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {

		logger.info("<!!------ Recieved a new web socket connection ------!!>");

	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String username = (String) headerAccessor.getSessionAttributes().get("username");

		if (username != null) {

			logger.info("<!!------ User Disconnected: " + username + " ------!!>");
			String userListFilePath = System.getProperty("user.dir") + "/src/main/resources/userList.txt";
			String tempFilePath = System.getProperty("user.dir") + "/src/main/resources/tempList.txt";
			File tempFile = new File(tempFilePath);
			File userListFile = new File(userListFilePath);
			try (FileReader userListFileReader = new FileReader(userListFile)) {
				tempFile.createNewFile();
				try (FileWriter tmpw = new FileWriter(tempFile, true)) {
					try (BufferedWriter bw = new BufferedWriter(tmpw)){
					try (BufferedReader br = new BufferedReader(userListFileReader)) {
							String usernameFromFile = null;
							while ((usernameFromFile = br.readLine()) != null) {
								logger.debug("username " + usernameFromFile);
								usernameFromFile = usernameFromFile.trim();
								if(!usernameFromFile.equalsIgnoreCase(username)) {
									bw.write(usernameFromFile);
									bw.newLine();
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(userListFile.delete()) {
				
				logger.debug("userListFile deleted....");
				boolean successful = tempFile.renameTo(userListFile);
				if(successful) {
					logger.debug("tempFile renamed to userListFile...");
				}
				
			}

			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setType(ChatMessage.MessageType.LEAVE);
			chatMessage.setSender(username);

			messagingTemplate.convertAndSend("/topic/public", chatMessage);

		}

	}

}
