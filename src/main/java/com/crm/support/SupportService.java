package com.crm.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.crm.Exception.Error;
import com.crm.notifications.Notifications;
import com.crm.notifications.NotificationsRepository;
import com.crm.security.JwtUtil;
import com.crm.user.Admins;
import com.crm.user.AdminsRepository;
import com.crm.user.Status;
import com.crm.user.User;
import com.crm.user.UserRepository;
import com.crm.user.UserServiceException;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class SupportService {

	@Autowired
	private SupportRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AdminsRepository adminRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	public ResponseEntity<?> generateSupportTicket(String token, Support support) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			String email = userClaims.get("email");

			System.out.println("User Role: " + role + ", Email: " + email);

//			if (!"SUPER ADMIN".equalsIgnoreCase(role)) {
//				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
//						.body("Forbidden: You do not have the necessary permissions.");
//			}

			System.out.println("Check Point 2");
			Support savedSupport;
			System.out.println("Check Point 3");

			if ("SALES".equalsIgnoreCase(role) || "CRM".equalsIgnoreCase(role)) {
				System.out.println("Check Point 4");
				Optional<User> userObject = userRepository.findById(support.getUserId());
				User user = userObject.orElseThrow(() -> new UserServiceException(409, "User not found"));
				System.out.println("Check Point 5");
				if (!userObject.isPresent()) {
					System.out.println("Check Point 6");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
				}
				System.out.println("Check Point 7");
				savedSupport = new Support();
				savedSupport.setName(support.getName());
				savedSupport.setEmail(support.getEmail());
				savedSupport.setPhone(support.getPhone());
				savedSupport.setRole(role);
				savedSupport.setDepartment(support.getDepartment());
				savedSupport.setUserId(support.getUserId());
				savedSupport.setQuery(support.getQuery());
				savedSupport.setStatus(Status.PENDING);
				savedSupport.setAdminId(user.getUserId());
				repository.save(savedSupport);
				System.out.println("Check Point 8");

				sendNotification(user.getUserId(),
						"Ticket is raised by " + user.getName() + " (" + user.getRole() + ").");
			} else if ("ADMIN".equalsIgnoreCase(role)) {
				System.out.println("Check Point 9");
				Admins user = adminRepository.findById(support.getUserId())
						.orElseThrow(() -> new UserServiceException(409, "User not found"));
				System.out.println("Check Point 10");
				savedSupport = new Support();
				savedSupport.setName(support.getName());
				savedSupport.setEmail(support.getEmail());
				savedSupport.setPhone(support.getPhone());
				savedSupport.setRole(role);
				savedSupport.setDepartment(support.getDepartment());
				savedSupport.setUserId(support.getUserId());
				savedSupport.setQuery(support.getQuery());
				savedSupport.setStatus(Status.PENDING);
				savedSupport.setAdminId(user.getUserId());
				repository.save(savedSupport);

				sendNotification(user.getUserId(),
						"Ticket is raised by " + user.getName() + " (" + user.getRole() + ").");
			} else {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			return ResponseEntity.ok(savedSupport);
		} catch (UserServiceException e) {
			System.out.println("Check Point 11");
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to create support ticket", System.currentTimeMillis()));
		} catch (Exception ex) {
			System.out.println("in second catch block");
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void sendNotification(long salesUser, String message) {
		try {
			Admins user = adminRepository.findById(salesUser)
					.orElseThrow(() -> new UserServiceException(409, "User not found"));
			Notifications notification = new Notifications(false, message, user.getEmail(), "checkSupport",
					System.currentTimeMillis());
			notificationsRepository.save(notification);

		} catch (Exception e) {
			throw new RuntimeException("Error saving dynamic fields", e);
		}
	}

	public ResponseEntity<?> getSupportTicketsList(String token, int page) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			String email = userClaims.get("email");

			System.out.println("User Role: " + role + ", Email: " + email);
			Pageable pageable = PageRequest.of(page - 1, 10);
			if ("SUPER ADMIN".equalsIgnoreCase(role)) {
				Admins superAdmin = adminRepository.findByEmail(email);
				Page<Support> byAdminId = repository.findByAdminIdOrderByCreatedOnDesc(superAdmin.getId(), pageable);
				Map<String, Page<Support>> result = new HashMap<>();
				result.put("other", byAdminId);
				return ResponseEntity.ok(byAdminId);
			} else if ("SALES".equalsIgnoreCase(role) || "CRM".equalsIgnoreCase(role)) {
				User user = userRepository.findByEmail(email);
				Page<Support> byUserIdAndRoleAndByEmail = repository
						.findByUserIdAndRoleAndEmailOrderByCreatedOnDesc(user.getId(), role, email, pageable);
				Map<String, Page<Support>> result = new HashMap<>();
				result.put("other", byUserIdAndRoleAndByEmail);
				return ResponseEntity.ok(byUserIdAndRoleAndByEmail);
			} else if ("ADMIN".equalsIgnoreCase(role)) {
				Admins superAdmin = adminRepository.findByEmail(email);
				Page<Support> byAdminId = repository.findByAdminIdOrderByCreatedOnDesc(superAdmin.getId(), pageable);
				Page<Support> byUserIdAndRoleAndByEmail = repository
						.findByUserIdAndRoleAndEmailOrderByCreatedOnDesc(superAdmin.getId(), role, email, pageable);
				Map<String, Page<Support>> result = new HashMap<>();
				result.put("self", byUserIdAndRoleAndByEmail);
				result.put("other", byAdminId);

				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find list", System.currentTimeMillis()));
		} catch (Exception ex) {
			System.out.println("in second catch block");
			return new ResponseEntity<>("An error occurred while processing the request.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> solveOrDeleteTicket1(String token, long id, String response, String note) {
		try {
			System.out.println("Token " + token);
			System.out.println("Support Id " + id);
			System.out.println("response " + response);
			System.out.println("Note " + note);

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String email = userClaims.get("email");
			Optional<Support> supportById = repository.findById(id);
			Support support = supportById.orElseThrow(() -> new UserServiceException(404, "Support ticket not found"));

			User user = userRepository.findByEmail(email);
			if (user == null) {
				throw new UserServiceException(409, "User not found with email: " + email);
			}

			Optional<User> bySupport = userRepository.findById(support.getUserId());
			User supportUser = bySupport.orElseThrow(() -> new UserServiceException(409, "User not found"));

			String notificationMessage;
			Notifications notification = null;
			String redirectKey = "checkSupport";

			if (response.equals("solve") || response.equals("reject")) {
				if (!user.getRole().equals("SALES") && !user.getRole().equals("CRM")) {
					throw new UserServiceException(403, "User does not have permission to solve or reject tickets");
				}
				if (response.equals("solve")) {
					System.out.println("if response is solve");
					if (user.getRole().equals("ADMIN") && user.getId() == support.getAdminId()) {
						support.markApproved();
					} else {
						support.markApproved();
					}
					notificationMessage = "Your Support ticket " + id + " is solved by " + user.getName() + " ("
							+ user.getRole() + ")";
					System.out.println("if solve then to advertiser");
					notification = new Notifications(false, notificationMessage, supportUser.getEmail(), redirectKey,
							System.currentTimeMillis());

				} else if (response.equals("reject")) {
					System.out.println("if response is reject");
					if (user.getRole().equals("ADMIN") && user.getId() == support.getAdminId()) {
						support.markRejected();
					} else {
						support.markRejected();
					}
					notificationMessage = "Your Support ticket " + id + " is rejected by " + user.getName() + " ("
							+ user.getRole() + ")";
					System.out.println("if reject then to advertiser");
					notification = new Notifications(false, notificationMessage, supportUser.getEmail(), redirectKey,
							System.currentTimeMillis());
				}
			} else if (response.equals("delete")) {
				if (!user.getRole().equals("CRM") && !user.getRole().equals("SALES")
						&& !!user.getRole().equals("ADMIN")) {
					throw new UserServiceException(403, "User does not have permission to delete tickets");
				}
				if (supportUser.getId() != user.getId()) {
					throw new UserServiceException(403, "User does not have permission to delete this ticket");
				}
				System.out.println("if response is delete");
				support.markDeleted();

				notificationMessage = "You have deleted token " + id;
				notification = new Notifications(false, notificationMessage, Long.toString(user.getId()), redirectKey,
						System.currentTimeMillis());
			} else {
				System.out.println("Invalid response");
				throw new UserServiceException(400, "Invalid response");
			}

			repository.save(support);
			notificationsRepository.save(notification);
			return ResponseEntity.ok(support);
		} catch (UserServiceException e) {
			System.out.println("In first catch block");
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to process the request", System.currentTimeMillis()));
		}
	}

	public ResponseEntity<?> solveOrDeleteTicket(String token, long id, String response, String note) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String email = userClaims.get("email");
			String role = userClaims.get("role");

			Support support = repository.findById(id)
					.orElseThrow(() -> new UserServiceException(404, "Support ticket not found"));

			Object userObj = ("SUPER ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role))
					? adminRepository.findByEmail(email)
					: userRepository.findByEmail(email);

			if (userObj == null) {
				throw new UserServiceException(409, "User not found with email: " + email);
			}

			String userName;
			Long userId;
			String userRole;

			if (userObj instanceof Admins) {
				Admins admin = (Admins) userObj;
				userName = admin.getName();
				userId = admin.getId();
				userRole = "ADMIN";
			} else {
				User user = (User) userObj;
				userName = user.getName();
				userId = user.getId();
				userRole = user.getRole();
			}

			User supportUser = userRepository.findById(support.getUserId())
					.orElseThrow(() -> new UserServiceException(409, "Support user not found"));

			String redirectKey = "checkSupport";
			String notificationMessage = null;
			Notifications notification = null;

			if ("solve".equalsIgnoreCase(response)) {
				if (!List.of("SALES", "CRM", "ADMIN").contains(userRole)) {
					throw new UserServiceException(403, "User does not have permission to solve tickets");
				}

				support.markApproved();

				notificationMessage = "Your Support ticket " + id + " is solved by " + userName + " (" + userRole + ")";
				notification = new Notifications(false, notificationMessage, supportUser.getEmail(), redirectKey,
						System.currentTimeMillis());
			}

			else if ("reject".equalsIgnoreCase(response)) {
				if (!List.of("SALES", "CRM", "ADMIN").contains(userRole)) {
					throw new UserServiceException(403, "User does not have permission to reject tickets");
				}

				support.markRejected();

				notificationMessage = "Your Support ticket " + id + " is rejected by " + userName + " (" + userRole
						+ ")";
				notification = new Notifications(false, notificationMessage, supportUser.getEmail(), redirectKey,
						System.currentTimeMillis());
			}

			else if ("delete".equalsIgnoreCase(response)) {
				if (!List.of("CRM", "SALES", "ADMIN").contains(userRole)) {
					throw new UserServiceException(403, "User does not have permission to delete tickets");
				}

				if (!Objects.equals(userId, supportUser.getId())) {
					throw new UserServiceException(403, "User can only delete their own support tickets");
				}

				support.markDeleted();

				notificationMessage = "You have deleted support ticket " + id;
				notification = new Notifications(false, notificationMessage, String.valueOf(userId), redirectKey,
						System.currentTimeMillis());
			}

			else {
				throw new UserServiceException(400, "Invalid response action: " + response);
			}

			repository.save(support);
			notificationsRepository.save(notification);

			return ResponseEntity.ok(support);

		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to process the request", System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal server error: " + e.getMessage());
		}
	}
}