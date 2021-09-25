package com.ap.greenpole.clientCompanyModule.repositories;

import com.ap.greenpole.clientCompanyModule.entity.Votes;
import com.ap.greenpole.clientCompanyModule.entity.VotingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Lewis Aguh on 09/09/2020.
 */

@Repository
public interface VotesRepository extends JpaRepository<Votes, Long> {

    List<Votes> findAllByVotingProcess(VotingProcess votingProcess);
}
