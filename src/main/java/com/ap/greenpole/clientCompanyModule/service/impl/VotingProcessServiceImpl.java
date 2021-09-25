package com.ap.greenpole.clientCompanyModule.service.impl;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import com.ap.greenpole.clientCompanyModule.entity.Meeting;
import com.ap.greenpole.clientCompanyModule.entity.Votes;
import com.ap.greenpole.clientCompanyModule.entity.VotingProcess;
import com.ap.greenpole.clientCompanyModule.repositories.MeetingRepository;
import com.ap.greenpole.clientCompanyModule.repositories.VotesRepository;
import com.ap.greenpole.clientCompanyModule.repositories.VotingProcessRepository;
import com.ap.greenpole.clientCompanyModule.service.MeetingService;
import com.ap.greenpole.clientCompanyModule.service.VotingProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by Lewis Aguh on 18/09/2020.
 */

@Service
public class VotingProcessServiceImpl implements VotingProcessService {

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    VotesRepository votesRepository;

    @Autowired
    VotingProcessRepository votingProcessRepository;

    @Override
    public VotingProcess create(VotingProcess votingProcess) {
        return votingProcessRepository.save(votingProcess);
    }

    @Override
    public Optional<VotingProcess> findById(Long id) {
        return votingProcessRepository.findById(id);
    }

    @Override
    public VotingProcess findByTitleAndStatus(String title, String status, Long clientCompanyId) {
        return votingProcessRepository.findByTitleAndStatus(title, status, clientCompanyId);
    }

    @Override
    public List<VotingProcess> getAllVotingProcessByClientCompanyId(Long clientCompanyId) {
        return votingProcessRepository.findAllByClientCompanyId(clientCompanyId);
    }


}
