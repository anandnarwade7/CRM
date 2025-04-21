package com.crm.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crm.Exception.Error;
import com.crm.security.JwtUtil;
import com.crm.user.Admins;
import com.crm.user.AdminsRepository;
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

//	public ResponseEntity<?> getProjectDetailsById(long projectId) {
//		try {
//			Optional<ProjectDetails> projectDetailsOpt = projectDetailsRepo.findById(projectId);
//			List<TowerDetails> byProjectId = towerDetailsRepo.getByProjectId(projectId);
//			for (TowerDetails towerDetails : byProjectId) {
//				List<FloorDetails> byTowerId = floorDetailsRepo.getByTowerId(towerDetails.getId());
//				for (FloorDetails floorDetails : byTowerId) {
//					flatRepo.findByFloorId(floorDetails.getId());
//				}
//			}
//			if (projectDetailsOpt.isPresent()) {
//				return ResponseEntity.ok(projectDetailsOpt.get());
//			} else {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
//			}
//		} catch (Exception ex) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve project details.");
//		}
//	}

	public ResponseEntity<?> getProjectDetailsById(long projectId) {
		try {
			Optional<ProjectDetails> projectDetailsOpt = projectDetailsRepo.findById(projectId);

			if (projectDetailsOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
			}

			ProjectDetails projectDetails = projectDetailsOpt.get();

//			projectDetails.setTowers(towers);

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
					int totalTowers = jsonNode.has("totalTowers") ? jsonNode.get("totalTowers").asInt() : 0;
					int totalFloors = jsonNode.has("totalFloors") ? jsonNode.get("totalFloors").asInt() : 0;
					int flatPerFloor = jsonNode.has("flatPerFloor") ? jsonNode.get("flatPerFloor").asInt() : 0;

					if (towerDetailsRepo.existsByTowerNameAndProjectId(towerName, projectId)) {
						throw new UserServiceException(409, "Tower name already exists for project ID: " + projectId);
					}

					Optional<ProjectDetails> projectOpt = projectDetailsRepo.findById(projectId);
					if (projectOpt.isEmpty()) {
						throw new UserServiceException(404, "Project not found for ID: " + projectId);
					}

					ProjectDetails project = projectOpt.get();

					TowerDetails towerDetails = new TowerDetails();
					towerDetails.setTowerName(towerName);
					towerDetails.setProject(project);
					towerDetails.setTotalTowers(totalTowers);
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

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Unexpected top-level error: " + e.getMessage()));
		}

		Map<String, Object> response = new HashMap<>();
		response.put("success", successMessages);
		response.put("failed", failedMessages);

		return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
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

	public ResponseEntity<?> updateFlatStatus(long flatId, String status) {
		try {
			Optional<Flat> flatOpt = flatRepo.findById(flatId);

			if (flatOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flat not found");
			}

			Flat flat = flatOpt.get();
			flat.setStatus(status);
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update flat status.");
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

	public ResponseEntity<?> updateFlat(long flatId, Map<String, Object> requestData) {
		try {
			Optional<Flat> flatOpt = flatRepo.findById(flatId);
			if (flatOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flat not found");
			}

			Flat flat = flatOpt.get();

			if (requestData.containsKey("flatSize")) {
				String flatSize = requestData.get("flatSize").toString();
				flat.setFlatSize(flatSize);
			}

			if (requestData.containsKey("flatType")) {
				String flatType = requestData.get("flatType").toString();
				flat.setFlatType(flatType);
			}

			if (requestData.containsKey("status")) {
				String status = requestData.get("status").toString();
				flat.setStatus(status);
			}

			Flat updatedFlat = flatRepo.save(flat);
			return ResponseEntity.ok(updatedFlat);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update flat");
		}
	}

	public ResponseEntity<?> projectsDetails(String token) {
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

			if (!"ADMIN".equalsIgnoreCase(role) && !"CRM".equalsIgnoreCase(role)) {
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

			List<ProjectDetails> byId = projectDetailsRepo.findByUserId(userId);
			List<Map<String, Object>> responseList = new ArrayList<>();
			for (ProjectDetails project : byId) {
				Map<String, Object> projectMap = new HashMap<>();
				projectMap.put("id", project.getId());
				projectMap.put("propertyName", project.getPropertyName());
				projectMap.put("address", project.getAddress());
				projectMap.put("propertyArea", project.getPropertyArea());
				projectMap.put("userId", project.getUserId());
				projectMap.put("createdOn", project.getCreatedOn());
				projectMap.put("updatedOn", project.getUpdatedOn());

				long towerCount = towerDetailsRepo.findByProjectId(project.getId());
				long totalFloors = towerDetailsRepo.getTotalFloorsByProjectId(project.getId());
				projectMap.put("totalTowers", towerCount != 0 ? towerCount : 0);
				projectMap.put("totalFloors", totalFloors != 0 ? totalFloors : 0);

				responseList.add(projectMap);
			}

			return ResponseEntity.ok(responseList);
		}

		catch (UserServiceException e) {
			return ResponseEntity.status(e.getStatusCode()).body(
					new Error(e.getStatusCode(), e.getMessage(), "Unable to process file", System.currentTimeMillis()));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserServiceException(409, "Failed to process file: " + ex.getMessage());
		}
	}
}
