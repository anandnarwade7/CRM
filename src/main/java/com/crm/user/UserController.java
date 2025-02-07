package com.crm.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.Exception.Error;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = { ("http://localhost:3000"), ("http://localhost:5173"), ("http://localhost:5174") })
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService service;

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/registerAdmin")
	public ResponseEntity<?> registerAdmin(@RequestBody String userJson) {
		try {
			return service.registerUser(userJson);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/addUser/{id}")
	public ResponseEntity<?> registerUser(@CookieValue(value = "token", required = false) String token,
			@PathVariable long id, @RequestBody String userJson) {
		try {
			return service.addUser(token, id, userJson);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody String userJson, HttpServletResponse response) {
		try {
			System.out.println("Check Point 0 :::: in Controller login ");
			return service.authenticateUser(userJson, response);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to login user", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getAdminById/{id}")
	public ResponseEntity<?> getUser(@CookieValue(value = "token", required = false) String token,
			@PathVariable long id) {
		try {
			return service.getAdmin(token, id);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getCRMById/{id}")
	public ResponseEntity<?> getCRM(@CookieValue(value = "token", required = false) String token,
			@PathVariable long id) {
		try {
			return service.getCRM(token, id);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getSalesById/{id}")
	public ResponseEntity<?> getSales(@CookieValue(value = "token", required = false) String token,
			@PathVariable long id) {
		try {
			return service.getSalesById(token, id);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/addDetails/{userId}")
	public ResponseEntity<?> updateUserForRegistration(@CookieValue(value = "token", required = false) String token,
			@PathVariable long adminId, @RequestBody User user) {
		try {
			return service.updateAdminForRegistration(token, adminId, user);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Invalid User Credentials", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/updateUser/{adminId}/{response}")
	public ResponseEntity<?> updateUserAsBlockUnBlock(@CookieValue(value = "token", required = false) String token,
			@PathVariable long adminId, @PathVariable String response,
			@RequestParam(name = "note", required = false) String note) {
		try {
			return service.updateUserAsBlockUnBlock(token, adminId, response, note);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Invalid User Credentials", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/deleteUser/{adminId}/{userId}")
	public ResponseEntity<?> deleteUser(@CookieValue(value = "token", required = false) String token,
			@PathVariable long adminId, @PathVariable long userId) {
		try {
			return service.deleteUser(token, adminId, userId);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Invalid User Credentials", System.currentTimeMillis()));
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getUsers")
	public ResponseEntity<?> getUsersListByRole(@CookieValue(value = "token", required = false) String token,
			@RequestParam int page, @RequestParam String role) {
		try {
			return service.getUsersListByRole(token, page, role);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/getCountByRole/{role}")
	public ResponseEntity<?> getTotalCountForAdmin(@CookieValue(value = "token", required = false) String token,
			@PathVariable String role) {
		try {
			return service.getTotalCountForAdmin(token, role);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user details");
		}
	}
}
