package com.crm.Notifications;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.crm.Exception.Error;

@RestController
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174") })
@RequestMapping("/api/notifications")
public class NotificationController {

	@Autowired
	private NotificationsRepository repository;

//	@Autowired
//	private SimpMessagingTemplate messagingTemplate;

	@CrossOrigin(origins = { "http://localhost:3000" })
	@GetMapping("/getAll")
	public ResponseEntity<?> getAllNotifications() {
		try {
			List<Notifications> notificationList = repository.findAll();

			if (notificationList.isEmpty()) {
				throw new NotificationException(404, "No notifications found.");
			}

			for (Notifications notification : notificationList) {
				notification.setIsSeen(true);
				repository.save(notification);
			}

			return ResponseEntity.ok(notificationList);
		} catch (NotificationException ex) {
			return ResponseEntity.status(ex.getStatusCode()).body(new Error(ex.getStatusCode(), ex.getMessage(),
					"No notifications available.", System.currentTimeMillis()));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage(),
							System.currentTimeMillis()));
		}
	}

	@GetMapping("/unseen/{email}")
	public ResponseEntity<?> getUnseenCountByUserId(@PathVariable String email) {
		try {
			List<Notifications> notificationList = repository.findByEmail(email);
			long unseenCount = 0;
			if (notificationList.isEmpty()) {
				return ResponseEntity.ok(unseenCount);
			}
			unseenCount = notificationList.stream().filter(notification -> !notification.getIsSeen()).count();

			return ResponseEntity.ok(unseenCount);
		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode())
					.body(new Error(e.getStatusCode(), e.getMessage(),
							"User not found or no notifications available for the provided email.",
							System.currentTimeMillis()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
							"Failed to fetch unseen count: " + e.getMessage(), System.currentTimeMillis()));
		}
	}

//	@CrossOrigin(origins = { "http://localhost:3000" })
//	@MessageMapping("/getNotificationsByEmail")
//	@SendTo("/topic/notifications/")
//	public void getNotification(@Payload String email) {
//		try {
//			List<Notifications> notificationList = repository.findByEmail(email);
//
//			List<Ads> findAll = adsRepository.findAll();
//			String username = null;
//			String redirectKey = "checkNotification";
//
//			for (Ads adsInfo : findAll) {
//				LocalDate currentDate = LocalDate.now();
//				long endDateMillis = adsInfo.getAdSet().getEndDate();
//
//				Instant instant = Instant.ofEpochMilli(endDateMillis);
//				LocalDate endDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
//				if (endDate.equals(currentDate)) {
//					username = adsInfo.getAdSet().getCampaign().getUser().getFirstName();
//					Notifications nn = new Notifications(false, username + " your ads show time will expire today",
//							email, redirectKey, System.currentTimeMillis());
//					repository.save(nn);
//				}
//			}
//
//			for (Notifications notifications : notificationList) {
//				notifications.setIsSeen(true);
//				repository.save(notifications);
//			}
//
//			// Send notification list to the specific user via WebSocket
//			messagingTemplate.convertAndSend("/topic/notifications/" + email, notificationList);
//
//		} catch (Exception ex) {
//			messagingTemplate.convertAndSend("/topic/notifications/" + email,
//					"An error occurred while processing notifications.");
//		}
//	}
//
//	@CrossOrigin(origins = { "http://localhost:3000" })
//	@MessageMapping("/getNotificationCountByEmail")
//	@SendTo("/topic/notifications/")
//	public void getUnseenCountByUserId(@Payload String email) {   
//		try {
//			List<Notifications> notificationList = repository.findByEmail(email);
//			long unseenCount = notificationList.stream().filter(notification -> !notification.getIsSeen()).count();
//			// Send the unseen count to the specific user via WebSocket
//			messagingTemplate.convertAndSend("/topic/notifications/" + email, unseenCount);
//		} catch (Exception e) {
//			messagingTemplate.convertAndSend("/topic/notifications/" + email,
//					"Failed to fetch unseen count: " + e.getMessage());
//		}
//	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/{email}")
	public ResponseEntity<?> getNotificationByRole(@PathVariable String email) {
		try {
			List<Notifications> notificationList = repository.findByEmail(email);
			if (notificationList.isEmpty()) {
				ResponseEntity.ok(notificationList);
			}
			notificationList.sort(Comparator.comparing(Notifications::getCreatedOn).reversed());
			for (Notifications notifications : notificationList) {
				notifications.setIsSeen(true);
				repository.save(notifications);
			}
			return ResponseEntity.ok(notificationList);
		} catch (NotificationException ex) {
			return ResponseEntity.status(ex.getStatusCode()).body(new Error(ex.getStatusCode(), ex.getMessage(),
					"Don't have any notifications ", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	to clear all notification (admin)
	@CrossOrigin(origins = { ("http://localhost:3000") })
	@DeleteMapping("delete")
	public void deleteAllNotification() {
		repository.deleteAll();
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@DeleteMapping("delete/{email}")
	public ResponseEntity<?> deleteAllNotification(@PathVariable String email) {
		try {
			List<Notifications> allByEmail = repository.findAllByEmail(email);
			for (Notifications notifications : allByEmail) {
				repository.deleteById(notifications.getId());
			}
			return ResponseEntity.ok("Notifications for " + email + " are deleted successfully !!!");
		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Don't have any notification to delete", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getNotification/{id}")
	public ResponseEntity<?> getNotificationById(@PathVariable long id) {
		try {
			Optional<Notifications> findById = repository.findById(id);
			if (findById.isPresent()) {

				return ResponseEntity.ok(findById);

			} else {
				return ResponseEntity.ok(0);
			}
		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Don't have any notification", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getNotification/{email}/{id}")
	public ResponseEntity<?> getNotificationById(@PathVariable String email, @PathVariable long id) {
		try {
			Optional<Notifications> findById = repository.findByEmailAndId(email, id);
			if (findById.isPresent()) {
				return ResponseEntity.ok(findById);

			} else {
				return ResponseEntity.ok(0);
			}
		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Don't have any notification", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@DeleteMapping("/deleteNotificationById/{id}")
	public ResponseEntity<?> clearNotification(@PathVariable long id) {
		try {
			if (repository.existsById(id)) {
				repository.deleteById(id);
				return ResponseEntity.ok(id);
			} else {
				return ResponseEntity.ok(0);
			}

		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Don't have any notification to delete", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@DeleteMapping("/{email}/{id}")
	public ResponseEntity<?> clearNotification(@PathVariable String email, @PathVariable long id) {
		try {

			Optional<Notifications> notificationOptional = repository.existsByEmailAndId(email, id);
			if (notificationOptional.isPresent()) {
				repository.deleteById(id);
				return ResponseEntity.ok("Notificaion with id:: " + id + " deleted successfully...!!");
			} else {
				return ResponseEntity.ok("Notification with id:: " + 0 + "does not found..!!");
			}

		} catch (NotificationException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Don't have any notification to delete", System.currentTimeMillis()));
		} catch (Exception ex) {
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
