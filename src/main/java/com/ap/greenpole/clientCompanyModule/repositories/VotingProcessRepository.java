package com.ap.greenpole.clientCompanyModule.repositories;

import com.ap.greenpole.clientCompanyModule.entity.Meeting;
import com.ap.greenpole.clientCompanyModule.entity.VotingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Lewis Aguh on 09/09/2020.
 */

@Repository
@Transactional
public interface VotingProcessRepository extends JpaRepository<VotingProcess, Long> {

    @Query("SELECT vp FROM voting_process vp WHERE title = ?1 AND status = ?2 AND client_company_id = ?3")
    VotingProcess findByTitleAndStatus(String title, String status, Long clientCompanyId);

    List<VotingProcess> findAllByClientCompanyId(Long clientCompanyId);

}
