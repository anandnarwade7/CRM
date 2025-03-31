package com.crm.user;

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
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174"), ("http://139.84.136.208") })
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService service;

	@PostMapping("/registerAdmin")
	public ResponseEntity<?> registerAdmin(@RequestBody String userJson) {
		try {
			return service.registerSuperAdmin(userJson);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add data", System.currentTimeMillis()));
		}
	}

	@PostMapping("/addUser/{id}")
	public ResponseEntity<?> registerUser(@CookieValue(value = "token", required = true) String token,
			@PathVariable long id, @RequestBody String userJson) {
		try {
			return service.addUser(token, id, userJson);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add data", System.currentTimeMillis()));
		}
	}

	@PostMapping("/addadmin/{id}")
	public ResponseEntity<?> registerAdmin(@CookieValue(value = "token", required = true) String token,
			@PathVariable long id, @RequestBody String userJson) {
		try {
			System.out.println("In add admin api");
			return service.addAdmin(token, id, userJson);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add data", System.currentTimeMillis()));
		}
	}

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

	@GetMapping("/getAdminById/{id}")
	public ResponseEntity<?> getUser(@CookieValue(value = "token", required = true) String token,
			@PathVariable long id) {
		try {
			return service.getAdmin(token, id);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/getCRMById/{id}")
	public ResponseEntity<?> getCRM(@CookieValue(value = "token", required = true) String token,
			@PathVariable long id) {
		try {
			return service.getCRM(token, id);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/getSalesById/{id}")
	public ResponseEntity<?> getSales(@CookieValue(value = "token", required = true) String token,
			@PathVariable long id) {
		try {
			return service.getSalesById(token, id);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@PutMapping("/addDetails/{userId}")
	public ResponseEntity<?> updateUserDetails(@CookieValue(value = "token", required = false) String token,
			@PathVariable long userId, @RequestBody User user) {
		try {
			return service.updateUserDetails(token, userId, user);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Invalid User Credentials", System.currentTimeMillis()));
		}
	}

	@PutMapping("/updateUser/{adminId}/{response}")
	public ResponseEntity<?> updateUserAsBlockUnBlock(@CookieValue(value = "token", required = true) String token,
			@PathVariable long adminId, @PathVariable Status response,
			@RequestParam(name = "note", required = false) String note) {
		try {
			return service.updateUserAsBlockUnBlock(token, adminId, response, note);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Invalid User Credentials", System.currentTimeMillis()));
		}
	}

	@PutMapping("/updateadmin/{superadminId}/{response}")
	public ResponseEntity<?> updateAdminAsBlockUnBlockBySuperAdmin(
			@CookieValue(value = "token", required = true) String token, @PathVariable long superadminId,
			@PathVariable Status response, @RequestParam(name = "note", required = false) String note) {
		try {
			return service.updateAdminAsBlockUnBlockBySuperAdmin(token, superadminId, response, note);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Invalid User Credentials", System.currentTimeMillis()));
		}
	}

	@PutMapping("/deleteUser/{adminId}/{userId}")
	public ResponseEntity<?> deleteUser(@CookieValue(value = "token", required = true) String token,
			@PathVariable long adminId, @PathVariable long userId) {
		try {
			return service.deleteUser(token, adminId, userId);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Invalid User Credentials", System.currentTimeMillis()));
		}
	}

	@GetMapping("/getUsers")
	public ResponseEntity<?> getUsersListByRole(@CookieValue(value = "token", required = true) String token,
			@RequestParam int page, @RequestParam String role) {
		try {
			return service.getUsersListByRole(token, page, role);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/getadmins")
	public ResponseEntity<?> getAdminsList(@CookieValue(value = "token", required = true) String token,
			@RequestParam int page) {
		try {
			return service.getAdminsList(token, page);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/getSales/{role}")
	public ResponseEntity<?> getSalesListByRole(@CookieValue(value = "token", required = true) String token,
			@PathVariable String role) {
		try {
			return service.getSalesListByRole(token, role);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/getCountByRole/{role}")
	public ResponseEntity<?> getTotalCountForAdmin(@CookieValue(value = "token", required = true) String token,
			@PathVariable String role) {
		try {
			return service.getTotalCountForAdmin(token, role);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user details");
		}
	}

	@GetMapping("/getUsersCountByRole")
	public ResponseEntity<?> getUsersCountByRole(@CookieValue(value = "token", required = true) String token) {
		try {
			return service.getUsersCountByRole(token);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user details");
		}
	}

	@PostMapping("/addclient/{id}")
	public ResponseEntity<?> addClientByCRM(@CookieValue(value = "token", required = true) String token,
			@PathVariable long id, @RequestBody String userJson) {
		try {
			System.out.println("In add admin api");
			return service.addClient(token, id, userJson);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add data", System.currentTimeMillis()));
		}
	}

	@PutMapping("/updatedetails/{id}")
	public ResponseEntity<?> updateUserDetailsAndAdminDetails(@CookieValue(value = "token", required = true) String token, @PathVariable long id,
			@RequestBody String userJson) {
		try {
			return service.updateUserDetailsAndAdminDetails(token, id, userJson);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add data", System.currentTimeMillis()));
		}
	}
}
