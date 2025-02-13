package com.crm.importLead;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.crm.user.UserServiceException;

@RestController
@RequestMapping("/api/import")
public class ImportLeadController {

	@Autowired
	private ImportLeadService service;

//	@PostMapping("/upload-template")
//	public ResponseEntity<?> uploadTemplate(@RequestParam long userId, @RequestParam("file") MultipartFile file) {
//		if (file.isEmpty()) {
//			return new ResponseEntity<>("Please upload a file!", HttpStatus.BAD_REQUEST);
//		}
//
//		try {
//			System.out.println();
//			return service.readLeadsFromExcel(userId, file);
//		} catch (UserServiceException e) {
//			System.out.println("In First Catch Block");
//			return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
//		} catch (Exception e) {
//			System.out.println("In Second Catch Block");
//			e.printStackTrace();
//			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

		@PostMapping("/upload-template")
		public ResponseEntity<?> uploadTemplate(@CookieValue(value = "token", required = true) String token, @RequestParam long userId,
				@RequestParam(required = false) List<Long> assignedTo, @RequestParam("file") MultipartFile file) {
			if (file.isEmpty()) {
				return new ResponseEntity<>("Please upload a file!", HttpStatus.BAD_REQUEST);
			}
	
			try {
				System.out.println();
				return service.readLeadsFromExcel(token, userId, assignedTo, file);
			} catch (UserServiceException e) {
				return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	@PutMapping("/assignLeads")
	public ResponseEntity<?> assignLeadsToSaled() {
		try {
			return service.assignLeadsToSaled();
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
