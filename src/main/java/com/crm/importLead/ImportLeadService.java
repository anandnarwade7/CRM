package com.crm.importLead;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class ImportLeadService {

	@Autowired
	private ImportLeadRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

//	@Autowired
//	private LeadService leadService;

	@Autowired
	private NotificationsRepository notificationsRepository;

//	private final String excelFilePath = "src/main/resources/Leads_Import_Tmplate.xlsx";

	private static final ObjectMapper objectMapper = new ObjectMapper();

//	public ResponseEntity<?> processFile(String adSetId, MultipartFile file, String type) {
//		System.out.println("Check Point 1 ");
////	    if (file.isEmpty()) {
////	    	System.out.println("Check Point 2 if file is empty ");
////	        return ResponseEntity.badRequest().body("Uploaded file is empty.");
////	    }
//
//		try (InputStream inputStream = file.getInputStream()) {
//			Workbook workbook = WorkbookFactory.create(inputStream);
//			Sheet sheet = workbook.getSheetAt(0);
//
//			if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
//				throw new UserServiceException(409,
//						"Uploaded file does not contain any data or Please Download Template");
//			}
//
//			Row firstRow = sheet.getRow(1);
//			Cell headerCell = firstRow.getCell(0);
//			System.out.println("Header Cell :: " + headerCell.getStringCellValue());
//			if (headerCell == null || !getCellValue(headerCell).equalsIgnoreCase("email")) {
//				throw new UserServiceException(409, "The first column must be 'Email'. Please Download Template");
//			}
//
//			int lastRowNum = sheet.getLastRowNum();
//
//			for (int i = 2; i <= lastRowNum; i++) {
//
//				Row row = sheet.getRow(i);
//
//				Cell emailCell = row.getCell(0);
////				if (emailCell == null || getCellValue(emailCell).isEmpty()) {
////					System.out.println("interation ::"+i);
////					throw new UserControllerException(409,
////							"Email column cannot be empty. Error at row: " + (row.getRowNum() + 1));
////				}
//
//				String email = getCellValue(emailCell);
//
//				String originalFilename = file.getOriginalFilename();
//
//				List<ExcludeInclude> allByAdSetId = excludeIncludeRepository.findAllByAdSetId(adSetId);
//
//				if (allByAdSetId.isEmpty()) {
//					ExcludeInclude newAdData = new ExcludeInclude();
//					newAdData.setEmail(email);
//					newAdData.setType(type);
//					newAdData.setAdSetId(adSetId);
//					newAdData.setFileName(originalFilename);
//					saveAdData(newAdData);
//					System.out.println("Added new record as adSetId was not present for email: " + email);
//				} else {
//					boolean recordExists = allByAdSetId.stream().anyMatch(
//							ex -> ex.getType().equalsIgnoreCase(type) && ex.getEmail().equalsIgnoreCase(email));
//					if (!recordExists) {
//						ExcludeInclude newAdData = new ExcludeInclude();
//						newAdData.setEmail(email);
//						newAdData.setType(type);
//						newAdData.setAdSetId(adSetId);
//						newAdData.setFileName(originalFilename);
//						saveAdData(newAdData);
//						System.out.println("Added new record for email: " + email + " under existing adSetId.");
//					}
//				}
//
////				ExcludeInclude adData = new ExcludeInclude();
////				adData.setEmail(email);
////				adData.setType(type);
////				adData.setAdSetId(adSetId);
////				adData.setFileName(originalFilename);
////
////				saveAdData(adData);
//			}
//
//			return ResponseEntity.ok("File processed successfully.");
//
//		} catch (UserServiceException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(
//					new Error(e.getStatusCode(), e.getMessage(), "Unable to create AdSet", System.currentTimeMillis()));
//		} catch (Exception ex) {
//			throw new UserServiceException(409, "Failed to Fetch File: " + ex.getMessage());
//		}
//	}
//
//	private String getCellValue(Cell cell) {
//		if (cell == null) {
//			return "";
//		}
//		switch (cell.getCellType()) {
//		case STRING:
//			return cell.getStringCellValue();
//		case NUMERIC:
//			if (isMobileNumberCell(cell)) {
//				return String.format("%.0f", cell.getNumericCellValue());
//			} else {
//				return String.valueOf(cell.getNumericCellValue());
//			}
//		case BOOLEAN:
//			return String.valueOf(cell.getBooleanCellValue());
//		default:
//			return "";
//		}
//	}
//
//	private boolean isMobileNumberCell(Cell cell) {
//		return true;
//	}

//	public ResponseEntity<?> readLeadsFromExcel(long userId, List<Long> assignedTo, MultipartFile file) {
//		int processedCount = 0;
//		int skippedCount = 0;
//
//		try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {
//
//			Sheet sheet = workbook.getSheetAt(0);
//			Iterator<Row> rowIterator = sheet.iterator();
//
//			if (rowIterator.hasNext()) {
//				rowIterator.next();
//			}
//
//			List<ImportLead> leadsToSave = new ArrayList<>();
//			List<Long> assignedToList = new ArrayList<>(assignedTo);
//			int assignedToSize = assignedToList.size();
//			int index = 0;
//
//			while (rowIterator.hasNext()) {
//				Row row = rowIterator.next();
//
//				String email = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
//				String mobileNumber = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
//				String name = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : "";
//				String propertyRange = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "";
//				String callTime = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : "";
//				String ad = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : "";
//				String adSet = row.getCell(7) != null ? row.getCell(7).getStringCellValue() : "";
//				String campaign = row.getCell(8) != null ? row.getCell(8).getStringCellValue() : "";
//				String city = row.getCell(9) != null ? row.getCell(9).getStringCellValue() : "";
//
//				boolean isDuplicate = repository.existsByAdNameAndAdSetAndCampaignAndCity(ad, adSet, campaign, city);
//				if (isDuplicate) {
//					System.out.println("Duplicate entry skipped: " + ad + ", " + adSet + ", " + campaign + ", " + city);
//					skippedCount++;
//					continue;
//				}
//
//				ImportLead lead = new ImportLead();
//				lead.setName(name);
//				lead.setEmail(email);
//				lead.setMobileNumber(mobileNumber);
//				lead.setDate(System.currentTimeMillis());
//				lead.setUserId(userId);
//				lead.setStatus(Status.PENDING);
//				lead.setAdName(ad);
//				lead.setAdSet(adSet);
//				lead.setCampaign(campaign);
//				lead.setCity(city);
//				lead.setCallTime(callTime);
//				lead.setPropertyRange(propertyRange);
//
//				lead.setAssignedTo(assignedToList.get(index % assignedToSize));
//				index++;
//
//				leadsToSave.add(lead);
//				processedCount++;
//			}
//
//			repository.saveAll(leadsToSave);
//			return ResponseEntity
//					.ok("File processed successfully. Processed: " + processedCount + ", Skipped: " + skippedCount);
//
//		} catch (UserServiceException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(
//					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
//		} catch (Exception ex) {
//			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
//		}
//	}

//	public ResponseEntity<?> readLeadsFromExcel(String token, long userId, List<Long> assignedTo, MultipartFile file) {
//		int processedCount = 0;
//		int skippedCount = 0;
//		if (jwtUtil.isTokenExpired(token)) {
//			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
//					.body("Unauthorized: Your session has expired.");
//		}
//
//		String role = jwtUtil.extractRole(token);
//
//		if (!"ADMIN".equalsIgnoreCase(role)) {
//			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
//					.body("Forbidden: You do not have the necessary permissions.");
//		}
//
//		try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {
//			Sheet sheet = workbook.getSheetAt(0);
//			Iterator<Row> rowIterator = sheet.iterator();
//
//			if (!rowIterator.hasNext()) {
//				throw new UserServiceException(400, "Uploaded file does not contain any data.");
//			}
//
//			rowIterator.next();
//
//			List<ImportLead> leadsToSave = new ArrayList<>();
//			List<Long> assignedToList = (assignedTo != null) ? new ArrayList<>(assignedTo) : new ArrayList<>();
//			int assignedToSize = assignedToList.size();
//
//			int index = 0;
//
//			Map<Long, Integer> assignedLeadCounts = new HashMap<>();
//
//			while (rowIterator.hasNext()) {
//				Row row = rowIterator.next();
//
//				Cell emailCell = row.getCell(1);
//				Cell mobileCell = row.getCell(2);
//				Cell nameCell = row.getCell(3);
//				Cell propertyRangeCell = row.getCell(4);
//				Cell callTimeCell = row.getCell(5);
//				Cell adCell = row.getCell(6);
//				Cell adSetCell = row.getCell(7);
//				Cell campaignCell = row.getCell(8);
//				Cell cityCell = row.getCell(9);
//
//				String email = getCellValueAsString(emailCell);
//				String mobileNumber = getCellValueAsString(mobileCell);
//				String name = getCellValueAsString(nameCell);
//				String propertyRange = getCellValueAsString(propertyRangeCell);
//				String callTime = getCellValueAsString(callTimeCell);
//				String ad = getCellValueAsString(adCell);
//				String adSet = getCellValueAsString(adSetCell);
//				String campaign = getCellValueAsString(campaignCell);
//				String city = getCellValueAsString(cityCell);
//
//				boolean isDuplicate = repository.existsByEmailAndAdNameAndAdSetAndCampaignAndCity(email, ad, adSet,
//						campaign, city);
//				if (isDuplicate) {
//					System.out.println("Duplicate entry skipped: " + ad + ", " + adSet + ", " + campaign + ", " + city);
//					skippedCount++;
//					continue;
//				}
//
//				ImportLead lead = new ImportLead();
//				lead.setName(name);
//				lead.setEmail(email);
//				lead.setMobileNumber(mobileNumber);
//				lead.setDate(System.currentTimeMillis());
//				lead.setUserId(userId);
//				lead.setStatus(Status.ASSIGNED);
//				lead.setAdName(ad);
//				lead.setAdSet(adSet);
//				lead.setCampaign(campaign);
//				lead.setCity(city);
//				lead.setCallTime(callTime);
//				lead.setPropertyRange(propertyRange);
//
//				if (!assignedToList.isEmpty()) {
////					Long assignedUserId = assignedToList.get(index % assignedToSize);
//					lead.setAssignedTo(assignedToList.get(index % assignedToSize));
//					User salesById = userRepository.findSalesById(assignedToList.get(index % assignedToSize));
//					System.out.println("User found :: " + salesById);
//					lead.setSalesPerson(salesById.getName());
//					index++;
//
//					Integer put = assignedLeadCounts.put(assignedToList.get(index % assignedToSize),
//							assignedLeadCounts.getOrDefault(assignedToList.get(index % assignedToSize), 0) + 1);
//					System.err.println("Value update for send noti :: " + put);
//				}
//				System.out.println("Check point 1 :: " + index);
//				leadsToSave.add(lead);
//				processedCount++;
//			}
//
//			repository.saveAll(leadsToSave);
//			if (assignedToList.isEmpty()) {
//				assignLeadsToSaled();
//			}
//
//			assignedLeadCounts.forEach((salesUserId, leadCount) -> {
//				System.out.println("Attempting to send notification for Sales User ID: " + salesUserId
//						+ " with lead count: " + leadCount);
//
//				User salesUser = userRepository.findById(salesUserId).orElse(null);
//				if (salesUser != null) {
//					String message = leadCount + " leads assigned to you.";
//					System.out.println("Sending Notification: " + message + " to " + salesUser.getName());
//					sendNotification(salesUser, message);
//				} else {
//					System.out.println("Sales User not found for ID: " + salesUserId);
//				}
//			});
//			return ResponseEntity
//					.ok("File processed successfully. Processed: " + processedCount + ", Skipped: " + skippedCount);
//
//		} catch (UserServiceException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(
//					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
//		}
//	}
//
//	private String getCellValueAsString(Cell cell) {
//		if (cell == null) {
//			return "";
//		}
//		try {
//			switch (cell.getCellType()) {
//			case STRING:
//				return cell.getStringCellValue().trim(); 
//			case NUMERIC:
//				if (DateUtil.isCellDateFormatted(cell)) {
//					return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
//				} else {
//					return new BigDecimal(cell.getNumericCellValue()).toPlainString();
//				}
//			case BOOLEAN:
//				return String.valueOf(cell.getBooleanCellValue());
//			case FORMULA:
//				try {
//					return cell.getStringCellValue(); 
//				} catch (IllegalStateException e) {
//					return String.valueOf(cell.getNumericCellValue());
//				}
//			case BLANK:
//				return "";
//			default:
//				return cell.toString(); 
//			}
//		} catch (Exception e) {
//		}
//	}

	/// ???? working
//	public ResponseEntity<?> readLeadsFromExcel(String token, long userId, List<Long> assignedTo, MultipartFile file) {
//		int processedCount = 0;
//		int skippedCount = 0;
//
//		if (jwtUtil.isTokenExpired(token)) {
//			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
//					.body("Unauthorized: Your session has expired.");
//		}
//
//		String role = jwtUtil.extractRole(token);
//
//		if (!"ADMIN".equalsIgnoreCase(role)) {
//			return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
//					.body("Forbidden: You do not have the necessary permissions.");
//		}
//
//		try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {
//			Sheet sheet = workbook.getSheetAt(0);
//			Iterator<Row> rowIterator = sheet.iterator();
//
//			if (!rowIterator.hasNext()) {
//				throw new UserServiceException(400, "Uploaded file does not contain any data.");
//			}
//
//			rowIterator.next();
//
//			Map<String, Integer> columnMap = new HashMap<>();
//			Row headerRow = sheet.getRow(0);
//			for (Cell cell : headerRow) {
//				columnMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
//			}
//
//			List<ImportLead> leadsToSave = new ArrayList<>();
//			List<Long> assignedToList = (assignedTo != null) ? new ArrayList<>(assignedTo) : new ArrayList<>();
//			int assignedToSize = assignedToList.size();
//			int index = 0;
//
//			Map<Long, Integer> assignedLeadCounts = new HashMap<>();
//
//			while (rowIterator.hasNext()) {
//				Row row = rowIterator.next();
//
//				String email = getCellValueAsString(row, columnMap.get("email"));
//				String mobileNumber = getCellValueAsString(row, columnMap.get("mobilenumber"));
//				String name = getCellValueAsString(row, columnMap.get("name"));
//				String propertyRange = getCellValueAsString(row, columnMap.get("propertyrange"));
//				String callTime = getCellValueAsString(row, columnMap.get("calltime"));
//				String ad = getCellValueAsString(row, columnMap.get("ad"));
//				String adSet = getCellValueAsString(row, columnMap.get("adset"));
//				String campaign = getCellValueAsString(row, columnMap.get("campaign"));
//				String city = getCellValueAsString(row, columnMap.get("city"));
//
//				boolean isDuplicate = repository.existsByEmailAndAdNameAndAdSetAndCampaignAndCity(email, ad, adSet,
//						campaign, city);
//				if (isDuplicate) {
//					System.out.println("Duplicate entry skipped: " + ad + ", " + adSet + ", " + campaign + ", " + city);
//					skippedCount++;
//					continue;
//				}
//
//				ImportLead lead = new ImportLead();
//				lead.setName(name);
//				lead.setEmail(email);
//				lead.setMobileNumber(mobileNumber);
//				lead.setDate(System.currentTimeMillis());
//				lead.setUserId(userId);
//				lead.setStatus(Status.ASSIGNED);
//				lead.setAdName(ad);
//				lead.setAdSet(adSet);
//				lead.setCampaign(campaign);
//				lead.setCity(city);
//				lead.setCallTime(callTime);
//				lead.setPropertyRange(propertyRange);
//
//				if (!assignedToList.isEmpty()) {
//
//					lead.setAssignedTo(assignedToList.get(index % assignedToSize));
//					System.out.println("User id :: " + assignedToList.get(index % assignedToSize));
//					User salesById = userRepository.findSalesById(lead.getAssignedTo());
//					System.out.println("User found :: " + salesById);
//					lead.setSalesPerson(salesById.getName());
//					index++;
//
//					assignedLeadCounts.put(assignedToList.get(index % assignedToSize),
//							assignedLeadCounts.getOrDefault(assignedToList.get(index % assignedToSize), 0) + 1);
//				}
//
//				leadsToSave.add(lead);
//				processedCount++;
//			}
//
//			repository.saveAll(leadsToSave);
//			if (assignedToList.isEmpty()) {
//				assignLeadsToSales();
//			}
//
//			assignedLeadCounts.forEach((salesUserId, leadCount) -> {
//				System.out.println("Attempting to send notification for Sales User ID: " + salesUserId
//						+ " with lead count: " + leadCount);
//
//				User salesUser = userRepository.findById(salesUserId).orElse(null);
//				if (salesUser != null) {
//					String message = leadCount + " leads assigned to you.";
//					sendNotification(salesUser, message);
//				}
//			});
//
//			return ResponseEntity
//					.ok("File processed successfully. Processed: " + processedCount + ", Skipped: " + skippedCount);
//
//		} catch (UserServiceException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(
//					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
//		}
//	}

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

			boolean hasAdColumns = columnMap.containsKey("ad") && columnMap.containsKey("adset")
					&& columnMap.containsKey("campaign") && columnMap.containsKey("city");

			List<ImportLead> leadsToSave = new ArrayList<>();
			List<Long> assignedToList = (assignedTo != null) ? new ArrayList<>(assignedTo) : new ArrayList<>();
			int assignedToSize = assignedToList.size();
			int index = 0;
			Map<Long, Integer> assignedLeadCounts = new HashMap<>();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				String email = getCellValueAsString(row, columnMap.get("email"));
				String mobileNumber = getCellValueAsString(row, columnMap.get("mobilenumber"));
				String name = getCellValueAsString(row, columnMap.get("name"));
				String propertyRange = getCellValueAsString(row, columnMap.get("propertyrange"));
				String callTime = getCellValueAsString(row, columnMap.get("calltime"));
				String city = getCellValueAsString(row, columnMap.get("city"));

				String ad = hasAdColumns ? getCellValueAsString(row, columnMap.get("ad")) : null;
				String adSet = hasAdColumns ? getCellValueAsString(row, columnMap.get("adset")) : null;
				String campaign = hasAdColumns ? getCellValueAsString(row, columnMap.get("campaign")) : null;

				boolean isDuplicate = hasAdColumns
						? repository.existsByEmailAndAdNameAndAdSetAndCampaignAndCity(email, ad, adSet, campaign, city)
						: repository.existsByEmailAndCity(email, city);

				if (isDuplicate) {
					System.out.println("Duplicate entry skipped: " + ad + ", " + adSet + ", " + campaign + ", " + city);
					skippedCount++;
					continue;
				}

				ImportLead lead = new ImportLead();
				lead.setName(name);
				lead.setEmail(email);
				lead.setMobileNumber(mobileNumber);
				lead.setDate(System.currentTimeMillis());
				lead.setUserId(userId);
				lead.setStatus(Status.ASSIGNED);
				lead.setCity(city);
				lead.setCallTime(callTime);
				lead.setPropertyRange(propertyRange);

				if (hasAdColumns) {
					lead.setAdName(ad);
					lead.setAdSet(adSet);
					lead.setCampaign(campaign);
				}

				if (!assignedToList.isEmpty()) {
					lead.setAssignedTo(assignedToList.get(index % assignedToSize));
					System.out.println("User id :: " + assignedToList.get(index % assignedToSize));
					User salesById = userRepository.findSalesById(lead.getAssignedTo());

					if (salesById != null) {
						System.out.println("User found :: " + salesById);
						lead.setSalesPerson(salesById.getName());
					} else {
						System.out.println("No user found for ID: " + assignedToList.get(index % assignedToSize));
						lead.setSalesPerson("Unknown");
					}

					index++;
					assignedLeadCounts.put(assignedToList.get(index % assignedToSize),
							assignedLeadCounts.getOrDefault(assignedToList.get(index % assignedToSize), 0) + 1);
				}

				leadsToSave.add(lead);
				processedCount++;
			}

			repository.saveAll(leadsToSave);
			if (assignedToList.isEmpty()) {
				assignLeadsToSales();
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

	public ResponseEntity<?> assignLeadsToSales() {
		try {
			List<User> salesUsers = userRepository.findUsersByRole("SALES");
			List<ImportLead> unassignedLeads = repository.findLeadsWhereAssignedToIsZero();

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
				unassignedLeads.get(leadIndex).setSalesPerson(salesUsers.get(i % totalSalesUsers).getName());
				leadIndex++;
			}

			List<ImportLead> saveAll = repository.saveAll(unassignedLeads);

			return ResponseEntity.ok(saveAll);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
		}
	}

	public ResponseEntity<?> assignLeads(String token, int page, Status status) {
		try {
			if (token == null) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: No token provided.");
			}

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String adminRole = jwtUtil.extractRole(token);
			if (!"ADMIN".equalsIgnoreCase(adminRole) && !"SALES".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Pageable pageable = PageRequest.of(page - 1, 10);
			Page<ImportLead> unassignedLeads = null;
			System.out.println("Status comming :: " + status);
			unassignedLeads = repository.findByStatusOrderByImportedOnDesc(status, pageable);
			System.out.println("Leads found :: " + unassignedLeads.getContent().size());

			if (unassignedLeads.isEmpty()) {
				return ResponseEntity.ok("No leads found");
			}
			return ResponseEntity.ok(unassignedLeads);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
		}
	}

	public ResponseEntity<?> getLeadsById(String token, long id) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"SALES".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Optional<ImportLead> byId = repository.findById(id);
			if (byId.isPresent()) {
				ImportLead importLead = byId.get();
				return ResponseEntity.ok(importLead);
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

	public ResponseEntity<?> getLeadsBysalesId(String token, long userId, int page) {
		try {
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"SALES".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Pageable pageable = PageRequest.of(page - 1, 10);
			Page<ImportLead> assignedLeads = repository.findByAssignedToOrderByImportedOnDesc(userId, pageable);
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
	public ImportLead addConversationLog(Long leadId, String date, String comment) {
		ImportLead lead = repository.findById(leadId)
				.orElseThrow(() -> new RuntimeException("Lead not found with ID: " + leadId));

		List<Map<String, String>> logs = getConversationLogs(lead);

		Map<String, String> logEntry = new HashMap<>();
		logEntry.put("date", date);
		logEntry.put("comment", comment);

		logs.add(logEntry);

		try {
			lead.setJsonData(objectMapper.writeValueAsString(logs));
			ImportLead leads = repository.save(lead);
			return leads;
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error saving conversation logs", e);
		}
	}

	public List<Map<String, String>> getConversationLogs(ImportLead lead) {
		if (lead.getJsonData() == null || lead.getJsonData().isEmpty()) {
			return new ArrayList<>();
		}
		try {
			return objectMapper.readValue(lead.getJsonData(), new TypeReference<List<Map<String, String>>>() {
			});
		} catch (JsonProcessingException e) {
			return new ArrayList<>();
		}
	}

	@Transactional
	public ImportLead addDynamicField(Long leadId, String key, Object value) {
		ImportLead lead = repository.findById(leadId)
				.orElseThrow(() -> new RuntimeException("Lead not found with ID: " + leadId));

		Map<String, Object> fields = getDynamicFields(lead);

		fields.put(key, value);

		try {
			lead.setDynamicFieldsJson(objectMapper.writeValueAsString(fields));
			ImportLead leads = repository.save(lead);
			return leads;

		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error saving dynamic fields", e);
		}
	}

	public Map<String, Object> getDynamicFields(ImportLead lead) {
		if (lead.getDynamicFieldsJson() == null || lead.getDynamicFieldsJson().isEmpty()) {
			return new HashMap<>();
		}
		try {
			return objectMapper.readValue(lead.getDynamicFieldsJson(), new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonProcessingException e) {
			return new HashMap<>();
		}
	}

	@Transactional
	public ImportLead addConversationLogAndDynamicField(Long leadId, Status status, String comment, List<String> key,
			List<Object> value) {

		ImportLead lead = repository.findById(leadId)
				.orElseThrow(() -> new RuntimeException("Lead not found with ID: " + leadId));

//		String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

		if (comment != null) {
			List<Map<String, String>> logs = getConversationLogs(lead);
			Map<String, String> logEntry = new HashMap<>();
//			logEntry.put("date", formattedDate);
			logEntry.put("comment", comment);

			logs.add(logEntry);
			try {
				lead.setJsonData(objectMapper.writeValueAsString(logs));
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
				lead.setDynamicFieldsJson(objectMapper.writeValueAsString(fieldsList));
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Error saving dynamic fields", e);
			}
		}

		try {
			if (status != null) {
				lead.setStatus(status);
			}
			return repository.save(lead);
		} catch (Exception e) {
			throw new RuntimeException("Error saving lead data", e);
		}
	}

	public ResponseEntity<?> updateLeadsToComplete(long leadId, Status status) {
		try {
			Optional<ImportLead> byId = repository.findById(leadId);
			if (!byId.isPresent()) {
				throw new UserServiceException(401, "User not exists");
			}
			ImportLead importLead = byId.get();
			importLead.setStatus(status);

			return ResponseEntity.ok(importLead);
		} catch (UserServiceException e) {
			return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body("lead not found: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					.body("Internal Server Error: " + e.getMessage());
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

			if (!"SALES".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			long assignedLeads = repository.countByAssignedTo(userId);
			long convertedLeads = repository.countLeadsByUserIdAndStatusNotAssigned(userId, Status.ASSIGNED);

			Map<String, Long> response = Map.of("totalLeads", assignedLeads, "convertedLeads", convertedLeads);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user details: " + e.getMessage());
		}
	}

	public byte[] downloadTemplateExcel() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("Leads_Import_Tmplate.xlsx");

		if (inputStream == null) {
			throw new FileNotFoundException("Template file not found");
		}
		return inputStream.readAllBytes();
	}

	public File getConvertedLeads() {
		try {
			List<ImportLead> convertedLeads = repository.findByStatus(Status.CONVERTED);

			return generateExcelForLeads(convertedLeads);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error generating Excel file: " + e.getMessage());
		}
	}

	public File generateExcelForLeads(List<ImportLead> leads) {
		File tempFile = null;
		try (XSSFWorkbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			XSSFSheet sheet = workbook.createSheet("Leads Data");

			List<String> baseColumns = Arrays.asList("name", "email", "mobile number", "status", "ad", "adset",
					"campaign", "city", "sales person", "questions", "conversation logs");

			XSSFRow headerRow = sheet.createRow(0);
			XSSFFont headerFont = workbook.createFont();
			headerFont.setBold(true);
			XSSFCellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFont(headerFont);

			Map<String, Integer> columnIndexMap = new HashMap<>();
			for (int i = 0; i < baseColumns.size(); i++) {
				columnIndexMap.put(baseColumns.get(i), i);
				XSSFCell cell = headerRow.createCell(i);
				cell.setCellValue(baseColumns.get(i));
				cell.setCellStyle(headerStyle);
			}

			int rowNum = 1;
			for (ImportLead lead : leads) {
				XSSFRow dataRow = sheet.createRow(rowNum++);

				dataRow.createCell(columnIndexMap.get("name")).setCellValue(lead.getName());
				dataRow.createCell(columnIndexMap.get("email")).setCellValue(lead.getEmail());
				dataRow.createCell(columnIndexMap.get("mobile number")).setCellValue(lead.getMobileNumber());
				dataRow.createCell(columnIndexMap.get("status")).setCellValue(lead.getStatus().toString());
				dataRow.createCell(columnIndexMap.get("ad")).setCellValue(lead.getAdName());
				dataRow.createCell(columnIndexMap.get("adset")).setCellValue(lead.getAdName());
				dataRow.createCell(columnIndexMap.get("campaign")).setCellValue(lead.getCampaign());
				dataRow.createCell(columnIndexMap.get("city")).setCellValue(lead.getCity());
				dataRow.createCell(columnIndexMap.get("sales person")).setCellValue(lead.getSalesPerson());

				String questionsData = lead.getDynamicFields() != null ? lead.getDynamicFields().stream()
						.flatMap(map -> map.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()))
						.collect(Collectors.joining("\n")) : "";

				dataRow.createCell(columnIndexMap.get("questions")).setCellValue(questionsData);

				String conversationLogs = lead.getConversationLogs() != null ? lead.getConversationLogs().stream()
						.map(log -> log.get("comment")).filter(Objects::nonNull).collect(Collectors.joining("\n")) : "";

				XSSFCell conversationCell = dataRow.createCell(columnIndexMap.get("conversation logs"));
				conversationCell.setCellValue(conversationLogs);
				conversationCell.setCellStyle(headerStyle);
			}

			for (int i = 0; i < baseColumns.size(); i++) {
				sheet.autoSizeColumn(i);
			}

			tempFile = File.createTempFile("LeadsReport", ".xlsx");
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				workbook.write(fos);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error generating Excel file: " + e.getMessage(), e);
		}
		return tempFile;
	}

}
