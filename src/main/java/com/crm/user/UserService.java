package com.crm.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.crm.Exception.Error;
import com.crm.importLead.ImportLeadRepository;
import com.crm.mailservice.MailService;
import com.crm.security.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private AdminsRepository adminRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ImportLeadRepository leadRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MailService mailService;

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
			responseJson.put("password", user.getPassword());
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
			System.out.println("Role :--> " + role);
			if ("SUPER ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
				Admins admin = adminRepository.findByEmail(email);
				System.out.println("Admin :--> " + admin);
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
			} else if ("SALES".equalsIgnoreCase(role) || "CRM".equalsIgnoreCase(role)) {
				User user = repository.findByEmail(email);
				System.out.println("User(SALES/CRM) :--> " + user);
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
			} else if ("CLIENT".equalsIgnoreCase(role)) {
				Client user = clientRepository.findByEmail(email);
				System.out.println("Client :--> " + user);
				responseJson.put("id", user.getId());
				responseJson.put("name", user.getName());
//			responseJson.put("lastName", user.getLastName());
				responseJson.put("email", user.getEmail());
				responseJson.put("mobile", user.getMobile());
				responseJson.put("role", user.getRole());
				responseJson.put("password", user.getPassword());
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
//			Client byClientEmail = null;

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
					if (byEmail.getAction() != Status.BLOCK) {
						return createResponse(response, byEmail.getEmail(), byEmail.getRole());
					} else {
						throw new UserServiceException(409, "Your profile is blocked, please reach to administration.");
					}
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
					if (byAdminEmail.getAction() != Status.BLOCK) {
						return createResponse(response, byAdminEmail.getEmail(), byAdminEmail.getRole());
					} else {
						throw new UserServiceException(409, "Your profile is blocked, please reach to administration.");
					}
				} else {
					throw new UserServiceException(409, "Invalid email and password");
				}
			}
//			else if (role.equalsIgnoreCase("CLIENT")) {
//				byClientEmail = clientRepository.findByEmail(email);
//				if (byClientEmail == null) {
//					throw new UserServiceException(409, "Admin profile not found");
//				}
//				if (!role.equalsIgnoreCase(byClientEmail.getRole())) {
//					throw new UserServiceException(409, "Admin role mismatch");
//				}
//				System.out.println("Admin found: " + byClientEmail);
//
//				if (byClientEmail.getPassword().equals(userPassword) && byClientEmail.getAction() != Status.BLOCK) {
//					return createResponse(response, byClientEmail.getEmail(), byClientEmail.getRole());
//				} else {
//					throw new UserServiceException(409, "Invalid email and password");
//				}
//			} 
			else {
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

//		Cookie cookie = new Cookie("token", token);
//		cookie.setHttpOnly(true);
//		cookie.setSecure(false);
//		cookie.setMaxAge(60 * 60 * 6);
//		cookie.setPath("/");
//		response.addCookie(cookie);

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

	public ResponseEntity<?> getUserDetails(String token) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String email = userClaims.get("email");

			if ("SALES".equalsIgnoreCase(role) || "CRM".equalsIgnoreCase(role)) {
				User user = repository.findByEmail(email);
				if (user != null) {
					String userObject = getUserObject(user);
					return ResponseEntity.ok(userObject);
				} else {
					throw new UserServiceException(401, "User not exists");
				}
			} else if ("ADMIN".equalsIgnoreCase(role) || "SUPER ADMIN".equalsIgnoreCase(role)) {
				Admins user = adminRepository.findByEmail(email);
				if (user != null) {
					String userObject = getUserObject(user);
					return ResponseEntity.ok(userObject);
				} else {
					throw new UserServiceException(401, "User not exists");
				}
			} else {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
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

			User dbUser = repository.findById(userId)
					.orElseThrow(() -> new UserServiceException(409, "User does not exist"));

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

	public ResponseEntity<?> updateUserAsBlockUnBlock(String token, long userId, Status response, String note) {
		try {
			System.out.println("Check 1");

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			User dbUser = repository.findById(userId)
					.orElseThrow(() -> new UserServiceException(409, "User does not exist"));

			if (!"ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			dbUser.setAction(response);
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

	public ResponseEntity<?> updateAdminAsBlockUnBlockBySuperAdmin(String token, long userId, Status response,
			String note) {
		try {
			System.out.println("Check 1");

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			Admins dbUser = adminRepository.findById(userId)
					.orElseThrow(() -> new UserServiceException(409, "User does not exist"));

			if (!"SUPER ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			dbUser.setAction(response);
			List<User> usersByUserId = repository.findUsersByUserId(dbUser.getId());
			for (User user : usersByUserId) {
				user.setAction(response);
				repository.save(user);
			}

			Admins existingUser = adminRepository.save(dbUser);
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

	public ResponseEntity<?> getAdminsList(String token, int page) {
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

			if (!"SUPER ADMIN".equalsIgnoreCase(adminRole) && !"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			Pageable pageable = PageRequest.of(page - 1, 10);
			Page<Admins> usersPage = adminRepository.findByRoleOrderByCreatedOnDesc("ADMIN", pageable);

			if (usersPage.isEmpty()) {
				return ResponseEntity.ok("No users found for the role: ADMIN");
			}

//			List<UserDTO> userDTOs = usersPage.getContent().stream().map(UserDTO::new).collect(Collectors.toList());
			Page<UserDTO> userDTOs = usersPage.map(UserDTO::new);
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

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String adminRole = userClaims.get("role");
			String email = userClaims.get("email");

			if (!"SUPER ADMIN".equalsIgnoreCase(adminRole) && !"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			Admins admin = adminRepository.findByEmail(email);
			if (admin == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found for email: " + email);
			}
			role = role.trim();
			Pageable pageable = PageRequest.of(page - 1, 10);
			Page<User> usersPage = repository.findByRoleAndUserIdOrderByCreatedOnDesc(role, admin.getId(), pageable);

			if (usersPage.isEmpty()) {
				return ResponseEntity.ok("No users found for the role: " + role);
			}

//			List<UserDTO> userDTOs = usersPage.getContent().stream().map(UserDTO::new).collect(Collectors.toList());
			Page<UserDTO> userDTOs = usersPage.map(UserDTO::new);

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

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String adminRole = userClaims.get("role");
			String email = userClaims.get("email");

			if (!"SUPER ADMIN".equalsIgnoreCase(adminRole) && !"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			Admins admin = adminRepository.findByEmail(email);
			if (admin == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found for email: " + email);
			}
			List<User> users = repository.findByRoleAndUserIdOrderByCreatedOnDesc(role, admin.getId());

			if (users.isEmpty()) {
				return ResponseEntity.ok(users);
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
			if (token == null || token.trim().isEmpty()) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: No token provided.");
			}

			System.out.println("Received token: " + token);

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String userRole = userClaims.get("role");
			String email = userClaims.get("email");

			System.out.println("User Role: " + userRole + ", Email: " + email);

			if (!"SUPER ADMIN".equalsIgnoreCase(userRole) && !"ADMIN".equalsIgnoreCase(userRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Admins admin = adminRepository.findByEmail(email);
			if (admin == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found for email: " + email);
			}

			long salesCount = repository.findByRoleAndUserId("SALES", admin.getId());
			long crmCount = repository.findByRoleAndUserId("CRM", admin.getId());
			long totalLeads = leadRepository.count();

			Map<String, Object> responseMap = new HashMap<>();
			if ("ADMIN".equalsIgnoreCase(userRole)) {
				responseMap.put("sales", salesCount);
				responseMap.put("crm", crmCount);
				responseMap.put("leads", totalLeads);

			} else if ("SUPER ADMIN".equalsIgnoreCase(userRole)) {
//				adminRepository.adminsCountByRole("ADMIN");
				List<Admins> admins = adminRepository.getByRole("ADMIN");
				long adminsCountByRole = admins.size();
				long sales = repository.findCountByRole("SALES");
				long crm = repository.findCountByRole("CRM");
				responseMap.put("admins", adminsCountByRole);
				responseMap.put("sales", sales);
				responseMap.put("crm", crm);
			}

			return ResponseEntity.ok(responseMap);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user details: " + e.getMessage());
		}
	}

//	public ResponseEntity<?> addClient(String token, long id, String userJson) {
//		try {
//			System.out.println("In add client service");
//
//			if (jwtUtil.isTokenExpired(token)) {
//				System.err.println("checking token" + jwtUtil.isTokenExpired(token));
//				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
//						.body("Unauthorized: Your session has expired.");
//			}
//
//			String role = jwtUtil.extractRole(token);
//
//			if (!"CRM".equalsIgnoreCase(role)) {
//				System.err.println("checking token role " + !"SUPER ADMIN".equalsIgnoreCase(role));
//				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
//						.body("Forbidden: You do not have the necessary permissions.");
//			}
//
//			System.out.println("Check point 1");
//
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode jsonNode = objectMapper.readTree(userJson);
//			String email = jsonNode.get("email").asText();
//			System.out.println("Check point 2 email " + email);
//			String userObject = null;
//			if (clientRepository.existsByEmail(email)) {
//				Client byEmail = clientRepository.findByEmail(email);
//				List<Long> crmIdList = byEmail.getCrmIds() != null ? new ArrayList<>(byEmail.getCrmIds())
//						: new ArrayList<>();
//				System.out.println("CRM id :: "+crmIdList.size()+" and values "+crmIdList.indexOf(0L));
//				byEmail.setCrmIds(crmIdList);
//				userObject = getUserObject1(byEmail.getRole(), email, null);
//				clientRepository.save(byEmail);
//			}
////			String password = jsonNode.get("password").asText();
//
//			return ResponseEntity.ok(userObject);
//		} catch (UserServiceException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
//					"Unable to register user", System.currentTimeMillis()));
//		} catch (Exception ex) {
//			throw new UserServiceException(409, "Invalid Credentials ");
//		}
//	}

	public ResponseEntity<?> addClient(String token, long crmId, String userJson) {
		try {
			System.out.println("In add client service");

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);
			if (!"CRM".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(userJson);
			String email = jsonNode.get("email").asText();

			System.out.println("Processing email: " + email + ", CRM ID: " + crmId);

			String userObject = null;

			if (clientRepository.existsByEmail(email)) {
				Client client = clientRepository.findByEmail(email);

				List<Long> crmIdList = client.getCrmIds() != null ? new ArrayList<>(client.getCrmIds())
						: new ArrayList<>();

				System.out.println("Existing CRM IDs: " + crmIdList);

				if (!crmIdList.contains(crmId)) {
					crmIdList.add(crmId);
					client.setCrmIds(crmIdList);
					clientRepository.save(client);
					System.out.println("CRM ID " + crmId + " added to client.");
				} else {
					System.out.println("CRM ID " + crmId + " already exists. Not adding again.");
				}

				userObject = getUserObject1(client.getRole(), email, null);
			}

			return ResponseEntity.ok(userObject);

		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to register user", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Invalid Credentials");
		}
	}

	public ResponseEntity<?> updateUserDetailsAndAdminDetails(
			@CookieValue(value = "token", required = true) String token, @PathVariable long id,
			@RequestBody String userJson) {
		try {
			if (token == null || token.trim().isEmpty()) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: No token provided.");
			}

			System.out.println("Received token: " + token);

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String userRole = userClaims.get("role");
			String email = userClaims.get("email");

			System.out.println("User Role: " + userRole + ", Email: " + email);
			if (!"SUPER ADMIN".equalsIgnoreCase(userRole)) {
				System.err.println("checking token role " + !"SUPER ADMIN".equalsIgnoreCase(userRole));
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(userJson);
			String password = jsonNode.get("password").asText();
			long startDate = jsonNode.get("startDate").asLong();
			long endDate = jsonNode.get("endDate").asLong();

			Admins admin = adminRepository.findById(id).orElseThrow(
					() -> new UserServiceException(409, "given credentioals are not presend with user id: " + id));
			if (admin != null) {
				admin.setPassword(password);
				admin.setStartDate(startDate);
				admin.setEndDate(endDate);
				adminRepository.save(admin);
			}
			String userObject = getUserObject(admin);
			return ResponseEntity.ok(userObject);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to update user", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Invalid Credentials ");
		}
	}

//	public ResponseEntity<?> getClientsForAdmin(String token) {
//		try {
//			if (token == null || token.trim().isEmpty()) {
//				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
//						.body("Unauthorized: No token provided.");
//			}
//
//			System.out.println("Received token: " + token);
//
//			if (jwtUtil.isTokenExpired(token)) {
//				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
//						.body("Unauthorized: Your session has expired.");
//			}
//
//			Map<String, String> userClaims = jwtUtil.extractRole1(token);
//			String userRole = userClaims.get("role");
//			String email = userClaims.get("email");
//
//			System.out.println("User Role: " + userRole + ", Email: " + email);
//			if (!"ADMIN".equalsIgnoreCase(userRole) && !"CRM".equalsIgnoreCase(userRole)) {
//				System.err.println("checking token role " + !"ADMIN".equalsIgnoreCase(userRole));
//				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
//						.body("Forbidden: You do not have the necessary permissions.");
//			}
//
//			List<Client> clients = null;
//			if ("ADMIN".equalsIgnoreCase(userRole)) {
//				Admins byEmail = adminRepository.findByEmail(email);
//				System.out.println("User  " + byEmail);
//				List<User> usersByUserId = repository.findUsersByUserId(byEmail.getId());
//				System.out.println("CRMS Role: " + usersByUserId.size());
//				for (User user : usersByUserId) {
//					clients = clientRepository.findClientsByUserId(user.getId());
//					System.out.println("Clients Role: " + clients);
//				}
//			} else if ("CRM".equalsIgnoreCase(userRole)) {
//				User byEmail = repository.findByEmail(email);
//				clients = clientRepository.findClientsByUserId(byEmail.getId());
//			}
//
//			return ResponseEntity.ok(clients);
//		} catch (UserServiceException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(
//					new Error(e.getStatusCode(), e.getMessage(), "Unable to find clients", System.currentTimeMillis()));
//		} catch (Exception ex) {
//			throw new UserServiceException(409, "Invalid Credentials ");
//		}
//	}

	public ResponseEntity<?> getClientsForAdmin(String token) {
		try {
			if (token == null || token.trim().isEmpty()) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: No token provided.");
			}

			System.out.println("Received token: " + token);

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String userRole = userClaims.get("role");
			String email = userClaims.get("email");

			System.out.println("User Role: " + userRole + ", Email: " + email);
			if (!"ADMIN".equalsIgnoreCase(userRole) && !"SALES".equalsIgnoreCase(userRole)
					&& !"CRM".equalsIgnoreCase(userRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			List<Client> clients = new ArrayList<>();

			if ("ADMIN".equalsIgnoreCase(userRole)) {
				Admins byEmail = adminRepository.findByEmail(email);
				System.out.println("Admin: " + byEmail);

				if (byEmail == null) {
					return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Admin not found.");
				}

				List<User> crmUsers = repository.findUsersByUserId(byEmail.getId());

				if (crmUsers != null && !crmUsers.isEmpty()) {
					List<Long> crmIds = crmUsers.stream().map(User::getId).collect(Collectors.toList());

					clients = clientRepository.findByCrmIdIn(crmIds);
				}
			} else if ("CRM".equalsIgnoreCase(userRole) || "SALES".equalsIgnoreCase(userRole)) {
				User crmUser = repository.findByEmail(email);
				if (crmUser == null) {
					return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("CRM user not found.");
				}
				List<Long> crmIdList = crmUser.getId() != 0
						? new ArrayList<>(Collections.singletonList(crmUser.getId()))
						: new ArrayList<>();
				if ("CRM".equalsIgnoreCase(userRole)) {
					clients = clientRepository.findByCrmIdIn(crmIdList);
				} else {
					clients = clientRepository.findBySalesIdIn(crmIdList);
				}

			}

			return ResponseEntity.ok(clients);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find clients", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Invalid Credentials ");
		}
	}

	public ResponseEntity<?> sendOTPtoClientLogin(String email) {
		try {
			Client byEmail = clientRepository.findByEmail(email);
			if (byEmail == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Clint not found for email: " + email);
			}

			RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('0', '9').build();
			String generate = generator.generate(6);

			byEmail.setOtp(generate);
			byEmail.setOtpCreationTime(LocalDateTime.now());

			String clientEmail = byEmail.getEmail();
			String subject = "Your Otp for login";
			String message = "Welcome to CRM" + ",<br><br>" + "Dear <br>" + byEmail.getName() + ",\nYour OTP is: "
					+ generate + "<br>Best regards,<br>CRM Team";

			clientRepository.save(byEmail);
			mailService.sendEmail(clientEmail, subject, message);
			return ResponseEntity.ok(byEmail);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find clients", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Invalid Credentials ");
		}
	}

	public ResponseEntity<?> authenticateClient(String userJson) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(userJson);
			String email = jsonNode.get("email").asText();
			String generatedOtp = jsonNode.get("generatedOtp").asText();

			Client byEmail = clientRepository.findByEmail(email);
			if (byEmail == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Clint not found for email: " + email);
			}
			if (!generatedOtp.matches("\\d{6}")) {
				throw new UserServiceException(400, "Invalid OTP. Must be a numeric value with exactly 6 digits.");
			}
			if (!byEmail.getOtp().equals(generatedOtp)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Please provide valid otp");
			}
			String token = jwtUtil.createToken(email, byEmail.getRole());
			String userObjectStr = getUserObject1(byEmail.getRole(), email, token);
			return ResponseEntity.ok(userObjectStr);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find client", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Invalid Credentials ");
		}
	}
}
