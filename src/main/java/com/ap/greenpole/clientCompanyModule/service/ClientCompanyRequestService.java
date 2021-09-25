package com.ap.greenpole.clientCompanyModule.service;

import com.ap.greenpole.clientCompanyModule.entity.ClientCompany;
import com.ap.greenpole.clientCompanyModule.entity.ModuleRequest;
import com.ap.greenpole.clientCompanyModule.entity.PagingResponse;
import com.ap.greenpole.clientCompanyModule.entity.Result;
import com.ap.greenpole.usermodule.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;

import java.util.Date;
import java.util.List;

public interface ClientCompanyRequestService {

    ModuleRequest createApprovalRequest(ModuleRequest moduleRequest, User user);
    List<ModuleRequest> getAllApprovalRequest();
    Result<ModuleRequest> getAllApprovalRequest(int pageNumber, int pageSize, Pageable pageable);
    ModuleRequest getApprovalRequestById(long approvalRequestId);
    Object approveRequest(long requestId, long userId);
    Object rejectRequest(long requestId, long userId, String reason);
    ModuleRequest updateApprovalRequest(ModuleRequest moduleRequest);
    ModuleRequest getApprovalRequestByResourceIdAndActionRequired(ModuleRequest ModuleRequest);
    List<ModuleRequest> getClientCompanyRequestWaiting();
    List<ModuleRequest> getShareholderRequestRejected();
    List<ModuleRequest> getShareholderRequestApproved();
    List<ModuleRequest> getClientCompanyRequestByStatus(Integer offset,Integer limit, String status, String type, long requesterId);
    Result<ModuleRequest> getClientCompanyApprovalNotificationByDateCreated(Date date, Pageable pageable);
    Result<ModuleRequest> getClientCompanyApprovalNotificationByDateCreatedAndStatus(Date dateObject,String status, String type,long requesterId, Pageable pageable);
    Result<ModuleRequest> search(Pageable pageable, String param, String status, String type, long requesterId);

}
