package com.crm.project;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TowerDetailsRepository extends JpaRepository<TowerDetails, Long>{

	List<TowerDetails> getByProjectId(long projectId);

	boolean existsByTowerNameAndProjectId(String towerName, long projectId);

	List<TowerDetails> getTowersByProjectId(long id);

}
