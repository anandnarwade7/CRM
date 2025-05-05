package com.crm.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>{

	boolean existsByEmail(String email);

	Client findByEmail(String email);

	List<Client> findClientsByUserId(long id);
}
