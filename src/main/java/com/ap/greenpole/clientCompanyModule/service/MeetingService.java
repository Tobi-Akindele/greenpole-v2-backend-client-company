package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.entity.*;

import java.util.List;
import java.util.Optional;

public interface MeetingService {

    Meeting create(Meeting meeting);

    Optional<Meeting> findById(long id);

    List<Meeting> getAllByClientCompany(ClientCompany clientCompany);

//    List<Long> findAllShareholdersInAttendance(Long meetingId);
}
