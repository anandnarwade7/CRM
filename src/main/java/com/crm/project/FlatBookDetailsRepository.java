package com.crm.project;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface FlatBookDetailsRepository extends JpaRepository<FlatBookDetails, Long> {

	FlatBookDetails findByFlatId(long flatId);
 
}