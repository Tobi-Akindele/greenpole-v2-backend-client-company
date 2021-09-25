package com.ap.greenpole.clientCompanyModule.controllers;

import com.ap.greenpole.clientCompanyModule.dtos.ClientCompanyAPIResponseCode;
import com.ap.greenpole.clientCompanyModule.entity.*;
import com.ap.greenpole.clientCompanyModule.exceptions.BadRequestException;
import com.ap.greenpole.clientCompanyModule.service.ReportService;
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
@RequestMapping(path= "/api/v1/client/report")
public class ReportController {

    private static Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    ReportService reportService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/archived", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_ALL_CLIENT_COMPANY_REPORTS"})
    @ResponseStatus(value = HttpStatus.OK)
    public ClientCompanyBaseResponse<Result<ClientCompanyReport>> getAllReports(@RequestHeader(value = "pageSize", required = false) String pageSize,
                                                                                @RequestHeader(value = "pageNumber", required = false) String pageNumber, HttpServletRequest request) {

        ClientCompanyBaseResponse<Result<ClientCompanyReport>> response = new ClientCompanyBaseResponse<>();
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

            Result<ClientCompanyReport> res = reportService.getAllReports(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), pageable);
            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(res);
        } catch (NumberFormatException e) {
            logger.error("[+] Error {} occurred while parsing page variable with message: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            response.setStatusMessage("The entered page and size must be integer values.");
            response.setHttpStatus("400");
        }

        return response;
    }

    @RequestMapping(value = "/search/archived", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_SEARCH_CLIENT_COMPANY_REPORTS"})
    @ResponseStatus(value = HttpStatus.OK)
    public ClientCompanyBaseResponse<Result<ClientCompanyReport>> searchReports(@RequestHeader(value = "reportName", required = false) String filename,
                                                     @RequestHeader(value = "pageNumber", required = false) String pageNumber,
                                                     @RequestHeader(value = "pageSize", required = false) String pageSize,
                                                     @RequestHeader(value = "fromDate", required = false) String fromDate,
                                                     @RequestHeader(value = "toDate", required = false) String toDate, HttpServletRequest request) throws ParseException {

        ClientCompanyBaseResponse<Result<ClientCompanyReport>> response = new ClientCompanyBaseResponse<>();
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

            if(!Utils.isEmptyString(toDate) && Utils.isEmptyString(fromDate)) {
                response.setStatusMessage("Start date cannot be empty.");
                response.setHttpStatus("400");
                return response;
            }
            if (!Utils.isEmptyString(fromDate) && !Utils.isValidDateFormat("yyyy-MM-dd", fromDate, Locale.ENGLISH)) {
                response.setStatusMessage("Please specify a valid date for start date (yyyy-MM-dd).");
                response.setHttpStatus("400");
                return response;
            }

            if (!Utils.isEmptyString(toDate) && !Utils.isValidDateFormat("yyyy-MM-dd", toDate, Locale.ENGLISH)) {
                response.setStatusMessage("Please specify a valid date for end date (yyyy-MM-dd).");
                response.setHttpStatus("400");
                return response;
            }

            if (!Utils.isEmptyString(fromDate) && !Utils.isEmptyString(toDate)) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date from = format.parse(fromDate);
                Date to = format.parse(toDate);
                if (from.after(to)) {
                    response.setStatusMessage("start date cannot be after end date.");
                    response.setHttpStatus("400");
                    return response;
                }
            }


            Result<ClientCompanyReport> res = reportService.searchReports(filename, fromDate, toDate, Integer.valueOf(pageNumber),
                    Integer.valueOf(pageSize), pageable);
            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(res);
        } catch (NumberFormatException e) {
            logger.error("[+] Error {} occurred while parsing page variable with message: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            response.setStatusMessage("The entered page and size must be integer values.");
            response.setHttpStatus("400");
        }

        return response;
    }

}
