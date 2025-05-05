package com.crm.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlatRepository extends JpaRepository<Flat, Long> {

	List<Flat> findByFloorId(long id);

	boolean existsByFlatNumberAndFloorId(int flatNumber, long id);

	List<Flat> findByFloor_Tower_Id(long towerId);

	@Query("SELECT f FROM Flat f " + "JOIN f.floor fl " + "JOIN fl.tower t " + "JOIN t.project p "
			+ "WHERE p.id = :projectId " + "AND t.id = :towerId " + "AND f.flatNumber >= 100 AND f.flatNumber < 200")
	List<Flat> findFlatsByProjectIdAndTowerIdAndFlatNumberStartsWith10(@Param("projectId") long projectId,
			@Param("towerId") long towerId);

	List<Flat> findByFloor_Tower_IdOrderByFlatNumberAsc(long towerId);

}
