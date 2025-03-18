package com.crm.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminsRepository extends JpaRepository<Admins, Long>{

	boolean existsByEmail(String email);

	Admins findByEmail(String email);

}
