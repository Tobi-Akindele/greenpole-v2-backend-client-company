package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import com.ap.greenpole.clientCompanyModule.entity.Meeting;
import com.ap.greenpole.clientCompanyModule.entity.Votes;
import com.ap.greenpole.clientCompanyModule.entity.VotingProcess;

import java.util.List;
import java.util.Optional;

public interface VotingProcessService {

    VotingProcess create(VotingProcess votingProcess);

    Optional<VotingProcess> findById(Long id);

    VotingProcess findByTitleAndStatus(String title, String status, Long clientCompanyId);

    List<VotingProcess> getAllVotingProcessByClientCompanyId(Long clientCompanyId);
}
