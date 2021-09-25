package com.ap.greenpole.clientCompanyModule.controllers;

import com.ap.greenpole.clientCompanyModule.dtos.GenericModuleRequestResponse;
import com.ap.greenpole.clientCompanyModule.dtos.GenericModuleRequestResult;
import com.ap.greenpole.clientCompanyModule.dtos.RejectionDto;
import com.ap.greenpole.clientCompanyModule.entity.*;
import com.ap.greenpole.clientCompanyModule.service.ClientCompanyRequestService;
import com.ap.greenpole.clientCompanyModule.utils.Const;
import com.ap.greenpole.clientCompanyModule.utils.ConstantUtils;
import com.ap.greenpole.clientCompanyModule.utils.Utils;
import com.ap.greenpole.usermodule.annotation.PreAuthorizePermission;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import org.apache.commons.lang3.StringUtils;
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
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * Created by Nelu.Akejelu on 31/08/2020
 **/

@RestController
@RequestMapping("/api/v1/client")
public class ApprovalContoller {

    private static Logger log = LoggerFactory.getLogger(ApprovalContoller.class);

    @Autowired
    ClientCompanyRequestService requestService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/request/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_APPROVAL_REQUEST"})
    public ResponseEntity<?> getApprovalRequestById(@PathVariable long id, HttpServletRequest request) {

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        ModuleRequest clientCompanyApprovalRequest = requestService.getApprovalRequestById(id);
        if (clientCompanyApprovalRequest == null) {
            return new ResponseEntity<>("Approval request not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(clientCompanyApprovalRequest, HttpStatus.OK);
    }

    @RequestMapping(value = "/requests/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_APPROVAL_REQUEST"})
    public ResponseEntity<?> getAllApprovalRequests() {

        List<ModuleRequest> allRequest = requestService.getAllApprovalRequest();

        List<GenericModuleRequestResponse> genericModuleRequestResponsesList = new ArrayList<>();
        for(ModuleRequest moduleRequest : allRequest){
            GenericModuleRequestResponse genericModuleRequestResponses = new GenericModuleRequestResponse(moduleRequest);
            genericModuleRequestResponsesList.add(genericModuleRequestResponses);
        }
        return new ResponseEntity<>(genericModuleRequestResponsesList,HttpStatus.OK);
    }

    @RequestMapping(value = "/requests/paginated", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_APPROVAL_REQUEST"})
    public ResponseEntity<?> getAllClientCompanyApprovalRequestsPaginated(
            @RequestHeader(value = "pageSize", required = false, defaultValue = "" + Integer.MAX_VALUE) String pageSize,
            @RequestHeader(value = "pageNumber", required = false, defaultValue = "1") String pageNumber, HttpServletRequest request) {

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        try {
            log.info("[+] Attempting to parse the pagination variables");
            int page = Integer.parseInt(pageNumber);
            int size = Integer.parseInt(pageSize);
            page = Math.max(0, page - 1);
            size = Math.max(1, size);
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            return new ResponseEntity<>(requestService
                    .getAllApprovalRequest(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), pageable),HttpStatus.OK);
        } catch (NumberFormatException e) {
            log.error("[+] Error {} occurred while parsing page variable with message: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            return new ResponseEntity<>("The entered page and size must be integer values", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value ={"/approve/{id}"}, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_CLIENT_COMPANY_APPROVAL"})
    public ResponseEntity<?> approveRequest(@PathVariable long id, HttpServletRequest request)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        Object response = requestService.approveRequest(id ,user.get().getId());

        if (response == Const.INVALID_STOCKBROKER){

            return new ResponseEntity<>("One or more shareholder has an Invalid stock broker", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response ,HttpStatus.OK);
    }

    @RequestMapping(value = {"/request/reject/{id}"}, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_CLIENT_COMPANY_APPROVAL"})
    public ResponseEntity<?> rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejectionDto, HttpServletRequest request){
        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(requestService.rejectRequest(id,user.get().getId(),rejectionDto.getReason()),HttpStatus.OK);
    }

    @RequestMapping(value ={"/requests"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_APPROVAL_REQUEST"})
    public ResponseEntity<?> getClientCompanyRequestByStatus(@RequestParam(defaultValue = "0") Integer offset,
                                                             @RequestParam(defaultValue = "10") Integer limit,
                                                             @RequestParam(value = "type") String type,
                                                             @RequestParam String status, HttpServletRequest request){
        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }
        List<ModuleRequest> clientCompanyRequest =  requestService.getClientCompanyRequestByStatus(offset,limit,status,type,user.get().getId());
        GenericModuleRequestResult result = new GenericModuleRequestResult();
        List<GenericModuleRequestResponse> genericModuleRequestResponsesList = new ArrayList<>();
        for(ModuleRequest moduleRequest : clientCompanyRequest){
            GenericModuleRequestResponse genericModuleRequestResponses = new GenericModuleRequestResponse(moduleRequest);
            genericModuleRequestResponsesList.add(genericModuleRequestResponses);
        }
        result.setCount(genericModuleRequestResponsesList.size());
        result.setRequestResponseList(genericModuleRequestResponsesList);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @RequestMapping(value ={"/request/approved"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_APPROVAL_REQUEST"})
    public ResponseEntity<?> findClientCompanyRequestApproved(HttpServletRequest request){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        List<ModuleRequest> clientCompanyRequest =  requestService.getShareholderRequestApproved();
        List<GenericModuleRequestResponse> genericModuleRequestResponsesList = new ArrayList<>();
        for(ModuleRequest moduleRequest : clientCompanyRequest){
            GenericModuleRequestResponse genericModuleRequestResponses = new GenericModuleRequestResponse(moduleRequest);
            genericModuleRequestResponsesList.add(genericModuleRequestResponses);
        }
        return new ResponseEntity<>(genericModuleRequestResponsesList,HttpStatus.OK);
    }

    @RequestMapping(value ={"/request/rejected"}, method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_APPROVAL_REQUEST"})
    public ResponseEntity<?> findClientCompanyRequestRejected(HttpServletRequest request){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        List<ModuleRequest> clientCompanyRequest =  requestService.getShareholderRequestRejected();
        List<GenericModuleRequestResponse> genericModuleRequestResponsesList = new ArrayList<>();
        for(ModuleRequest moduleRequest : clientCompanyRequest){
            GenericModuleRequestResponse genericModuleRequestResponses = new GenericModuleRequestResponse(moduleRequest);
            genericModuleRequestResponsesList.add(genericModuleRequestResponses);
        }
        return new ResponseEntity<>(genericModuleRequestResponsesList,HttpStatus.OK);
    }

    @RequestMapping(value = {"/requests/filter"}, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_APPROVAL_REQUEST"})
    public ResponseEntity<?> filterRequests(@RequestParam(value = "date")String date,
                                            @RequestParam(value = "type") String type,
                                            @RequestParam(value = "offset", defaultValue = "0") int offset,
                                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                                            @RequestParam(value = "status") String status,
                                            HttpServletRequest request){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(date)){
            return new ResponseEntity<>("Date must not be empty",HttpStatus.BAD_REQUEST);
        }

        if (!Utils.isValidDate(ConstantUtils.FILTER_DATE_FORMAT, date)) {
            return new ResponseEntity<>("Invalid date",HttpStatus.BAD_REQUEST);

        }

        Date dateObject = Utils.getDate(ConstantUtils.FILTER_DATE_FORMAT, date);

        if (dateObject == null) {
            return new ResponseEntity<>("date parsed is null",HttpStatus.BAD_REQUEST);
        }

        int page = Math.max(0, offset - 1);
        int size = Math.max(1, limit);
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_on").ascending());
        Result<ModuleRequest> allFilteredModuleRequest = requestService
                .getClientCompanyApprovalNotificationByDateCreatedAndStatus(dateObject, status,type,user.get().getId(), pageable);

        GenericModuleRequestResult result = new GenericModuleRequestResult();
        List<GenericModuleRequestResponse> genericModuleRequestResponsesList = new ArrayList<>();
        for(ModuleRequest moduleRequest : allFilteredModuleRequest.getList()){
            GenericModuleRequestResponse genericModuleRequestResponses = new GenericModuleRequestResponse(moduleRequest);
            genericModuleRequestResponsesList.add(genericModuleRequestResponses);
        }

        result.setCount(genericModuleRequestResponsesList.size());
        result.setRequestResponseList(genericModuleRequestResponsesList);

        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @RequestMapping(value = {"/requests/search"},  method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_APPROVAL_REQUEST"})
    public ResponseEntity<?> searchWithParams(@RequestParam(value = "query") String param,
                                              @RequestParam(value = "status") String status,
                                              @RequestParam(value = "type") String type,
                                              @RequestParam(value = "offset", defaultValue = "0") int offset,
                                              @RequestParam(value = "limit", defaultValue = "10") int limit, HttpServletRequest request){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        int page = Math.max(0, offset - 1);
        int size = Math.max(1, limit);
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_on").ascending());

        Result<ModuleRequest> searchResult = requestService.search(pageable,param,status,type, user.get().getId());

        GenericModuleRequestResult result = new GenericModuleRequestResult();
        List<GenericModuleRequestResponse> genericModuleRequestResponsesList = new ArrayList<>();
        if (searchResult.getList().size() > 0){
        for(ModuleRequest moduleRequest : searchResult.getList()){
            GenericModuleRequestResponse genericModuleRequestResponses = new GenericModuleRequestResponse(moduleRequest);
            genericModuleRequestResponsesList.add(genericModuleRequestResponses);
          }
        }
        result.setCount(genericModuleRequestResponsesList.size());
        result.setRequestResponseList(genericModuleRequestResponsesList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value ={"/request/approval"}, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_CLIENT_COMPANY_APPROVAL"})
    public ResponseEntity<?> approveRequest(@RequestBody AuthorizeRequest authorizeRequest, HttpServletRequest request) {

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        if(authorizeRequest.getAction() == "" || authorizeRequest.getAction() == null)
            return new ResponseEntity<>("Action is required", HttpStatus.BAD_REQUEST);

        if(authorizeRequest.getRequestId() <= 0)
            return new ResponseEntity<>("Request ID is required", HttpStatus.BAD_REQUEST);

        if (!Arrays.asList(ConstantUtils.APPROVAL_ACTIONS).contains(authorizeRequest.getAction()))
            return new ResponseEntity<>("Approval action can either be APPROVE or REJECT", HttpStatus.BAD_REQUEST);

        Object response = null;

        if(authorizeRequest.getAction() == ConstantUtils.APPROVAL_ACTIONS[0])
            response = requestService.approveRequest(authorizeRequest.getRequestId() ,user.get().getId());

        if(authorizeRequest.getAction() == ConstantUtils.APPROVAL_ACTIONS[1])
            response = requestService.rejectRequest(authorizeRequest.getRequestId() ,user.get().getId(), authorizeRequest.getComment());

        if (response == Const.INVALID_STOCKBROKER){
            return new ResponseEntity<>("One or more shareholder has an Invalid stock broker", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response ,HttpStatus.OK); //the user id should be gotten from the token...
    }

}
