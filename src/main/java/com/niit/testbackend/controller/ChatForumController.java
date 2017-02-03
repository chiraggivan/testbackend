package com.niit.testbackend.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.niit.testbackend.config.AppInitializer;
import com.niit.testbackend.model.Message;
import com.niit.testbackend.model.OutputMessage;

@Controller
public class ChatForumController {

	private static final Logger log = LoggerFactory.getLogger(ChatForumController.class);
	
	@MessageMapping("/chat_forum") /// send message
	@SendTo("/topic/message")      /// receive message
	public OutputMessage sendMessage(Message message){
		log.debug("Calling sendMessage(message) from chatForumController.java");
		log.debug("Message :", message.getMessage());
		log.debug("Message ID :", message.getId());
		return new OutputMessage(message, new Date()); /// append current date
	}
}
