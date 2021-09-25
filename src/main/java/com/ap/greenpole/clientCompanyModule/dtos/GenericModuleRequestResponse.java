package com.ap.greenpole.clientCompanyModule.dtos;

import com.ap.greenpole.clientCompanyModule.entity.ModuleRequest;
import com.ap.greenpole.clientCompanyModule.utils.Const;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class GenericModuleRequestResponse<T> {

    public GenericModuleRequestResponse(){}

  public GenericModuleRequestResponse(ModuleRequest moduleRequest) {
    requestId = moduleRequest.getRequestId();
    requesterId = moduleRequest.getRequesterId();
    requestCode = moduleRequest.getRequestCode();
    resourceId = moduleRequest.getResourceId();
    status = moduleRequest.getStatus();
    reason = moduleRequest.getReason();
    modules = moduleRequest.getModules();
    createdOn = moduleRequest.getCreatedOn();
    actionRequired = moduleRequest.getActionRequired();
    approverId = moduleRequest.getApproverId();
    approvedOn = moduleRequest.getApprovedOn();
    Gson gson = new GsonBuilder().setDateFormat(Const.DATE_FORMATE).create();

    if (actionRequired.equalsIgnoreCase(Const.CREATE)){
        newRecord = gson.fromJson(moduleRequest.getNewRecord(),ClientCompanyRequestDto.class);
    }
      if (actionRequired.equalsIgnoreCase(Const.TRADE_RIGHTS)){
          newRecord =  gson.fromJson(moduleRequest.getNewRecord(),TradeRightsDto.class);
      }
      if (actionRequired.equalsIgnoreCase(Const.CREATE_SHAREHOLDER_INTRODUCTION)){
          newRecord = gson.fromJson(moduleRequest.getNewRecord(), ShareHolderRequestDto[].class);
      }
      if (actionRequired.equalsIgnoreCase(Const.CREATE_SHAREHOLDER_IN_MASS)){
          newRecord =  gson.fromJson(moduleRequest.getNewRecord(), ShareHolderInMassRequestDto[].class);
      }
      if (actionRequired.equalsIgnoreCase(Const.INVALIDATE)){
          newRecord = gson.fromJson(moduleRequest.getOldRecord(),ClientCompanyResponseDto.class);
      }
      if (actionRequired.equalsIgnoreCase(Const.MANUAL_SHARE_CAPITAL)){
          newRecord = gson.fromJson(moduleRequest.getNewRecord(), ManualShareCapitalDto.class);
      }

    }

    private long requestId;

    @JsonProperty("requester_id")
    long requesterId;

    @JsonProperty("resource_id")
    long resourceId;

    @JsonProperty("approver_id")
    long approverId;

    int status;

    private String reason, modules;

    @JsonProperty("request_code")
    String requestCode;

    @DateTimeFormat(pattern= Const.DATE_FORMATE)
    @JsonProperty("created_on")
    private Date createdOn;

    @DateTimeFormat(pattern=Const.DATE_FORMATE)
    @JsonProperty("approved_on")
    Date approvedOn;

    @JsonProperty("old_record")
    private Object oldRecord;

    @JsonProperty("new_record")
    private Object newRecord;

    @JsonProperty("action_required")
    private String actionRequired;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(long requesterId) {
        this.requesterId = requesterId;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public long getApproverId() {
        return approverId;
    }

    public void setApproverId(long approverId) {
        this.approverId = approverId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getModules() {
        return modules;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getApprovedOn() {
        return approvedOn;
    }

    public void setApprovedOn(Date approvedOn) {
        this.approvedOn = approvedOn;
    }

    public Object getOldRecord() {
        return oldRecord;
    }

    public void setOldRecord(Object oldRecord) {
        this.oldRecord = oldRecord;
    }

    public Object getNewRecord() {
        return newRecord;
    }

    public void setNewRecord(Object newRecord) {
        this.newRecord = newRecord;
    }

    public String getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(String actionRequired) {
        this.actionRequired = actionRequired;
    }
}
