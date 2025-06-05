package com.crm.chat;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.crm.Exception.Error;
import com.crm.user.UserServiceException;

@RestController
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174"), ("http://139.84.136.208 ") })
@RequestMapping("/api/chat")
public class ChatController {

	@Autowired
	private ChatsService chatsService;

//	@Autowired
//	private SimpMessagingTemplate messagingTemplate;

//	@MessageMapping("/addChat")
//	@SendTo("/topic/chats")
//	public void addChats(@Payload Chats chat) {
//		try {
//			ResponseEntity<Chats> newComData = chatsService.addChats(chat);
//			Chats newChat = newComData.getBody();
//
//			long supportId = newChat.getSupportId();

			// Broadcast new comment to all subscribers of the supportId topic
//			messagingTemplate.convertAndSend("/topic/chats/" + supportId, newChat);

			// Optionally, send a confirmation message back to the user who posted the
			// comment
//			messagingTemplate.convertAndSend("/topic/response/" + chat.getUserId(), "Chats added successfully");

//		} catch (UserServiceException e) {
			// Log the error and optionally send an error message via WebSocket
//			e.printStackTrace();
//			messagingTemplate.convertAndSend("/topic/errors/" + chat.getSupportId(),
//					new Error(e.getStatusCode(), e.getMessage(), "Failed to add chat", System.currentTimeMillis()));
//		}
//	}

//	@MessageMapping("/getChatsBySupportId")
//	@SendTo("/topic/chats")
//	public void getChatsBySupportId(@Payload Chats chat) throws UserServiceException {
//		try {
//			long supportId = chat.getSupportId();
//			List<Map<String, Object>> chatsBySupportId = chatsService.getChatsBySupportId(supportId);
//
//			messagingTemplate.convertAndSend("/topic/chats/" + supportId, chatsBySupportId);
//		} catch (UserServiceException e) {
//			long supportId = chat.getSupportId();
//			messagingTemplate.convertAndSend("/topic/errors/" + supportId, new Error(e.getStatusCode(), e.getMessage(),
//					"An error occurred while retrieving chat data", System.currentTimeMillis()));
//		}
//	}

    @PostMapping("/add")
    public ResponseEntity<?> addChat(@RequestBody(required=true) Chats chat) {
        try {
            return chatsService.addChats(chat);
        } catch (UserServiceException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/support/{supportId}")
    public ResponseEntity<?> getChatsBySupportId(@PathVariable(name="supportId", required=true) Long supportId) {
        try {
            List<Map<String, Object>> chats = chatsService.getChatsBySupportId(supportId);
            return ResponseEntity.ok(chats);
        } catch (UserServiceException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }
	
	@GetMapping("/getChats/{userId}")
	public ResponseEntity<?> getChatsByUserId(@PathVariable long userId) {
		try {
			List<Chats> chats = chatsService.getChatsByUserId(userId);
			if (chats.isEmpty()) {
				return ResponseEntity.notFound().build();
			} else {
				return ResponseEntity.ok(chats);
			}
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"An error occurred while retrieving chat data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/getChats/{userId}/{supportId}")
	public ResponseEntity<?> getChatsByUserId(@PathVariable long userId, @PathVariable long supportId) {
		try {
			List<Chats> byUserIdAndSupportId = chatsService.getChatsByUserIdAndSupportId(userId, supportId);
			return ResponseEntity.ok(byUserIdAndSupportId);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"An error occurred while retrieving chat data", System.currentTimeMillis()));
		}

	}

}
