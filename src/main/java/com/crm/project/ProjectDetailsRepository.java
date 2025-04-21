package com.crm.project;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDetailsRepository extends JpaRepository<ProjectDetails, Long>{

	ProjectDetails findByUserIdAndId(long userId, long projectId);

	ProjectDetails findByPropertyName(String propertyName);

	List<ProjectDetails> findByUserId(long userId);

}
