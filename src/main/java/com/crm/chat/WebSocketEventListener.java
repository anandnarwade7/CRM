package com.crm.chat;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.SocketIOClient;
import com.crm.notifications.NotificationsRepository;
import com.crm.user.UserRepository;
import com.crm.user.UserServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketEventListener {

    @Autowired
    private ChatsService chatsService;

    @Autowired
    private NotificationsRepository notificationsRepo;

    @Autowired
    private UserRepository userRepo;

    private final SocketIOServer server;

    @Autowired
    public WebSocketEventListener(SocketIOServer server) {
        this.server = server;

        // Assign userId to client on connect (via query param)
        server.addConnectListener(client -> {
            String userIdStr = client.getHandshakeData().getSingleUrlParam("userId");
            if (userIdStr != null) {
                try {
                    long userId = Long.parseLong(userIdStr);
                    client.set("userId", userId);
                    System.out.println("User connected with ID: " + userId);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid userId on connect: " + userIdStr);
                }
            }
        });

        // Handle addChat event
        server.addEventListener("addChat", Chats.class, (client, chat, ackSender) -> {
            try {
                Chats newChat = chatsService.addChats(chat).getBody();
                long supportId = newChat.getSupportId();
                server.getBroadcastOperations().sendEvent("chat-" + supportId, newChat);
            } catch (UserServiceException e) {
                server.getBroadcastOperations().sendEvent("chat-error-" + chat.getSupportId(), e.getMessage());
            }
        });

        // Handle getChatsBySupportId event
        server.addEventListener("getChatsBySupportId", Chats.class, (client, chat, ackSender) -> {
            long supportId = chat.getSupportId();
            List<Map<String, Object>> chats = chatsService.getChatsBySupportId(supportId);
            client.sendEvent("chat-" + supportId, chats);
        });
    }

    // Helper method to send notification event to a single user client
    public void sendNotificationToUser(Long userId, String message) {
        for (SocketIOClient client : server.getAllClients()) {
            Long connectedUserId = client.get("userId");
            if (connectedUserId != null && connectedUserId.equals(userId)) {
                client.sendEvent("notification", message);
            }
        }
    }

    public void addNotificationToUser(Long userId, String message) {
        for (SocketIOClient client : server.getAllClients()) {
            Long connectedUserId = client.get("userId");
            if (connectedUserId != null && connectedUserId.equals(userId)) {
                client.sendEvent("notification", message);
            }
        }
    }
}
