package com.crm.leads;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.crm.user.UserServiceException;

@Controller
@CrossOrigin
@RequestMapping("/api/clients")
public class LeadController {

	
	@Autowired
	private LeadService leadService;
	
	@PostMapping("/upload")
	public ResponseEntity<?> uploadTemplate(@CookieValue(value = "token", required = true) String token,
			@RequestParam long userId, @RequestParam(required = false) List<Long> assignedTo,
			@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please upload a file!", HttpStatus.BAD_REQUEST);
		}
		try {
			System.out.println();
			return leadService.readLeadsFromExcel(token, userId, assignedTo, file);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
