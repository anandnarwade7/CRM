package com.crm.leads;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository  extends JpaRepository<LeadDetails, Long>{

	boolean existsByLeadEmailAndAdNameAndAdSetAndCampaignAndCity(String email, String ad, String adSet, String campaign,
			String city);

	@Query("SELECT l FROM LeadDetails l WHERE l.assignedTo = 0")
	List<LeadDetails> findByAssignedTo();

}
