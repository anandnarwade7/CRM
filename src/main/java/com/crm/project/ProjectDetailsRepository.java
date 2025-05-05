package com.crm.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDetailsRepository extends JpaRepository<ProjectDetails, Long>{

	ProjectDetails findByUserIdAndId(long userId, long projectId);

	ProjectDetails findByPropertyName(String propertyName);

	Page<ProjectDetails> findByUserIdOrderByCreatedOnDesc(long userId, Pageable pageable);

}
