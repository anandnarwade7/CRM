package com.crm.support;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {

	Page<Support> findByAdminIdOrderByCreatedOnDesc(long id, Pageable pageable);

	Page<Support> findByUserIdAndRoleAndEmailOrderByCreatedOnDesc(long id, String role, String email, Pageable pageable);

	List<Support> findByUserId(long userId);
}
