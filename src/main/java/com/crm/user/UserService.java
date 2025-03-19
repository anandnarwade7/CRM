package com.crm.user;

import java.util.List;
import java.util.Map;
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
import com.crm.importLead.ImportLeadRepository;
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
	private AdminsRepository adminRepository;

	@Autowired
	private ImportLeadRepository leadRepository;

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

	public String getUserObject(Admins user) {
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
			responseJson.put("startDate", user.getStartDate());
			responseJson.put("endDate", user.getEndDate());
			responseJson.put("createdOn", user.getCreatedOn());
			responseJson.put("propertyName", user.getPropertyName());
			return responseJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getUserObject1(String role, String email, String token) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode responseJson = objectMapper.createObjectNode();

			if ("SUPER ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
				Admins admin = adminRepository.findByEmail(email);
				responseJson.put("id", admin.getId());
				responseJson.put("name", admin.getName());
//				responseJson.put("lastName", admin.getLastName());
				responseJson.put("email", admin.getEmail());
				responseJson.put("mobile", admin.getMobile());
				responseJson.put("role", admin.getRole());
				responseJson.put("profilePic", admin.getProfilePic());
				responseJson.put("action", admin.getAction().toString());
				responseJson.put("propertyName", admin.getPropertyName());
				responseJson.put("createdOn", admin.getCreatedOn());
				responseJson.put("startDate", admin.getStartDate());
				responseJson.put("endDate", admin.getEndDate());

				responseJson.put("token", token);
			} else {
				User user = repository.findByEmail(email);
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
			}
			return responseJson.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getAdminObject1(Admins user, String token) {
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
			responseJson.put("propertyName", user.getPropertyName());
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

	public ResponseEntity<?> registerSuperAdmin(String userJson) {
		try {
			System.out.println("In super admin register");
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(userJson);
			String email = jsonNode.get("email").asText();
			if (adminRepository.existsByEmail(email)) {
				System.out.println("Check Point 1 ");
				throw new UserServiceException(409, "Email already exist");
			}
			String password = jsonNode.get("password").asText();
			String name = jsonNode.get("name").asText();
//			String lastName = jsonNode.get("lastName").asText();
//			String role = jsonNode.get("role").asText();
			String mobile = jsonNode.get("mobile").asText();

			Admins user = new Admins();
			user.setName(name);
//			user.setLastName(lastName);
			user.setEmail(email);
			user.setPassword(password);
			user.setMobile(mobile);
			user.setAction(Status.UNBLOCK);
			user.setRole("SUPER ADMIN");
			Admins save = adminRepository.save(user);
			String userObject = getAdminObject1(save, "");
			return ResponseEntity.ok(userObject);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to register user", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Invalid Credentials ");
		}
	}

	public ResponseEntity<?> addAdmin(String token, long id, String userJson) {
		try {
			System.out.println("In add admin service");

			if (jwtUtil.isTokenExpired(token)) {
				System.err.println("checking token" + jwtUtil.isTokenExpired(token));
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"SUPER ADMIN".equalsIgnoreCase(role)) {
				System.err.println("checking token role " + !"SUPER ADMIN".equalsIgnoreCase(role));
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			System.out.println("Check point 1");

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(userJson);
			String email = jsonNode.get("email").asText();
			System.out.println("Check point 2 email " + email);

			if (adminRepository.existsByEmail(email)) {
				System.err.println("Check Point 1 ");
				throw new UserServiceException(409, "Email already exist");
			}
			String password = jsonNode.get("password").asText();
			String name = jsonNode.get("name").asText();
//			String lastName = jsonNode.get("lastName").asText();
//			String userRole = jsonNode.get("role").asText();
			String mobile = jsonNode.get("mobile").asText();
			String propertyName = jsonNode.get("propertyName").asText();
			long startDate = jsonNode.get("startDate").asLong();
			long endDate = jsonNode.get("endDate").asLong();

			Admins admin = new Admins();
			admin.setName(name);
//			admin.setLastName(lastName);
			admin.setEmail(email);
			admin.setPassword(password);
			admin.setMobile(mobile);
			admin.setAction(Status.UNBLOCK);
			admin.setPropertyName(propertyName);
			admin.setUserId(id);
			admin.setStartDate(startDate);
			admin.setEndDate(endDate);
			admin.setRole("ADMIN");
			Admins save = adminRepository.save(admin);
			String userObject = getUserObject(save);
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
			user.setUserId(id);
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

//	public ResponseEntity<?> authenticateUser(String user, HttpServletResponse response) {
//		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode jsonNode = objectMapper.readTree(user);
//			String role = jsonNode.get("role").asText();
//			String email = jsonNode.get("email").asText();
//			String userPassword = jsonNode.get("password").asText();
//
//			// Email validation
//			if (!isValidEmail(email)) {
//
//				throw new UserServiceException(400, "Invalid email format");
//			}
//
//			User byEmail = repository.findByEmail(email);
//
//			System.out.println("User found :: " + byEmail);
//			if (byEmail == null || !role.equals(byEmail.getRole())) {
//				System.out.println("In role check :: " + byEmail.getRole() + role);
//				throw new UserServiceException(409, "User profile not found");
//			}
//
//			System.out.println("commig pass :: " + userPassword);
//			String dbPassword = byEmail.getPassword();
//			System.out.println("dbPassword :: " + dbPassword);
//
//			if (dbPassword.equals(userPassword)) {
//
//				String token = jwtUtil.createToken(byEmail.getEmail(), byEmail.getRole());
//				System.out.println("Token Created Successfully :: " + token);
//
//				Cookie cookie = new Cookie("token", token);
//				cookie.setHttpOnly(true); // Helps mitigate XSS attacks
//				cookie.setSecure(false); // Ensures cookies are sent over HTTPS only (set to false in development
//											// environments without HTTPS)
//				cookie.setMaxAge(60 * 60 * 6); // Set the expiry time (6 hrs)
//				cookie.setPath("/"); // Set the path to make the cookie available across the entire domain
//				response.addCookie(cookie);
//				String userObject = getUserObject1(byEmail, token);
//				return ResponseEntity.ok(userObject);
//
//			} else {
//				throw new UserServiceException(409, "Invalid email and password");
//			}
//		} catch (UserServiceException e) {
//
//			return ResponseEntity.status(e.getStatusCode()).body(
//					new Error(e.getStatusCode(), e.getMessage(), "Unable to login user", System.currentTimeMillis()));
//
//		} catch (Exception ex) {
//			throw new UserServiceException(409, "Invalid Credentials");
//		}
//	}

	public ResponseEntity<?> authenticateUser(String user, HttpServletResponse response) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(user);
			String role = jsonNode.get("role").asText();
			String email = jsonNode.get("email").asText();
			String userPassword = jsonNode.get("password").asText();

			if (!isValidEmail(email)) {
				throw new UserServiceException(400, "Invalid email format");
			}

			User byEmail = null;
			Admins byAdminEmail = null;

			if (role.equalsIgnoreCase("SALES") || role.equalsIgnoreCase("CRM")) {
				byEmail = repository.findByEmail(email);
				if (byEmail == null) {
					throw new UserServiceException(409, "User profile not found");
				}
				if (!role.equalsIgnoreCase(byEmail.getRole())) {
					throw new UserServiceException(409, "User role mismatch");
				}
				System.out.println("User found: " + byEmail);

				if (byEmail.getPassword().equals(userPassword)) {
					return createResponse(response, byEmail.getEmail(), byEmail.getRole());
				} else {
					throw new UserServiceException(409, "Invalid email and password");
				}
			} else if (role.equalsIgnoreCase("SUPER ADMIN") || role.equalsIgnoreCase("ADMIN")) {
				byAdminEmail = adminRepository.findByEmail(email);
				if (byAdminEmail == null) {
					throw new UserServiceException(409, "Admin profile not found");
				}
				if (!role.equalsIgnoreCase(byAdminEmail.getRole())) {
					throw new UserServiceException(409, "Admin role mismatch");
				}
				System.out.println("Admin found: " + byAdminEmail);

				if (byAdminEmail.getPassword().equals(userPassword)) {
					return createResponse(response, byAdminEmail.getEmail(), byAdminEmail.getRole());
				} else {
					throw new UserServiceException(409, "Invalid email and password");
				}
			} else {
				throw new UserServiceException(409, "Invalid role provided. Only USER or ADMIN are allowed.");
			}
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to login user", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Invalid Credentials");
		}
	}

	private ResponseEntity<?> createResponse(HttpServletResponse response, String email, String role) {
		String token = jwtUtil.createToken(email, role);
		System.out.println("Token Created Successfully :: " + token);

		Cookie cookie = new Cookie("token", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setMaxAge(60 * 60 * 6);
		cookie.setPath("/");
		response.addCookie(cookie);

		String userObjectStr = getUserObject1(role, email, token);
		return ResponseEntity.ok(userObjectStr);
	}

	public ResponseEntity<?> getAdmin(String token, long id) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"ADMIN".equalsIgnoreCase(role) && !"SUPER ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Admins admin = adminRepository.findById(id)
					.orElseThrow(() -> new UserServiceException(401, "User not exists"));
			String userObject = getUserObject(admin);
			return ResponseEntity.ok(userObject);
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

			if (!"ADMIN".equalsIgnoreCase(role) && !dbUser.getRole().equalsIgnoreCase(role)) {
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

	public ResponseEntity<?> getAdminsList(String token, int page, String role) {
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

			if (!"ADMIN".equalsIgnoreCase(adminRole) && !"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			role = role.trim();
			Pageable pageable = PageRequest.of(page - 1, 10);
			Page<Admins> usersPage = adminRepository.findByRoleOrderByCreatedOnDesc("ADMIN", pageable);

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

	public ResponseEntity<?> getUsersListByRole(String token, int page, String role) {
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
			Admins admin = adminRepository.findByRole(adminRole);
			if (!"ADMIN".equalsIgnoreCase(adminRole) && !"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			role = role.trim();
			Pageable pageable = PageRequest.of(page - 1, 10);
			Page<User> usersPage = repository.findByRoleAndUserIdOrderByCreatedOnDesc(role, admin.getId(), pageable);

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

	public ResponseEntity<?> getSalesListByRole(@CookieValue(value = "accessToken", required = false) String token,
			String role) {
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
			List<User> users = repository.findByRoleOrderByCreatedOnDesc(role);

			if (users.isEmpty()) {
				return ResponseEntity.ok("No users found for the role: " + role);
			}

			List<UserDTO> userDTOs = users.stream().map(UserDTO::new).collect(Collectors.toList());

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
			Admins admin = adminRepository.findByRole(adminRole);
			if (!"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			List<User> allByUserId = repository.findByRoleWhereUserId(role, admin.getId());
			System.out.println("List found and its size is : " + allByUserId.size());
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

	public ResponseEntity<?> getUsersCountByRole(String token) {
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
			if (!"ADMIN".equalsIgnoreCase(adminRole) && !"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			List<User> sales = repository.findByRole("SALES");
			List<User> crm = repository.findByRole("CRM");
			long adminsCountByRole = adminRepository.adminsCountByRole("ADMIN");
			long leads = leadRepository.count();
			if (!"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.ok(Map.of("sales", sales.size(), "crm", crm.size(), "leads", leads));
			} else {
				return ResponseEntity.ok(Map.of("amins", adminsCountByRole));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user details");
		}
	}

}
