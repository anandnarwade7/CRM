package com.crm.eventDetails;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDetailsRepository extends JpaRepository<EventDetails, Long> {

	EventDetails findByEventIdAndCrManagerId(long eventId, long crManagerId);

	List<EventDetails> findByCrManagerId(long crManagerId);

	EventDetails deleteByEventId(long eventId);

	EventDetails getEventDetailsByEventIdAndLeadId(long eventId, long leadId);

	List<EventDetails> findByLeadId(long leadId);

	EventDetails deleteAllByClientId(long clientId);

}
