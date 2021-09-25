package com.ap.greenpole.clientCompanyModule.repositories;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompanyReport;
import com.ap.greenpole.clientCompanyModule.entity.ShareCapitalManagers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Lewis.Aguh on 04/10/2020.
 */


@Repository
public interface ShareCapitalManagersRepository extends JpaRepository<ShareCapitalManagers, Long>{

	@Query(value = "SELECT * FROM share_capital_managers scm WHERE client_company_id = ?1", nativeQuery = true)
	public Page<ShareCapitalManagers> findAll(Long clientCompanyId, Pageable pageable);

	@Query(value = "SELECT * FROM share_capital_managers scm WHERE name = ?1 AND position = ?2 AND client_company_id = ?3", nativeQuery = true)
	public ShareCapitalManagers getManagerByNameAndPosition(String name, String position, long clientCompanyId);
}
