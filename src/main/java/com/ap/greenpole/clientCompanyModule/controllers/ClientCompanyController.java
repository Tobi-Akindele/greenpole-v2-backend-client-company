package com.ap.greenpole.clientCompanyModule.controllers;

import com.ap.greenpole.clientCompanyModule.dtos.*;
import com.ap.greenpole.clientCompanyModule.entity.*;
import com.ap.greenpole.clientCompanyModule.entity.Error;
import com.ap.greenpole.clientCompanyModule.service.*;
import com.ap.greenpole.clientCompanyModule.utils.ConstantUtils;
import com.ap.greenpole.clientCompanyModule.utils.GeneratePdfReport;
import com.ap.greenpole.clientCompanyModule.utils.Utils;
import com.google.gson.Gson;
import com.ap.greenpole.usermodule.annotation.PreAuthorizePermission;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ap.greenpole.clientCompanyModule.controllers.ApiParameters.CLIENT_COMPANY_ID;
import static com.ap.greenpole.clientCompanyModule.controllers.util.EntityMapper.*;

/**
 * Created by Nelu Akejelu on 18/08/2020.
 */

@RestController
@RequestMapping(path= "/api/v1/client")
public class ClientCompanyController {

    private static Logger logger = LoggerFactory.getLogger(ClientCompanyController.class);

    @Value("${resource.location}")
    private String resourceLocation;

    @Value("${download.server}")
    private String downloadServer;

    @Autowired
    ClientCompanyRequestService clientCompanyRequestService;

    @Autowired
    ClientCompanyService clientCompanyService;

    @Autowired
    MeetingService meetingService;

    @Autowired
    ShareHolderService shareHolderService;

    @Autowired
    ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private UploadShareCapitalChecklistService uploadShareCapitalChecklistService;

    @Transactional
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_CREATE_CLIENT_COMPANY"})
    public ResponseEntity<?> createClientCompany(@Valid @RequestBody ClientCompanyRequestDto requestDto, HttpServletRequest request){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        ModuleRequest moduleRequest  = clientCompanyRequestService.createApprovalRequest(mapToClientCompanyApprovalRequest(requestDto), user.get());
        return new ResponseEntity<>(mapModuleRequestToCreateClientCompanyResponse(mapToModuleResponseDto(moduleRequest),requestDto), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/update/{clientCompanyId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_UPDATE_CLIENT_COMPANY"})
    public ClientCompanyBaseResponse<ResponseEntity<?>> updateClientCompany(@RequestBody ClientCompanyRequestDto requestDto, @PathVariable Long clientCompanyId, HttpServletRequest request) {

        ClientCompanyBaseResponse<ResponseEntity<?>> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        logger.info("[+] Inside updateClientCompany with payload: {}", new Gson().toJson(requestDto));
        ResponseEntity<?> responseEntity = null;
        try {
            if (clientCompanyId == null) {
                response.setStatusMessage("Client company ID  cannot be empty.");
                response.setHttpStatus("400");
                return response;
            }
            if (requestDto == null) {
                response.setStatusMessage("Request body is required.");
                response.setHttpStatus("400");
                return response;
            }

            //TODO
            // check if necessary data exists in the request dto here

            // check if client company exists
            ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
            if(clientCompany == null) {
                response.setStatusMessage("Client company does not exist or has been deactivated.");
                response.setHttpStatus("404");
                return response;
            }

            // check if client company is undergoing approval already
            ModuleRequest clientCompanyApprovalRequest = Utils.checkForOngoingApproval(clientCompanyRequestService,
                    clientCompanyId, ConstantUtils.REQUEST_TYPES[1]);
            logger.info(" checkForOngoingApproval in Utils returned: {}", new Gson().toJson(clientCompanyApprovalRequest));

            // set new, old data and request type
            Gson gson = new Gson();
            clientCompany.setUpdatedAt(java.time.LocalDateTime.now());
            clientCompanyApprovalRequest.setOldRecord(gson.toJson(mapToClientCompanyResponseDto(clientCompany)));
            clientCompanyApprovalRequest.setNewRecord(gson.toJson(requestDto));
            clientCompanyApprovalRequest.setActionRequired(ConstantUtils.REQUEST_TYPES[1]);
            Date date = new Date();
            clientCompanyApprovalRequest.setCreatedOn(date);

            ModuleRequest moduleRequest = clientCompanyRequestService.createApprovalRequest(clientCompanyApprovalRequest,user.get());
            responseEntity = new ResponseEntity<>(moduleRequest, HttpStatus.CREATED);

            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(responseEntity);
        } catch (Exception e) {
            logger.info("[-] Exception occurred while updating client company {}", e.getMessage());
            response.setStatusMessage("Error processing request.");
            response.setHttpStatus("500");
        }

        return response;
    }

    @RequestMapping(path = ApiPaths.ALL, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_CLIENT_COMPANY"})
    public ResponseEntity<?> getAllClientCompanies(HttpServletRequest request){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        List<ClientCompany> clientCompanies = clientCompanyService.getAllClientCompanies(); //active client companies
        return new ResponseEntity<>(mapToClientCompanyResponseDtos(clientCompanies),HttpStatus.OK);
    }

    @RequestMapping(path = ApiPaths.ALL +ApiPaths.INACTIVE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_CLIENT_COMPANY"})
    public ResponseEntity<?> getAllInactiveClientCompanies(HttpServletRequest request){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        List<ClientCompany> clientCompanies = clientCompanyService.getAllInactiveClientCompanies(); //inactive client companies
        return new ResponseEntity<>(mapToClientCompanyResponseDtos(clientCompanies),HttpStatus.OK);
    }

    @RequestMapping(path = "/{client-company-id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_CLIENT_COMPANY"})
    public ResponseEntity<?> getClientCompanyById(@PathVariable("client-company-id") long clientCompanyId, HttpServletRequest request){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        return new ResponseEntity<>(mapToClientCompanyResponseDto(clientCompany),HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(path = ApiPaths.INTRODUCTION, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_CREATE_CLIENT_COMPANY"})
    public ResponseEntity<?> createClientCompanyByIntroduction(@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId,
                                                               @RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        if (clientCompany == null) {
            return new ResponseEntity<>("Client company does not exist", HttpStatus.BAD_REQUEST);
        }

        List<ShareholderIntroductionDto> shareholderDto = clientCompanyService.parseFileToShareholderDto(file);
        shareholderDto.get(0).setClientCompanyId(clientCompany.getId());
        ModuleRequest moduleRequest = clientCompanyRequestService.createApprovalRequest(mapToClientCompanyApprovalRequest(shareholderDto,clientCompany.getId()),user.get());
        return new ResponseEntity<>(mapToModuleResponseDto(moduleRequest), HttpStatus.CREATED);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_CREATE_HOLDER"})
    @RequestMapping(path = ApiPaths.UPLOAD_IN_MASS, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<?> uploadShareholdersInMass(@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId, @RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException{

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        if (clientCompany == null) {
            return new ResponseEntity<>("Client company does not exist", HttpStatus.BAD_REQUEST);
        }

        List<ShareHolderInMassRequestDto> shareHolderRequestDtos = clientCompanyService.parseFileToShareholderInMassDto(file);
        ModuleRequest moduleRequest = clientCompanyRequestService
                .createApprovalRequest(mapInMassToClientCompanyApprovalRequest(shareHolderRequestDtos,clientCompany.getId()),user.get());
        return new ResponseEntity<>(mapToModuleResponseDto(moduleRequest), HttpStatus.CREATED);
    }

    @Transactional
    @RequestMapping(path = ApiPaths.INVALIDATE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_INVALIDATE_CLIENT_COMPANY"})
    public ResponseEntity<?> invalidateClientCompany(@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId, HttpServletRequest request){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        if (clientCompany == null) {
            return new ResponseEntity<>("Client company does not exist", HttpStatus.BAD_REQUEST);
        }
        ModuleRequest moduleRequest = clientCompanyRequestService.createApprovalRequest(mapToInvalidateRequest(clientCompany), user.get());
        return new ResponseEntity<>(mapToModuleResponseDto(moduleRequest), HttpStatus.CREATED);
    }

    @Transactional
    @RequestMapping(path = ApiPaths.TRADE_RIGHTS, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_TRADE_RIGHTS"})
        public ResponseEntity<?> tradeRights(@Valid  @RequestBody TradeRightsDto tradeRightsDto, HttpServletRequest request ){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        Shareholder buyer = shareHolderService.findById(tradeRightsDto.getBuyerId());
        if (buyer == null){
            return new ResponseEntity<>("Buyer isn't a valid shareholder", HttpStatus.BAD_REQUEST);
        }

        Shareholder seller = shareHolderService.findById(tradeRightsDto.getSellerId());
        if (seller == null){
            return new ResponseEntity<>("Seller isn't a valid shareholder", HttpStatus.BAD_REQUEST);
        }

        if (seller.getShareUnit() == null || tradeRightsDto.getRights().compareTo(seller.getShareUnit()) > 0){
            return new ResponseEntity<>("Seller doesn't have up to " +tradeRightsDto.getRights() +
                    " to trade. Available rights is "+seller.getShareUnit(), HttpStatus.BAD_REQUEST);
        }

        ModuleRequest moduleRequest  = clientCompanyRequestService.createApprovalRequest(mapToTradeRightsRequest(tradeRightsDto),user.get());
        return new ResponseEntity<>(mapToModuleResponseDto(moduleRequest), HttpStatus.CREATED);
    }

    @RequestMapping(path = ApiPaths.SHAREHOLDERS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_SHAREHOLDER"})
    public ResponseEntity<?> getAllShareHoldersByClientCompany(@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId){
        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        if (clientCompany == null) {
            return new ResponseEntity<>("Client company does not exist", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(mapShareholderToResponseDtos(shareHolderService.findByClientCompany(clientCompany)), HttpStatus.OK);
    }


    @RequestMapping(path = ApiPaths.SHARE_HOLDER_DOWNLOAD_EXCEL, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_DOWNLOAD_SHAREHOLDERS"})
    public ResponseEntity<?> downloadShareholderListExcel(@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId) {
        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        if (clientCompany == null) {
            return new ResponseEntity<>("Client company does not exist", HttpStatus.BAD_REQUEST);
        }
        String filename = "shareholder.xlsx";
        InputStreamResource file = new InputStreamResource(clientCompanyService.shareHolderToExcel(clientCompany));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @RequestMapping(path = ApiPaths.SHARE_HOLDER_DOWNLOAD_PDF, method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_DOWNLOAD_SHAREHOLDERS"})
    public ResponseEntity<?> downloadShareholderListPDF(@PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId) {

        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
        if (clientCompany == null) {
            return new ResponseEntity<>("Client company does not exist", HttpStatus.BAD_REQUEST);
        }

        List<Shareholder> shareholders = shareHolderService.findByClientCompany(clientCompany);
        ByteArrayInputStream bis = GeneratePdfReport.shareHolderReport(shareholders);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=shareholders_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @Transactional
    @RequestMapping(path = ApiPaths.SHARE_HOLDER_DOWNLOAD_CSV, method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_DOWNLOAD_SHAREHOLDERS"})
    public void downloadShareholderListCsv(HttpServletResponse response, @PathVariable(name = CLIENT_COMPANY_ID) long clientCompanyId) throws IOException{

        ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);

        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=shareholders.csv";
        response.setHeader(headerKey,headerValue);

        List<Shareholder> shareholders = shareHolderService.findByClientCompany(clientCompany);
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] columns = new String[]{"Bank Name", "First Name", "Middle Name", "Last Name", "Email", "Address", "Share Unit", "Phone Number"};
        String [] nameMapping = {"bankName","firstName","middleName", "lastName", "email", "address", "shareUnit", "phone"};

        csvBeanWriter.writeHeader(columns);

        for (Shareholder shareholder : shareholders){
            csvBeanWriter.write(shareholder,nameMapping);
        }

        csvBeanWriter.close();
    }

    @Transactional
    @RequestMapping(value = "/manual_share_capital_list", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_MANUAL_SHARE_CAPITAL"})
    public ResponseEntity<?> getManualShareCapitalList(HttpServletRequest request){

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if (!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }

        List<ClientCompany> clientCompanyList = clientCompanyService.getAllClientCompanies();
        List<ShareCapitalDto> shareCapitalDtoList = clientCompanyList.stream()
                .map(this::mapClientCompanyToShareCapital)
                .collect(Collectors.toList());

        return new ResponseEntity<>(shareCapitalDtoList, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "/{clientCompanyId}/manual_share_capital", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_MANUAL_SHARE_CAPITAL"})
    public ResponseEntity<?> manualShareCapital(@RequestBody @Valid ManualShareCapitalDto shareCapitalDto, @PathVariable("clientCompanyId") long clientCompanyId, HttpServletRequest request) {

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if (!user.isPresent()) {
            return new ResponseEntity<>("User not authorized", HttpStatus.BAD_REQUEST);
        }
        ModuleRequest moduleRequest = clientCompanyRequestService.createApprovalRequest(mapToManualShareCapitalApprovalRequest(shareCapitalDto, clientCompanyId), user.get());
        return new ResponseEntity<>(mapToModuleResponseDto(moduleRequest), HttpStatus.CREATED);
    }

    @RequestMapping(path = ApiPaths.SEARCH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_GET_CLIENT_COMPANY"})
    public ResponseEntity<?> searchWithParams(
            @And({
                    @Spec(path = "registerName", params = "registerName", spec = Like.class),
                    @Spec(path = "registerCode", params = "registerCode", spec = Like.class),
                    @Spec(path = "ceoName", params = "ceoName", spec = Like.class),
                    @Spec(path = "address", params = "address", spec = Like.class),
                    @Spec(path = "emailAddress", params = "emailAddress", spec = Equal.class),
                    @Spec(path = "depository", params = "depository", spec = Equal.class),
                    @Spec(path = "nseSector", params = "nseSector", spec = Equal.class),
                    @Spec(path = "rcNumber", params = "rcNumber", spec = Equal.class),
                    @Spec(path = "symbol", params = "symbol", spec = Like.class),
                    @Spec(path = "phoneNumber", params = "phoneNumber", spec = Equal.class),
                    @Spec(
                            path = "approvedAt",
                            params = {"approvedAt", "updatedAt"},
                            spec = Between.class)
            })
                    Specification<ClientCompany> spec,
            Sort sort,
            @RequestHeader HttpHeaders headers) {
        final PagingResponse response = clientCompanyService.get(spec, headers, sort);
        return new ResponseEntity<>(response.getElements(), returnHttpHeaders(response), HttpStatus.OK);
    }

    @RequestMapping(value = "/merge", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_MERGE_CLIENT_COMPANIES"})
    public ClientCompanyBaseResponse<ResponseEntity<?>> mergeClientCompanies(@RequestHeader Long primaryCompanyId, @RequestBody List<Long> secondaryCompaniesId, HttpServletRequest request) {

        ClientCompanyBaseResponse<ResponseEntity<?>> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        ResponseEntity<?> responseEntity = null;
        try {
            if (primaryCompanyId == null) {
                response.setStatusMessage("Primary company ID is required.");
                response.setHttpStatus("400");
                return response;
            }
            if (secondaryCompaniesId == null) {
                response.setStatusMessage("Request body is required.");
                response.setHttpStatus("400");
                return response;
            }

            // check if client companies exists
            List<Error> errors = new ArrayList<>();
            ClientCompany priClientCompany = clientCompanyService.getClientCompanyById(primaryCompanyId);
            if(priClientCompany == null) {
                Error error = new Error(null, "Primary company ID: " + primaryCompanyId +" does not exist or has been deactivated");
                errors.add(error);
            }

            for(Long secondaryCompanyId: secondaryCompaniesId ) {
                ClientCompany secClientCompany = clientCompanyService.getClientCompanyById(secondaryCompanyId);
                if(secClientCompany == null) {
                    Error error = new Error(null, "Secondary company ID: " + secondaryCompanyId +" does not exist or has been deactivated");
                    errors.add(error);
                    continue;
                }
            }

            if (!errors.isEmpty()) {
                response.setStatusMessage(errors.toString());
                response.setHttpStatus("404");
                return response;
            }


//             check if client companies are undergoing approval already
            Utils.checkForOngoingApproval(clientCompanyRequestService, primaryCompanyId, ConstantUtils.REQUEST_TYPES[3]);
            for(Long secondaryCompanyId: secondaryCompaniesId ) {
                Utils.checkForOngoingApproval(clientCompanyRequestService, secondaryCompanyId, ConstantUtils.REQUEST_TYPES[3]);
            }

            // start approval
            ModuleRequest clientCompanyApprovalRequest = new ModuleRequest();
            MergeClientCompany mergeClientCompany = new MergeClientCompany();
            Gson gson = new Gson();

            mergeClientCompany.setPrimaryCompanyId(primaryCompanyId);
            mergeClientCompany.setSecondaryCompaniesIds(secondaryCompaniesId);

            clientCompanyApprovalRequest.setOldRecord(gson.toJson(mergeClientCompany));
            clientCompanyApprovalRequest.setActionRequired(ConstantUtils.REQUEST_TYPES[3]);

            ModuleRequest moduleRequest = clientCompanyRequestService.createApprovalRequest(clientCompanyApprovalRequest,user.get());
            responseEntity = new ResponseEntity<>(moduleRequest, HttpStatus.CREATED);

            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(responseEntity);
        } catch (Exception e) {
            logger.info("[-] Exception occurred while merging client companies {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
            response.setHttpStatus("500");
        }

        return response;
    }

    @RequestMapping(value = "/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_EXPORT_CLIENT_COMPANY"})
    public ClientCompanyBaseResponse<ClientCompanyReport> exportClientCompaniesDetails(@RequestHeader String format, @RequestBody List<Long> clientCompanyIds, HttpServletRequest request) {
        ClientCompanyBaseResponse<ClientCompanyReport> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        try {
            if (format == null || format == "") {
                response.setStatusMessage("Format is required.");
                response.setHttpStatus("400");
                return response;
            }

            if (clientCompanyIds == null) {
                response.setStatusMessage("Request body is required.");
                response.setHttpStatus("400");
                return response;
            }

            if (!Arrays.asList(ConstantUtils.SUPPORTED_EXPORT_FORMATS).contains(format)) {
                response.setStatusMessage("Format is not supported.");
                response.setHttpStatus("400");
                return response;
            }

            // check if client companies exists
            List<Error> errors = new ArrayList<>();
            List<ClientCompany> list = new ArrayList<>();
            for(Long clientCompanyId: clientCompanyIds ) {
                ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
                if(clientCompany == null) {
                    Error error = new Error(null, "Client company ID: " + clientCompanyId +" does not exist or has been deactivated");
                    errors.add(error);
                    continue;
                } else {
                    list.add(clientCompany);
                }
            }

            if (!errors.isEmpty()) {
                response.setStatusMessage(errors.toString());
                response.setHttpStatus("400");
                return response;
            }

            ClientCompanyReport res = clientCompanyService.exportClientCompaniesDetails(format, list);

            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(res);
        } catch (Exception e) {
            logger.info("[-] Exception occurred while generating clientCompanyReport {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
            response.setHttpStatus("500");
        }

        return response;
    }

    @RequestMapping(value = "/upload/annual/report", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_UPLOAD_CLIENT_COMPANY_ANNUAL_REPORT"})
    public ClientCompanyBaseResponse<ResponseEntity<?>> uploadAnnualReport(@RequestHeader String title, @RequestHeader String reportYear, @RequestHeader Long clientCompanyId, @RequestParam("file") MultipartFile file, HttpServletRequest request ) {
        ClientCompanyBaseResponse<ResponseEntity<?>> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        ClientCompanyReport clientCompanyReport = new ClientCompanyReport();
        ResponseEntity<?> responseEntity = null;
        try {
            if (title == null || title == "") {
                response.setStatusMessage("Title is required.");
                response.setHttpStatus("400");
                return response;
            }

            if (reportYear == null || reportYear == "") {
                response.setStatusMessage("Report year is required.");
                response.setHttpStatus("400");
                return response;
            }

            if (clientCompanyId == null) {
                response.setStatusMessage("Client Company ID is required.");
                response.setHttpStatus("400");
                return response;
            }

            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            if(!ext.equals("xlsx")) {
                response.setStatusMessage("File must be an excel file.");
                response.setHttpStatus("400");
                return response;
            }

            // check if client company exists
            ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
            if(clientCompany == null) {
                response.setStatusMessage("Client company ID: " + clientCompanyId + " does not exist or has been deactivated.");
                response.setHttpStatus("400");
                return response;
            }

            String directories = resourceLocation + ConstantUtils.ANNUAL_REPORT_DIRECTORY;
            String filename = directories + ConstantUtils.ANNUAL_GENERAL_REPORT + "_"
                    + Utils.getDateString(new Date()) + "_" + System.currentTimeMillis();
            filename = FilenameUtils.normalize(filename);
            filename = filename.replaceAll(" ", "_");
            filename += ".xlsx";


            // save file
            reportService.storeFile(file, filename, directories);

            Path path = Paths.get(filename);
            String url = downloadServer + ConstantUtils.ANNUAL_REPORT_DIRECTORY + path.getFileName().toString();

            clientCompanyReport.setFilename(filename);
            clientCompanyReport.setFile_url(url);
            clientCompanyReport.setCreatedOn(java.time.LocalDateTime.now());
            ClientCompanyReport res = reportService.create(clientCompanyReport);

            // start approval
            ModuleRequest clientCompanyApprovalRequest = new ModuleRequest();
            ClientCompanyAnnualReport clientCompanyAnnualReport = new ClientCompanyAnnualReport();
            Gson gson = new Gson();

            clientCompanyAnnualReport.setClient_company_id(clientCompanyId);
            clientCompanyAnnualReport.setTitle(title);
            clientCompanyAnnualReport.setReport_year(reportYear);
            clientCompanyAnnualReport.setClientCompanyReport(clientCompanyReport);

            clientCompanyApprovalRequest.setOldRecord(gson.toJson(clientCompanyAnnualReport));
            clientCompanyApprovalRequest.setActionRequired(ConstantUtils.REQUEST_TYPES[5]);

            ModuleRequest modulerequest = clientCompanyRequestService.createApprovalRequest(clientCompanyApprovalRequest, user.get());
            responseEntity = new ResponseEntity<>(modulerequest, HttpStatus.CREATED);

            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(responseEntity);

            return response;
        } catch (Exception e) {
            logger.info("[-] Exception occurred while uploading annual report {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
            response.setHttpStatus("500");
        }

        return response;
    }

    @RequestMapping(value = "/upload/sharecapitalchecklist", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_SUPER_USER,ROLE_ADMIN')")
    @PreAuthorizePermission({"PERMISSION_UPLOAD_CLIENT_COMPANY_SHARE_CAPITAL_CHECKLIST"})
    public ClientCompanyBaseResponse<ResponseEntity<?>> uploadShareCapitalChecklist(@RequestParam("file") MultipartFile file, HttpServletRequest request ) throws IOException {
        ClientCompanyBaseResponse<ResponseEntity<?>> response = new ClientCompanyBaseResponse<>();
        response.setStatus(ClientCompanyAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(ClientCompanyAPIResponseCode.FAILED.name());

        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
        if(!user.isPresent()) {
            response.setStatusMessage("User not authorized.");
            response.setHttpStatus("400");
            return response;
        }

        ResponseEntity<?> responseEntity = null;
        try {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            if(!ext.equals("xlsx")) {
                response.setStatusMessage("File must be an excel file.");
                response.setHttpStatus("400");
                return response;
            }


            // process excel content
            responseEntity = uploadShareCapitalChecklistService.processExcel(file);

            response.setStatus(ClientCompanyAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(ClientCompanyAPIResponseCode.SUCCESSFUL.name());
            response.setHttpStatus("200");
            response.setData(responseEntity);

            return response;
        } catch (Exception e) {
            logger.info("[-] Exception occurred while uploading share capital checklist {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
            response.setHttpStatus("500");
        }

        return response;
    }

    private HttpHeaders returnHttpHeaders(PagingResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(PagingHeaders.COUNT.getName(), String.valueOf(response.getCount()));
        headers.set(PagingHeaders.PAGE_SIZE.getName(), String.valueOf(response.getPageSize()));
        headers.set(PagingHeaders.PAGE_OFFSET.getName(), String.valueOf(response.getPageOffset()));
        headers.set(PagingHeaders.PAGE_NUMBER.getName(), String.valueOf(response.getPageNumber()));
        headers.set(PagingHeaders.PAGE_TOTAL.getName(), String.valueOf(response.getPageTotal()));
        return headers;
    }

    public ShareCapitalDto mapClientCompanyToShareCapital(ClientCompany clientCompany){

        List<Shareholder> shareholders = shareHolderService.findByClientCompany(clientCompany);
        int shareHolderCount = shareholders.size();

        BigInteger totalShareUnits = shareholders.stream()
                .map(Shareholder::getShareUnit)
                .reduce(BigInteger::add).orElse(BigInteger.ZERO);

        return ShareCapitalDto.builder()
                .clientCompanyId(clientCompany.getId())
                .clientCompanyName(clientCompany.getRegisterName())
                .clientCompanyRegisterCode(clientCompany.getRegisterCode())
                .paidUpShareCapital(clientCompany.getPaidUpShareCapital())
                .authorizedShareCapital(clientCompany.getAuthorizedShareCapital())
                .holdersCount(shareHolderCount)
                .variance(clientCompany.getPaidUpShareCapital().subtract(totalShareUnits))
                .build();
    }
}
