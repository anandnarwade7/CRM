package com.crm.leads;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crm.user.Status;

@Repository
public interface LeadRepository  extends JpaRepository<LeadDetails, Long>{

	boolean existsByLeadEmailAndAdNameAndAdSetAndCampaignAndCity(String email, String ad, String adSet, String campaign,
			String city);

	@Query("SELECT l FROM LeadDetails l WHERE l.assignedTo = 0")
	List<LeadDetails> findByAssignedTo();

	Page<LeadDetails> findByStatusOrderByCreateOnDesc(Status status, Pageable pageable);

	boolean existsByLeadEmailAndCity(String email, String city);

	Page<LeadDetails> findByAssignedToOrderByCreateOnDesc(long userId, Pageable pageable);

	long countByAssignedTo(Long userId);
	
	@Query("SELECT COUNT(l.id) FROM LeadDetails l WHERE l.assignedTo = :userId AND l.status <> :status")
	long countLeadsByUserIdAndStatusNotAssigned(@Param("userId") Long userId, @Param("status") Status status);

	long countLeadsByAssignedTo(long id);

}
