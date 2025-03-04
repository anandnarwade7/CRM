package com.crm.leads;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.crm.Exception.Error;
import com.crm.notifications.Notifications;
import com.crm.notifications.NotificationsRepository;
import com.crm.security.JwtUtil;
import com.crm.user.Status;
import com.crm.user.User;
import com.crm.user.UserRepository;
import com.crm.user.UserServiceException;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class LeadService {

	@Autowired
	private LeadRepository repository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

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

			rowIterator.next();

			Map<String, Integer> columnMap = new HashMap<>();
			Row headerRow = sheet.getRow(0);
			for (Cell cell : headerRow) {
				columnMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
			}

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
//				String propertyRange = getCellValueAsString(row, columnMap.get("propertyrange"));
//				String callTime = getCellValueAsString(row, columnMap.get("calltime"));
				String ad = getCellValueAsString(row, columnMap.get("ad"));
				String adSet = getCellValueAsString(row, columnMap.get("adset"));
				String campaign = getCellValueAsString(row, columnMap.get("campaign"));
				String city = getCellValueAsString(row, columnMap.get("city"));
				String msg = getCellValueAsString(row, columnMap.get("conversation logs"));
				String fields = getCellValueAsString(row, columnMap.get("questions"));

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
//				client.setCallTime(callTime);
//				client.setPropertyRange(propertyRange);
				client.setMassagesJsonData(msg);
				client.setDynamicFieldsJson(fields);

				if (!assignedToList.isEmpty()) {

					client.setAssignedTo(assignedToList.get(index % assignedToSize));
					System.out.println("User id :: " + assignedToList.get(index % assignedToSize));
					User CrById = userRepository.findSalesById(client.getAssignedTo());
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
				assignLeadsToSaled();
			}

			assignedLeadCounts.forEach((salesUserId, leadCount) -> {
				System.out.println("Attempting to send notification for Sales User ID: " + salesUserId
						+ " with lead count: " + leadCount);

				User salesUser = userRepository.findById(salesUserId).orElse(null);
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

	private Status getStatusValue(String status) {
		if (status.equalsIgnoreCase("Converted")) {
			return Status.CONVERTED;
		} else {
			return Status.COMPLETED;
		}
	}

	public ResponseEntity<?> assignLeadsToSaled() {
		try {
			List<User> salesUsers = userRepository.findUsersByRole("CRM");
			List<LeadDetails> unassignedLeads = repository.findByAssignedTo();

			if (salesUsers.isEmpty() || unassignedLeads.isEmpty()) {
				return ResponseEntity.ok("No sales users or leads available for assignment.");
			}

			int totalSalesUsers = salesUsers.size();
			int totalLeads = unassignedLeads.size();
			int leadsPerUser = totalLeads / totalSalesUsers;
			int remainingLeads = totalLeads % totalSalesUsers;

			int leadIndex = 0;
			for (User salesUser : salesUsers) {
				for (int j = 0; j < leadsPerUser; j++) {
					unassignedLeads.get(leadIndex).setAssignedTo(salesUser.getId());
					leadIndex++;
				}
			}

			for (int i = 0; i < remainingLeads; i++) {
				unassignedLeads.get(leadIndex).setAssignedTo(salesUsers.get(i % totalSalesUsers).getId());
				unassignedLeads.get(leadIndex).setCrPerson(salesUsers.get(i % totalSalesUsers).getName());
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
			Notifications notification = new Notifications(false, message, salesUser.getEmail(), message,
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

}
