package com.crm.eventDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crm.fileHandler.FilesManager;
import com.crm.mailservice.MailService;
import com.crm.security.JwtUtil;
import com.crm.user.Client;
import com.crm.user.ClientRepository;
import com.crm.user.Status;
import com.crm.user.UserServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class EventDetailsService {

	@Autowired
	public EventDetailsRepository detailsRepository;

	@Autowired
	private FilesManager filesManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MailService mailService;

	@Autowired
	private ClientRepository clientRepository;

	public ResponseEntity<?> addEventDetails(String token, long crManagerId, MultipartFile statusReport,
			MultipartFile architectsLetter, MultipartFile invoice, MultipartFile receipt, String eventDetails) {
		try {

			if (jwtUtil.isTokenExpired(token)) {
				System.out.println("Jwt expiration checking");
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"CRM".equalsIgnoreCase(role)) {
				System.out.println("role checking ");
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			System.out.println("In serivce 2");

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(eventDetails);
			System.out.println("check the json data :: " + eventDetails);
//			long salesPersonId = jsonNode.get("salesPersonId").asLong();
//			long leadId = jsonNode.get("leadId").asLong();
////			long clientId = jsonNode.get("clientId").asLong();
//			long flatId = jsonNode.get("flatId").asLong();
//
//			String propertyName = jsonNode.get("propertyName").asText();
//			String eventName = jsonNode.get("eventName").asText();
//			double percentage = jsonNode.get("percentage").asDouble();
//			double basePriceAmount = jsonNode.get("basePriceAmount").asDouble();
//			double gstAmount = jsonNode.get("gstAmount").asDouble();
//			long invoiceDate = jsonNode.get("invoiceDate").asLong();
//			long dueDate = jsonNode.get("dueDate").asLong();
//			long paymentDate = jsonNode.get("paymentDate").asLong();
//			String paidByName = jsonNode.get("paidByName").asText();
			long salesPersonId = jsonNode.has("salesPersonId") && !jsonNode.get("salesPersonId").isNull()
					? jsonNode.get("salesPersonId").asLong()
					: 0L;

			long leadId = jsonNode.has("leadId") && !jsonNode.get("leadId").isNull() ? jsonNode.get("leadId").asLong()
					: 0L;

			long flatId = jsonNode.has("flatId") && !jsonNode.get("flatId").isNull() ? jsonNode.get("flatId").asLong()
					: 0L;

			long clientId = jsonNode.has("clientId") && !jsonNode.get("clientId").isNull()
					? jsonNode.get("clientId").asLong()
					: 0L;

			String propertyName = jsonNode.has("propertyName") && !jsonNode.get("propertyName").isNull()
					? jsonNode.get("propertyName").asText()
					: "";

			String eventName = jsonNode.has("eventName") && !jsonNode.get("eventName").isNull()
					? jsonNode.get("eventName").asText()
					: "";

			double percentage = jsonNode.has("percentage") && !jsonNode.get("percentage").isNull()
					? jsonNode.get("percentage").asDouble()
					: 0.0;

			double basePriceAmount = jsonNode.has("basePriceAmount") && !jsonNode.get("basePriceAmount").isNull()
					? jsonNode.get("basePriceAmount").asDouble()
					: 0.0;

			double gstAmount = jsonNode.has("gstAmount") && !jsonNode.get("gstAmount").isNull()
					? jsonNode.get("gstAmount").asDouble()
					: 0.0;

			long invoiceDate = jsonNode.has("invoiceDate") && !jsonNode.get("invoiceDate").isNull()
					? jsonNode.get("invoiceDate").asLong()
					: 0L;

			long dueDate = jsonNode.has("dueDate") && !jsonNode.get("dueDate").isNull()
					? jsonNode.get("dueDate").asLong()
					: 0L;

			long paymentDate = jsonNode.has("paymentDate") && !jsonNode.get("paymentDate").isNull()
					? jsonNode.get("paymentDate").asLong()
					: 0L;

			String paidByName = jsonNode.has("paidByName") && !jsonNode.get("paidByName").isNull()
					? jsonNode.get("paidByName").asText()
					: "";

			String statusReportDoc = filesManager.uploadFile(statusReport, "statusReport");
			String architectsLetterDoc = filesManager.uploadFile(architectsLetter, "architectsLetter");
			String invoiceDoc = filesManager.uploadFile(invoice, "invoice");
			String receiptDoc = filesManager.uploadFile(receipt, "receipt");
			// clientId= 0 (set in following constructor)
			EventDetails eventDetailsObj = new EventDetails(crManagerId, salesPersonId, leadId, clientId, flatId,
					propertyName, eventName, percentage, basePriceAmount, gstAmount, paidByName, eventDetails,
					propertyName, invoiceDate, dueDate, paymentDate, paidByName, paidByName, Status.SUBMITTED);
			System.out.println("check URL :: " + statusReportDoc);
			eventDetailsObj.setStatusReport(statusReportDoc);
			eventDetailsObj.setArchitectsLetter(architectsLetterDoc);
			eventDetailsObj.setInvoice(invoiceDoc);
			eventDetailsObj.setReceipt(receiptDoc);
			EventDetails eventDetailsObject = detailsRepository.save(eventDetailsObj);
			return ResponseEntity.ok(eventDetailsObject);
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> updateEventDetails(String token, long eventId, long crManagerId,
			MultipartFile statusReport, MultipartFile architectsLetter, MultipartFile invoice, MultipartFile receipt,
			String eventDetails) {
		try {
			String role = jwtUtil.extractRole(token);
			if (!"CRM".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Optional<EventDetails> eventDetailsFromDatabase = detailsRepository.findById(eventId);
			if (!eventDetailsFromDatabase.isPresent()) {
				return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
						.body("Event with ID " + eventId + " not found.");
			}

			EventDetails newEventDetails = eventDetailsFromDatabase.get();

			if (statusReport != null) {
				filesManager.deleteFile(newEventDetails.getStatusReport());
				String newStatusReport = filesManager.uploadFile(statusReport, "statusReport");
				newEventDetails.setStatusReport(newStatusReport);
			}

			if (architectsLetter != null) {
				filesManager.deleteFile(newEventDetails.getArchitectsLetter());
				String newArchitectsLetter = filesManager.uploadFile(architectsLetter, "architectsLetter");
				newEventDetails.setArchitectsLetter(newArchitectsLetter);
			}

			if (invoice != null) {
				filesManager.deleteFile(newEventDetails.getInvoice());
				String newInvoice = filesManager.uploadFile(invoice, "invoice");
				newEventDetails.setInvoice(newInvoice);
			}

			if (receipt != null) {
				filesManager.deleteFile(newEventDetails.getReceipt());
				String newReceipt = filesManager.uploadFile(receipt, "receipt");
				newEventDetails.setReceipt(newReceipt);
			}

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(eventDetails);

			if (jsonNode.has("salesPersonId") && !jsonNode.get("salesPersonId").isNull()) {
				newEventDetails.setSalesPersonId(jsonNode.get("salesPersonId").asLong());
			}
			if (jsonNode.has("flatId") && !jsonNode.get("flatId").isNull()) {
				newEventDetails.setFlatId(jsonNode.get("flatId").asLong());
			}
			if (jsonNode.has("propertyName") && !jsonNode.get("propertyName").isNull()
					&& !jsonNode.get("propertyName").asText().isBlank()) {
				newEventDetails.setPropertyName(jsonNode.get("propertyName").asText());
			}
			if (jsonNode.has("percentage") && !jsonNode.get("percentage").isNull()) {
				newEventDetails.setPercentage(jsonNode.get("percentage").asDouble());
			}
			if (jsonNode.has("basePriceAmount") && !jsonNode.get("basePriceAmount").isNull()) {
				newEventDetails.setBasePriceAmount(jsonNode.get("basePriceAmount").asDouble());
			}
			if (jsonNode.has("gstAmount") && !jsonNode.get("gstAmount").isNull()) {
				newEventDetails.setGstAmount(jsonNode.get("gstAmount").asDouble());
			}
			if (jsonNode.has("invoiceDate") && !jsonNode.get("invoiceDate").isNull()) {
				newEventDetails.setInvoiceDate(jsonNode.get("invoiceDate").asLong());
			}
			if (jsonNode.has("dueDate") && !jsonNode.get("dueDate").isNull()) {
				newEventDetails.setDueDate(jsonNode.get("dueDate").asLong());
			}
			if (jsonNode.has("paymentDate") && !jsonNode.get("paymentDate").isNull()) {
				newEventDetails.setPaymentDate(jsonNode.get("paymentDate").asLong());
			}
			if (jsonNode.has("paidByName") && !jsonNode.get("paidByName").isNull()
					&& !jsonNode.get("paidByName").asText().isBlank()) {
				newEventDetails.setPaidByName(jsonNode.get("paidByName").asText());
			}
			if (jsonNode.has("eventName") && !jsonNode.get("eventName").isNull()
					&& !jsonNode.get("eventName").asText().isBlank()) {
				newEventDetails.setPaidByName(jsonNode.get("eventName").asText());
			}

//			newEventDetails.setSalesPersonId(jsonNode.get("salesPersonId").asLong());
//			newEventDetails.setFlatId(jsonNode.get("flatId").asLong());
//			newEventDetails.setPropertyName(jsonNode.get("propertyName").asText());
//			newEventDetails.setPercentage(jsonNode.get("percentage").asDouble());
//			newEventDetails.setBasePriceAmount(jsonNode.get("basePriceAmount").asLong());
//			newEventDetails.setGstAmount(jsonNode.get("gstAmount").asLong());
//			newEventDetails.setInvoiceDate(jsonNode.get("invoiceDate").asLong());
//			newEventDetails.setDueDate(jsonNode.get("dueDate").asLong());
//			newEventDetails.setPaymentDate(jsonNode.get("paymentDate").asLong());
//			newEventDetails.setPaidByName(jsonNode.get("paidByName").asText());

			EventDetails updatedEventDetails = detailsRepository.save(newEventDetails);

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("eventDetails", updatedEventDetails);
			// Add file responses without duplication in eventDetails
			responseMap.put("statusReport", buildFileResponse(updatedEventDetails.getStatusReport()));
			responseMap.put("architectsLetter", buildFileResponse(updatedEventDetails.getArchitectsLetter()));
			responseMap.put("invoice", buildFileResponse(updatedEventDetails.getInvoice()));
			responseMap.put("receipt", buildFileResponse(updatedEventDetails.getReceipt()));

			return ResponseEntity.ok(responseMap);

		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

//Helper method to build a map with URL and fileName
	private Map<String, String> buildFileResponse(String fileUrl) {
		Map<String, String> fileDetails = new HashMap<>();
		if (fileUrl != null && !fileUrl.isEmpty()) {
			String[] parts = fileUrl.split("fileName=");
			String filePath = parts[1].split("&")[0];
			String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);

			fileDetails.put("url", fileUrl); 
			fileDetails.put("fileName", fileName);
		} else {
			fileDetails.put("url", "");
			fileDetails.put("fileName", "");
		}
		return fileDetails;
	}

	public ResponseEntity<?> getEventDetailsByIdAndCRManagerId(long eventId, long crManagerId) {
		try {

			EventDetails byIdAndCrManagerId = detailsRepository.findByEventIdAndCrManagerId(eventId, crManagerId);
			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("eventId", byIdAndCrManagerId.getEventId());
			responseMap.put("crManagerId", byIdAndCrManagerId.getCrManagerId());
			responseMap.put("salesPersonId", byIdAndCrManagerId.getSalesPersonId());
			responseMap.put("leadId", byIdAndCrManagerId.getLeadId());
			responseMap.put("eventName", byIdAndCrManagerId.getEventName());
			responseMap.put("flatId", byIdAndCrManagerId.getFlatId());
			responseMap.put("propertyName", byIdAndCrManagerId.getPropertyName());
			responseMap.put("percentage", byIdAndCrManagerId.getPercentage());
			responseMap.put("basePriceAmount", byIdAndCrManagerId.getBasePriceAmount());
			responseMap.put("gstAmount", byIdAndCrManagerId.getGstAmount());
			responseMap.put("invoiceDate", byIdAndCrManagerId.getInvoiceDate());
			responseMap.put("dueDate", byIdAndCrManagerId.getDueDate());
			responseMap.put("paymentDate", byIdAndCrManagerId.getPaymentDate());
			responseMap.put("paidByName", byIdAndCrManagerId.getPaidByName());
			responseMap.put("eventDetailsStatus", byIdAndCrManagerId.getEventDetailsStatus());
			responseMap.put("createdOn", byIdAndCrManagerId.getCreatedOn());
			responseMap.put("editedOn", byIdAndCrManagerId.getEditedOn());
			responseMap.put("statusReport", buildFileResponse(byIdAndCrManagerId.getStatusReport()));
			responseMap.put("architectsLetter", buildFileResponse(byIdAndCrManagerId.getArchitectsLetter()));
			responseMap.put("invoice", buildFileResponse(byIdAndCrManagerId.getInvoice()));
			responseMap.put("receipt", buildFileResponse(byIdAndCrManagerId.getReceipt()));

			return ResponseEntity.ok(responseMap);

		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> getEventDetailsByCRManagerId(long crManagerId) {
		try {
			List<EventDetails> byCrManagerIdFromDb = detailsRepository.findByCrManagerId(crManagerId);

			List<Map<String, Object>> responseList = new ArrayList<>();

			for (EventDetails byCrManagerId : byCrManagerIdFromDb) {
				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("eventId", byCrManagerId.getEventId());
				responseMap.put("crManagerId", byCrManagerId.getCrManagerId());
				responseMap.put("salesPersonId", byCrManagerId.getSalesPersonId());
				responseMap.put("leadId", byCrManagerId.getLeadId());
				responseMap.put("eventName", byCrManagerId.getEventName());
				responseMap.put("flatId", byCrManagerId.getFlatId());
				responseMap.put("propertyName", byCrManagerId.getPropertyName());
				responseMap.put("percentage", byCrManagerId.getPercentage());
				responseMap.put("basePriceAmount", byCrManagerId.getBasePriceAmount());
				responseMap.put("gstAmount", byCrManagerId.getGstAmount());
				responseMap.put("invoiceDate", byCrManagerId.getInvoiceDate());
				responseMap.put("dueDate", byCrManagerId.getDueDate());
				responseMap.put("paymentDate", byCrManagerId.getPaymentDate());
				responseMap.put("paidByName", byCrManagerId.getPaidByName());
				responseMap.put("eventDetailsStatus", byCrManagerId.getEventDetailsStatus());
				responseMap.put("createdOn", byCrManagerId.getCreatedOn());
				responseMap.put("editedOn", byCrManagerId.getEditedOn());
				responseMap.put("statusReport", buildFileResponse(byCrManagerId.getStatusReport()));
				responseMap.put("architectsLetter", buildFileResponse(byCrManagerId.getArchitectsLetter()));
				responseMap.put("invoice", buildFileResponse(byCrManagerId.getInvoice()));
				responseMap.put("receipt", buildFileResponse(byCrManagerId.getReceipt()));
				responseList.add(responseMap);
			}

			return ResponseEntity.ok(responseList);

		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> getEventDetailsByLeadId(String token, long leadId) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				System.out.println("Jwt expiration checking");
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"CRM".equalsIgnoreCase(role) && !"CLIENT".equalsIgnoreCase(role)) {
				System.out.println("role checking ");
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			List<EventDetails> byCrManagerIdFromDb = detailsRepository.findByLeadId(leadId);

			List<Map<String, Object>> responseList = new ArrayList<>();

			for (EventDetails byCrManagerId : byCrManagerIdFromDb) {
				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("eventId", byCrManagerId.getEventId());
				responseMap.put("crManagerId", byCrManagerId.getCrManagerId());
				responseMap.put("salesPersonId", byCrManagerId.getSalesPersonId());
				responseMap.put("leadId", byCrManagerId.getLeadId());
				responseMap.put("eventName", byCrManagerId.getEventName());
				responseMap.put("flatId", byCrManagerId.getFlatId());
				responseMap.put("propertyName", byCrManagerId.getPropertyName());
				responseMap.put("percentage", byCrManagerId.getPercentage());
				responseMap.put("basePriceAmount", byCrManagerId.getBasePriceAmount());
				responseMap.put("gstAmount", byCrManagerId.getGstAmount());
				responseMap.put("invoiceDate", byCrManagerId.getInvoiceDate());
				responseMap.put("dueDate", byCrManagerId.getDueDate());
				responseMap.put("paymentDate", byCrManagerId.getPaymentDate());
				responseMap.put("paidByName", byCrManagerId.getPaidByName());
				responseMap.put("eventDetailsStatus", byCrManagerId.getEventDetailsStatus());
				responseMap.put("createdOn", byCrManagerId.getCreatedOn());
				responseMap.put("editedOn", byCrManagerId.getEditedOn());
				responseMap.put("statusReport", buildFileResponse(byCrManagerId.getStatusReport()));
				responseMap.put("architectsLetter", buildFileResponse(byCrManagerId.getArchitectsLetter()));
				responseMap.put("invoice", buildFileResponse(byCrManagerId.getInvoice()));
				responseMap.put("receipt", buildFileResponse(byCrManagerId.getReceipt()));
				responseList.add(responseMap);
			}

			return ResponseEntity.ok(responseList);

		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> deleteDetailsById(String token, long eventId) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				System.out.println("Jwt expiration checking");
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"CRM".equalsIgnoreCase(role)) {
				System.out.println("role checking ");
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			detailsRepository.deleteById(eventId);
			return ResponseEntity.ok("data deleted successfully");
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> getEventDetailsByIdAndLeadId(long eventId, long leadId) {
		try {
			EventDetails byIdAndCrManagerId = detailsRepository.getEventDetailsByEventIdAndLeadId(eventId, leadId);
			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("eventId", byIdAndCrManagerId.getEventId());
			responseMap.put("crManagerId", byIdAndCrManagerId.getCrManagerId());
			responseMap.put("salesPersonId", byIdAndCrManagerId.getSalesPersonId());
			responseMap.put("leadId", byIdAndCrManagerId.getLeadId());
			responseMap.put("eventName", byIdAndCrManagerId.getEventName());
			responseMap.put("flatId", byIdAndCrManagerId.getFlatId());
			responseMap.put("propertyName", byIdAndCrManagerId.getPropertyName());
			responseMap.put("percentage", byIdAndCrManagerId.getPercentage());
			responseMap.put("basePriceAmount", byIdAndCrManagerId.getBasePriceAmount());
			responseMap.put("gstAmount", byIdAndCrManagerId.getGstAmount());
			responseMap.put("invoiceDate", byIdAndCrManagerId.getInvoiceDate());
			responseMap.put("dueDate", byIdAndCrManagerId.getDueDate());
			responseMap.put("paymentDate", byIdAndCrManagerId.getPaymentDate());
			responseMap.put("paidByName", byIdAndCrManagerId.getPaidByName());
			responseMap.put("eventDetailsStatus", byIdAndCrManagerId.getEventDetailsStatus());
			responseMap.put("createdOn", byIdAndCrManagerId.getCreatedOn());
			responseMap.put("editedOn", byIdAndCrManagerId.getEditedOn());
			responseMap.put("statusReport", buildFileResponse(byIdAndCrManagerId.getStatusReport()));
			responseMap.put("architectsLetter", buildFileResponse(byIdAndCrManagerId.getArchitectsLetter()));
			responseMap.put("invoice", buildFileResponse(byIdAndCrManagerId.getInvoice()));
			responseMap.put("receipt", buildFileResponse(byIdAndCrManagerId.getReceipt()));

			return ResponseEntity.ok(responseMap);

		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> deleteAllEventByClient(long clientId) {
		try {
			EventDetails deleteByEventId = detailsRepository.deleteAllByClientId(clientId);
			return ResponseEntity.ok(deleteByEventId);
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("Lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> sendMailToClient(long eventDetailsId, long clientId) {
		try {
			Optional<Client> clientOpt = clientRepository.findById(clientId);
			if (clientOpt.isEmpty()) {
				return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
						.body("Client not found with ID: " + clientId);
			}

			String clientEmail = clientOpt.get().getEmail();

			Optional<EventDetails> eventOpt = detailsRepository.findByEventIdAndClientId(eventDetailsId, clientId);
			if (eventOpt.isEmpty()) {
				return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
						.body("No matching EventDetails found for given client and event ID");
			}

			EventDetails event = eventOpt.get();

			String content = "<html><head><meta charset=\"UTF-8\"></head><body>" + "<h2>New Event Created</h2>"
					+ "<p>Dear " + clientEmail + ",</p>" + "<p>Your new event has been added for the property: <b>"
					+ event.getPropertyName() + "</b>.</p>" + "<p><b>Event Name:</b> " + event.getEventName() + "<br>"
					+ "<b>Base Price:</b> " + event.getBasePriceAmount() + "<br>" + "<b>GST:</b> "
					+ event.getGstAmount() + "<br>" + "<b>Invoice Date:</b> " + formatDate(event.getInvoiceDate())
					+ "<br>" + "<b>Due Date:</b> " + formatDate(event.getDueDate()) + "</p>"

					+ "<p><b>Documents:</b></p><ul>"
					+ (event.getStatusReport() != null && !event.getStatusReport().isBlank()
							? "<li>Status Report: <a href=\"" + event.getStatusReport() + "\">Download</a></li>"
							: "")
					+ (event.getArchitectsLetter() != null && !event.getArchitectsLetter().isBlank()
							? "<li>Architect's Letter: <a href=\"" + event.getArchitectsLetter()
									+ "\">Download</a></li>"
							: "")
					+ (event.getInvoice() != null && !event.getInvoice().isBlank()
							? "<li>Invoice: <a href=\"" + event.getInvoice() + "\">Download</a></li>"
							: "")
					+ (event.getReceipt() != null && !event.getReceipt().isBlank()
							? "<li>Receipt: <a href=\"" + event.getReceipt() + "\">Download</a></li>"
							: "")
					+ "</ul><p>Thank you!</p></body></html>";

			mailService.sendEmail(clientEmail, "Event Details Notification", content);

			return ResponseEntity.ok("Mail sent successfully to " + clientEmail);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	private String formatDate(long millis) {
		return millis > 0 ? new SimpleDateFormat("dd MMM yyyy").format(new Date(millis)) : "N/A";
	}
}
