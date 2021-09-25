package com.ap.greenpole.clientCompanyModule.repositories;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


/**
 * Created by Nelu on 18/08/2020.
 */
@Repository
public interface ClientCompanyRepository extends PagingAndSortingRepository<ClientCompany, Long>, JpaSpecificationExecutor<ClientCompany> {

    ClientCompany getById(long id);

    @Query(value = "SELECT * FROM client_company WHERE status = ?1", nativeQuery = true)
    List<ClientCompany> findByStatusOrderByApprovedAtDesc(String status);
}
