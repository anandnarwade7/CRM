package com.crm.project;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TowerDetailsRepository extends JpaRepository<TowerDetails, Long> {

	List<TowerDetails> getByProjectId(long projectId);

	boolean existsByTowerNameAndProjectId(String towerName, long projectId);

	List<TowerDetails> getTowersByProjectId(long id);

	@Query("SELECT COUNT(l.id) FROM TowerDetails l WHERE l.project.id = :id")
	long findByProjectId(@Param("id") long id);

	@Query("SELECT SUM(t.totalFloors) FROM TowerDetails t WHERE t.project.id = :projectId")
	long getTotalFloorsByProjectId(@Param("projectId") Long projectId);

}
