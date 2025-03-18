package com.crm.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlatRepository extends JpaRepository<Flat, Long>{

	List<Flat> findByFloorId(long id);

	boolean existsByFlatNumberAndFloorId(int flatNumber, long id);

}
