package com.ap.greenpole.clientCompanyModule.controllers;


import com.ap.greenpole.clientCompanyModule.dtos.AttendanceRequestDto;
import com.ap.greenpole.clientCompanyModule.entity.Meeting;
import com.ap.greenpole.clientCompanyModule.entity.Shareholder;
import com.ap.greenpole.clientCompanyModule.service.ClientCompanyService;
import com.ap.greenpole.clientCompanyModule.service.MeetingService;
import com.ap.greenpole.clientCompanyModule.service.ShareHolderService;
import com.ap.greenpole.usermodule.annotation.PreAuthorizePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ap.greenpole.clientCompanyModule.controllers.ApiParameters.MEETING_ID;
import static com.ap.greenpole.clientCompanyModule.controllers.ApiPaths.ATTENDANCE_PATH;
import static com.ap.greenpole.clientCompanyModule.controllers.util.EntityMapper.mapMeetingToAttendanceResponse;

@RestController
@RequestMapping(path= "/api/v1/client/{client-company-id}/meeting/{meeting-id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class AttendanceController {

    @Autowired
    ClientCompanyService clientCompanyService;

    @Autowired
    MeetingService meetingService;

    @Autowired
    ShareHolderService shareHolderService;


    @RequestMapping(path = ATTENDANCE_PATH, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_CREATE_ATTENDANCE"})
    public ResponseEntity<?> createAttendance(@PathVariable(name = MEETING_ID) long meetingId, @Valid @RequestBody AttendanceRequestDto attendanceRequestDto) {
        Optional<Meeting> meeting = meetingService.findById(meetingId);

        if (meeting.isPresent()) {
            meeting.get().setAttendees(attendanceRequestDto.getAttendeesIds());
            Meeting meetingWithAttendance = meetingService.create(meeting.get());
            List<Shareholder> attendees = meetingWithAttendance.getAttendees()
                    .stream()
                    .map(attendeeId -> shareHolderService.findById(attendeeId))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(mapMeetingToAttendanceResponse(meetingWithAttendance,attendees), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Meeting with ID " + meetingId + "does not exist", HttpStatus.BAD_REQUEST);
        }
    }

}
