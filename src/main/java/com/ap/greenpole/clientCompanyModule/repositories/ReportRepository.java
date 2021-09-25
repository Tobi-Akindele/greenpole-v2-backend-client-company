package com.ap.greenpole.clientCompanyModule.repositories;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompanyReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Lewis.Aguh on 19/08/2020.
 */


@Repository
public interface ReportRepository extends JpaRepository<ClientCompanyReport, Long>{

	@Query(value = "SELECT * FROM reports rp WHERE (rp.is_deleted = false)", nativeQuery = true)
	public Page<ClientCompanyReport> findAll(Pageable pageable);

	@Query(value = "SELECT * FROM reports rp WHERE (rp.is_deleted = false)", nativeQuery = true)
	public Page<ClientCompanyReport> searchReports(String filename, String fromDate, String toDate, Pageable pageable);
}
