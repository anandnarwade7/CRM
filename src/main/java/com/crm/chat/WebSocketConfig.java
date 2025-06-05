package com.crm.chat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;

import jakarta.annotation.PreDestroy;


//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//	@Override
//	public void registerStompEndpoints(StompEndpointRegistry registry) {
//		registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:5173", "http://localhost:5174",
//				"http://localhost:5172", "http://localhost:5175", "http://localhost:5171").withSockJS();
//	}
//
//	@Override
//	public void configureMessageBroker(MessageBrokerRegistry config) {
//		config.enableSimpleBroker("/topic");
//		config.setApplicationDestinationPrefixes("/app");
//	}

//}

//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//	@Override
//	public void registerStompEndpoints(StompEndpointRegistry registry) {
//		System.out.println("✅ Registering STOMP endpoint /ws with allowed origins");
//		registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:5173", "http://localhost:5174",
//				"http://localhost:5172", "http://localhost:5175", "http://localhost:5171").withSockJS();
//	}
//
//	@Override
//	public void configureMessageBroker(MessageBrokerRegistry config) {
//		System.out.println("✅ Configuring message broker with /topic and /app");
//		config.enableSimpleBroker("/topic");
//		config.setApplicationDestinationPrefixes("/app");
//	}
//}

@Configuration
public class WebSocketConfig {

	private SocketIOServer server;

	@Bean
	public SocketIOServer socketIOServer() {
		com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
		config.setHostname("localhost");
		config.setPort(9092);

		   config.setOrigin("*"); 	

	        config.setAuthorizationListener(new AuthorizationListener() {
	            @Override
	            public boolean isAuthorized(HandshakeData data) {
	                return true;
	            }
	        });
	        
		server = new SocketIOServer(config);
		server.start();

		return server;
	}

	@PreDestroy
	public void stopServer() {
		if (server != null) {
			server.stop();
		}
	}
}
