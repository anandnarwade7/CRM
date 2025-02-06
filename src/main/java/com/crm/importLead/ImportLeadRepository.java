package com.crm.importLead;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportLeadRepository extends JpaRepository<ImportLead, Long> {

	boolean existsByAdNameAndAdSetAndCampaignAndCity(String adName, String adSet, String campaign, String city);

	@Query("SELECT l FROM ImportLead l WHERE l.assignedTo = 0")
	List<ImportLead> findLeadsWhereAssignedToIsZero();

}
