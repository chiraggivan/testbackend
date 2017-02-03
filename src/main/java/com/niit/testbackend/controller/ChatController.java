package com.niit.testbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.niit.testbackend.model.Message;

@Controller
public class ChatController {

	private static final Logger log = LoggerFactory.getLogger(ChatForumController.class);
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@MessageMapping("/chat")
	@SendTo("/queue/message/{friendID}")
	public void sendMessage(Message message){
		log.debug("Calling sendMessage methof in ChatController.javga");
		log.debug("Message :" + message.getMessage());
		log.debug("Friend ID :" + message.getFriendID());
		simpMessagingTemplate.convertAndSend("/queue/message/"+ message.getFriendID());
	}
}
