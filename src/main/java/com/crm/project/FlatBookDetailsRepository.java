package com.crm.project;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface FlatBookDetailsRepository extends JpaRepository<FlatBookDetails, Long> {

	FlatBookDetails findByFlatId(long flatId);

	List<FlatBookDetails> findBySalesId(long salesId);

	List<FlatBookDetails> findByCrmId(long crmId);

	List<FlatBookDetails> getByAdminId(long id);
}