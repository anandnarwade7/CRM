package com.crm.importLead;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.user.Status;

@Repository
public interface ImportLeadRepository extends JpaRepository<ImportLead, Long> {

	boolean existsByEmailAndAdNameAndAdSetAndCampaignAndCity(String email, String adName, String adSet, String campaign,
			String city);

	@Query("SELECT l FROM ImportLead l WHERE l.assignedTo = 0")
	List<ImportLead> findLeadsWhereAssignedToIsZero();

	@Query("SELECT u FROM ImportLead u WHERE u.status = :status ORDER BY u.importedOn DESC")
	Page<ImportLead> findByStatusOrderByImportedOnDesc(@Param("status") Status status, Pageable pageable);

	@Query("SELECT l FROM ImportLead l WHERE l.assignedTo = :id ORDER BY l.importedOn DESC")
	Page<ImportLead> findByAssignedToOrderByImportedOnDesc(@Param("id") long id, Pageable pageable);

}
