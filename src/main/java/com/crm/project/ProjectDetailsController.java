package com.crm.project;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crm.user.UserServiceException;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins = { ("http://localhost:5173"), ("http://localhost:3000"), ("http://localhost:3001"),
		("http://localhost:5174"), ("http://139.84.136.208 ") })
@RequestMapping("/api/project")
public class ProjectDetailsController {

	@Autowired
	private ProjectDetailsService projectDetailsService;

	@PostMapping("/create/{userId}")
	public ResponseEntity<?> createProjectDetails(@RequestHeader(value = "Authorization", required = true) String token,
			@RequestBody ProjectDetails details, @PathVariable long userId) {
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
	public ResponseEntity<?> createTowerDetails1(@RequestBody List<String> requestData) {
		try {
			return projectDetailsService.createTower1(requestData);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save tower details.");
		}
	}

	//New changes implemented for saving layout.................................................//////
	@PostMapping(value = "/tower/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> createTowerDetails1(@RequestPart("towerData") List<String> towerDataList,
			@RequestPart("layoutImages") List<MultipartFile> layoutImages) {
		try {
			return projectDetailsService.createTower1(towerDataList, layoutImages);
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

	// main working to update status and area in sq.ft.
	@PutMapping("/update/flat/status/{flatId}")
	public ResponseEntity<?> updateFlatStatus(@PathVariable long flatId,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "area", required = false) String area) {
		try {
			return projectDetailsService.updateFlatStatus(flatId, status, area);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update flat status.");
		}
	}

	@PutMapping("/update/{flatId}")
	public ResponseEntity<?> updateFlat(@RequestHeader("Authorization") String token,
			@PathVariable("flatId") long flatId, @RequestBody Map<String, Object> requestData) {
		try {
			return projectDetailsService.updateFlat(token, flatId, requestData);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to update details of flat");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/details/{page}")
	public ResponseEntity<?> getProjectDetails(@RequestHeader("Authorization") String token, @PathVariable int page) {
		try {
			return projectDetailsService.projectsDetails(token, page);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to fetch details");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/towerdetails/{projectId}")
	public ResponseEntity<?> TowersDetail(@RequestHeader("Authorization") String token,
			@PathVariable("projectId") long projectId) {
		try {
			return projectDetailsService.TowersDetail(token, projectId);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to fetch details");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/flatsdetails/{towerId}")
	public ResponseEntity<?> flatsDetail(@RequestHeader("Authorization") String token,
			@PathVariable("towerId") long towerId) {
		try {
			return projectDetailsService.flatsDetail(token, towerId);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to fetch details");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/gettower/{projectId}/{towerId}")
	public ResponseEntity<?> towerDetailsToFillArea(@RequestHeader("Authorization") String token,
			@PathVariable("projectId") long projectId, @PathVariable("towerId") long towerId) {
		try {
			return projectDetailsService.towerDetailsToFillArea(token, projectId, towerId);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to fetch details");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/update-flats/{towerId}")
	public ResponseEntity<?> updateTowerFlats(@RequestHeader("Authorization") String token, @PathVariable long towerId,
			@RequestBody List<Map<String, Object>> flatList) {
		try {
			return projectDetailsService.updateFlatsInTower(token, towerId, flatList);
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body("Unable to update details");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
