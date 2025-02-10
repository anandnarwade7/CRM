package com.crm.user;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import com.crm.Exception.Error;
import com.crm.security.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private JwtUtil jwtUtil;

	public String getUserObject(User user) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode responseJson = objectMapper.createObjectNode();
			responseJson.put("id", user.getId());
			responseJson.put("name", user.getName());
//			responseJson.put("lastName", user.getLastName());
			responseJson.put("email", user.getEmail());
			responseJson.put("mobile", user.getMobile());
			responseJson.put("role", user.getRole());
			responseJson.put("profilePic", user.getProfilePic());
			responseJson.put("action", user.getAction().toString());
			responseJson.put("createdOn", user.getCreatedOn());
			return responseJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getUserObject1(User user, String token) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode responseJson = objectMapper.createObjectNode();
			responseJson.put("id", user.getId());
			responseJson.put("name", user.getName());
//			responseJson.put("lastName", user.getLastName());
			responseJson.put("email", user.getEmail());
			responseJson.put("mobile", user.getMobile());
			responseJson.put("role", user.getRole());
			responseJson.put("profilePic", user.getProfilePic());
			responseJson.put("action", user.getAction().toString());
			responseJson.put("createdOn", user.getCreatedOn());
			responseJson.put("token", token);
			return responseJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isValidEmail(String email) {
		// Updated regex to support multi-part TLDs and valid email structures
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public ResponseEntity<?> registerUser(String userJson) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(userJson);
			String email = jsonNode.get("email").asText();
			if (repository.existsByEmail(email)) {
				System.out.println("Check Point 1 ");
				throw new UserServiceException(409, "Email already exist");
			}
			String password = jsonNode.get("password").asText();
			String name = jsonNode.get("name").asText();
//			String lastName = jsonNode.get("lastName").asText();
//			String role = jsonNode.get("role").asText();
			String mobile = jsonNode.get("mobile").asText();

			User user = new User();
			user.setName(name);
//			user.setLastName(lastName);
			user.setEmail(email);
			user.setPassword(password);
			user.setMobile(mobile);
			user.setAction(Status.UNBLOCK);
			user.setRole("ADMIN");
			User save = repository.save(user);
			String userObject = getUserObject1(save, "");
			return ResponseEntity.ok(userObject);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to register user", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Invalid Credentials ");
		}
	}

	public ResponseEntity<?> addUser(String token, long id, String userJson) {
		try {

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(userJson);
			String email = jsonNode.get("email").asText();
			if (repository.existsByEmail(email)) {
				System.out.println("Check Point 1 ");
				throw new UserServiceException(409, "Email already exist");
			}
			String password = jsonNode.get("password").asText();
			String name = jsonNode.get("name").asText();
//			String lastName = jsonNode.get("lastName").asText();
			String userRole = jsonNode.get("role").asText();
			String mobile = jsonNode.get("mobile").asText();

			User user = new User();
			user.setName(name);
//			user.setLastName(lastName);
			user.setEmail(email);
			user.setPassword(password);
			user.setMobile(mobile);
			user.setAction(Status.UNBLOCK);
			user.setRole(userRole);
			User save = repository.save(user);
			String userObject = getUserObject(save);
			return ResponseEntity.ok(userObject);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to register user", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Invalid Credentials ");
		}
	}

	public ResponseEntity<?> authenticateUser(String user, HttpServletResponse response) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(user);
			String email = jsonNode.get("email").asText();
			String userPassword = jsonNode.get("password").asText();

			// Email validation
			if (!isValidEmail(email)) {

				throw new UserServiceException(400, "Invalid email format");
			}

			User byEmail = repository.findByEmail(email);

			System.out.println("User found :: " + byEmail);
			if (byEmail == null) {

				throw new UserServiceException(409, "User profile not found");

			}

			System.out.println("commig pass :: " + userPassword);
			String dbPassword = byEmail.getPassword();
			System.out.println("dbPassword :: " + dbPassword);

			if (dbPassword.equals(userPassword)) {

				String token = jwtUtil.createToken(byEmail.getEmail(), byEmail.getRole());
				System.out.println("Token Created Successfully :: " + token);

				Cookie cookie = new Cookie("token", token);
				cookie.setHttpOnly(true); // Helps mitigate XSS attacks
				cookie.setSecure(false); // Ensures cookies are sent over HTTPS only (set to false in development
											// environments without HTTPS)
				cookie.setMaxAge(60 * 30); // Set the expiry time (30 minutes)
				cookie.setPath("/"); // Set the path to make the cookie available across the entire domain
				response.addCookie(cookie);
				String userObject = getUserObject1(byEmail, token);
				return ResponseEntity.ok(userObject);

			} else {
				throw new UserServiceException(409, "Invalid email and password");
			}
		} catch (UserServiceException e) {

			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to login user", System.currentTimeMillis()));

		} catch (Exception ex) {
			throw new UserServiceException(409, "Invalid Credentials");
		}
	}

	public ResponseEntity<?> getAdmin(String token, long id) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Optional<User> user = repository.findById(id);
			if (user.isPresent()) {
				String userObject = getUserObject(user.get());
				return ResponseEntity.ok(userObject);
			} else {
				throw new UserServiceException(401, "User not exists");
			}
		} catch (ExpiredJwtException e) {
			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
					.body("Unauthorized: Your session has expired.");
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("User not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> getCRM(String token, long id) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"CRM".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Optional<User> user = repository.findById(id);
			if (user.isPresent()) {
				String userObject = getUserObject(user.get());
				return ResponseEntity.ok(userObject);
			} else {
				throw new UserServiceException(401, "User not exists");
			}
		} catch (ExpiredJwtException e) {
			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
					.body("Unauthorized: Your session has expired.");
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("User not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> getSalesById(String token, long id) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"SALES".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Optional<User> user = repository.findById(id);
			if (user.isPresent()) {
				String userObject = getUserObject(user.get());
				return ResponseEntity.ok(userObject);
			} else {
				throw new UserServiceException(401, "User not exists");
			}
		} catch (ExpiredJwtException e) {
			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
					.body("Unauthorized: Your session has expired.");
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("User not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> updateUserDetails(String token, long userId, User user) {
		try {
			System.out.println("Check 1");

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			Optional<User> byId = repository.findById(userId);
			if (byId == null) {
				throw new UserServiceException(409, "User does not exist");
			}
			User dbUser = byId.get();

			if (!dbUser.getRole().equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			if (user.getName() != null && !user.getName().isEmpty()) {
				dbUser.setName(user.getName());
			}
			if (user.getMobile() != null && !user.getMobile().isEmpty()) {
				dbUser.setMobile(user.getMobile());
			}
			if (user.getEmail() != null && !user.getEmail().isEmpty()) {
				dbUser.setEmail(user.getEmail());
			}
			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				dbUser.setPassword(user.getPassword());
			}
			User existingUser = repository.save(dbUser);
			String userObject = getUserObject(existingUser);
			return ResponseEntity.ok(userObject);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to Register User", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, " Invalid Credentials ");
		}
	}

	public ResponseEntity<?> updateUserAsBlockUnBlock(String token, long userId, String response, String note) {
		try {
			System.out.println("Check 1");

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			Optional<User> byId = repository.findById(userId);
			if (byId == null) {
				throw new UserServiceException(409, "User does not exist");
			}
			User dbUser = byId.get();

			if (!"ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			if ("unblock".equalsIgnoreCase(response)) {
				dbUser.setAction(Status.UNBLOCK);
			} else if ("block".equalsIgnoreCase(response)) {
				dbUser.setAction(Status.BLOCK);
			}

			User existingUser = repository.save(dbUser);
			String userObject = getUserObject(existingUser);
			return ResponseEntity.ok(userObject);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to Register User", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, " Invalid Credentials ");
		}
	}

	public ResponseEntity<?> deleteUser(String authorization, long adminId, long userId) {
		try {
			if (jwtUtil.isTokenExpired(authorization)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(authorization);

			if ("ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Optional<User> byId = repository.findById(userId);
			User user = byId.get();
			repository.deleteById(userId);

			return ResponseEntity.ok(user.getRole() + " Deleted Successfully !!!! ");
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to Register User", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, " Invalid Credentials ");
		}
	}

	public ResponseEntity<?> getUsersListByRole(@CookieValue(value = "accessToken", required = false) String token,
			int page, String role) {
		try {
			if (token == null) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: No token provided.");
			}

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String adminRole = jwtUtil.extractRole(token);
			if (!"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			role = role.trim();
			Pageable pageable = PageRequest.of(page - 1, 10);
			Page<User> usersPage = repository.findByRoleOrderByCreatedOnDesc(role, pageable);

			if (usersPage.isEmpty()) {
				return ResponseEntity.ok("No users found for the role: " + role);
			}

			List<UserDTO> userDTOs = usersPage.getContent().stream().map(UserDTO::new).collect(Collectors.toList());

			return ResponseEntity.ok(userDTOs);

		} catch (ExpiredJwtException e) {
			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
					.body("Unauthorized: Your session has expired.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}

	}

	public ResponseEntity<?> getTotalCountForAdmin(String token, String role) {
		try {
			if (token == null) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: No token provided.");
			}

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String adminRole = jwtUtil.extractRole(token);
			if (!"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			List<User> allByUserId = repository.findByRole(role);
			int size = allByUserId.size();
			int pages = (int) Math.ceil((double) size / 10);
			if (pages == 0) {
				pages = 1;
			}
			return ResponseEntity.ok(pages);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user details");
		}
	}
}
