package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.entity.Shareholder;
import com.ap.greenpole.clientCompanyModule.entity.Votes;
import com.ap.greenpole.clientCompanyModule.entity.VotingProcess;
import com.ap.greenpole.clientCompanyModule.utils.ConstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Lewis.Aguh on 16/09/2020
 */

@Service
public class VotesService {

    @Autowired
    VotingProcessService votingProcessService;

    @Autowired
    VoteService voteService;

    public static<T> boolean listEqualsIgnoreOrder(List<Long> shareholdersIds, List<Votes> votesList) {
        List<Long> shareholdersIdsVoted = new ArrayList<>();

        for(Votes votes: votesList) {
            shareholdersIdsVoted.add(votes.getShareholder_id());
        }

        return new HashSet<>(shareholdersIds).equals(new HashSet<>(shareholdersIdsVoted));
    }

    public ResponseEntity<?> endVotingProcess(VotingProcess votingProcess) {
        votingProcess.setStatus(ConstantUtils.VOTING_PROCESS_STATUS[1]);
        votingProcess.setEnded_at(java.time.LocalDateTime.now());
        votingProcessService.create(votingProcess);

        List<Votes> votesList = voteService.findByVotingProcess(votingProcess);

        return new ResponseEntity<>(votesList, HttpStatus.OK);
    }

}
