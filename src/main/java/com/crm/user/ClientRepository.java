package com.crm.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

	boolean existsByEmail(String email);

	Client findByEmail(String email);

	@Query(value = "SELECT * FROM client WHERE :id = ANY(sales_id)", nativeQuery = true)
	List<Client> findClientsBySalesId(@Param("id") Long id);

	@Query(value = "SELECT c.* FROM client c " + "JOIN client_sales_ids cu ON c.id = cu.client_id "
			+ "WHERE c.email = :email AND cu.sales_id = :salesId", nativeQuery = true)
	Client findByEmailAndSalesId(@Param("email") String email, @Param("salesId") Long salesId);

	@Query("SELECT DISTINCT c FROM Client c JOIN c.crmIds crmId WHERE crmId IN :crmIds")
	List<Client> findByCrmIdIn(@Param("crmIds") List<Long> crmIds);

	@Query(value = "SELECT DISTINCT c.* FROM client c JOIN client_sales_ids cs ON c.id = cs.client_id WHERE cs.sales_id IN (:salesIds)", nativeQuery = true)
	List<Client> findBySalesIdIn(@Param("salesIds") List<Long> salesIds);

}
