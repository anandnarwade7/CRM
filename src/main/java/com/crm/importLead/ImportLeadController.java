package com.crm.importLead;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crm.Exception.Error;
import com.crm.user.UserServiceException;

@RestController
@CrossOrigin(origins = { ("http://localhost:3000"), ("http://localhost:5173"), ("http://localhost:5174") })
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
	public ResponseEntity<?> uploadTemplate(@CookieValue(value = "token", required = true) String token,
			@RequestParam long userId, @RequestParam(required = false) List<Long> assignedTo,
			@RequestParam("file") MultipartFile file) {
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

	@GetMapping("/assigned")
	public ResponseEntity<?> assignedLeads(@CookieValue(value = "token", required = true) String token,
			@RequestParam int page, @RequestParam String status) {
		try {
			return service.assignLeads(token, page, status);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getLeadsById/{id}")
	public ResponseEntity<?> getSales(@CookieValue(value = "token", required = true) String token,
			@PathVariable long id) {
		try {
			return service.getLeadsById(token, id);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/leads")
	public ResponseEntity<?> getLeadsBysalesId(@CookieValue(value = "token", required = true) String token,
			@RequestParam long userId, @RequestParam int page) {
		try {
			return service.getLeadsBysalesId(token, userId, page);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/addConversationLog/{leadId}")
	public ResponseEntity<?> addConversationLog(@PathVariable Long leadId, @RequestParam String date,
			@RequestParam String comment) {
		try {
			return ResponseEntity.ok(service.addConversationLog(leadId, date, comment));
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/addDynamicField/{leadId}")
	public ResponseEntity<?> addDynamicField(@PathVariable Long leadId, @RequestParam String key,
			@RequestParam Object value) {
		try {
			return ResponseEntity.ok(service.addDynamicField(leadId, key, value));
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/updateFields/{leadId}")
	public ResponseEntity<?> addAndUpdateData(@PathVariable Long leadId, @RequestParam(required = false) String status,
			@RequestParam(required = false) String comment, @RequestParam(required = false) String key,
			@RequestParam(required = false) Object value) {
		try {
			return ResponseEntity.ok(service.addConversationLogAndDynamicField(leadId, status, comment, key, value));
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> updateLeadsToComplete(@PathVariable long leadId, @RequestParam String status) {
		try {
			return service.updateLeadsToComplete(leadId, status);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Uploaded file does not contain any data.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
