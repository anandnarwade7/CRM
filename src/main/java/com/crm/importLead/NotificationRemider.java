package com.crm.importLead;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import com.crm.leads.LeadDetails;
import com.crm.leads.LeadRepository;
import com.crm.notifications.Notifications;
import com.crm.notifications.NotificationsRepository;
import com.crm.user.User;
import com.crm.user.UserRepository;
import com.crm.user.UserServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationRemider {

	private final ImportLeadRepository leadRepository;
	private final LeadRepository clientRepository;
	private final UserRepository userRepository;
	private final NotificationsRepository notificationRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public NotificationRemider(ImportLeadRepository leadRepository, NotificationsRepository notificationRepository,
			LeadRepository clientRepository, UserRepository userRepository) {
		this.leadRepository = leadRepository;
		this.notificationRepository = notificationRepository;
		this.clientRepository = clientRepository;
		this.userRepository = userRepository;
	}

//	@Scheduled(fixedRate = 60000) 
	@Scheduled(fixedRate = 28800000) // Runs every 60 seconds (adjust as needed)
	@Async // Runs in a separate thread to prevent performance issues
	public void checkDueDatesAndNotify() {
		List<ImportLead> leads = leadRepository.findAll();
		String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

		for (ImportLead lead : leads) {
			try {
				if (lead.getJsonData() != null) {
					JsonNode logs = objectMapper.readTree(lead.getJsonData());
					for (JsonNode logEntry : logs) {
						if (logEntry.has("dueDate")) {
							long dueDateMillis = logEntry.get("dueDate").asLong();
							String formattedDueDate = new SimpleDateFormat("dd/MM/yyyy HH:mm")
									.format(new Date(dueDateMillis));

							if (formattedDueDate.equals(currentTime)) {
								sendNotification(lead.getAssignedTo(), logEntry, lead.getId());
							}
						}
					}
				}
			} catch (Exception e) {
				System.err.println("Error processing lead ID " + lead.getId() + ": " + e.getMessage());
			}
		}
	}

//	@Scheduled(fixedRate = 60000) 
	@Scheduled(fixedRate = 28800000) // Runs every 60 seconds (adjust as needed)
	@Async // Runs in a separate thread to prevent performance issues
	public void checkClientsDueDatesAndNotify() {
		List<LeadDetails> leads = clientRepository.findAll();
		String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

		for (LeadDetails lead : leads) {
			try {
				if (lead.getMassagesJsonData() != null) {
					JsonNode logs = objectMapper.readTree(lead.getMassagesJsonData());
					for (JsonNode logEntry : logs) {
						if (logEntry.has("dueDate")) {
							long dueDateMillis = logEntry.get("dueDate").asLong();
							String formattedDueDate = new SimpleDateFormat("dd/MM/yyyy HH:mm")
									.format(new Date(dueDateMillis));

							if (formattedDueDate.equals(currentTime)) {
								sendNotification(lead.getAssignedTo(), logEntry, lead.getId());
							}
						}
					}
				}
			} catch (Exception e) {
				System.err.println("Error processing lead ID " + lead.getId() + ": " + e.getMessage());
			}
		}
	}

	private void sendNotification(long userId, JsonNode logEntry, long leadId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserServiceException(409, "User not found, unable to send notifications"));
		String message = "Reminder: " + logEntry.get("comment").asText() + " is due!";
		Notifications notifications = new Notifications(false, message, user.getEmail(), "check",
				System.currentTimeMillis());
		notificationRepository.save(notifications);
		System.out.println("Notification sent to User " + user.getUserId() + " email " + user.getEmail()
				+ " for Lead ID: " + leadId);
	}

}
