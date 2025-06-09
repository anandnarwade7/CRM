package com.crm.importLead;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.crm.leads.LeadRepository;
import com.crm.notifications.Notifications;
import com.crm.notifications.NotificationsRepository;
import com.crm.user.User;
import com.crm.user.UserServiceException;
import com.crm.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NotificationReminder {

	private final ImportLeadRepository leadRepository;
	private final LeadRepository clientRepository;
	private final UserRepository userRepository;
	private final NotificationsRepository notificationRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();

	// Memory-safe notification tracker (no duplicates in 24h)
	private final Set<String> sentNotificationKeys = ConcurrentHashMap.newKeySet();

	public NotificationReminder(ImportLeadRepository leadRepository, NotificationsRepository notificationRepository,
			LeadRepository clientRepository, UserRepository userRepository) {
		this.leadRepository = leadRepository;
		this.notificationRepository = notificationRepository;
		this.clientRepository = clientRepository;
		this.userRepository = userRepository;
	}

	@Scheduled(fixedRate = 30000)
	public void checkDueDatesAndNotify() {
		LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
		System.out.println("⏰ Running checkDueDatesAndNotify at: " + now);

		leadRepository.findAll().parallelStream()
				.forEach(lead -> processLead(lead.getId(), lead.getAssignedTo(), lead.getJsonData(), now, true));
	}

	@Scheduled(fixedRate = 30000)
	public void checkClientsDueDatesAndNotify() {
		LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
		System.out.println("⏰ Running checkClientsDueDatesAndNotify at: " + now);

		clientRepository.findAll().parallelStream().forEach(
				lead -> processLead(lead.getId(), lead.getAssignedTo(), lead.getMassagesJsonData(), now, false));
	}

	private void processLead(long leadId, long assignedTo, String jsonData, LocalDateTime now, boolean isImportLead) {
		if (jsonData == null)
			return;

		try {
			JsonNode logs = objectMapper.readTree(jsonData);
			for (JsonNode logEntry : logs) {
				if (!logEntry.has("dueDate"))
					continue;

				long dueDateMillis = logEntry.get("dueDate").asLong();
				LocalDateTime dueDateTime = Instant.ofEpochMilli(dueDateMillis).atZone(ZoneId.systemDefault())
						.toLocalDateTime();

				if (!now.toLocalDate().equals(dueDateTime.toLocalDate()))
					continue;

				LocalTime dueTime = dueDateTime.toLocalTime().withSecond(0).withNano(0);
				LocalTime nowTime = now.toLocalTime();

				checkAndSendNotification(assignedTo, leadId, logEntry, dueDateMillis, nowTime, dueTime);
			}
		} catch (Exception e) {
			System.err.println("❌ Error processing lead ID " + leadId + ": " + e.getMessage());
		}
	}

	private void checkAndSendNotification(long userId, long leadId, JsonNode logEntry, long dueDateMillis,
                                          LocalTime now, LocalTime dueTime) {
        String comment = logEntry.has("comment") ? logEntry.get("comment").asText() : "No comment";

        // 30 minutes before
        LocalTime thirtyMinBefore = dueTime.minusMinutes(30);
        if (now.equals(thirtyMinBefore)) {
            String key = leadId + "_" + dueDateMillis + "_30min";
            if (sentNotificationKeys.add(key)) {
                sendNotification(userId, comment, leadId, "⏳ 30 minutes before due time");
            }
        }

        // Exact due time
        if (now.equals(dueTime)) {
            String key = leadId + "_" + dueDateMillis + "_exact";
            if (sentNotificationKeys.add(key)) {
                sendNotification(userId, comment, leadId, "⏰ Exact due time");
            }
        }
        
        // 10:00 AM on due date
        LocalTime tenAM = LocalTime.of(10, 0); 
        if (now.equals(tenAM)) {
            String key = leadId + "_" + dueDateMillis + "_10am";
            if (sentNotificationKeys.add(key)) {
                System.out.println("📅 10:00 AM on due date matched at: " + now);
                sendNotification(userId, comment, leadId, "📅 Reminder at 10:00 AM on due date");
            }
        }

        // DEBUGGING - Always show this to track flow
        System.out.println("🧪 [DEBUG] Now: " + now + ", DueTime: " + dueTime + ", 10AM: " + tenAM);
    }

	private void sendNotification(long userId, String comment, long leadId, String timing) {
		try {
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new UserServiceException(409, "User not found, unable to send notifications"));

			String message = "Reminder (" + timing + "): " + comment + " is due!";
			Notifications notification = new Notifications(false, message, user.getEmail(), "check",
					System.currentTimeMillis());
			notificationRepository.save(notification);

			System.out.println("✅ Notification sent to User " + user.getUserId() + " (" + user.getEmail()
					+ ") for Lead ID: " + leadId);
		} catch (Exception e) {
			System.err.println(
					"❌ Notification failed for Lead ID: " + leadId + ", User ID: " + userId + ": " + e.getMessage());
		}
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//import com.corundumstudio.socketio.SocketIOServer;
//import com.crm.notifications.Notifications;
//import com.crm.notifications.NotificationsRepository;
//import com.crm.user.User;
//import com.crm.user.UserRepository;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import java.time.*;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.*;
//
//@Component
//public class NotificationReminder {
//
//    private final ImportLeadRepository leadRepository;
//    private final NotificationsRepository notificationRepository;
//    private final UserRepository userRepository;
//    private final SocketIOServer socketIOServer;  
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final Set<String> sentNotificationKeys = ConcurrentHashMap.newKeySet();
//
//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
//    private final ConcurrentHashMap<Long, ScheduledFuture<?>> userSchedulers = new ConcurrentHashMap<>();
//
//    @Autowired
//    public NotificationReminder(ImportLeadRepository leadRepository,
//                                NotificationsRepository notificationRepository,
//                                UserRepository userRepository,
//                                SocketIOServer socketIOServer) {
//        this.leadRepository = leadRepository;
//        this.notificationRepository = notificationRepository;
//        this.userRepository = userRepository;
//        this.socketIOServer = socketIOServer;
//    }
//
//    public void startReminderForUser(Long userId) {
//        if (userSchedulers.containsKey(userId)) {
//            System.out.println("🔁 Reminder already running for user ID: " + userId);
//            return;
//        }
//
//        System.out.println("▶️ Starting reminder for user ID: " + userId);
//        Runnable task = () -> checkUserLeads(userId);
//        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
//        userSchedulers.put(userId, future);
//    }
//
//    public void stopReminderForUser(Long userId) {
//        ScheduledFuture<?> future = userSchedulers.remove(userId);
//        if (future != null) {
//            future.cancel(true);
//            System.out.println("⏹️ Stopped reminder for user ID: " + userId);
//        }
//    }
//
//    private void checkUserLeads(Long userId) {
//        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
//        List<ImportLead> leads = leadRepository.findByAssignedTo(userId);
//
//        for (ImportLead lead : leads) {
//            processLead(lead.getId(), userId, lead.getJsonData(), now);
//        }
//    }
//
//    private void processLead(long leadId, long assignedTo, String jsonData, LocalDateTime now) {
//        if (jsonData == null) return;
//        try {
//            JsonNode logs = objectMapper.readTree(jsonData);
//            for (JsonNode logEntry : logs) {
//                if (!logEntry.has("dueDate")) continue;
//
//                long dueDateMillis = logEntry.get("dueDate").asLong();
//                LocalDateTime dueDateTime = Instant.ofEpochMilli(dueDateMillis)
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDateTime();
//
//                if (!now.toLocalDate().equals(dueDateTime.toLocalDate())) continue;
//
//                LocalTime dueTime = dueDateTime.toLocalTime().withSecond(0).withNano(0);
//                LocalTime nowTime = now.toLocalTime();
//
//                checkAndSendNotification(assignedTo, leadId, logEntry, dueDateMillis, nowTime, dueTime);
//            }
//        } catch (Exception e) {
//            System.err.println("❌ Error processing lead ID " + leadId + ": " + e.getMessage());
//        }
//    }
//
//    private void checkAndSendNotification(long userId, long leadId, JsonNode logEntry, long dueDateMillis,
//                                          LocalTime now, LocalTime dueTime) {
//        String comment = logEntry.has("comment") ? logEntry.get("comment").asText() : "No comment";
//        LocalTime thirtyMinBefore = dueTime.minusMinutes(30);
//
//        if (now.equals(thirtyMinBefore)) {
//            String key = leadId + "_" + dueDateMillis + "_30min";
//            if (sentNotificationKeys.add(key)) {
//                sendNotification(userId, comment, leadId, "⏳ 30 minutes before due time");
//            }
//        }
//
//        if (now.equals(dueTime)) {
//            String key = leadId + "_" + dueDateMillis + "_exact";
//            if (sentNotificationKeys.add(key)) {
//                sendNotification(userId, comment, leadId, "⏰ Exact due time");
//            }
//        }
//    }
//
//    private void sendNotification(long userId, String comment, long leadId, String timing) {
//        try {
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            String message = "Reminder (" + timing + "): " + comment + " is due!";
//            Notifications notification = new Notifications(false, message, user.getEmail(), "check",
//                    System.currentTimeMillis());
//            notificationRepository.save(notification);
//
//            for (var client : socketIOServer.getAllClients()) {
//                Long connectedUserId = client.get("userId");
//                if (connectedUserId != null && connectedUserId.equals(userId)) {
//                    client.sendEvent("notification", message);
//                }
//            }
//
//            System.out.println("✅ Notification sent to User " + user.getUserId() + " (" + user.getEmail()
//                    + ") for Lead ID: " + leadId);
//        } catch (Exception e) {
//            System.err.println("❌ Notification failed for Lead ID: " + leadId + ", User ID: " + userId + ": " + e.getMessage());
//        }
//    }
//}
