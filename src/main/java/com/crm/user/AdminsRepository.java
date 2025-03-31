package com.crm.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminsRepository extends JpaRepository<Admins, Long> {

	boolean existsByEmail(String email);

	@Query("SELECT a FROM Admins a WHERE a.email = :email")
	Admins findByEmail(String email);

	Admins findByRole(String adminRole);

	Page<Admins> findByRoleOrderByCreatedOnDesc(String string, Pageable pageable);

	@Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
	long adminsCountByRole(@Param("role") String role);

	List<Admins> getByRole(String string);

}
