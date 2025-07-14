package com.crm.chat;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.crm.Exception.Error;
import com.crm.user.UserServiceException;

@RestController
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174"), ("https://propertysearch.ai"), ("https://mloneusk.lol"), ("http://13.127.78.242"),
		("http://65.20.75.1") })
@RequestMapping("api/chats")
public class ChatsController {

	@Autowired
	private ChatsService chatsService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

////	@MessageMapping("/addchats") 
////    @SendTo("/topic/chats")
//	@PostMapping("/addchats")
//	public ResponseEntity<?> addchats(@RequestBody chat chat) {
//		try {
//			return chatsService.addchat(chat);
//
//		} catch (SupportException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
//					"Don't have access to process the request", System.currentTimeMillis()));
//		}
//	}

	@MessageMapping("/addchat")
	@SendTo("/topic/chats")
	public void addchats(@Payload Chats chat) {
		try {
			ResponseEntity<Chats> newComData = chatsService.addChats(chat);
			Chats newchat = newComData.getBody();

			long supportId = newchat.getSupportId();

			// Broadcast new chat to all subscribers of the supportId topic
			messagingTemplate.convertAndSend("/topic/chats/" + supportId, newchat);

			// Optionally, send a confirmation message back to the user who posted the
			// chat
			messagingTemplate.convertAndSend("/topic/response/" + chat.getUserId(), "chat added successfully");

		} catch (UserServiceException e) {
			ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add data", System.currentTimeMillis()));
			;
		}
	}

	@GetMapping("/getchats/{userId}")
	public ResponseEntity<?> getchatsByUserId(@PathVariable long userId) {
		try {
			List<Chats> chats = chatsService.getChatsByUserId(userId);
			if (chats.isEmpty()) {
				return ResponseEntity.notFound().build();
			} else {
				return ResponseEntity.ok(chats);
			}
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/getchats/{userId}/{supportId}")
	public ResponseEntity<?> getchatsByUserId(@PathVariable long userId, @PathVariable long supportId) {
		try {
			List<Chats> byUserIdAndSupportId = chatsService.getChatsByUserIdAndSupportId(userId, supportId);
			return ResponseEntity.ok(byUserIdAndSupportId);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"An error occurred while retrieving chat data", System.currentTimeMillis()));
		}

	}

	@MessageMapping("/getchatsBySupportId")
	@SendTo("/topic/chats")
	public void getchatsBySupportId(@Payload Chats chat) throws UserServiceException {
		try {
			// Retrieve chats for the given supportId
			long supportId = chat.getSupportId();
			List<Map<String, Object>> chatsBySupportId = chatsService.getChatsBySupportId(supportId);

			// Send the data to the subscribed topic
			messagingTemplate.convertAndSend("/topic/chats/" + supportId, chatsBySupportId);
		} catch (UserServiceException e) {
			 ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add data", System.currentTimeMillis()));
		}
	}
}
