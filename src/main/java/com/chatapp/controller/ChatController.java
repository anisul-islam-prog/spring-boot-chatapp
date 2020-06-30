package com.chatapp.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.chatapp.model.ChatMessage;

@Controller
public class ChatController {
	public static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	String path = System.getProperty("user.dir") + "/src/main/resources/userList.txt";
	String tempFilePath = System.getProperty("user.dir") + "/src/main/resources/tempList.txt";

	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {

		return chatMessage;

	}

	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

		/**
		 * Add username in the websocket session
		 */
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		String username = chatMessage.getSender();
		logger.info("<!!------ New User Connected: " + username + " ------!!>");
		File userListFile = new File(path);
		try {
			if (!(userListFile.exists())) { // checking file exist or not
				userListFile.createNewFile(); // Creating new file
				logger.debug("New File created....");
				try (FileWriter fWrite = new FileWriter(userListFile, true)) {
					try (BufferedWriter bWrite = new BufferedWriter(fWrite)) {
						bWrite.write(username);
						bWrite.newLine();
					}
				}
			} else {
				logger.debug("File already exisit....");
				try (FileWriter fWrite = new FileWriter(userListFile, true)) {
					try (BufferedWriter bWrite = new BufferedWriter(fWrite)) {
						bWrite.write(username);
						bWrite.newLine();
					}
				}
				logger.debug("File write complete.....");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chatMessage;

	}

	@MessageMapping("/chat.removeUser")
	@SendTo("/topic/public")
	public ChatMessage removeUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

		/**
		 * Remove a user in the websocket session
		 */
		boolean kickuser = false;
		File tempFile = new File(tempFilePath);
		File userListFile = new File(path);
		try (FileReader userListFileReader = new FileReader(userListFile)) {
			tempFile.createNewFile();
			try (FileWriter tmpw = new FileWriter(tempFile, true)) {
				try (BufferedReader br = new BufferedReader(userListFileReader)) {
					try (BufferedWriter bw = new BufferedWriter(tmpw)) {
						String usernameFromFile = null;
						while ((usernameFromFile = br.readLine()) != null) {
							logger.debug("username " + usernameFromFile);
							usernameFromFile = usernameFromFile.trim();
							if (!usernameFromFile.equalsIgnoreCase(chatMessage.getAdminCommand())) {
								bw.write(usernameFromFile);
								bw.newLine();
							} else {
								kickuser = true;
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (userListFile.delete()) {

			logger.debug("userListFile deleted....");
			boolean successful = tempFile.renameTo(userListFile);
			if (successful) {
				logger.debug("tempFile renamed to userListFile...");
			}

		}

		String username = chatMessage.getAdminCommand();
		if (kickuser) {

			headerAccessor.getSessionAttributes().put("adminCommand", chatMessage.getAdminCommand());
			logger.info("<!!------ Kicked User: " + username + " ------!!>");

		} else {
			
			String errorMessage ="Error: Sorry, the User does not exist!";
			chatMessage.setErrorMessage(errorMessage);
			logger.info("<!!------ Error: Sorry, the User does not exist! ------!!>");
		}

		return chatMessage;

	}

}
