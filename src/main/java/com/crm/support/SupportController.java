package com.crm.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.crm.user.UserServiceException;

@RestController
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174"), ("http://139.84.136.208 "), ("crm.propertysearch.ai") })
@RequestMapping("/api/support")
public class SupportController {

	@Autowired
	private SupportService supportService;

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/raiseTicket")
	public ResponseEntity<?> generateSupportTicket(
			@RequestHeader(value = "Authorization", required = true) String token, @RequestBody Support support) {
		try {
			System.out.println("Check Point 1 ");
			return supportService.generateSupportTicket(token, support);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/tickets/{pageNumber}")
	public ResponseEntity<?> fetchSupportTicket(@RequestHeader(value = "Authorization", required = true) String token,
			@PathVariable int pageNumber) {
		try {
			System.out.println("Check Point 1 ");
			return supportService.getSupportTicketsList(token, pageNumber);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PutMapping("/update/{userId}/{id}/{response}")
	public ResponseEntity<?> solveOrDeleteTicket(@RequestHeader(value = "Authorization", required = true) String token,
			@PathVariable("userId") Long userId, @PathVariable("id") long id, @PathVariable("response") String response,
			@RequestParam(name = "note", required = false) String note) {
		try {
			return supportService.solveOrDeleteTicket(token, id, response, note);
		} catch (UserServiceException e) {
			System.out.println("Caught UserControllerException:");
			System.out.println("Exception message: " + e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
