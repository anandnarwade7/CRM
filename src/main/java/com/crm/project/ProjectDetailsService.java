package com.crm.project;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.crm.Exception.Error;
import com.crm.fileHandler.FilesManager;
import com.crm.leads.LeadDetails;
import com.crm.leads.LeadRepository;
import com.crm.notifications.Notifications;
import com.crm.notifications.NotificationsRepository;
import com.crm.security.JwtUtil;
import com.crm.user.Admins;
import com.crm.user.AdminsRepository;
import com.crm.user.Client;
import com.crm.user.ClientRepository;
import com.crm.user.User;
import com.crm.user.UserRepository;
import com.crm.user.UserServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ProjectDetailsService {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private ProjectDetailsRepository projectDetailsRepo;

	@Autowired
	private TowerDetailsRepository towerDetailsRepo;

	@Autowired
	private FloorDetailsRepository floorDetailsRepo;

	@Autowired
	private FlatRepository flatRepo;

	@Autowired
	private AdminsRepository adminsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LeadRepository leadRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private FilesManager fileManager;

	@Autowired
	private FlatBookDetailsRepository bookDetailsRepository;

	@Transactional
	public ResponseEntity<?> createProjectDetails(String token, ProjectDetails details, long userId) {
		try {
			System.out.println("In serivce ");
			if (jwtUtil.isTokenExpired(token)) {
				System.out.println("Jwt expiration checking");
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			String role = jwtUtil.extractRole(token);

			if (!"ADMIN".equalsIgnoreCase(role)) {
				System.out.println("role checking ");
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			System.out.println("In serivce 2");

			details.setUserId(userId);
			details.setCreatedOn(System.currentTimeMillis());
			System.out.println("In serivce 3");
			ProjectDetails projectDetails = projectDetailsRepo.save(details);
			System.out.println("Object save " + projectDetails);
			return ResponseEntity.ok(projectDetails);
		} catch (Exception ex) {
			System.out.println("In catch ");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create project details.");
		}
	}

	public ResponseEntity<?> getProjectDetailsById(long projectId) {
		try {
			Optional<ProjectDetails> projectDetailsOpt = projectDetailsRepo.findById(projectId);

			if (projectDetailsOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
			}

			ProjectDetails projectDetails = projectDetailsOpt.get();

			return ResponseEntity.ok(projectDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve project details.");
		}
	}

	public ResponseEntity<?> createTower(String requestData) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(requestData);

			String towerName = jsonNode.has("towerName") ? jsonNode.get("towerName").asText() : null;
			long projectId = jsonNode.has("project_id") ? jsonNode.get("project_id").asLong() : 0;
			int totalTowers = jsonNode.has("totalTowers") ? jsonNode.get("totalTowers").asInt() : 0;
			int totalFloors = jsonNode.has("totalFloors") ? jsonNode.get("totalFloors").asInt() : 0;
			int flatPerFloor = jsonNode.has("flatPerFloor") ? jsonNode.get("flatPerFloor").asInt() : 0;

			if (towerName == null || projectId == 0 || totalTowers == 0 || totalFloors == 0 || flatPerFloor == 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input data");
			}

			boolean existsByTowerNameAndProjectId = towerDetailsRepo.existsByTowerNameAndProjectId(towerName,
					projectId);
			if (existsByTowerNameAndProjectId) {
				throw new UserServiceException(409, "Cannot add same tower name for the project.");
			}

			Optional<ProjectDetails> projectOpt = projectDetailsRepo.findById(projectId);
			if (projectOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
			}

			ProjectDetails project = projectOpt.get();

			TowerDetails towerDetails = new TowerDetails();
			towerDetails.setTowerName(towerName);
			towerDetails.setProject(project);
			towerDetails.setTotalTowers(totalTowers);
			towerDetails.setTotalFloors(totalFloors);
			towerDetails.setFlatPerFloor(flatPerFloor);

			List<FloorDetails> floors = new ArrayList<>();

			for (int i = 1; i <= totalFloors; i++) {
				FloorDetails floor = new FloorDetails();
				floor.setFloorName("Floor " + i);
				floor.setTower(towerDetails);

				List<Flat> flats = new ArrayList<>();
				for (int j = 1; j <= flatPerFloor; j++) {
					Flat flat = new Flat();
					int flatNumber = (i * 100) + j;
					flat.setFlatNumber(flatNumber);
					flat.setStatus("Available");
					flat.setFloor(floor);

//					flat.setFlatNumber(towerName + "-" + flatNumber);

					flats.add(flat);
				}
				floors.add(floor);
			}

			towerDetailsRepo.save(towerDetails);

			return ResponseEntity.status(HttpStatus.CREATED).body("Tower created successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create tower");
		}
	}

	public ResponseEntity<?> createTower1(List<String> requestData) {
		try {
			List<String> successMessages = new ArrayList<>();
			List<String> failedMessages = new ArrayList<>();
			ObjectMapper objectMapper = new ObjectMapper();

			try {
				if (requestData == null || requestData.isEmpty()) {
					throw new UserServiceException(409, "Request data list is empty or null.");
				}

				for (String jsonString : requestData) {
					try {
						JsonNode jsonNode = objectMapper.readTree(jsonString);

						String towerName = jsonNode.has("towerName") ? jsonNode.get("towerName").asText() : null;
						long projectId = jsonNode.has("project_id") ? jsonNode.get("project_id").asLong() : 0;
//					int totalTowers = jsonNode.has("totalTowers") ? jsonNode.get("totalTowers").asInt() : 0;
						int totalFloors = jsonNode.has("totalFloors") ? jsonNode.get("totalFloors").asInt() : 0;
						int flatPerFloor = jsonNode.has("flatPerFloor") ? jsonNode.get("flatPerFloor").asInt() : 0;

						if (towerDetailsRepo.existsByTowerNameAndProjectId(towerName, projectId)) {
							throw new UserServiceException(409,
									"Tower name: " + towerName + " already exists for project ID: " + projectId);
						}

						Optional<ProjectDetails> projectOpt = projectDetailsRepo.findById(projectId);
						if (projectOpt.isEmpty()) {
							throw new UserServiceException(404, "Project not found for ID: " + projectId);
						}

						ProjectDetails project = projectOpt.get();

						TowerDetails towerDetails = new TowerDetails();
						towerDetails.setTowerName(towerName);
						towerDetails.setProject(project);
//					towerDetails.setTotalTowers(totalTowers);
						towerDetails.setTotalFloors(totalFloors);
						towerDetails.setFlatPerFloor(flatPerFloor);
						TowerDetails savedTower = towerDetailsRepo.save(towerDetails);

						for (int i = 1; i <= totalFloors; i++) {
							FloorDetails floor = new FloorDetails();
							floor.setFloorName("Floor " + i);
							floor.setTower(savedTower);
							FloorDetails savedFloor = floorDetailsRepo.save(floor);

							for (int j = 1; j <= flatPerFloor; j++) {
								Flat flat = new Flat();
								int flatNumber = (i * 100) + j;
								flat.setFlatNumber(flatNumber);
								flat.setStatus("Available");
								flat.setFloor(savedFloor);
								flatRepo.save(flat);
							}
						}

						successMessages.add("Tower '" + towerName + "' created successfully.");

					} catch (JsonProcessingException e) {
						failedMessages.add("Invalid JSON format: " + e.getOriginalMessage());
					} catch (UserServiceException e) {
						failedMessages.add("Business rule violation: " + e.getMessage());
					} catch (Exception e) {
						failedMessages.add("Unexpected error: " + e.getMessage());
					}
				}

			} catch (UserServiceException e) {
				return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
						"Unexpected top-level error: " + e.getMessage(), System.currentTimeMillis()));
			}

			Map<String, Object> response = new HashMap<>();
			response.put("success", successMessages);
			response.put("failed", failedMessages);

			return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add details", System.currentTimeMillis()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserServiceException(409, "Failed to add details: " + ex.getMessage());
		}
	}

	// working
	// ...........................................................................................//////
//	public ResponseEntity<?> createTower1(List<String> requestData, List<MultipartFile> layoutImages) {
//		try {
//			if (requestData == null || requestData.isEmpty()) {
//				throw new UserServiceException(409, "Request data list is empty or null.");
//			}
//			if (layoutImages == null || layoutImages.size() != requestData.size()) {
//				throw new UserServiceException(409, "Number of images must match number of tower data entries.");
//			}
//
//			List<String> successMessages = new ArrayList<>();
//			List<String> failedMessages = new ArrayList<>();
//			ObjectMapper objectMapper = new ObjectMapper();
//
//			for (int index = 0; index < requestData.size(); index++) {
//				String jsonString = requestData.get(index);
//				MultipartFile imageFile = layoutImages.get(index);
//
//				try {
//					JsonNode jsonNode = objectMapper.readTree(jsonString);
//
//					String towerName = jsonNode.has("towerName") ? jsonNode.get("towerName").asText() : null;
//					long projectId = jsonNode.has("project_id") ? jsonNode.get("project_id").asLong() : 0;
//					int totalFloors = jsonNode.has("totalFloors") ? jsonNode.get("totalFloors").asInt() : 0;
//					int flatPerFloor = jsonNode.has("flatPerFloor") ? jsonNode.get("flatPerFloor").asInt() : 0;
//
//					if (towerName == null || towerName.isEmpty()) {
//						throw new UserServiceException(400, "Tower name is missing or empty.");
//					}
//					if (projectId == 0) {
//						throw new UserServiceException(400, "Project ID is missing or invalid.");
//					}
//
//					if (towerDetailsRepo.existsByTowerNameAndProjectId(towerName, projectId)) {
//						throw new UserServiceException(409,
//								"Tower name: " + towerName + " already exists for project ID: " + projectId);
//					}
//
//					Optional<ProjectDetails> projectOpt = projectDetailsRepo.findById(projectId);
//					if (projectOpt.isEmpty()) {
//						throw new UserServiceException(404, "Project not found for ID: " + projectId);
//					}
//					ProjectDetails project = projectOpt.get();
//
//					TowerDetails towerDetails = new TowerDetails();
//					towerDetails.setTowerName(towerName);
//					towerDetails.setProject(project);
//					towerDetails.setTotalFloors(totalFloors);
//					towerDetails.setFlatPerFloor(flatPerFloor);
//
//					if (imageFile != null && !imageFile.isEmpty()) {
//						String uploadedFilePath = fileManager.uploadFile(imageFile);
//						towerDetails.setLayoutImage(uploadedFilePath);
//					} else {
//						throw new UserServiceException(400, "Layout image is missing for tower: " + towerName);
//					}
//
//					TowerDetails savedTower = towerDetailsRepo.save(towerDetails);
//
//					for (int floorNum = 1; floorNum <= totalFloors; floorNum++) {
//						FloorDetails floor = new FloorDetails();
//						floor.setFloorName("Floor " + floorNum);
//						floor.setTower(savedTower);
//						FloorDetails savedFloor = floorDetailsRepo.save(floor);
//
//						for (int flatNum = 1; flatNum <= flatPerFloor; flatNum++) {
//							Flat flat = new Flat();
//							int flatNumber = (floorNum * 100) + flatNum;
//							flat.setFlatNumber(flatNumber);
//							flat.setStatus("Available");
//							flat.setFloor(savedFloor);
//							flatRepo.save(flat);
//						}
//					}
//
//					successMessages.add("Tower '" + towerName + "' created successfully with layout image.");
//
//				} catch (JsonProcessingException e) {
//					failedMessages.add("Invalid JSON format at index " + index + ": " + e.getOriginalMessage());
//				} catch (UserServiceException e) {
//					failedMessages.add("Business rule violation at index " + index + ": " + e.getMessage());
//				} catch (Exception e) {
//					failedMessages.add("Unexpected error at index " + index + ": " + e.getMessage());
//				}
//			}
//
//			Map<String, Object> response = new HashMap<>();
//			response.put("success", successMessages);
//			response.put("failed", failedMessages);
//
//			return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
//
//		} catch (UserServiceException e) {
//			return ResponseEntity.status(e.getStatusCode()).body(
//					new Error(e.getStatusCode(), e.getMessage(), "Unable to add details", System.currentTimeMillis()));
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			throw new UserServiceException(409, "Failed to add details: " + ex.getMessage());
//		}
//	}

	public ResponseEntity<?> createTower1(List<String> requestData, List<MultipartFile> layoutImages) {
		try {
			if (requestData == null || requestData.isEmpty()) {
				throw new UserServiceException(409, "Request data list is empty or null.");
			}

			List<String> successMessages = new ArrayList<>();
			List<String> failedMessages = new ArrayList<>();
			ObjectMapper objectMapper = new ObjectMapper();

			for (int index = 0; index < requestData.size(); index++) {
				String jsonString = requestData.get(index);
				MultipartFile imageFile = (layoutImages != null && layoutImages.size() > index)
						? layoutImages.get(index)
						: null;

				try {
					JsonNode jsonNode = objectMapper.readTree(jsonString);

					String towerName = jsonNode.has("towerName") ? jsonNode.get("towerName").asText() : null;
					long projectId = jsonNode.has("project_id") ? jsonNode.get("project_id").asLong() : 0;
					int totalFloors = jsonNode.has("totalFloors") ? jsonNode.get("totalFloors").asInt() : 0;
					int flatPerFloor = jsonNode.has("flatPerFloor") ? jsonNode.get("flatPerFloor").asInt() : 0;

					// Get existing image path from JSON if present
					String existingImage = jsonNode.has("layoutImage") ? jsonNode.get("layoutImage").asText() : null;

					if (towerName == null || towerName.isEmpty()) {
						throw new UserServiceException(400, "Tower name is missing or empty.");
					}
					if (projectId == 0) {
						throw new UserServiceException(400, "Project ID is missing or invalid.");
					}

					if (towerDetailsRepo.existsByTowerNameAndProjectId(towerName, projectId)) {
						throw new UserServiceException(409,
								"Tower name: " + towerName + " already exists for project ID: " + projectId);
					}

					Optional<ProjectDetails> projectOpt = projectDetailsRepo.findById(projectId);
					if (projectOpt.isEmpty()) {
						throw new UserServiceException(404, "Project not found for ID: " + projectId);
					}
					ProjectDetails project = projectOpt.get();

					TowerDetails towerDetails = new TowerDetails();
					towerDetails.setTowerName(towerName);
					towerDetails.setProject(project);
					towerDetails.setTotalFloors(totalFloors);
					towerDetails.setFlatPerFloor(flatPerFloor);

					String finalImagePath = null;
					if (imageFile != null && !imageFile.isEmpty()) {
						finalImagePath = fileManager.uploadFile(imageFile);
					} else if (existingImage != null && !existingImage.isEmpty()) {
						finalImagePath = existingImage;
					} else {
						throw new UserServiceException(400, "No layout image found for tower: " + towerName);
					}

//					towerDetails.setLayoutImage(finalImagePath);

					TowerDetails savedTower = towerDetailsRepo.save(towerDetails);

					for (int floorNum = 1; floorNum <= totalFloors; floorNum++) {
						FloorDetails floor = new FloorDetails();
						floor.setFloorName("Floor " + floorNum);
						floor.setTower(savedTower);
						FloorDetails savedFloor = floorDetailsRepo.save(floor);

						for (int flatNum = 1; flatNum <= flatPerFloor; flatNum++) {
							Flat flat = new Flat();
							int flatNumber = (floorNum * 100) + flatNum;
							flat.setFlatNumber(flatNumber);
							flat.setStatus("Available");
							flat.setFloor(savedFloor);
							flatRepo.save(flat);
						}
					}

					successMessages.add("Tower '" + towerName + "' created successfully with layout image.");

				} catch (JsonProcessingException e) {
					failedMessages.add("Invalid JSON format at index " + index + ": " + e.getOriginalMessage());
				} catch (UserServiceException e) {
					failedMessages.add("Business rule violation at index " + index + ": " + e.getMessage());
				} catch (Exception e) {
					failedMessages.add("Unexpected error at index " + index + ": " + e.getMessage());
				}
			}

			Map<String, Object> response = new HashMap<>();
			response.put("success", successMessages);
			response.put("failed", failedMessages);

			return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);

		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to add details", System.currentTimeMillis()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserServiceException(409, "Failed to add details: " + ex.getMessage());
		}
	}

	private final ObjectMapper objectMapper = new ObjectMapper();

//	public Map<String, Object> createTowersWithLayouts(Map<String, String> requestData,
//			List<Map<String, MultipartFile>> layoutImages) {
//
//		List<String> successMessages = new ArrayList<>();
//		List<String> failedMessages = new ArrayList<>();
//
//		int totalEntries = (int) requestData.keySet().stream().filter(k -> k.startsWith("requestData[")).count();
//
//		for (int index = 0; index < totalEntries; index++) {
//			try {
//				String jsonKey = "requestData[" + index + "]";
//				String jsonString = requestData.get(jsonKey);
//				
////				MultipartFile imageFile = layoutImages.get(imageKey);
//			
//
//				if (jsonString == null || jsonString.isEmpty()) {
//					failedMessages.add("Missing JSON at index " + index);
//					continue;
//				}
//
//				JsonNode jsonNode = objectMapper.readTree(jsonString);
//
//				String towerName = jsonNode.has("towerName") ? jsonNode.get("towerName").asText() : null;
//				long projectId = jsonNode.has("project_id") ? jsonNode.get("project_id").asLong() : 0;
//				int totalFloors = jsonNode.has("totalFloors") ? jsonNode.get("totalFloors").asInt() : 0;
//				int flatPerFloor = jsonNode.has("flatPerFloor") ? jsonNode.get("flatPerFloor").asInt() : 0;
//
//				if (towerName == null || towerName.isEmpty()) {
//					throw new UserServiceException(400, "Tower name is missing or empty.");
//				}
//				if (projectId == 0) {
//					throw new UserServiceException(400, "Project ID is missing or invalid.");
//				}
//
//				if (towerDetailsRepo.existsByTowerNameAndProjectId(towerName, projectId)) {
//					throw new UserServiceException(409,
//							"Tower '" + towerName + "' already exists in project ID: " + projectId);
//				}
//
//				ProjectDetails project = projectDetailsRepo.findById(projectId)
//						.orElseThrow(() -> new UserServiceException(404, "Project not found for ID: " + projectId));
//
//				TowerDetails towerDetails = new TowerDetails();
//				towerDetails.setTowerName(towerName);
//				towerDetails.setProject(project);
//				towerDetails.setTotalFloors(totalFloors);
//				towerDetails.setFlatPerFloor(flatPerFloor);
//
////				if (imageFile != null && !imageFile.isEmpty()) {
////					String uploadedFilePath = fileManager.uploadFile(imageFile);
////					towerDetails.setLayoutImage(uploadedFilePath);
////				} else {
////					throw new UserServiceException(400, "Layout image is missing for tower: " + towerName);
////				}
//				
//			     if (index >= layoutImages.size()) {
//		                throw new UserServiceException(400, "Missing layout images for index: " + index);
//		            }
//
//		            Map<String, MultipartFile> layoutImageMap = layoutImages.get(index);
//
//		            String evenLayout = uploadLayoutImage(layoutImageMap.get("evenLayout"), "evenLayout", towerName);
//		            String oddLayout = uploadLayoutImage(layoutImageMap.get("oddLayout"), "oddLayout", towerName);
//		            String groundLayout = uploadLayoutImage(layoutImageMap.get("groundLayout"), "groundLayout", towerName);
//		            String customLayout = uploadLayoutImage(layoutImageMap.get("customLayout"), "customLayout", towerName);
//		            towerDetails.setEvenLayout(evenLayout);
//		            towerDetails.setOddLayout(oddLayout);
//		            towerDetails.setGroundLayout(groundLayout);
//		            towerDetails.setCustomLayout(customLayout);
//		            
//
//				TowerDetails savedTower = towerDetailsRepo.save(towerDetails);
//
//				for (int floorNum = 1; floorNum <= totalFloors; floorNum++) {
//					FloorDetails floor = new FloorDetails();
//					floor.setFloorName("Floor " + floorNum);
//					floor.setTower(savedTower);
//					FloorDetails savedFloor = floorDetailsRepo.save(floor);
//
//					for (int flatNum = 1; flatNum <= flatPerFloor; flatNum++) {
//						Flat flat = new Flat();
//						flat.setFlatNumber((floorNum * 100) + flatNum);
//						flat.setStatus("Available");
//						flat.setFloor(savedFloor);
//						flatRepo.save(flat);
//					}
//				}
//
//				successMessages.add("Tower '" + towerName + "' created successfully.");
//
//			} catch (JsonProcessingException e) {
//				failedMessages.add("Invalid JSON at index " + index + ": " + e.getOriginalMessage());
//			} catch (UserServiceException e) {
//				failedMessages.add("Business rule violation at index " + index + ": " + e.getMessage());
//			} catch (Exception e) {
//				failedMessages.add("Unexpected error at index " + index + ": " + e.getMessage());
//			}
//		}
//
//		Map<String, Object> result = new HashMap<>();
//		result.put("success", successMessages);
//		result.put("failed", failedMessages);
//		return result;
//	}

//	private String uploadLayoutImage(MultipartFile file, String layoutType, String towerName) {
//		if (file == null || file.isEmpty()) {
//			throw new UserServiceException(400, layoutType + " image is missing for tower: " + towerName);
//		}
//		return fileManager.uploadFile(file);
//	}

	public Map<String, Object> createTowersWithLayouts(Map<String, String> requestData,
			List<Map<String, MultipartFile>> layoutImages) {

		List<String> successMessages = new ArrayList<>();
		List<String> failedMessages = new ArrayList<>();

		int totalEntries = (int) requestData.keySet().stream().filter(k -> k.startsWith("requestData[")).count();

		for (int index = 0; index < totalEntries; index++) {
			try {
				String jsonKey = "requestData[" + index + "]";
				String jsonString = requestData.get(jsonKey);

				if (jsonString == null || jsonString.isEmpty()) {
					failedMessages.add("Missing JSON at index " + index);
					continue;
				}

				JsonNode jsonNode = objectMapper.readTree(jsonString);

				String towerName = jsonNode.has("towerName") ? jsonNode.get("towerName").asText() : null;
				long projectId = jsonNode.has("project_id") ? jsonNode.get("project_id").asLong() : 0;
				int totalFloors = jsonNode.has("totalFloors") ? jsonNode.get("totalFloors").asInt() : 0;
				int flatPerFloor = jsonNode.has("flatPerFloor") ? jsonNode.get("flatPerFloor").asInt() : 0;

				if (towerName == null || towerName.isEmpty()) {
					throw new UserServiceException(400, "Tower name is missing or empty.");
				}

				if (projectId == 0) {
					throw new UserServiceException(400, "Project ID is missing or invalid.");
				}

				if (towerDetailsRepo.existsByTowerNameAndProjectId(towerName, projectId)) {
					throw new UserServiceException(409,
							"Tower '" + towerName + "' already exists in project ID: " + projectId);
				}

				ProjectDetails project = projectDetailsRepo.findById(projectId)
						.orElseThrow(() -> new UserServiceException(404, "Project not found for ID: " + projectId));

				TowerDetails towerDetails = new TowerDetails();
				towerDetails.setTowerName(towerName);
				towerDetails.setProject(project);
				towerDetails.setTotalFloors(totalFloors);
				towerDetails.setFlatPerFloor(flatPerFloor);

				if (index >= layoutImages.size()) {
					throw new UserServiceException(400, "Missing layout images for index: " + index);
				}

				Map<String, MultipartFile> layoutImageMap = layoutImages.get(index);

//				String evenLayout = uploadLayoutImage(layoutImageMap.get("evenLayout"), "evenLayout", towerName);
//				String oddLayout = uploadLayoutImage(layoutImageMap.get("oddLayout"), "oddLayout", towerName);
//				String groundLayout = uploadLayoutImage(layoutImageMap.get("groundLayout"), "groundLayout", towerName);

//				towerDetails.setEvenLayout(evenLayout);
//				towerDetails.setOddLayout(oddLayout);
//				towerDetails.setGroundLayout(groundLayout);

				Map<String, String> customLayoutMap = new HashMap<>();
				for (Map.Entry<String, MultipartFile> entry : layoutImageMap.entrySet()) {
					String layoutKey = entry.getKey();
//					if (!List.of("oddLayout", "evenLayout", "groundLayout").contains(layoutKey)) {
					String uploadedPath = uploadLayoutImage(entry.getValue(), layoutKey, towerName);
					customLayoutMap.put(layoutKey, uploadedPath);
//					}
				}

				towerDetails.setCustomLayouts(customLayoutMap);
				TowerDetails savedTower = towerDetailsRepo.save(towerDetails);

				for (int floorNum = 1; floorNum <= totalFloors; floorNum++) {
					FloorDetails floor = new FloorDetails();
					floor.setFloorName("Floor " + floorNum);
					floor.setTower(savedTower);
					FloorDetails savedFloor = floorDetailsRepo.save(floor);

					for (int flatNum = 1; flatNum <= flatPerFloor; flatNum++) {
						Flat flat = new Flat();
						flat.setFlatNumber((floorNum * 100) + flatNum);
						flat.setStatus("Available");
						flat.setFloor(savedFloor);
						flatRepo.save(flat);
					}
				}

				successMessages.add("Tower '" + towerName + "' created successfully.");

			} catch (JsonProcessingException e) {
				failedMessages.add("Invalid JSON at index " + index + ": " + e.getOriginalMessage());
			} catch (UserServiceException e) {
				failedMessages.add("Business rule violation at index " + index + ": " + e.getMessage());
			} catch (Exception e) {
				failedMessages.add("Unexpected error at index " + index + ": " + e.getMessage());
			}
		}

		Map<String, Object> result = new HashMap<>();
		result.put("success", successMessages);
		result.put("failed", failedMessages);
		return result;
	}

	private String uploadLayoutImage(MultipartFile file, String layoutType, String towerName) {
		if (file == null || file.isEmpty()) {
			throw new UserServiceException(400, layoutType + " image is missing for tower: " + towerName);
		}
		return fileManager.uploadFile(file); // Adjust this based on your upload logic
	}

	public ResponseEntity<?> createFloorDetails(FloorDetails floorDetails) {
		try {
			boolean existsByTowerNameAndProjectId = floorDetailsRepo
					.existsByFloorNameAndTowerId(floorDetails.getFloorName(), floorDetails.getTower().getId());
			if (existsByTowerNameAndProjectId) {
				throw new UserServiceException(409, "cannot add same name");
			}
			floorDetailsRepo.save(floorDetails);
			return ResponseEntity.ok("Floor details saved successfully");
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save floor details.");
		}
	}

	public ResponseEntity<?> createFloorDetails(String requestData) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(requestData);

			String floorName = jsonNode.get("floorName").asText();
			long towerId = jsonNode.get("tower_id").asLong();
			int flatsPerFloor = jsonNode.get("flatsPerFloor").asInt();

			boolean existsByFloorNameAndTowerId = floorDetailsRepo.existsByFloorNameAndTowerId(floorName, towerId);
			if (existsByFloorNameAndTowerId) {
				throw new UserServiceException(409, "Floor with the same name already exists");
			}

			Optional<TowerDetails> towerOpt = towerDetailsRepo.findById(towerId);
			if (towerOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tower not found");
			}

			TowerDetails tower = towerOpt.get();

			// Create and save floor
			FloorDetails floorDetails = new FloorDetails();
			floorDetails.setFloorName(floorName);
			floorDetails.setTower(tower);
			floorDetailsRepo.save(floorDetails);

			// Generate flats
			int floorNumber = Integer.parseInt(floorName);
			List<Flat> flats = new ArrayList<>();

			for (int i = 1; i <= flatsPerFloor; i++) {
				Flat flat = new Flat();
				flat.setFlatNumber(floorNumber * 100 + i);
				flat.setStatus("Available");
				flat.setFloor(floorDetails);

				flats.add(flat);
			}

			// Save flats to the database
			flatRepo.saveAll(flats);

			return ResponseEntity.ok("Floor and Flats saved successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save floor and flats.");
		}
	}

	public ResponseEntity<?> createFlat(Flat flat) {
		try {
			boolean existsByTowerNameAndProjectId = flatRepo.existsByFlatNumberAndFloorId(flat.getFlatNumber(),
					flat.getFloor().getId());
			if (existsByTowerNameAndProjectId) {
				throw new UserServiceException(409, "cannot add same name");
			}
			flatRepo.save(flat);
			return ResponseEntity.ok("Flat details saved successfully");
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save flat details.");
		}
	}

	public ResponseEntity<?> getOverallDataByProjectId(long projectId) {
		try {
			Optional<ProjectDetails> projectDetailsOpt = projectDetailsRepo.findById(projectId);

			if (projectDetailsOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
			}

			ProjectDetails projectDetails = projectDetailsOpt.get();
//			projectDetails.getTowers().forEach(tower -> tower.getFloors().forEach(floor -> floor.getFlats().size()));

			return ResponseEntity.ok(projectDetails);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve project data.");
		}
	}

	public ResponseEntity<?> updateFloorDetails(long floorId, FloorDetails floorDetails) {
		try {
			Optional<FloorDetails> floorOpt = floorDetailsRepo.findById(floorId);

			if (floorOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Floor not found");
			}

			floorDetails.setId(floorId);
			floorDetailsRepo.save(floorDetails);
			return ResponseEntity.ok("Floor details updated successfully");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update floor details.");
		}
	}

	public ResponseEntity<?> updateFlatDetails(long flatId, Flat flat) {
		try {
			Optional<Flat> flatOpt = flatRepo.findById(flatId);

			if (flatOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flat not found");
			}

			flat.setId(flatId);
			flatRepo.save(flat);
			return ResponseEntity.ok("Flat details updated successfully");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update flat details.");
		}
	}

	public ResponseEntity<?> updateFlatStatus(long flatId, String status, String area) {
		try {
			Optional<Flat> flatOpt = flatRepo.findById(flatId);

			if (flatOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flat not found");
			}

			Flat flat = flatOpt.get();
			if (status != null) {
				flat.setStatus(status);
			}
			if (area != null) {
				flat.setFlatSize(area);
			}
			flatRepo.save(flat);
			return ResponseEntity.ok("Flat status updated successfully");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update flat status.");
		}
	}

	public ResponseEntity<?> getProjectById(long projectId) {
		Optional<ProjectDetails> projectOpt = projectDetailsRepo.findById(projectId);
		if (projectOpt.isEmpty()) {
			throw new RuntimeException("Project not found with ID: " + projectId);
		}
		return ResponseEntity.ok(convertToMap(projectOpt.get()));
	}

	public ResponseEntity<?> getFlatById(long projectId) {
		try {
			Flat flat = flatRepo.findById(projectId)
					.orElseThrow(() -> new UserServiceException(409, "Data not find for given id"));

			return ResponseEntity.ok(flat);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Failed to retrive details.");
		}
	}

	private Map<String, Object> convertToMap(ProjectDetails project) {
		Map<String, Object> projectMap = new HashMap<>();

		projectMap.put("id", project.getId());
		projectMap.put("propertyName", project.getPropertyName());
		projectMap.put("address", project.getAddress());
		projectMap.put("propertyArea", project.getPropertyArea());
		projectMap.put("userId", project.getUserId());
		projectMap.put("createdOn", project.getCreatedOn());
		projectMap.put("updatedOn", project.getUpdatedOn());

		List<TowerDetails> towers = towerDetailsRepo.getTowersByProjectId(project.getId());
		List<Map<String, Object>> towersWithFloors = new ArrayList<>();

		for (TowerDetails tower : towers) {
			Map<String, Object> towerMap = new HashMap<>();
			towerMap.put("id", tower.getId());
			towerMap.put("towerName", tower.getTowerName());
			towerMap.put("totalTowers", tower.getTotalTowers());
			towerMap.put("flatPerFloor", tower.getFlatPerFloor());
			towerMap.put("totalFloors", tower.getTotalFloors());

			List<FloorDetails> floors = floorDetailsRepo.getFloorDetailsByTowerId(tower.getId());
			List<Map<String, Object>> floorsWithFlats = new ArrayList<>();

			for (FloorDetails floor : floors) {
				Map<String, Object> floorMap = new HashMap<>();
				floorMap.put("id", floor.getId());
				floorMap.put("floorName", floor.getFloorName());

				List<Flat> flats = flatRepo.findByFloorId(floor.getId());
				List<Map<String, Object>> flatList = new ArrayList<>();

				for (Flat flat : flats) {
					Map<String, Object> flatMap = new HashMap<>();
					flatMap.put("id", flat.getId());
					flatMap.put("flatSize", flat.getFlatSize());
					flatMap.put("flatNumber", flat.getFlatNumber());
					flatMap.put("flatType", flat.getFlatType());
					flatMap.put("status", flat.getStatus());
					flatList.add(flatMap);
				}

				floorMap.put("flats", flatList);
				floorsWithFlats.add(floorMap);
			}

			towerMap.put("floors", floorsWithFlats);
			towersWithFloors.add(towerMap);
		}

		projectMap.put("towers", towersWithFloors);

		return projectMap;
	}

	public ResponseEntity<?> updateFlat(String token, long flatId, Map<String, Object> requestData) {
		try {
			System.out.println("In service");

			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			String email = userClaims.get("email");

			if (!"ADMIN".equalsIgnoreCase(role) && !"SALES".equalsIgnoreCase(role) && !"CRM".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			Optional<Flat> flatOpt = flatRepo.findById(flatId);

			if (flatOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flat not found");
			}

			Flat flat = flatOpt.get();

			if (requestData.containsKey("flatSize")) {
				flat.setFlatSize(String.valueOf(requestData.get("flatSize")));
			}
			if (requestData.containsKey("flatType")) {
				flat.setFlatType(String.valueOf(requestData.get("flatType")));
			}
			if ("ADMIN".equalsIgnoreCase(role) && requestData.containsKey("status")) {
				flat.setStatus(String.valueOf(requestData.get("status")));
				flat.setFlatInfo(String.valueOf(requestData.get("flatInfo")));
			}
			if ("SALES".equalsIgnoreCase(role) || "CRM".equalsIgnoreCase(role)) {
				User salesUser = userRepository.findByEmail(email);
				if (salesUser == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CRM user not found for email: " + email);
				}

				if (requestData.containsKey("status") && requestData.containsKey("clientEmail")) {
					String status = String.valueOf(requestData.get("status"));
					String flatInfo = String.valueOf(requestData.get("flatInfo"));
					String clientEmail = String.valueOf(requestData.get("clientEmail"));

					LeadDetails lead = leadRepository.findByLeadEmail(clientEmail);
					System.out.println("Check client data ::" + lead);
					if (lead == null) {
						return ResponseEntity.status(HttpStatus.NOT_FOUND)
								.body("Lead not found for email: " + clientEmail);
					}
					Client client = clientRepository.findByEmail(lead.getLeadEmail());
					if (client == null) {
						return ResponseEntity.status(HttpStatus.NOT_FOUND)
								.body("Client not found for email: " + lead.getLeadEmail());
					}

//					flat.setCrmId();
					flat.setStatus(status);
					flat.setFlatInfo(flatInfo);
					flat.setClientsId(salesUser.getId());

					Flat flatData = flatRepo.save(flat);
					System.out.println("Check flat data :: " + flatData);
					FlatBookDetails bookDetails;
					if ("SALES".equalsIgnoreCase(role)) {
						bookDetails = new FlatBookDetails(client.getId(), client.getName(), salesUser.getId(),
								salesUser.getName(), 0, null, flatData);
						System.out.println("Check the data :: " + bookDetails);
						bookDetailsRepository.save(bookDetails);
						flat.setSalesId(lead.getSalesId());
					} else if ("CRM".equalsIgnoreCase(role)) {
						FlatBookDetails byFlatId = bookDetailsRepository.findByFlatId(flatId);
						byFlatId.setCrmId(salesUser.getId());
						byFlatId.setCrmName(salesUser.getName());
//						byFlatId.se
						bookDetailsRepository.save(byFlatId);
						flat.setCrmId(salesUser.getId());
					}

					sendNotificationToUser(salesUser,
							"Flat " + flatId + " is " + status + " for client " + client.getEmail());
					sendNotificationClient(client, "Congrats! Your flat " + flatId + " has been allocated to you by "
							+ salesUser.getEmail() + "( " + salesUser.getRole() + " )");

				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Both 'status' and 'clientEmail' must be provided for CRM update.");
				}
			}

			Flat updatedFlat = flatRepo.save(flat);
			return ResponseEntity.ok(updatedFlat);

		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to update flats details", System.currentTimeMillis()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserServiceException(409, "Failed to update details: " + ex.getMessage());
		}
	}

	public ResponseEntity<?> projectsDetails(String token, int page) {
		try {
			System.out.println("In serivce ");
			if (jwtUtil.isTokenExpired(token)) {
				System.out.println("Jwt expiration checking");
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			String email = userClaims.get("email");

			if (!"ADMIN".equalsIgnoreCase(role) && !"CRM".equalsIgnoreCase(role) && !"SALES".equalsIgnoreCase(role)) {
				System.out.println("role checking ");
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			long userId;
			if ("ADMIN".equalsIgnoreCase(role)) {
				Admins byEmail = adminsRepository.findByEmail(email);
				userId = byEmail.getId();
			} else {
				User byEmail = userRepository.findByEmail(email);
				userId = byEmail.getUserId();
			}

			Pageable pageable = PageRequest.of(page - 1, 10);
//			List<ProjectDetails> byId = projectDetailsRepo.findByUserIdOrderByCreatedOnDesc(userId);
			Page<ProjectDetails> projectPage = projectDetailsRepo.findByUserIdOrderByCreatedOnDesc(userId, pageable);
			System.out.println("Data found ::" + projectPage.getSize());

			List<Map<String, Object>> content = projectPage.getContent().stream().map(project -> {
				Map<String, Object> map = new HashMap<>();
				map.put("id", project.getId());
				map.put("propertyName", project.getPropertyName());
				map.put("address", project.getAddress());
				map.put("propertyArea", project.getPropertyArea());
				map.put("userId", project.getUserId());
				map.put("createdOn", project.getCreatedOn());
				map.put("updatedOn", project.getUpdatedOn());

				long towerCount = towerDetailsRepo.findByProjectId(project.getId());
				Long totalFloors = towerDetailsRepo.getTotalFloorsByProjectId(project.getId());
				map.put("totalTowers", towerCount);
				map.put("totalFloors", totalFloors != null ? totalFloors : 0L);
				return map;
			}).collect(Collectors.toList());

			Map<String, Object> response = new HashMap<>();
			response.put("content", content);
			response.put("pageNumber", projectPage.getNumber() + 1);
			response.put("pageSize", projectPage.getSize());
			response.put("totalElements", projectPage.getTotalElements());
			response.put("totalPages", projectPage.getTotalPages());
			response.put("last", projectPage.isLast());
			response.put("first", projectPage.isFirst());
			response.put("numberOfElements", projectPage.getNumberOfElements());
			response.put("sort", projectPage.getSort());
			response.put("empty", projectPage.isEmpty());

			return ResponseEntity.ok(response);

		}

		catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to fetch details of projects", System.currentTimeMillis()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserServiceException(409, "Failed to fetch projects details: " + ex.getMessage());
		}
	}

	public ResponseEntity<?> TowersDetail(String token, long projectId) {
		try {
			System.out.println("In serivce ");
			if (jwtUtil.isTokenExpired(token)) {
				System.out.println("Jwt expiration checking");
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			if (!"ADMIN".equalsIgnoreCase(role) && !"CRM".equalsIgnoreCase(role) && !"SALES".equalsIgnoreCase(role)) {
				System.out.println("role checking ");
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			List<TowerDetails> byProjectId = towerDetailsRepo.getByProjectId(projectId);
			return ResponseEntity.ok(byProjectId);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to fetch details of projects", System.currentTimeMillis()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserServiceException(409, "Failed to fetch projects details: " + ex.getMessage());
		}
	}

	public ResponseEntity<?> flatsDetail(String token, long towerId) {
		try {
			System.out.println("In service ");
			if (jwtUtil.isTokenExpired(token)) {
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			if (!"ADMIN".equalsIgnoreCase(role) && !"CRM".equalsIgnoreCase(role) && !"SALES".equalsIgnoreCase(role)) {
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			List<Flat> flats = flatRepo.findByFloor_Tower_Id(towerId);

			Set<Flat> uniqueFlats = new HashSet<>(flats);

			Map<Integer, List<Flat>> grouped = new TreeMap<>();
			for (Flat flat : uniqueFlats) {
				int flatNo = flat.getFlatNumber();
				int floorNumber = flatNo / 100;

				grouped.computeIfAbsent(floorNumber, k -> new ArrayList<>()).add(flat);
			}

			List<List<Flat>> result = new ArrayList<>();
			for (Map.Entry<Integer, List<Flat>> entry : grouped.entrySet()) {
				List<Flat> sortedList = entry.getValue().stream().sorted(Comparator.comparingInt(Flat::getFlatNumber))
						.collect(Collectors.toList());
				result.add(sortedList);
			}

			result.forEach(list -> {
				list.forEach(flat -> System.out.println("Flat No: " + flat.getFlatNumber()));
			});

			return ResponseEntity.ok(result);

		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to fetch details of projects", System.currentTimeMillis()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserServiceException(409, "Failed to fetch projects details: " + ex.getMessage());
		}
	}

	public ResponseEntity<?> towerDetailsToFillArea(String token, long projectId, long towerId) {
		try {
			System.out.println("In serivce ");
			if (jwtUtil.isTokenExpired(token)) {
				System.out.println("Jwt expiration checking");
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			if (!"ADMIN".equalsIgnoreCase(role) && !"CRM".equalsIgnoreCase(role) && !"SALES".equalsIgnoreCase(role)) {
				System.out.println("role checking ");
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}

			List<Flat> byProjectId = flatRepo.findFlatsByProjectIdAndTowerIdAndFlatNumberStartsWith10(projectId,
					towerId);
			return ResponseEntity.ok(byProjectId);
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to fetch details of projects", System.currentTimeMillis()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserServiceException(409, "Failed to fetch projects details: " + ex.getMessage());
		}
	}

	@Transactional
	public ResponseEntity<?> updateFlatsInTower(String token, long towerId, List<Map<String, Object>> flatList) {
		try {

			if (jwtUtil.isTokenExpired(token)) {
				System.out.println("Jwt expiration checking");
				return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
						.body("Unauthorized: Your session has expired.");
			}

			Map<String, String> userClaims = jwtUtil.extractRole1(token);
			String role = userClaims.get("role");
			if (!"ADMIN".equalsIgnoreCase(role) && !"CRM".equalsIgnoreCase(role) && !"SALES".equalsIgnoreCase(role)) {
				System.out.println("role checking ");
				return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
						.body("Forbidden: You do not have the necessary permissions.");
			}
			List<Flat> allFlats = flatRepo.findByFloor_Tower_Id(towerId);

			for (Map<String, Object> flatMap : flatList) {
				int baseFlatNumber = (int) flatMap.get("flatNumber");
				String flatSize = (String) flatMap.get("flatSize");
				String flatType = flatMap.get("flatType") != null ? (String) flatMap.get("flatType") : null;
				String status = flatMap.get("status") != null ? (String) flatMap.get("status") : null;

				int unitNumber = baseFlatNumber % 100;

				for (Flat flat : allFlats) {
					if (flat.getFlatNumber() % 100 == unitNumber) {
						flat.setFlatSize(flatSize + " sq. ft.");
						flat.setFlatType(flatType);
						flat.setStatus(status);
					}
				}
			}

			flatRepo.saveAll(allFlats);

			List<Flat> updatedFlats = flatRepo.findByFloor_Tower_IdOrderByFlatNumberAsc(towerId);
			return ResponseEntity.ok(updatedFlats);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Failed to update flats.");
		}
	}

	private void sendNotificationClient(Client clientUser, String message) {
		try {
			Notifications notification = new Notifications(false, message, clientUser.getEmail(), "Client Details",
					System.currentTimeMillis());
			notificationsRepository.save(notification);
		} catch (Exception e) {
			throw new RuntimeException("Error saving dynamic fields", e);
		}
	}

	private void sendNotificationToUser(User user, String message) {
		try {
			Notifications notification = new Notifications(false, message, user.getEmail(), "Client Details",
					System.currentTimeMillis());
			notificationsRepository.save(notification);
		} catch (Exception e) {
			throw new RuntimeException("Error saving dynamic fields", e);
		}
	}

	public ResponseEntity<?> getDetailsOfTowerByTowerId(Long towerId) {
		try {
			Optional<TowerDetails> tower = towerDetailsRepo.findById(towerId);
			if (tower.isEmpty()) {
				throw new RuntimeException("Towers Details not found with ID: " + towerId);
			}
			return ResponseEntity.ok(tower.get());
		} catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(new Error(e.getStatusCode(), e.getMessage(),
					"Unable to fetch details of tower details", System.currentTimeMillis()));
		}
	}
}
