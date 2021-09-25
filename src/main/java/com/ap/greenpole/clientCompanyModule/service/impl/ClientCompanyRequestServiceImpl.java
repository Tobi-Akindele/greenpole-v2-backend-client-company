package com.ap.greenpole.clientCompanyModule.service.impl;

import com.ap.greenpole.clientCompanyModule.dtos.*;
import com.ap.greenpole.clientCompanyModule.entity.*;
import com.ap.greenpole.clientCompanyModule.enums.GenericStatusEnum;
import com.ap.greenpole.clientCompanyModule.exceptions.NotFoundException;
import com.ap.greenpole.clientCompanyModule.repositories.*;
import com.ap.greenpole.clientCompanyModule.service.ClientCompanyRequestService;
import com.ap.greenpole.clientCompanyModule.service.ClientCompanyService;
import com.ap.greenpole.clientCompanyModule.utils.Const;
import com.ap.greenpole.clientCompanyModule.utils.ConstantUtils;
import com.ap.greenpole.clientCompanyModule.utils.StatusEnum;
import com.ap.greenpole.usermodule.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ap.greenpole.clientCompanyModule.controllers.util.EntityMapper.*;
import static com.ap.greenpole.clientCompanyModule.controllers.util.EntityMapper.mapInMassRequestToShareHolder;

@Service
@Log
public class ClientCompanyRequestServiceImpl implements ClientCompanyRequestService {

    @Autowired
    ClientCompanyRequestRepository requestRepository;

    @Autowired
    ClientCompanyService clientCompanyService;

    @Autowired
    ShareHolderRepository shareHolderRepository;

    @Autowired
    DirectorRepository directorRepository;

    @Autowired
    StockBrokerRepository stockBrokerRepository;

    @Autowired
    ClientCompanyAnnualReportRepository clientCompanyAnnualReportRepository;

    @Override
    public ModuleRequest createApprovalRequest(ModuleRequest moduleRequest, User user) {

        moduleRequest.setRequesterId(user.getId());
        moduleRequest.setStatus(Const.PENDING);
        moduleRequest.setModules(ConstantUtils.MODULE);
        moduleRequest.setRequestCode(requestCode(Const.CLIENT));
        return requestRepository.save(moduleRequest);
    }

    @Override
    public List<ModuleRequest> getAllApprovalRequest() {
        return (List<ModuleRequest>) requestRepository.findAll();
    }

    @Override
    public Result<ModuleRequest> getAllApprovalRequest(int pageNumber, int pageSize, Pageable pageable) {

        Page<ModuleRequest> allRecords = requestRepository.findAll(pageable);
        long noOfRecords = allRecords.getTotalElements();

        return new Result<>(0, allRecords.getContent(), noOfRecords, pageNumber, pageSize);
    }

    @Override
    public ModuleRequest getApprovalRequestById(long approvalRequestId) {
        return requestRepository.findModuleRequestByRequestId(approvalRequestId);
    }

    @Override
    public Object approveRequest(long requestId, long userId) {

        Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create();
        ModuleRequest request = getApprovalRequestById(requestId);

        if (request == null) return "Client company request not found";

        if (request.getStatus() != Const.PENDING) return "Request can not be approved";

        if (request.getActionRequired().equalsIgnoreCase(Const.CREATE)) {
            ClientCompany clientCompany = gson.fromJson(request.getNewRecord(), ClientCompany.class);
            request.setStatus(Const.APPROVED);
            Date date = new Date();
            request.setApprovedOn(date);
            request.setApproverId(userId);

            ClientCompany builtClientCompany = buildClientCompany(clientCompany);
            ClientCompany savedClientCompany = clientCompanyService.save(builtClientCompany);

            List<Director> savedDirectors = new ArrayList<>();
            for (Director director : clientCompany.getDirectors()) {
                Director newDirector = directorRepository.save(buildDirector(savedClientCompany, director));
                savedDirectors.add(newDirector);
            }
            requestRepository.save(request);
            savedClientCompany.setDirectors(savedDirectors);
            return mapToClientCompanyResponseDto(savedClientCompany);
        }

        if (request.getActionRequired().equalsIgnoreCase(Const.CREATE_SHAREHOLDER_INTRODUCTION)) {
            ShareholderIntroductionDto[] dtos = gson.fromJson(request.getNewRecord(), ShareholderIntroductionDto[].class);

            request.setStatus(Const.APPROVED);
            Date date = new Date();
            request.setApprovedOn(date);
            request.setApproverId(userId);

            long clientCompanyId = request.getResourceId();

            List<Shareholder> shareholders = new ArrayList<>();
            for (ShareholderIntroductionDto shareholderIntroductionDto : dtos){
                Shareholder shareholder = mapIntroductionRequestToShareHolder(shareholderIntroductionDto);
                StockBroker stockBroker = stockBrokerRepository.findByStockBrokerName(shareholderIntroductionDto.getStockBrokerName());
                if (stockBroker == null) throw new NotFoundException("404", "Stockbroker " + shareholderIntroductionDto.getStockBrokerName()+ " does not exist");

                shareholder.setStockBroker(stockBroker.getId());
                shareholder.setClientCompany(clientCompanyId);
                shareholder.setStatus(GenericStatusEnum.ACTIVE);
                shareHolderRepository.save(shareholder);

                shareholders.add(shareholder);
            }

            requestRepository.save(request);

            return shareholders;
        }

        if (request.getActionRequired().equalsIgnoreCase(ConstantUtils.REQUEST_TYPES[1])) {
            ClientCompany clientCompany = gson.fromJson(request.getNewRecord(), ClientCompany.class);
            ClientCompany oldClientCompany = gson.fromJson(request.getOldRecord(), ClientCompany.class);
            clientCompany.setId(oldClientCompany.getId());
            request.setStatus(Const.APPROVED);
            Date date = new Date();
            request.setApprovedOn(date);
            request.setApproverId(userId);

            clientCompany.setApprovedAt(java.time.LocalDateTime.now());
            clientCompany.setUpdatedAt(oldClientCompany.getUpdatedAt());
            clientCompany.setStatus(GenericStatusEnum.ACTIVE);

            ClientCompany savedClientCompany = clientCompanyService.save(clientCompany);

            requestRepository.save(request);
            return mapToClientCompanyResponseDto(savedClientCompany);
        }

        if (request.getActionRequired().equalsIgnoreCase(Const.INVALIDATE)) {
            ClientCompany oldClientCompany = gson.fromJson(request.getOldRecord(), ClientCompany.class);

            List<Shareholder> shareholders =
                    shareHolderRepository.findAllByClientCompany(oldClientCompany.getId(),GenericStatusEnum.ACTIVE.name());

            if (shareholders.size() > 0) { // get shareholders and mark them inactive
                for (Shareholder shareholder : shareholders) {
                    shareholder.setStatus(GenericStatusEnum.INACTIVE);
                    shareHolderRepository.save(shareholder);
                }
                oldClientCompany.setStatus(GenericStatusEnum.INACTIVE); // mark client company as inactive
                clientCompanyService.save(oldClientCompany);
            } else { // no share holders therefore purge/deactivate from the system
                oldClientCompany.setStatus(GenericStatusEnum.INACTIVE);
                clientCompanyService.save(oldClientCompany);
            }
        }

        if (request.getActionRequired().equalsIgnoreCase(Const.TRADE_RIGHTS)) {
            TradeRightsDto rightsDto = gson.fromJson(request.getNewRecord(), TradeRightsDto.class);

            Shareholder buyer = shareHolderRepository.findByShareholder_id(rightsDto.getBuyerId(),GenericStatusEnum.ACTIVE.name());
            Shareholder seller = shareHolderRepository.findByShareholder_id(rightsDto.getSellerId(),GenericStatusEnum.ACTIVE.name());
            BigInteger rights = rightsDto.getRights();
            BigInteger sellerRightsValue = seller.getShareUnit();
            BigInteger buyerRightValue =
                    buyer.getShareUnit() == null ? BigInteger.ZERO : buyer.getShareUnit();

            BigInteger newBuyerRights = rights.add(buyerRightValue);
            buyer.setShareUnit(newBuyerRights); // update buyers share unit
            Shareholder updatedBuyer = shareHolderRepository.save(buyer);

            BigInteger newSellerRights = sellerRightsValue.subtract(rights);
            seller.setShareUnit(newSellerRights); // update seller share unit
            Shareholder updatedSeller = shareHolderRepository.save(seller);

            List<Shareholder> shareholders = new ArrayList<>();
            shareholders.add(updatedBuyer);
            shareholders.add(updatedSeller);

            requestRepository.save(request);
            return mapShareholderToResponseDtos(shareholders);
        }

        if (request.getActionRequired().equalsIgnoreCase(Const.CREATE_SHAREHOLDER_IN_MASS)) {

            ShareHolderInMassRequestDto[] requestDtos =
                    gson.fromJson(request.getNewRecord(), ShareHolderInMassRequestDto[].class);

            long clientCompanyId = request.getResourceId();

            List<Shareholder> savedShareholders = new ArrayList<>();
            for (ShareHolderInMassRequestDto requestDto : requestDtos) {
                Shareholder shareholder = mapInMassRequestToShareHolder(requestDto);
                shareholder.setClientCompany(clientCompanyId);
                shareholder.setStatus(GenericStatusEnum.ACTIVE);
                StockBroker stockBroker = stockBrokerRepository.findByStockBrokerName(requestDto.getStockBroker());
                if (stockBroker == null){
                    return Const.INVALID_STOCKBROKER;
                }
                shareholder.setStockBroker(stockBroker.getId());
                Shareholder savedShareholder = shareHolderRepository.save(shareholder);

                savedShareholders.add(savedShareholder);
            }
            return mapToShareHolderToResponseDtos(savedShareholders);
        }

        if (request.getActionRequired().equalsIgnoreCase(Const.MANUAL_SHARE_CAPITAL)) {
            ManualShareCapitalDto shareCapitalDto =
                    gson.fromJson(request.getNewRecord(), ManualShareCapitalDto.class);
            long clientCompanyId = request.getResourceId();
            ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);
            if (clientCompany == null)
                throw new NotFoundException(
                        "404", "Client company does not exist id is: " + clientCompanyId);
            clientCompany.setAuthorizedShareCapital(shareCapitalDto.getAuthorizedShareCapital());
            clientCompany.setPaidUpShareCapital(shareCapitalDto.getPaidUpShareCapital());
            clientCompanyService.save(clientCompany);
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        }

        if (request.getActionRequired().equalsIgnoreCase(ConstantUtils.REQUEST_TYPES[3])) {
            MergeClientCompany mergeClientCompany = gson.fromJson(request.getOldRecord(), MergeClientCompany.class);

            // primary client company
            ClientCompany priclientCompany = clientCompanyService.getClientCompanyById(mergeClientCompany.getPrimaryCompanyId());
            if(priclientCompany == null) throw new NotFoundException("404", "No record found for client company with id: " + priclientCompany);

            for(Long clientCompanyId: mergeClientCompany.getSecondaryCompaniesIds()) {
                // check if client company exists
                ClientCompany clientCompany = clientCompanyService.getClientCompanyById(clientCompanyId);

                if(clientCompany == null) throw new NotFoundException("404", "No record found for client company with id: " + clientCompanyId);

                // get all shareholders with client company id
                List<Shareholder> shareholders =
                        shareHolderRepository.findAllByClientCompany(clientCompanyId,GenericStatusEnum.ACTIVE.name());

                //update shareholders to new client company
                if (shareholders.size() > 0) {
                    for (Shareholder shareholder : shareholders) {
                        shareholder.setClientCompany(clientCompanyId);
                        shareHolderRepository.save(shareholder);
                    }
                }

                // invalidate client company
                clientCompany.setUpdatedAt(java.time.LocalDateTime.now());
                clientCompany.setStatus(GenericStatusEnum.INACTIVE);
                clientCompanyService.save(clientCompany);
            }

            return new ResponseEntity<>("Succesful", HttpStatus.OK);
        }

        if (request.getActionRequired().equalsIgnoreCase(ConstantUtils.REQUEST_TYPES[5])) {
            ClientCompanyAnnualReport annualReport = gson.fromJson(request.getOldRecord(), ClientCompanyAnnualReport.class);

            request.setStatus(Const.APPROVED);
            Date date = new Date();
            request.setApprovedOn(date);
            request.setApproverId(userId);

            // create annual report
            clientCompanyAnnualReportRepository.save(annualReport);

            requestRepository.save(request);

            return new ResponseEntity<>("Succesful", HttpStatus.OK);
        }

        return null;
    }

    private Director buildDirector(ClientCompany savedClientCompany, Director director) {
        Director newDirector = new Director();
        newDirector.setClientCompany(savedClientCompany);
        newDirector.setEmail(director.getEmail());
        newDirector.setFirstName(director.getFirstName());
        newDirector.setMiddleName(director.getMiddleName());
        newDirector.setLastName(director.getLastName());
        newDirector.setPhoneNumber(director.getPhoneNumber());
        return newDirector;
    }

    private ClientCompany buildClientCompany(ClientCompany clientCompany) {
        ClientCompany builtClientCompany = new ClientCompany();
        builtClientCompany.setStatus(GenericStatusEnum.ACTIVE);
        builtClientCompany.setState(clientCompany.getState());
        builtClientCompany.setAddress(clientCompany.getAddress());
        builtClientCompany.setAlternatePhoneNumber(clientCompany.getAlternatePhoneNumber());
        builtClientCompany.setApprovedAt(LocalDateTime.now());
        builtClientCompany.setCeoName(clientCompany.getCeoName());
        builtClientCompany.setChairmanName(clientCompany.getChairmanName());
        builtClientCompany.setCountry(clientCompany.getCountry());
        builtClientCompany.setDateOfIncorporation(clientCompany.getDateOfIncorporation());
        builtClientCompany.setDepository(clientCompany.getDepository());
        builtClientCompany.setEmailAddress(clientCompany.getEmailAddress());
        builtClientCompany.setExchange(clientCompany.getExchange());
        builtClientCompany.setLga(clientCompany.getLga());
        builtClientCompany.setNseSector(clientCompany.getNseSector());
        builtClientCompany.setPhoneNumber(clientCompany.getPhoneNumber());
        builtClientCompany.setPostalCode(clientCompany.getPostalCode());
        builtClientCompany.setRcNumber(clientCompany.getRcNumber());
        builtClientCompany.setRegisterCode(clientCompany.getRegisterCode());
        builtClientCompany.setRegisterName(clientCompany.getRegisterName());
        builtClientCompany.setRegistrationCode(clientCompany.getRegistrationCode());
        builtClientCompany.setSecretary(clientCompany.getSecretary());
        builtClientCompany.setSymbol(clientCompany.getSymbol());
        return builtClientCompany;
    }

    @Override
    public Object rejectRequest(long requestId, long userId, String reason) {

        ModuleRequest clientCompanyRequest = getApprovalRequestById(requestId);

        if (clientCompanyRequest == null) return "Request not found";

        if (clientCompanyRequest.getStatus() != Const.PENDING) return "Request can not be rejected";

        clientCompanyRequest.setReason(reason);
        clientCompanyRequest.setStatus(Const.REJECTED);
        Date date = new Date();
        clientCompanyRequest.setApprovedOn(date);
        clientCompanyRequest.setApproverId(userId);
        requestRepository.save(clientCompanyRequest);
        return Const.REJECTED_REQUEST;
    }

    @Override
    public ModuleRequest updateApprovalRequest(ModuleRequest moduleRequest) {

        log.info("Updating resource {} " + moduleRequest);
        ModuleRequest updatedModuleRequest = requestRepository.save(moduleRequest);
        log.info("Updated resource {} " + updatedModuleRequest);
        return updatedModuleRequest;
    }

    @Override
    public ModuleRequest getApprovalRequestByResourceIdAndActionRequired(ModuleRequest moduleRequest) {
        return requestRepository.findFirstModuleRequestByResourceIdAndActionRequiredAndModulesOrderByRequestIdDesc(
                moduleRequest.getResourceId(), moduleRequest.getActionRequired(), ConstantUtils.MODULE
        );
    }

    @Override
    public List<ModuleRequest> getClientCompanyRequestWaiting() {
        return requestRepository.awaitingApproval(Const.PENDING);
    }

    private String requestCode(String clientCompany) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmsss");
        String date = simpleDateFormat.format(new Date());
        return clientCompany+date;
    }

    public List<ModuleRequest> getShareholderRequestApproved() {
        return requestRepository.approved(Const.APPROVED);
    }

    @Override
    public List<ModuleRequest> getClientCompanyRequestByStatus(
            Integer offset, Integer limit, String status, String type, long requesterId) {
        if (StringUtils.isBlank(status) && StringUtils.isBlank(type)) {
            Pageable paging = PageRequest.of(offset, limit, Sort.by("createdOn").ascending());
            Page<ModuleRequest> allRequests = requestRepository.findAllByModules(paging,ConstantUtils.MODULE);
            if (allRequests.hasContent()) {
                return allRequests.getContent();

            } else {
                return new ArrayList<ModuleRequest>();
            }
        }

        if (StringUtils.isBlank(status) && StringUtils.isNotBlank(type)) {
            Pageable paging = PageRequest.of(offset, limit, Sort.by("createdOn").ascending());
            Page<ModuleRequest> allRequests = requestRepository.findAllByModulesAndRequesterId(paging,ConstantUtils.MODULE,requesterId);
            if (allRequests.hasContent()) {
                return allRequests.getContent();

            } else {
                return new ArrayList<ModuleRequest>();
            }
        }

        if (StringUtils.isNotBlank(status) && StringUtils.isBlank(type)) {
            for (StatusEnum statusEnum : StatusEnum.values()) {
                if (statusEnum.name().equals(status.toUpperCase())) {
                    int stat = StatusEnum.valueOf(status.toUpperCase()).getStatus();
                    Pageable paging = PageRequest.of(offset, limit, Sort.by("created_on").ascending());
                    Page<ModuleRequest> pagedResult =
                            requestRepository.findAllByStatusAndModules(paging, stat, ConstantUtils.MODULE);
                    if (pagedResult.hasContent()) {
                        return pagedResult.getContent();
                    } else {
                        return new ArrayList<ModuleRequest>();
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(status) && StringUtils.isNotBlank(type)) {
            for (StatusEnum statusEnum : StatusEnum.values()) {
                if (statusEnum.name().equals(status.toUpperCase())) {
                    int stat = StatusEnum.valueOf(status.toUpperCase()).getStatus();
                    Pageable paging = PageRequest.of(offset, limit, Sort.by("created_on").ascending());
                    Page<ModuleRequest> pagedResult =
                            requestRepository.findAllByStatusAndModulesAAndRequesterId(paging, stat, ConstantUtils.MODULE,requesterId);
                    if (pagedResult.hasContent()) {
                        return pagedResult.getContent();
                    } else {
                        return new ArrayList<ModuleRequest>();
                    }
                }
            }
        }

        return new ArrayList<ModuleRequest>();
    }

    @Override
    public Result<ModuleRequest> getClientCompanyApprovalNotificationByDateCreated(Date date, Pageable pageable) {
        Page<ModuleRequest> allRecords =  requestRepository.findModuleRequestByModulesAndCreatedOn(pageable,ConstantUtils.MODULE,date);
        long noOfRecords = allRecords.getTotalElements();
        return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
    }

    @Override
    public Result<ModuleRequest> getClientCompanyApprovalNotificationByDateCreatedAndStatus(
            Date dateObject, String status, String type,long requesterId, Pageable pageable) {

        DateTime dtOrg = new DateTime(dateObject);
        DateTime endDate = dtOrg.plusDays(1);

        if (dateObject != null && StringUtils.isNotBlank(status) && StringUtils.isBlank(type)) {
            for (StatusEnum statusEnum : StatusEnum.values()) {
                if (statusEnum.name().equals(status.toUpperCase())) {
                    int stat = StatusEnum.valueOf(status.toUpperCase()).getStatus();
                    Page<ModuleRequest> allRecords =
                            requestRepository.findModuleRequestByModulesAndCreatedOnBetweenAndStatus(
                                     ConstantUtils.MODULE, dateObject,endDate.toDate(), stat,pageable);
                    long noOfRecords = allRecords.getTotalElements();
                    return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                }
            }
        }

        if (dateObject != null && StringUtils.isNotBlank(status) && StringUtils.isNotBlank(type)) {
            for (StatusEnum statusEnum : StatusEnum.values()) {
                if (statusEnum.name().equals(status.toUpperCase())) {
                    int stat = StatusEnum.valueOf(status.toUpperCase()).getStatus();
                    if (type.equalsIgnoreCase("sent")){
                    Page<ModuleRequest> allRecords =
                            requestRepository.findModuleRequestByModulesAndCreatedOnBetweenAndStatusAndRequesterId(
                                    ConstantUtils.MODULE, dateObject,endDate.toDate(), stat, requesterId, pageable);
                    long noOfRecords = allRecords.getTotalElements();
                    return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                    }else{
                        Page<ModuleRequest> allRecords =
                                requestRepository.findModuleRequestByModulesAndCreatedOnBetweenAndStatus(
                                        ConstantUtils.MODULE, dateObject,endDate.toDate(), stat,pageable);
                        long noOfRecords = allRecords.getTotalElements();
                        return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                    }
                }
            }
        }

        if (dateObject == null && StringUtils.isNotBlank(status) && StringUtils.isBlank(type)) {
            for (StatusEnum statusEnum : StatusEnum.values()) {
                if (statusEnum.name().equals(status.toUpperCase())) {
                    int stat = StatusEnum.valueOf(status.toUpperCase()).getStatus();
                    Page<ModuleRequest> allRecords =
                            requestRepository.findAllByStatusAndModules(pageable, stat, ConstantUtils.MODULE);
                    long noOfRecords = allRecords.getTotalElements();
                    return new Result<>(
                            0,
                            allRecords.getContent(),
                            noOfRecords,
                            pageable.getPageNumber() + 1,
                            pageable.getPageSize());
                }
            }
        }

        if (dateObject == null && StringUtils.isNotBlank(status) && StringUtils.isNotBlank(type)) {
            for (StatusEnum statusEnum : StatusEnum.values()) {
                if (statusEnum.name().equals(status.toUpperCase())) {
                    int stat = StatusEnum.valueOf(status.toUpperCase()).getStatus();
                    if (type.equalsIgnoreCase("sent")){
                    Page<ModuleRequest> allRecords = requestRepository.findAllByStatusAndModulesAndRequesterId(pageable, stat, ConstantUtils.MODULE,requesterId);
                    long noOfRecords = allRecords.getTotalElements();
                    return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                    }else{
                        Page<ModuleRequest> allRecords = requestRepository.findAllByStatusAndModules(pageable, stat, ConstantUtils.MODULE);
                        long noOfRecords = allRecords.getTotalElements();
                        return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                    }
                }
            }
        }

        if (dateObject != null && StringUtils.isBlank(status) && StringUtils.isBlank(type)){
            Page<ModuleRequest> allRecords =
                    requestRepository.findModuleRequestByModulesAndCreatedOn(pageable,ConstantUtils.MODULE, dateObject);
            long noOfRecords = allRecords.getTotalElements();
            return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
        }

        if (dateObject != null && StringUtils.isBlank(status) && StringUtils.isNotBlank(type)){
            if (type.equalsIgnoreCase("sent")){
            Page<ModuleRequest> allRecords = requestRepository.findModuleRequestByModulesAndCreatedOnAndRequesterId(pageable,ConstantUtils.MODULE, dateObject,requesterId);
            long noOfRecords = allRecords.getTotalElements();
            return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
            }else{
                Page<ModuleRequest> allRecords = requestRepository.findModuleRequestByModulesAndCreatedOn(pageable,ConstantUtils.MODULE, dateObject);
                long noOfRecords = allRecords.getTotalElements();
                return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
            }
        }

        return new Result<ModuleRequest>();

    }

    @Override
    public Result<ModuleRequest> search(Pageable pageable, String param, String status, String type, long requesterId) {


        if (StringUtils.isNotBlank(param) && StringUtils.isBlank(status) && StringUtils.isBlank(type)) {
            Page<ModuleRequest> allRequests = requestRepository.findModuleRequestByNewRecordAndOldRecord(pageable, param, ConstantUtils.MODULE);
            long noOfRecords = allRequests.getTotalElements();
            return new Result<>(0, allRequests.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
        }

        if (StringUtils.isNotBlank(param) && StringUtils.isBlank(status) && StringUtils.isNotBlank(type)) {
            if (type.equalsIgnoreCase("sent")){
            Page<ModuleRequest> allRequests = requestRepository.findModuleRequestByNewRecordAndOldRecordAAndRequesterId(pageable, param, ConstantUtils.MODULE,requesterId);
            long noOfRecords = allRequests.getTotalElements();
            return new Result<>(0, allRequests.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
            }else {
                Page<ModuleRequest> allRequests = requestRepository.findModuleRequestByNewRecordAndOldRecord(pageable, param, ConstantUtils.MODULE);
                long noOfRecords = allRequests.getTotalElements();
                return new Result<>(0, allRequests.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
            }
        }


        if (StringUtils.isBlank(param) && StringUtils.isNotBlank(status) && StringUtils.isBlank(type)){
            for (StatusEnum statusEnum : StatusEnum.values()) {
                if (statusEnum.name().equals(status.toUpperCase())) {
                    int stat = StatusEnum.valueOf(status.toUpperCase()).getStatus();
                    Page<ModuleRequest> allRecords = requestRepository.findAllByStatusAndModules(pageable,stat, ConstantUtils.MODULE);
                    long noOfRecords = allRecords.getTotalElements();
                    return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                }
            }
        }

        if (StringUtils.isBlank(param) && StringUtils.isNotBlank(status) && StringUtils.isNotBlank(type)){
            for (StatusEnum statusEnum : StatusEnum.values()) {
                if (statusEnum.name().equals(status.toUpperCase())) {
                    int stat = StatusEnum.valueOf(status.toUpperCase()).getStatus();

                    if (type.equalsIgnoreCase("sent")){
                    Page<ModuleRequest> allRecords = requestRepository.findAllByStatusAndModulesAAndRequesterId(pageable,stat, ConstantUtils.MODULE,requesterId);
                    long noOfRecords = allRecords.getTotalElements();
                    return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                    }

                    else{
                        Page<ModuleRequest> allRecords = requestRepository.findAllByStatusAndModules(pageable,stat, ConstantUtils.MODULE);
                        long noOfRecords = allRecords.getTotalElements();
                        return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                    }
                }
            }
        }



        if (StringUtils.isNotBlank(param) && StringUtils.isNotBlank(status) && StringUtils.isBlank(type)){
            for (StatusEnum statusEnum : StatusEnum.values()) {
                if (statusEnum.name().equals(status.toUpperCase())) {
                    int stat = StatusEnum.valueOf(status.toUpperCase()).getStatus();
                    Page<ModuleRequest> allRecords = requestRepository.findModuleRequestByNewRecordAndOldRecordAndStatus(pageable, param, ConstantUtils.MODULE,stat);
                    long noOfRecords = allRecords.getTotalElements();
                    return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                }
            }
        }

        if (StringUtils.isNotBlank(param) && StringUtils.isNotBlank(status) && StringUtils.isNotBlank(type)){
            for (StatusEnum statusEnum : StatusEnum.values()) {
                if (statusEnum.name().equals(status.toUpperCase())) {
                    int stat = StatusEnum.valueOf(status.toUpperCase()).getStatus();
                    if (type.equalsIgnoreCase("sent")){
                    Page<ModuleRequest> allRecords = requestRepository.findModuleRequestByNewRecordAndOldRecordAndStatusAndRequesterId(pageable, param, ConstantUtils.MODULE,stat,requesterId);
                    long noOfRecords = allRecords.getTotalElements();
                    return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                    }else{
                        Page<ModuleRequest> allRecords = requestRepository.findModuleRequestByNewRecordAndOldRecordAndStatus(pageable, param, ConstantUtils.MODULE,stat);
                        long noOfRecords = allRecords.getTotalElements();
                        return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
                    }
                }
            }
        }

        Page<ModuleRequest> allRequests = requestRepository. findModuleRequestByModules(pageable,ConstantUtils.MODULE);
        long noOfRecords = allRequests.getTotalElements();

        return new Result<>(0, allRequests.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
    }


    public List<ModuleRequest> getShareholderRequestRejected() {
        return requestRepository.rejected(Const.REJECTED);
    }


}
