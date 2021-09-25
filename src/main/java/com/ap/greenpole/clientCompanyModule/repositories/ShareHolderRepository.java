package com.ap.greenpole.clientCompanyModule.repositories;

import com.ap.greenpole.clientCompanyModule.entity.Shareholder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareHolderRepository extends CrudRepository<Shareholder, Long> {

    @Query(value = "SELECT * FROM shareholder WHERE client_company = ?1 and status = ?2", nativeQuery = true)
    List<Shareholder> findAllByClientCompany(long clientCompanyId, String status);

    @Query(value = "SELECT * FROM shareholder WHERE shareholder_id = ?1 and status = ?2", nativeQuery = true)
    Shareholder findByShareholder_id(long shareHolderId, String status);

    @Query(value = "SELECT * FROM shareholder WHERE status = ?1", nativeQuery = true)
    List<Shareholder> findByStatus(String status);

    @Query(value = "SELECT * FROM shareholder WHERE bvn = ?1 and status = ?2", nativeQuery = true)
    List<Shareholder> findByBvnandAndStatusOrderByCreatedOnDesc(String bvn, String status);

}
