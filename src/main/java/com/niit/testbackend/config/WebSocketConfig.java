package com.niit.testbackend.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
@ComponentScan("com.niit")
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config){
		log.debug("->->Starting of configureMessageBroker method in WebSocketConfig.java");
		config.enableSimpleBroker("/topic","/queue");
		config.setApplicationDestinationPrefixes("/app");
		log.debug("->->Ending of configureMessageBroker method in WebSocketConfig.java");
	}
	
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		log.debug("->->Starting of registerStompEndpoints method in WebSocketConfig.java");
		registry.addEndpoint("/chat").withSockJS();
		registry.addEndpoint("/chat_forum").withSockJS();
		log.debug("->->Ending of registerStompEndpoints method in WebSocketConfig.java");
	}
}
