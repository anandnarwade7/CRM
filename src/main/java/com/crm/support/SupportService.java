package com.crm.support;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.crm.Exception.Error;
import com.crm.notifications.NotificationsRepository;
import com.crm.user.Status;
import com.crm.user.User;
import com.crm.user.UserServiceException;
import com.crm.user.UserRepository;

@Service
public class SupportService {

	@Autowired
	private SupportRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	public ResponseEntity<?> generateSupportTicket(String query, String description,
			long userId) {
		try {
			Optional<User> userObject = userRepository.findById(userId);
			User user = userObject.orElseThrow(() -> new UserServiceException(409, "User not found"));
			if (!userObject.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
			}
			Support support = new Support();
			support.setQuery(query);
			support.setStatus(Status.PENDING);
			support.setUser(user);
			Support savedSupport = repository.save(support);

//			sendNotification("Ticket is raised by " + user.getName(), "checkSupport" + user.getRole());

			return ResponseEntity.ok(savedSupport);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find user", System.currentTimeMillis()));
		} catch (Exception ex) {
			System.out.println("in second catch block");
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
