package com.crm.project;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174"), ("http://139.84.136.208 ") })
@RequestMapping("/api/project")
public class ProjectDetailsController {

	@Autowired
	private ProjectDetailsService projectDetailsService;

	@PostMapping("/create")
	public ResponseEntity<?> createProjectDetails(@CookieValue(value = "token", required = true) String token,
			@RequestBody ProjectDetails details, @RequestParam long userId) {
		try {
			System.out.println("In controller ");
			return projectDetailsService.createProjectDetails(token, details, userId);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create project details.");
		}
	}

	@GetMapping("/getflat/{projectId}")
	@Transactional
	public ResponseEntity<?> getProjectDetailsById(@PathVariable long projectId) {
		try {
			return projectDetailsService.getProjectById(projectId);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve project details.");
		}
	}
	
	@GetMapping("/get/{flatId}")
	@Transactional
	public ResponseEntity<?> getFlatById(@PathVariable long flatId) {
		try {
			return projectDetailsService.getFlatById(flatId);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve flat details.");
		}
	}

	@PostMapping("/tower/create")
	public ResponseEntity<?> createTowerDetails(@RequestBody String requestData) {
		try {
			return projectDetailsService.createTower1(requestData);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save tower details.");
		}
	}

	@PostMapping("/floor/create")
	public ResponseEntity<?> createFloorDetails(@RequestBody FloorDetails floorDetails) {
		try {
			return projectDetailsService.createFloorDetails(floorDetails);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save floor details.");
		}
	}

	@PostMapping("/flat/create")
	public ResponseEntity<?> createFlat(@RequestBody Flat flat) {
		try {
			return projectDetailsService.createFlat(flat);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save flat details.");
		}
	}

	@GetMapping("/get/overall/{projectId}")
	public ResponseEntity<?> getOverallProjectData(@PathVariable long projectId) {
		try {
			return projectDetailsService.getOverallDataByProjectId(projectId);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve project data.");
		}
	}

	@PutMapping("/update/floor/{floorId}")
	public ResponseEntity<?> updateFloorDetails(@PathVariable long floorId, @RequestBody FloorDetails updatedFloor) {
		try {
			return projectDetailsService.updateFloorDetails(floorId, updatedFloor);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update floor details.");
		}
	}

	@PutMapping("/update/flat/{flatId}")
	public ResponseEntity<?> updateFlatDetails(@PathVariable long flatId, @RequestBody Flat updatedFlat) {
		try {
			return projectDetailsService.updateFlatDetails(flatId, updatedFlat);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update flat details.");
		}
	}

	@PutMapping("/update/flat/status/{flatId}")
	public ResponseEntity<?> updateFlatStatus(@PathVariable long flatId, @RequestParam String status) {
		try {
			return projectDetailsService.updateFlatStatus(flatId, status);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update flat status.");
		}
	}

	@PutMapping("/update/{flatId}")
	public ResponseEntity<?> updateFlat(@PathVariable("flatId") long flatId,
			@RequestBody Map<String, Object> requestData) {
		try {
			return projectDetailsService.updateFlat(flatId, requestData);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update flat");
		}
	}

}
