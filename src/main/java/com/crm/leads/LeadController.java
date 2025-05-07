package com.crm.leads;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.crm.Exception.Error;
import com.crm.fileHandler.FilesManager;
import com.crm.user.Status;
import com.crm.user.UserServiceException;

@RestController
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174"), ("http://139.84.136.208") })
@RequestMapping("/api/clients")
public class LeadController {

	@Autowired
	private LeadService leadService;

	@Autowired
	private FilesManager filesManager;

//	private String serverDocsUrl = "D:\\Files\\MediaData\\";
	private String serverDocsUrl = "/root/mediadata/Docs/";

	@PostMapping("/upload")
	public ResponseEntity<?> uploadTemplate(@RequestHeader(value = "Authorization", required = true) String token,
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

	@PostMapping("/assign")
	public ResponseEntity<?> assignConvertedLeads(@RequestHeader(value = "Authorization", required = true) String token,
			@RequestParam long userId, @RequestParam(required = false) List<Long> assignedTo,
			@RequestParam(required = false) List<Long> leadIds) {
		try {
			System.out.println();
			return leadService.assignConvertedLeads(token, userId, assignedTo, leadIds);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("please specify data properly.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/listbystatus")
	public ResponseEntity<?> importedClients(@RequestHeader(value = "Authorization", required = true) String token,
			@RequestParam int page, @RequestParam Status status) {
		try {
			return leadService.importedClients(token, page, status);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to fetch data", System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/file")
	public ResponseEntity<?> getFile(@RequestParam("fileName") String fileName) throws IOException {
		try {
			String path;
			path = serverDocsUrl + fileName;
			Resource file = filesManager.load(path);
			if (file == null || !file.exists()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
			}
			String fname = file.getFilename();
			String mimeType = Files.probeContentType(Paths.get(path));
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fname + "\"")
					.contentType(MediaType.parseMediaType(mimeType)).body(file);

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error reading the file: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred: " + e.getMessage());
		}
	}

	@CrossOrigin(origins = { ("http://localhost:3000") })
	@GetMapping("/getById/{id}")
	public ResponseEntity<?> getLeadById(@RequestHeader(value = "Authorization", required = true) String token,
			@PathVariable long id) {
		try {
			return leadService.getClientById(token, id);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}

	@GetMapping("/clientsbycr")
	public ResponseEntity<?> getClientsByCrmId(@RequestHeader(value = "Authorization", required = true) String token,
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
			@RequestParam(required = false) String comment, @RequestParam(required = false) long dueDate,
			@RequestParam(required = false) List<String> key, @RequestParam(required = false) List<Object> value) {
		try {
			return leadService.addConversationLogAndDynamicField(leadId, status, comment, dueDate, key, value);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to process the request.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/uploaddocs/{id}")
	public ResponseEntity<?> uploadDocs(@RequestHeader(value = "Authorization", required = true) String token,
			@PathVariable long id, @RequestParam(value = "agreement") MultipartFile agreement,
			@RequestParam(value = "stampDuty") MultipartFile stampDuty,
			@RequestParam(value = "tdsDoc") MultipartFile tdsDoc,
			@RequestParam(value = "bankSanction") MultipartFile bankSanction) {
		try {
			return leadService.uploadDocs(token, id, agreement, stampDuty, tdsDoc, bankSanction);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to process the request.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/get/client/data/{id}/{page}")
	public ResponseEntity<?> getDataOfClientByCliectEmailToViewAndDownload(
			@RequestHeader(value = "Authorization", required = true) String token, @PathVariable long id,
			@PathVariable int page) {
		try {
			return leadService.getDataOfClientByCliectEmailToViewAndDownload(token, id, page);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to process the request.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getleadcount")
	public ResponseEntity<?> getUsersCountByRole(
			@RequestHeader(value = "Authorization", required = true) String token,
			@RequestParam(value = "userId", required = false) Long userId) {
		try {
			return leadService.getTotalCountsOfLeads(token, userId);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data", System.currentTimeMillis()));
		}
	}
	
}
