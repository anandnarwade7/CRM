package com.crm.project;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorDetailsRepository extends JpaRepository<FloorDetails, Long>{

	List<FloorDetails> getByTowerId(long id);

	boolean existsByFloorNameAndTowerId(String flatNumber, long id);

	List<FloorDetails> getFloorDetailsByTowerId(long id);

}
