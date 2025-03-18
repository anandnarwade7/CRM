package com.crm.project;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDetailsRepository extends JpaRepository<ProjectDetails, Long>{

	Optional<ProjectDetails> findByUserIdAndId(long userId, long projectId);

	Optional<ProjectDetails> findByPropertyName(String propertyName);

}
