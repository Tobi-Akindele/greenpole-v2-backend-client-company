package com.ap.greenpole.clientCompanyModule.service.impl;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import com.ap.greenpole.clientCompanyModule.entity.Meeting;
import com.ap.greenpole.clientCompanyModule.entity.Votes;
import com.ap.greenpole.clientCompanyModule.entity.VotingProcess;
//import com.ap.greenpole.clientCompanyModule.repositories.MeetingAttendeesRepository;
import com.ap.greenpole.clientCompanyModule.repositories.MeetingRepository;
import com.ap.greenpole.clientCompanyModule.repositories.VotesRepository;
import com.ap.greenpole.clientCompanyModule.repositories.VotingProcessRepository;
import com.ap.greenpole.clientCompanyModule.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by Nelu Akejelu on 18/08/2020.
 */

@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    MeetingRepository meetingRepository;

//    @Autowired
//    MeetingAttendeesRepository meetingAttendeesRepository;

    @Autowired
    VotesRepository votesRepository;

    @Autowired
    VotingProcessRepository votingProcessRepository;

    @Override
    public Meeting create(Meeting meeting) {
        meeting.setDateCreated(LocalDateTime.now());
        return meetingRepository.save(meeting);
    }

    @Override
    public Optional<Meeting> findById(long id) {
        return meetingRepository.findById(id);
    }

    @Override
    public List<Meeting> getAllByClientCompany(ClientCompany clientCompany) {
        return meetingRepository.findAllByClientCompanyId(clientCompany.getId());
    }

//    @Override
//    public List<Long> findAllShareholdersInAttendance(Long meetingId) {
//        return MeetingAttendeesRepository.findAllShareholdersInAttendance(meetingId);
//    }


}
