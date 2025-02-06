package com.crm.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectDetailsRepository extends JpaRepository<ProjectDetails, Long>{

}
