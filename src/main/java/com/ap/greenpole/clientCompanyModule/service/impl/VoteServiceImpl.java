package com.ap.greenpole.clientCompanyModule.service.impl;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import com.ap.greenpole.clientCompanyModule.entity.Meeting;
import com.ap.greenpole.clientCompanyModule.entity.Votes;
import com.ap.greenpole.clientCompanyModule.entity.VotingProcess;
import com.ap.greenpole.clientCompanyModule.repositories.MeetingRepository;
import com.ap.greenpole.clientCompanyModule.repositories.VotesRepository;
import com.ap.greenpole.clientCompanyModule.repositories.VotingProcessRepository;
import com.ap.greenpole.clientCompanyModule.service.MeetingService;
import com.ap.greenpole.clientCompanyModule.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by Lewis Aguh on 18/09/2020.
 */

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    VotesRepository votesRepository;

    @Override
    public Votes create(Votes votes) {
        return votesRepository.save(votes);
    }

    @Override
    public List<Votes> findByVotingProcess(VotingProcess votingProcess) {
        return votesRepository.findAllByVotingProcess(votingProcess);
    }

}
