package com.crm.importLead;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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
import com.crm.user.Status;
import com.crm.user.UserServiceException;

@RestController
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174"), ("http://139.84.136.208 ") })
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
	public ResponseEntity<?> assignLeadsToSales() {
		try {
			return service.assignLeadsToSales();
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to assign");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/assigned")
	public ResponseEntity<?> assignedLeads(@CookieValue(value = "token", required = true) String token,
			@RequestParam int page, @RequestParam Status status) {
		try {
			return service.assignLeads(token, page, status);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to load data.");
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
			return ResponseEntity.badRequest().body("Unable to load data");
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
			return ResponseEntity.badRequest().body("Unable to add log.");
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
			return ResponseEntity.badRequest().body("Unable to add fields.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/updateFields/{leadId}")
	public ResponseEntity<?> addAndUpdateData(@PathVariable Long leadId, @RequestParam(required = false) Status status,
			@RequestParam(required = false) String comment, @RequestParam(required = false) long dueDate,
			@RequestParam(required = false) List<String> key, @RequestParam(required = false) List<Object> value) {
		try {
			return ResponseEntity
					.ok(service.addConversationLogAndDynamicField(leadId, status, comment, dueDate, key, value));
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to process the request.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateStatus")
	public ResponseEntity<?> updateLeadsToComplete(@PathVariable long leadId, @RequestParam Status status) {
		try {
			return service.updateLeadsToComplete(leadId, status);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to process request.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getLeadsCount")
	public ResponseEntity<?> getUsersCountByRole(@CookieValue(value = "token", required = true) String token,
			@RequestParam(value = "userId", required = false) Long userId) {
		try {
			return service.getTotalCountsOfLeads(token, userId);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/download-template")
	public ResponseEntity<byte[]> downloadTemplate() {
		try {
			byte[] excelBytes = service.downloadTemplateExcel();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType
					.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
			headers.setContentDispositionFormData("attachment", "Template.xlsx");

			return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/export")
	public ResponseEntity<?> exportLeadToExcel() {
		File leadFile = service.getConvertedLeads();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Leads_Data.xlsx");
			headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//				headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName);
//				headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

			InputStreamResource resource = new InputStreamResource(new FileInputStream(leadFile));
			return ResponseEntity.ok().headers(headers).contentLength(leadFile.length()).body(resource);

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error returning Excel file: " + e.getMessage());
		} finally {
			leadFile.delete();
		}
	}
}
