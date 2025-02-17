package com.crm.importLead;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crm.Exception.Error;
import com.crm.security.JwtUtil;
import com.crm.user.Status;
import com.crm.user.User;
import com.crm.user.UserRepository;
import com.crm.user.UserServiceException;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ImportLeadService {

	@Autowired
	private ImportLeadRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

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

			List<ImportLead> leadsToSave = new ArrayList<>();
			List<Long> assignedToList = (assignedTo != null) ? new ArrayList<>(assignedTo) : new ArrayList<>();
			int assignedToSize = assignedToList.size();

			int index = 0;

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				Cell emailCell = row.getCell(1);
				Cell mobileCell = row.getCell(2);
				Cell nameCell = row.getCell(3);
				Cell propertyRangeCell = row.getCell(4);
				Cell callTimeCell = row.getCell(5);
				Cell adCell = row.getCell(6);
				Cell adSetCell = row.getCell(7);
				Cell campaignCell = row.getCell(8);
				Cell cityCell = row.getCell(9);

				String email = (emailCell != null) ? emailCell.getStringCellValue() : "";
				String mobileNumber = getCellValueAsString(mobileCell);
				String name = (nameCell != null) ? nameCell.getStringCellValue() : "";
				String propertyRange = (propertyRangeCell != null) ? propertyRangeCell.getStringCellValue() : "";
				String callTime = (callTimeCell != null) ? callTimeCell.getStringCellValue() : "";
				String ad = (adCell != null) ? adCell.getStringCellValue() : "";
				String adSet = (adSetCell != null) ? adSetCell.getStringCellValue() : "";
				String campaign = (campaignCell != null) ? campaignCell.getStringCellValue() : "";
				String city = (cityCell != null) ? cityCell.getStringCellValue() : "";

				boolean isDuplicate = repository.existsByEmailAndAdNameAndAdSetAndCampaignAndCity(email, ad, adSet,
						campaign, city);
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
				lead.setAdName(ad);
				lead.setAdSet(adSet);
				lead.setCampaign(campaign);
				lead.setCity(city);
				lead.setCallTime(callTime);
				lead.setPropertyRange(propertyRange);

				if (!assignedToList.isEmpty()) {
					lead.setAssignedTo(assignedToList.get(index % assignedToSize));
					User salesById = userRepository.findSalesById(assignedToList.get(index % assignedToSize));
					lead.setSalesPerson(salesById.getName());

					index++;
				}

				leadsToSave.add(lead);
				processedCount++;
			}

			repository.saveAll(leadsToSave);
			if (assignedToList.isEmpty()) {
				assignLeadsToSaled();
			}
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

	private String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
			} else {
				return String.valueOf((long) cell.getNumericCellValue());
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		default:
			return "";
		}
	}

	public ResponseEntity<?> assignLeadsToSaled() {
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

	public ResponseEntity<?> assignLeads(String token, int page, String status) {
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
			if (!"ADMIN".equalsIgnoreCase(adminRole)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Pageable pageable = PageRequest.of(page - 1, 10);
			Page<ImportLead> unassignedLeads = null;
			if (status.equalsIgnoreCase("assigned")) {
				unassignedLeads = repository.findByStatusOrderByImportedOnDesc(Status.ASSIGNED, pageable);
			} else if (status.equalsIgnoreCase("completed")) {
				unassignedLeads = repository.findByStatusOrderByImportedOnDesc(Status.COMPLETED, pageable);
			}

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

			if (!"SALES".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			 Optional<ImportLead> byId = repository.findById(id);
			if (byId.isPresent()) {
				ImportLead importLead = byId.get();
				return ResponseEntity.ok(importLead);
			} else {
				throw new UserServiceException(401, "User not exists");
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
}
