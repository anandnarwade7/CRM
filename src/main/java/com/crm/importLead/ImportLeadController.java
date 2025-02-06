package com.crm.importLead;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	
	@PostMapping("/upload-template")
	public ResponseEntity<?> uploadTemplate(@RequestParam long userId, @RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please upload a file!", HttpStatus.BAD_REQUEST);
		}

		try {
			System.out.println();
			return service.readLeadsFromExcel(userId, file);
		} catch (UserServiceException e) {
			System.out.println("In First Catch Block");
			return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
		} catch (Exception e) {
			System.out.println("In Second Catch Block");
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
