package com.crm.importLead;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.crm.Exception.Error;
import com.crm.user.Status;
import com.crm.user.User;
import com.crm.user.UserRepository;
import com.crm.user.UserServiceException;

@Service
public class ImportLeadService {

	@Autowired
	private ImportLeadRepository repository;

	@Autowired
	private UserRepository userRepository;

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

	public ResponseEntity<?> readLeadsFromExcel(long userId, MultipartFile file) {
		int processedCount = 0;
		int skippedCount = 0;

		try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {

			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			if (rowIterator.hasNext()) {
				rowIterator.next();
			}

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				String email = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
				String mobileNumber = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
				String name = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : "";
				String propertyRange = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "";
				String callTime = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : "";
				String ad = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : "";
				String adSet = row.getCell(7) != null ? row.getCell(7).getStringCellValue() : "";
				String campaign = row.getCell(8) != null ? row.getCell(8).getStringCellValue() : "";
				String city = row.getCell(9) != null ? row.getCell(9).getStringCellValue() : "";

				boolean isDuplicate = repository.existsByAdNameAndAdSetAndCampaignAndCity(ad, adSet, campaign, city);
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
				lead.setStatus(Status.PENDING);
				lead.setAdName(ad);
				lead.setAdSet(adSet);
				lead.setCampaign(campaign);
				lead.setCity(city);
				lead.setCallTime(callTime);
				lead.setPropertyRange(propertyRange);

				repository.save(lead);
				processedCount++;
			}

			return ResponseEntity
					.ok("File processed successfully. Processed: " + processedCount + ", Skipped: " + skippedCount);

		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
		} catch (Exception ex) {
			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
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
}
