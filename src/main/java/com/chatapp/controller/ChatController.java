package com.chatapp.controller;

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
	
	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {

		return chatMessage;

	}

	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

		/**
		 *  Add username in the websocket session
		 */
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		String username = chatMessage.getSender();
		logger.info("<!!------ New User Connected: " + username + " ------!!>");
		return chatMessage;

	}

}
