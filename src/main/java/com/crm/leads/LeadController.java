package com.crm.leads;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.crm.Exception.Error;
import com.crm.user.Status;
import com.crm.user.UserServiceException;

@Controller
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174"), ("http://139.84.136.208") })
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

	@GetMapping("/listbystatus")
	public ResponseEntity<?> importdClients(@CookieValue(value = "token", required = true) String token,
			@RequestParam int page, @RequestParam Status status) {
		try {
			return leadService.importdClients(token, page, status);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to load data.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getById/{id}")
	public ResponseEntity<?> getLeadById(@CookieValue(value = "token", required = true) String token,
			@PathVariable long id) {
		try {
			return leadService.getClientById(token, id);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/clientsbycr")
	public ResponseEntity<?> getClientsByCrmId(@CookieValue(value = "token", required = true) String token,
			@RequestParam long userId, @RequestParam int page) {
		try {
			return leadService.getClientsByCrmId(token, userId, page);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to load data");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/updateFields/{leadId}")
	public ResponseEntity<?> addAndUpdateData(@PathVariable Long leadId, @RequestParam(required = false) Status status,
			@RequestParam(required = false) String comment, @RequestParam(required = false) List<String> key,
			@RequestParam(required = false) List<Object> value) {
		try {
			return leadService.addConversationLogAndDynamicField(leadId, status, comment, key, value);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to process the request.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
