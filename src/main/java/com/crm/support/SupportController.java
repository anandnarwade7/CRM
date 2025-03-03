package com.crm.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174"),("http://139.84.136.208 ")})
@RequestMapping("/api/support")
public class SupportController {

	
	@Autowired
	private SupportService supportService;
	
	
	
	@CrossOrigin(origins = { ("http://localhost:3000") })
	@PostMapping("/raiseTicket")
	public ResponseEntity<?> generateSupportTicket(String query, String description, long userId) {
		try {
			System.out.println("Check Point 1 ");
			return supportService.generateSupportTicket(query, description, userId);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}


