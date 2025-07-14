package com.crm.leads;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.crm.Exception.Error;
import com.crm.fileHandler.FilesManager;
import com.crm.importLead.ImportLead;
import com.crm.importLead.ImportLeadRepository;
import com.crm.mailservice.MailService;
import com.crm.notifications.Notifications;
import com.crm.notifications.NotificationsRepository;
import com.crm.security.JwtUtil;
import com.crm.user.Admins;
import com.crm.user.AdminsRepository;
import com.crm.user.Client;
import com.crm.user.ClientRepository;
import com.crm.user.Status;
import com.crm.user.User;
import com.crm.user.UserRepository;
import com.crm.user.UserServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class LeadService {

	@Autowired
	private LeadRepository repository;

	@Autowired
	private ImportLeadRepository leadRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private FilesManager fileManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AdminsRepository adminRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private NotificationsRepository notificationsRepository;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public ResponseEntity<?> readLeadsFromExcel(String token, long userId, List<Long> assignedTo, MultipartFile file) {
		int processedCount = 0;
		int skippedCount = 0;

		if (jwtUtil.isTokenExpired(token)) {
			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
					.body("Unauthorized: Your session has expired.");
		}

		String role = jwtUtil.extractRole(token);

		if (!"ADMIN".equalsIgnoreCase(role)) {
			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
					.body("Forbidden: You do not have the necessary permissions.");
		}

		try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			if (!rowIterator.hasNext()) {
				throw new UserServiceException(400, "Uploaded file does not contain any data.");
			}

			rowIterator.next(); // Skip header row

			Map<String, Integer> columnMap = new HashMap<>();
			Row headerRow = sheet.getRow(0);
			for (Cell cell : headerRow) {
				columnMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
			}

			// Check if ad, adset, and campaign columns exist
			boolean hasAd = columnMap.containsKey("ad");
			boolean hasAdSet = columnMap.containsKey("adset");
			boolean hasCampaign = columnMap.containsKey("campaign");

			List<LeadDetails> leadsToSave = new ArrayList<>();
			List<Long> assignedToList = (assignedTo != null) ? new ArrayList<>(assignedTo) : new ArrayList<>();
			int assignedToSize = assignedToList.size();
			int index = 0;

			Map<Long, Integer> assignedLeadCounts = new HashMap<>();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				String name = getCellValueAsString(row, columnMap.get("name"));
				String email = getCellValueAsString(row, columnMap.get("email"));
				String mobileNumber = getCellValueAsString(row, columnMap.get("mobile number"));
				String status = getCellValueAsString(row, columnMap.get("status"));
				String city = getCellValueAsString(row, columnMap.get("city"));
				String msg = getCellValueAsString(row, columnMap.get("conversation logs"));
				String fields = getCellValueAsString(row, columnMap.get("questions"));

				String ad = hasAd ? getCellValueAsString(row, columnMap.get("ad")) : null;
				String adSet = hasAdSet ? getCellValueAsString(row, columnMap.get("adset")) : null;
				String campaign = hasCampaign ? getCellValueAsString(row, columnMap.get("campaign")) : null;

//	            boolean isDuplicate;
//	            if (hasAd && hasAdSet && hasCampaign) {
//	            	System.err.println("Check Point 1 ");
//	                isDuplicate = repository.existsByLeadEmailAndAdNameAndAdSetAndCampaignAndCity(email, ad, adSet, campaign, city);
//	            } else {
//	            	System.err.println("Check Point 2 ");
//	                isDuplicate = repository.existsByLeadEmailAndCity(email, city);
//	            }
//
//	            if (isDuplicate) {
//	                System.out.println("Duplicate entry skipped: " + email + ", " + city);
//	                skippedCount++;
//	                continue;
//	            }

				boolean isDuplicate = repository.existsByLeadEmailAndAdNameAndAdSetAndCampaignAndCity(email, ad, adSet,
						campaign, city);
				if (isDuplicate) {
					System.out.println("Duplicate entry skipped: " + ad + ", " + adSet + ", " + campaign + ", " + city);
					skippedCount++;
					continue;
				}

				LeadDetails client = new LeadDetails();
				client.setLeadName(name);
				client.setLeadEmail(email);
				client.setLeadmobile(mobileNumber);
				client.setDate(System.currentTimeMillis());
				client.setUserId(userId);
				client.setStatus(getStatusValue(status));
				client.setAdName(ad);
				client.setAdSet(adSet);
				client.setCampaign(campaign);
				client.setCity(city);
				client.setMassagesJsonData(parseConversationLogs(msg));
				client.setDynamicFieldsJson(fields);

//				client.setConversationLogs(parseConversationLogs(msg));

				if (!assignedToList.isEmpty()) {
					client.setAssignedTo(assignedToList.get(index % assignedToSize));
					User CrById = userRepository.findSalesById(assignedToList.get(index % assignedToSize));
					System.out.println("User found :: " + CrById);
					client.setCrPerson(CrById.getName());
					index++;

					assignedLeadCounts.put(assignedToList.get(index % assignedToSize),
							assignedLeadCounts.getOrDefault(assignedToList.get(index % assignedToSize), 0) + 1);
				}

				leadsToSave.add(client);
				processedCount++;
			}

			repository.saveAll(leadsToSave);
			if (assignedToList.isEmpty()) {
				assignLeadsToCRM();
			}

			assignedLeadCounts.forEach((crUserId, leadCount) -> {
				System.out.println("Attempting to send notification for CR User ID: " + crUserId + " with lead count: "
						+ leadCount);
				User salesUser = userRepository.findById(crUserId).orElse(null);
				if (salesUser != null) {
					String message = leadCount + " leads assigned to you.";
					sendNotification(salesUser, message);
				}
			});

			return ResponseEntity
					.ok("File processed successfully. Processed: " + processedCount + ", Skipped: " + skippedCount);

		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
		}
	}

	public ResponseEntity<?> assignConvertedLeads(String token, long userId, List<Long> assignedTo,
			List<Long> leadIds) {
		try {
			int processedCount = 0;
			int skippedCount = 0;

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			String email = userClaims.get("email");

			System.out.println("User Role: " + role + ", Email: " + email);

			if (!"ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			List<LeadDetails> leadsToSave = new ArrayList<>();
			Map<Long, Long> assignedLeadCounts = new HashMap<>();

			Admins admin = adminRepository.findByEmail(email);
			if (assignedTo != null && !assignedTo.isEmpty()) {
				for (Long userIdFromList : assignedTo) {
					long leadCount = repository.countLeadsByAssignedTo(userIdFromList);
					assignedLeadCounts.put(userIdFromList, leadCount);
				}
			} else {
				List<User> crmUsers = userRepository.getByRoleAndUserId("CRM", admin.getId());
				crmUsers.forEach(user -> {
					long leadCount = repository.countLeadsByAssignedTo(user.getId());
					assignedLeadCounts.put(user.getId(), leadCount);
				});
			}

			for (Long id : leadIds) {

				ImportLead lead = leadRepository.findById(id)
						.orElseThrow(() -> new UserServiceException(409, "Data with given id is not found"));

				if (repository.existsByLeadEmailAndAdNameAndAdSetAndCampaignAndCity(lead.getEmail(), lead.getAdName(),
						lead.getAdSet(), lead.getCampaign(), lead.getCity())) {
					skippedCount++;
					continue;
				}

				LeadDetails client = new LeadDetails();
				client.setLeadName(lead.getName());
				client.setLeadEmail(lead.getEmail());
				client.setLeadmobile(lead.getMobileNumber());
				client.setDate(System.currentTimeMillis());
				client.setUserId(userId);
				client.setStatus(lead.getStatus());
				client.setAdName(lead.getAdName());
				client.setAdSet(lead.getAdSet());
				client.setCampaign(lead.getCampaign());
				client.setCity(lead.getCity());
				client.setMassagesJsonData(lead.getJsonData());
				client.setDynamicFieldsJson(lead.getDynamicFieldsJson());
				client.setSalesId(lead.getId());

				Long assignedUserId = assignedLeadCounts.entrySet().stream().min(Map.Entry.comparingByValue())
						.map(Map.Entry::getKey).orElse(null);

				if (assignedUserId != null) {
					client.setAssignedTo(assignedUserId);
					User CrById = userRepository.findSalesById(assignedUserId);
					client.setCrPerson(CrById.getName());

					assignedLeadCounts.put(assignedUserId, assignedLeadCounts.get(assignedUserId) + 1);
				}

				leadsToSave.add(client);
				processedCount++;
				lead.setConvertedClient(true);
			}

			repository.saveAll(leadsToSave);

			assignedLeadCounts.forEach((crUserId, leadCount) -> {
				User salesUser = userRepository.findById(crUserId).orElse(null);
				if (salesUser != null) {
					sendNotification(salesUser, leadCount + " leads assigned to you.");
				}
			});

			return ResponseEntity
					.ok("File processed successfully. Processed: " + processedCount + ", Skipped: " + skippedCount);

		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
		}
	}

//	private String parseConversationLogs(String msg) {
//	    if (msg == null || msg.trim().isEmpty()) {
//	        return "[]"; // Return empty JSON array if msg is null/empty
//	    }
//
//	    String[] logs = msg.split("\\r?\\n"); // Splitting messages by newline (\n or \r\n)
//	    List<Map<String, String>> conversationLogs = new ArrayList<>();
//
//	    for (String log : logs) {
//	        if (!log.trim().isEmpty()) { // Ignore empty lines
//	            Map<String, String> entry = new HashMap<>();
//	            entry.put("comment", log.trim());
//	            conversationLogs.add(entry);
//	        }
//	    }
//
//	    try {
//	        return new ObjectMapper().writeValueAsString(conversationLogs); // Convert list to JSON string
//	    } catch (JsonProcessingException e) {
//	        e.printStackTrace();
//	        return "[]"; // Return empty JSON if conversion fails
//	    }
//	}

	private Status getStatusValue(String status) {
		if (status.equalsIgnoreCase("Converted")) {
			return Status.CONVERTED;
		} else {
			return Status.COMPLETED;
		}
	}

	public ResponseEntity<?> assignLeadsToCRM() {
		try {
			List<User> crmUsers = userRepository.findUsersByRole("CRM");
			List<LeadDetails> unassignedLeads = repository.findByAssignedTo();

			if (crmUsers.isEmpty() || unassignedLeads.isEmpty()) {
				return ResponseEntity.ok("No sales users or leads available for assignment.");
			}

			int totalSalesUsers = crmUsers.size();
			int totalLeads = unassignedLeads.size();
			int leadsPerUser = totalLeads / totalSalesUsers;
			int remainingLeads = totalLeads % totalSalesUsers;

			int leadIndex = 0;
			for (User salesUser : crmUsers) {
				for (int j = 0; j < leadsPerUser; j++) {
					unassignedLeads.get(leadIndex).setAssignedTo(salesUser.getId());
					leadIndex++;
				}
			}

			for (int i = 0; i < remainingLeads; i++) {
				unassignedLeads.get(leadIndex).setAssignedTo(crmUsers.get(i % totalSalesUsers).getId());
				unassignedLeads.get(leadIndex).setCrPerson(crmUsers.get(i % totalSalesUsers).getName());
				leadIndex++;
			}

			List<LeadDetails> saveAll = repository.saveAll(unassignedLeads);

			return ResponseEntity.ok(saveAll);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
		}
	}

	private void sendNotification(User salesUser, String message) {
		try {
			Notifications notification = new Notifications(false, message, salesUser.getEmail(), "Client Details",
					System.currentTimeMillis());
			notificationsRepository.save(notification);
		} catch (Exception e) {
			throw new RuntimeException("Error saving dynamic fields", e);
		}
	}

	private void sendNotificationToClient(Client client, String message) {
		try {
			Notifications notification = new Notifications(false, message, client.getEmail(), "Client Details",
					System.currentTimeMillis());
			notificationsRepository.save(notification);
		} catch (Exception e) {
			throw new RuntimeException("Error saving dynamic fields", e);
		}
	}

	private String getCellValueAsString(Row row, int columnIndex) {
		Cell cell = row.getCell(columnIndex);
		if (cell == null)
			return "";

		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
			} else {
				return new BigDecimal(cell.getNumericCellValue()).toPlainString().replaceAll("\\.0$", "");
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		default:
			return "";
		}
	}

//	public ResponseEntity<?> importedClients(String token, int page, Status status) {
//		try {
//			if (token == null) {
//				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
//						.body("Unauthorized: No token provided.");
//			}
//			if (jwtUtil.isTokenExpired(token)) {
//				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
//						.body("Unauthorized: Your session has expired.");
//			}
//			Map<String, String> userClaims = jwtUtil.extractRole1(token);
//			String userRole = userClaims.get("role");
//			String email = userClaims.get("email");
//
//			System.out.println("User Role: " + userRole + ", Email: " + email);
//
//			Pageable pageable = PageRequest.of(page - 1, 10);
//			Page<LeadDetails> unassignedLeads = null;
//
//			if (!"CRM".equalsIgnoreCase(userRole) && !"ADMIN".equalsIgnoreCase(userRole)) {
//				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
//						.body("Forbidden: You do not have the necessary permissions.");
//			}
//
//			if (userRole == "CRM") {
//				User user = userRepository.findByEmail(email);
//				if (user == null) {
//					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CRM not found for email: " + email);
//				}
//				System.out.println("Status comming :: " + status);
//				unassignedLeads = repository.findByStatusAndAssignedToOrderByCreateOnDesc(status, user.getId(),
//						pageable);
//			} else if (userRole == "ADMIN") {
//				Admins admin = adminRepository.findByEmail(email);
//				if (admin == null) {
//					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CRM not found for email: " + email);
//				}
//				unassignedLeads = repository.findByStatusOrderByCreateOnDesc(status, pageable);
//			}
//
//			System.out.println("Leads found :: " + unassignedLeads.getContent().size());
//
//			if (unassignedLeads.isEmpty()) {
//				return ResponseEntity.ok("No clients found");
//			}
//			return ResponseEntity.ok(unassignedLeads);
//		} catch (UserServiceException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(
//					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
//		} catch (Exception ex) {
//			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
//		}
//	}

	public ResponseEntity<?> importedClients(String token, int page, Status status) {
		try {
			if (token == null || token.trim().isEmpty()) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: No token provided.");
			}

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String userRole = userClaims.get("role");
			String email = userClaims.get("email");

			System.out.println("User Role: " + userRole + ", Email: " + email);

			Pageable pageable = PageRequest.of(page - 1, 10);
			Page<LeadDetails> leadPage;

			if (!"CRM".equalsIgnoreCase(userRole) && !"ADMIN".equalsIgnoreCase(userRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			if ("CRM".equalsIgnoreCase(userRole)) {
				User user = userRepository.findByEmail(email);
				if (user == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CRM user not found for email: " + email);
				}

				System.out.println("Fetching leads for CRM user...");
				leadPage = repository.findByStatusAndAssignedToOrderByCreateOnDesc(status, user.getId(), pageable);
			} else {
				Admins admin = adminRepository.findByEmail(email);
				if (admin == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found for email: " + email);
				}

				System.out.println("Fetching leads for ADMIN...");
				leadPage = repository.findByStatusAndUserIdOrderByCreateOnDesc(status, admin.getId(), pageable);
			}

			if (leadPage == null || leadPage.isEmpty()) {
				return ResponseEntity.ok(Collections.emptyMap());
			}

			return ResponseEntity.ok(leadPage);

		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to find data ", System.currentTimeMillis()));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error: " + ex.getMessage());
		}
	}

	public ResponseEntity<?> getClientById(String token, long id) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"CRM".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Optional<LeadDetails> byId = repository.findById(id);
			if (byId.isPresent()) {
				LeadDetails client = byId.get();
				return ResponseEntity.ok(client);
			} else {
				throw new UserServiceException(401, "Lead not exists");
			}
		} catch (ExpiredJwtException e) {
			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
					.body("Unauthorized: Your session has expired.");
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("User not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> getClientsByCrmId(String token, long userId, int page) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"CRM".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Pageable pageable = PageRequest.of(page - 1, 10);
			Page<LeadDetails> assignedLeads = repository.findByAssignedToOrderByCreateOnDesc(userId, pageable);
			if (!assignedLeads.isEmpty()) {
				return ResponseEntity.ok(assignedLeads);
			} else {
				return ResponseEntity.ok(assignedLeads);
			}
		} catch (ExpiredJwtException e) {
			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
					.body("Unauthorized: Your session has expired.");
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	@Transactional
	public ResponseEntity<?> addConversationLogAndDynamicField(Long clientId, Status status, String comment,
			long dueDate, List<String> key, List<Object> value) {

		LeadDetails client = repository.findById(clientId)
				.orElseThrow(() -> new RuntimeException("Clients not found with ID: " + clientId));

		if (comment != null && !comment.trim().isEmpty()) {
			List<Map<String, String>> logs = getConversationLogs(client);

			if (logs == null || logs.isEmpty()) {
				logs = new ArrayList<>();
			}

			String[] commentLines = comment.split("\\r?\\n");
//		String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

			for (String line : commentLines) {
				if (!line.trim().isEmpty()) {
					Map<String, String> logEntry = new HashMap<>();
//					logEntry.put("date", String.valueOf(System.currentTimeMillis()));
					logEntry.put("comment", comment);
					if (dueDate != 0) {
						String due = Long.toString(dueDate);
						logEntry.put("dueDate", due);
					}
					logs.add(logEntry);
				}
			}

			try {
				client.setMassagesJsonData(objectMapper.writeValueAsString(logs));
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Error saving conversation logs", e);
			}
		}

		if (key != null && value != null && key.size() == value.size()) {
//			Map<String, Object> fields = getDynamicFields(lead);
			List<Map<String, Object>> fieldsList = new ArrayList<>();
			for (int i = 0; i < key.size(); i++) {
//				fields.put(key.get(i), value.get(i));
				Map<String, Object> fieldEntry = new HashMap<>();
				fieldEntry.put(key.get(i), value.get(i));
				fieldsList.add(fieldEntry);
			}

			try {
				client.setDynamicFieldsJson(objectMapper.writeValueAsString(fieldsList));
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Error saving dynamic fields", e);
			}
		}

		try {
			if (status != null) {
				client.setStatus(status);
			}
			LeadDetails save = repository.save(client);
			return ResponseEntity.ok(save);
		} catch (Exception e) {
			throw new RuntimeException("Error saving lead data", e);
		}
	}

	private String parseConversationLogs(String msg) {
		try {
			if (msg == null || msg.trim().isEmpty()) {
				return "[]";
			}

			String[] logs = msg.split("\\r?\\n");
			List<Map<String, String>> conversationLogs = new ArrayList<>();

			for (String log : logs) {
				log = log.trim();
				if (!log.isEmpty()) {
					Map<String, String> entry = new HashMap<>();
					entry.put("comment", log);
					conversationLogs.add(entry);
				}
			}

			return new ObjectMapper().writeValueAsString(conversationLogs);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "[]";
		}
	}

	public List<Map<String, String>> getConversationLogs(LeadDetails client) {
		try {
			if (client.getMassagesJsonData() == null || client.getMassagesJsonData().isEmpty()) {
				return new ArrayList<>();
			}
			return objectMapper.readValue(client.getMassagesJsonData(), new TypeReference<List<Map<String, String>>>() {
			});
		} catch (JsonProcessingException e) {
			return new ArrayList<>();
		}
	}

	public ResponseEntity<?> getTotalCountsOfLeads(String token, Long userId) {
		try {
			if (token == null) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: No token provided.");
			}

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"CRM".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			long assignedLeads = repository.countByAssignedTo(userId);
			long convertedLeads = repository.countByAssignedToAndStatusIsNot(userId, Status.CONVERTED);

			Map<String, Long> response = Map.of("totalLeads", assignedLeads, "convertedLeads", convertedLeads);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user details: " + e.getMessage());
		}
	}

	private String extractFileName(String fileUrl) {
		return fileUrl.substring(fileUrl.lastIndexOf("=") + 1).replaceAll("^[0-9]+", "");
	}

	public ResponseEntity<?> uploadDocs(String token, long id, MultipartFile agreement, MultipartFile stampDuty,
			MultipartFile tdsDoc, MultipartFile bankSanction) {
		try {
			if (token == null) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: No token provided.");
			}

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			String email = userClaims.get("email");

			if (!"CRM".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			User user = userRepository.findByEmail(email);
			LeadDetails leadData = repository.findById(id)
					.orElseThrow(() -> new UserServiceException(409, "leads data not found for given id" + id));
			if (leadData != null) {
				String agreementPath = fileManager.uploadFile(agreement);
				String stampDutyPath = fileManager.uploadFile(stampDuty);
				String tdsDocPath = fileManager.uploadFile(tdsDoc);
				String bankSanctionPath = fileManager.uploadFile(bankSanction);

//				leadData.setAgreement(agreementPath);
//				leadData.setStampDuty(stampDutyPath);
//				leadData.setTdsDoc(tdsDocPath);
//				leadData.setBankSanction(bankSanctionPath);

				leadData.setAgreement(
						(agreementPath != null && !agreementPath.isEmpty()) ? agreementPath : leadData.getAgreement());
				leadData.setStampDuty(
						(stampDutyPath != null && !stampDutyPath.isEmpty()) ? stampDutyPath : leadData.getStampDuty());
				leadData.setTdsDoc((tdsDocPath != null && !tdsDocPath.isEmpty()) ? tdsDocPath : leadData.getTdsDoc());
				leadData.setBankSanction((bankSanctionPath != null && !bankSanctionPath.isEmpty()) ? bankSanctionPath
						: leadData.getBankSanction());

				LeadDetails leadDetails = repository.save(leadData);
				Client client = clientRepository.findByEmail(leadData.getLeadEmail());
				if (client != null) {
					sendNotificationToClient(client,
							"Documents uploaded by " + user.getName() + " (" + user.getRole() + ")" + " please check");

					String clientEmail = client.getEmail();
					String subject = "Property Documents Uploaded for Review";
					String message = "Dear " + client.getName() + ",<br><br>"
							+ "The following property documents have been uploaded by " + user.getName() + " ("
							+ user.getRole() + "). Please review them:<br><br>" + "<ul>" + "<li><b>Agreement:</b> "
							+ extractFileName(leadDetails.getAgreement()) + " - <a href='" + leadDetails.getAgreement()
							+ "'>Download</a></li>" + "<li><b>Stamp Duty:</b> "
							+ extractFileName(leadDetails.getStampDuty()) + " - <a href='" + leadDetails.getStampDuty()
							+ "'>Download</a></li>" + "<li><b>TDS Document:</b> "
							+ extractFileName(leadDetails.getTdsDoc()) + " - <a href='" + leadDetails.getTdsDoc()
							+ "'>Download</a></li>" + "<li><b>Bank Sanction:</b> "
							+ extractFileName(leadDetails.getBankSanction()) + " - <a href='"
							+ leadDetails.getBankSanction() + "'>Download</a></li>" + "</ul>"
							+ "<br>Best regards,<br>CRM Team";

					mailService.sendEmail(clientEmail, subject, message);
				}
			}
			return ResponseEntity.ok(leadData);
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

	public ResponseEntity<?> getDataOfClientByCliectEmailToViewAndDownload(String token, long id, int page) {
		try {
			if (token == null) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: No token provided.");
			}

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			String email = userClaims.get("email");

			if (!"CLIENT".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			Client user = clientRepository.findByEmail(email);
			Pageable pageable = PageRequest.of(page - 1, 20);
			Page<LeadDetails> details = repository.findAllDataOfClientByLeadEmailOrderByCreateOnDesc(user.getEmail(),
					pageable);
			return ResponseEntity.ok(details);
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
		}
	}

}
