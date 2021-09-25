package com.ap.greenpole.clientCompanyModule.controllers;

import com.ap.greenpole.clientCompanyModule.dtos.ClientCompanyAPIResponseCode;
import com.ap.greenpole.clientCompanyModule.dtos.MeetingRequestDto;
import com.ap.greenpole.clientCompanyModule.entity.*;
import com.ap.greenpole.clientCompanyModule.exceptions.BadRequestException;
import com.ap.greenpole.clientCompanyModule.exceptions.NotFoundException;
import com.ap.greenpole.clientCompanyModule.service.*;
import com.ap.greenpole.clientCompanyModule.utils.ConstantUtils;
import com.ap.greenpole.usermodule.annotation.PreAuthorizePermission;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.ap.greenpole.clientCompanyModule.controllers.ApiParameters.CLIENT_COMPANY_ID;
import static com.ap.greenpole.clientCompanyModule.controllers.ApiParameters.VOTING_PROCESS_ID;
import static com.ap.greenpole.clientCompanyModule.controllers.util.EntityMapper.*;
/**
 * Created by Nelu Akejelu on 18/08/2020.
 */

@RestController
@RequestMapping(path= "/api/v1/client/{client-company-id}/meeting", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class MeetingController {

    private static Logger logger = LoggerFactory.getLogger(Meeting.class);

    @Autowired
    ClientCompanyService clientCompanyService;

    @Autowired
    MeetingService meetingService;

    @Autowired
    VotingProcessService votingProcessService;

    @Autowired
    VoteService voteService;

    @Autowired
    ShareHolderService shareHolderService;

    @Autowired
    VotesService votesService;

    @Autowired
    UserService userService;

    @Transactional
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_SETUP_GENERAL_MEETING"})
    public ResponseEntity<?> createMeeting(@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId, @RequestBody @Valid MeetingRequestDto meetingRequestDto, HttpServletRequest request) {
        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        if (clientCompany == null) {
            return new ResponseEntity<>("Client company does not exist", HttpStatus.BAD_REQUEST);
        }
        Meeting meeting = mapDtoToMeeting(meetingRequestDto);
        meeting.setClientCompanyId(clientCompany.getId());

        return new ResponseEntity<>(mapMeetingToResponseDto(meetingService.create(meeting)), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_GENERAL_MEETING"})
    public ResponseEntity<?> getAllMeetingsByClientId(@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId, HttpServletRequest request) {

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        if (clientCompany == null) {
            return new ResponseEntity<>("Client company does not exist", HttpStatus.BAD_REQUEST);
        }
        List<Meeting> meetings = meetingService.getAllByClientCompany(clientCompany);
        return new ResponseEntity<>(mapMeetingsToDtos(meetings), HttpStatus.OK);
    }

    @RequestMapping(value = "/voting/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_CREATE_VOTING_PROCESS"})
    public ClientCompanyBaseResponse<VotingProcess> createVotingProcess(@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId,
            @RequestHeader long meetingId, @RequestHeader String title, @RequestHeader String notes, HttpServletRequest req) {

        ClientCompanyBaseResponse<VotingProcess> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(req.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        try {
            // validate users input
            if (title == null || title == "") {
                response.setStatusMessage("Title is required.");
                response.setHttpStatus("400");
                return response;
            }

            if (meetingId <= 0) {
                response.setStatusMessage("Meeting ID is required.");
                response.setHttpStatus("400");
                return response;
            }

            // check if client company and meeting exists
            Optional<Meeting> meeting = meetingService.findById(meetingId);
            if (!meetingService.findById(meetingId).isPresent()) {
                response.setStatusMessage("No Meeting Found.");
                response.setHttpStatus("404");
                return response;
            }

            ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
            if (clientCompany == null) {
                response.setStatusMessage("Client company does not exist or has been deactivated.");
                response.setHttpStatus("404");
                return response;
            }

            VotingProcess votingProcess = new VotingProcess();
            votingProcess.setClientCompanyId(clientCompanyId);
            votingProcess.setMeeting(meeting.get());
            votingProcess.setNotes(notes);
            votingProcess.setTitle(title);
            votingProcess.setStatus(ConstantUtils.VOTING_PROCESS_STATUS[0]);
            votingProcess.setCreated_on(java.time.LocalDateTime.now());

            // check if any voting process is active with same title
            VotingProcess vp = votingProcessService.findByTitleAndStatus(title, ConstantUtils.VOTING_PROCESS_STATUS[0], clientCompanyId);
            if(vp != null) {
                response.setStatusMessage("An Active Voting Process Already Exist.");
                response.setHttpStatus("400");
                return response;
            }

            VotingProcess request = votingProcessService.create(votingProcess);

            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(request);
            return response;

        } catch (Exception ex) {
            logger.info("[-] Exception occurred while creating voting process {}", ex.getMessage());
            response.setStatusMessage("Error processing request.");
            response.setHttpStatus("500");
        }

        return response;
    }

    @RequestMapping(value = "/voting/vote", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_CAST_VOTE"})
    public ClientCompanyBaseResponse<ResponseEntity<?>> vote (@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId,
                                  @RequestHeader Long votingProcessId, @RequestHeader String verdict, @RequestHeader long meetingId, @RequestHeader Long shareholderId, HttpServletRequest req) {

        ClientCompanyBaseResponse<ResponseEntity<?>> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(req.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        ResponseEntity<?> responseEntity = null;
        try {
            // validate users input
            if (votingProcessId == null) {
                response.setStatusMessage("Voting process ID is required.");
                response.setHttpStatus("400");
                return response;
            }

            if (meetingId <= 0) {
                response.setStatusMessage("Meeting ID is required.");
                response.setHttpStatus("400");
                return response;
            }

            if (shareholderId <= 0) {
                response.setStatusMessage("Shareholder ID is required\".");
                response.setHttpStatus("400");
                return response;
            }

            if (verdict == null || verdict == "") {
                response.setStatusMessage("Verdict is required.");
                response.setHttpStatus("400");
                return response;
            }

            if (!Arrays.asList(ConstantUtils.VOTING_OPTIONS).contains(verdict)) {
                response.setStatusMessage("Verdict is not supported.");
                response.setHttpStatus("400");
                return response;
            }

            Optional<VotingProcess> votingProcess = votingProcessService.findById(votingProcessId);
            if (!votingProcessService.findById(votingProcessId).isPresent()) {
                response.setStatusMessage("No Voting Process Found.");
                response.setHttpStatus("404");
                return response;
            }

            // check if voting process is still active
            if(!votingProcess.get().getStatus().equals(ConstantUtils.VOTING_PROCESS_STATUS[0])) {
                response.setStatusMessage("Voting process is not active.");
                response.setHttpStatus("400");
                return response;
            }

            ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
            if (clientCompany == null) {
                response.setStatusMessage("Client company does not exist or has been deactivated.");
                response.setHttpStatus("404");
                return response;
            }

            Optional<Meeting> meeting = meetingService.findById(meetingId);
            if (!meetingService.findById(meetingId).isPresent()) {
                response.setStatusMessage("No Meeting Found.");
                response.setHttpStatus("404");
                return response;
            }

            Shareholder shareholder = shareHolderService.findById(shareholderId);
            if (shareholder == null) {
                response.setStatusMessage("Shareholder does not exist.");
                response.setHttpStatus("404");
                return response;
            }

            Votes votes = new Votes();
            votes.setShareholder_id(shareholderId);
            votes.setVerdict(verdict);
            votes.setVotingProcess(votingProcess.get());
            votes.setVoted_at(java.time.LocalDateTime.now());

            // get all shareholders in attendance
            List<Long> shareholderIds = new ArrayList<>();
            shareholderIds = meeting.get().getAttendees();

            // get all votes for voting process and check if shareholder already voted
            List<Votes> votesList = voteService.findByVotingProcess(votingProcess.get());

            for(Votes v : votesList){
                if(v.getShareholder_id() == shareholderId) {
                    response.setStatusMessage("You have already casted a vote.");
                    response.setHttpStatus("400");
                    return response;
                }
            }

            // create vote if shareholder has not voted
            Votes request = voteService.create(votes);

            // automatically end voting process if all shareholders in attendance has voted, count vote and return
            votesList.add(request);
            Boolean allHasVoted = votesService.listEqualsIgnoreOrder(shareholderIds, votesList);
            if(allHasVoted == true) {
                responseEntity = votesService.endVotingProcess(votingProcess.get());

                response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
                response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
                response.setHttpStatus("200");
                response.setData(responseEntity);
                return response;
            }

            responseEntity = new ResponseEntity<>(request, HttpStatus.OK);
            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(responseEntity);

            return response;
        } catch (Exception ex) {
            logger.info("[-] Exception occurred while creating voting process {}", ex.getMessage());
            response.setStatusMessage("Error processing request.");
            response.setHttpStatus("500");
        }

        return response;
    }

    @RequestMapping(value = "/voting/cancel", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_CANCEL_VOTING_PROCESS"})
    public ClientCompanyBaseResponse<VotingProcess> cancelVotingProcess(@RequestHeader Long votingProcessId, HttpServletRequest req) {
        ClientCompanyBaseResponse<VotingProcess> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(req.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        try {
            // validate users input
            if (votingProcessId == null) {
                response.setStatusMessage("Voting process ID is required.");
                response.setHttpStatus("400");
                return response;
            }

            Optional<VotingProcess> votingProcess = votingProcessService.findById(votingProcessId);
            if (!meetingService.findById(votingProcessId).isPresent()) {
                response.setStatusMessage("No Voting Process Found.");
                response.setHttpStatus("404");
                return response;
            }

            if(votingProcess.get().getStatus() != ConstantUtils.VOTING_PROCESS_STATUS[0]) {
                response.setStatusMessage("Voting process is not active.");
                response.setHttpStatus("400");
                return response;
            }


            votingProcess.get().setStatus(ConstantUtils.VOTING_PROCESS_STATUS[2]);
            votingProcess.get().setEnded_at(java.time.LocalDateTime.now());

            VotingProcess request = votingProcessService.create(votingProcess.get());
            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(request);

            return response;
        } catch (Exception ex) {
            logger.info("[-] Exception occurred while cancelling voting process {}", ex.getMessage());
            response.setStatusMessage("Error processing request.");
            response.setHttpStatus("500");
        }

        return response;
    }

    @RequestMapping(value = "/voting/end", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_END_VOTING_PROCESS"})
    public ClientCompanyBaseResponse<ResponseEntity<?>> endVotingProcess(@RequestHeader Long votingProcessId, HttpServletRequest request) {
        ClientCompanyBaseResponse<ResponseEntity<?>> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        try {
            // validate users input
            if (votingProcessId == null) {
                response.setStatusMessage("Voting process ID is required.");
                response.setHttpStatus("400");
                return response;
            }

            Optional<VotingProcess> votingProcess = votingProcessService.findById(votingProcessId);
            if (!meetingService.findById(votingProcessId).isPresent()) {
                response.setStatusMessage("No Voting Process Found.");
                response.setHttpStatus("404");
                return response;
            }

            if(votingProcess.get().getStatus() != ConstantUtils.VOTING_PROCESS_STATUS[0]) {
                response.setStatusMessage("Voting process is not active.");
                response.setHttpStatus("400");
                return response;
            }

            ResponseEntity<?> res = votesService.endVotingProcess(votingProcess.get());

            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(res);

            return response;
        } catch (Exception ex) {
            logger.info("[-] Exception occurred while cancelling voting process {}", ex.getMessage());
            response.setStatusMessage("Error processing request.");
            response.setHttpStatus("500");
        }

        return response;
    }

    @RequestMapping(value = "/voting/all", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_ALL_VOTING_PROCESS"})
    public ClientCompanyBaseResponse<List<VotingProcess>> getAllVotingProcessByClientId(@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId, HttpServletRequest request) {

        ClientCompanyBaseResponse<List<VotingProcess>> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        if (clientCompany == null) {
            response.setStatusMessage("Client company does not exist.");
            response.setHttpStatus("400");
            return response;
        }

        List<VotingProcess> votingProcesses = votingProcessService.getAllVotingProcessByClientCompanyId(clientCompanyId);

        response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
        response.setHttpStatus("200");
        response.setData(votingProcesses);

        return response;
    }

    @RequestMapping(value = "/voting/{voting-process-id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_VOTING_PROCESS_BY_ID"})
    public ClientCompanyBaseResponse<VotingProcess> getVotingProcessById(
            @PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId,
            @PathVariable(name = VOTING_PROCESS_ID) long votingProcessId, HttpServletRequest request) {

        ClientCompanyBaseResponse<VotingProcess> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        if (clientCompany == null) {
            response.setStatusMessage("Client company does not exist.");
            response.setHttpStatus("404");
            return response;
        }

        Optional<VotingProcess> votingProcess = votingProcessService.findById(votingProcessId);
        if (!votingProcessService.findById(votingProcessId).isPresent()) {
            response.setStatusMessage("No Voting Process Found.");
            response.setHttpStatus("404");
            return response;
        }

        response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
        response.setHttpStatus("200");
        response.setData(votingProcess.get());

        return response;
    }


}
