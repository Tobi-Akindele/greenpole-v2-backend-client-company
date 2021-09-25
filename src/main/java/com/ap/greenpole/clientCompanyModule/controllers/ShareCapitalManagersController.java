package com.ap.greenpole.clientCompanyModule.controllers;

import com.ap.greenpole.clientCompanyModule.dtos.ClientCompanyAPIResponseCode;
import com.ap.greenpole.clientCompanyModule.entity.*;
import com.ap.greenpole.clientCompanyModule.entity.Error;
import com.ap.greenpole.clientCompanyModule.exceptions.BadRequestException;
import com.ap.greenpole.clientCompanyModule.exceptions.NotFoundException;
import com.ap.greenpole.clientCompanyModule.service.ClientCompanyService;
import com.ap.greenpole.clientCompanyModule.service.ProcessShareCapitalManagersService;
import com.ap.greenpole.clientCompanyModule.service.ReportService;
import com.ap.greenpole.clientCompanyModule.service.ShareCapitalManagersService;
import com.ap.greenpole.clientCompanyModule.utils.ConstantUtils;
import com.ap.greenpole.clientCompanyModule.utils.Utils;
import com.ap.greenpole.usermodule.annotation.PreAuthorizePermission;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Lewis.Aguh on 19/08/2020.
 */

@RestController
@RequestMapping(path= "/api/v1/client/sharecapitalmanagers")
public class ShareCapitalManagersController {

    private static Logger logger = LoggerFactory.getLogger(ShareCapitalManagersController.class);

    @Autowired
    ShareCapitalManagersService shareCapitalManagersService;

    @Autowired
    ProcessShareCapitalManagersService processShareCapitalManagersService;

    @Autowired
    ClientCompanyService clientCompanyService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/all", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_ALL_CLIENT_COMPANY_SHARE_CAPITAL_MANAGERS"})
    @ResponseStatus(value = HttpStatus.OK)
    public ClientCompanyBaseResponse<Result<ShareCapitalManagers>> getAllShareCapitalManagers(@RequestHeader(value = "pageSize", required = false) String pageSize,
                                                                                              @RequestHeader(value = "pageNumber", required = false) String pageNumber,
                                                                                              @RequestHeader(value = "clientCompanyId", required = false) Long clientCompanyId, HttpServletRequest request) {

        ClientCompanyBaseResponse<Result<ShareCapitalManagers>> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        try {
            logger.info("[+] Attempting to parse the pagination variables");
            int page = Integer.parseInt(pageNumber);
            int size = Integer.parseInt(pageSize);
            page = Math.max(0, page - 1);
            size = Math.max(1, size);
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            if(clientCompanyId == null) {
                response.setStatusMessage("Client company ID is required.");
                response.setHttpStatus("400");
                return response;
            }

            ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
            if (clientCompany == null) {
                response.setStatusMessage("Client company does not exist or has been deactivated.");
                response.setHttpStatus("404");
                return response;
            }

            Result<ShareCapitalManagers> res =  shareCapitalManagersService.getAllShareCapitalManagers(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), pageable, clientCompanyId);
            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(res);
            return response;
        } catch (NumberFormatException e) {
            logger.error("[+] Error {} occurred while parsing page variable with message: {}",
                    e.getClass().getSimpleName(), e.getMessage());

            response.setStatusMessage("The entered page and size must be integer values.");
            response.setHttpStatus("400");
        }

        return response;
    }

    @RequestMapping(value = "/available", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_AVAILABLE_CLIENT_COMPANY_MANAGERS"})
    @ResponseStatus(value = HttpStatus.OK)
    public ClientCompanyBaseResponse<List<ShareCapitalManagers>> getAvailableManagers(@RequestHeader(value = "clientCompanyId", required = false) Long clientCompanyId, HttpServletRequest request) {
        ClientCompanyBaseResponse<List<ShareCapitalManagers>> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        try {
            if(clientCompanyId == null) {
                response.setStatusMessage("Client company ID is required.");
                response.setHttpStatus("400");
                return response;
            }

            ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
            if (clientCompany == null) {
                response.setStatusMessage("Client company does not exist or has been deactivated.");
                response.setHttpStatus("404");
                return response;
            }

            List<ShareCapitalManagers> res = processShareCapitalManagersService.processShareCapitalManagers(clientCompany);
            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(res);
            return response;

        } catch (NumberFormatException e) {
            logger.error("[+] Error {} occurred while parsing fetching available managers with message: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
            response.setHttpStatus("500");
        }

        return response;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_REGISTER_CLIENT_COMPANY_SHARE_CAPITAL_MANAGERS"})
    @ResponseStatus(value = HttpStatus.OK)
    public ClientCompanyBaseResponse<List<ShareCapitalManagers>> registerManagers(@RequestHeader Long clientCompanyId, @RequestBody List<ShareCapitalManagers> shareCapitalManagersList, HttpServletRequest request) {
        ClientCompanyBaseResponse<List<ShareCapitalManagers>> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        try {
            if(clientCompanyId == null) {
                response.setStatusMessage("Client company ID is required.");
                response.setHttpStatus("400");
                return response;
            }

            ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
            if (clientCompany == null) {
                response.setStatusMessage("Client company does not exist or has been deactivated.");
                response.setHttpStatus("404");
                return response;
            }

            //check if manager already registered
            List<com.ap.greenpole.clientCompanyModule.entity.Error> errors = new ArrayList<>();
            for(ShareCapitalManagers scm: shareCapitalManagersList) {
                scm.setClientCompanyId(clientCompanyId);
                ShareCapitalManagers manager = shareCapitalManagersService.getManagerByNameAndPosition(scm.getName(), scm.getPosition(), scm.getClientCompanyId());
                if(manager != null) {
                    Error error = new Error(null, "Manager : " + manager.getName() +" already registered");
                    errors.add(error);
                }
            }

            if (!errors.isEmpty()) {
                response.setStatusMessage(errors.toString());
                response.setHttpStatus("404");
                return response;
            }

            for(ShareCapitalManagers scm: shareCapitalManagersList) {
                scm.setCreated_on(java.time.LocalDateTime.now());
                shareCapitalManagersService.create(scm);
            }

            List<ShareCapitalManagers> res =  shareCapitalManagersList;
            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(res);
            return response;
        } catch (Exception e) {
            logger.error("[+] Error {} occurred while creating managers: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
            response.setHttpStatus("500");
        }

        return response;
    }



}
