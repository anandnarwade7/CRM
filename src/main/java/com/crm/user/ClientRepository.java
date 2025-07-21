package com.crm.user;

import java.util.List;
import java.util.Optional;

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

//	@Query("SELECT c FROM Client c JOIN c.crmIds crmId WHERE c.clientsLeadId = :leadId AND crmId = :crmManagerId")
//	List<Client> findByClientsLeadIdAndSalesIdAndCrmId(@Param("leadId") Long leadId, @Param("crmManagerId") Long crmManagerId);

	@Query(value = "SELECT * FROM client c " + "WHERE c.clients_lead_id = :leadId "
			+ "AND EXISTS (SELECT 1 FROM client_crm_ids crm WHERE crm.client_id = c.id AND crm.crm_id = :crmManagerId) "
			+ "AND EXISTS (SELECT 1 FROM client_sales_ids sales WHERE sales.client_id = c.id AND sales.sales_id = :salesManagerId)", nativeQuery = true)
	Optional<Client> findByClientsLeadIdAndSalesIdAndCrmId(@Param("leadId") Long leadId,
			@Param("salesManagerId") Long salesManagerId, @Param("crmManagerId") Long crmManagerId);

	@Query("SELECT c FROM Client c WHERE c.clientsLeadId = :leadId")
	List<Client> findAllByClientsLeadId(@Param("leadId") Long leadId);

	Optional<Client> findByClientsLeadId(Long clientId);

}
