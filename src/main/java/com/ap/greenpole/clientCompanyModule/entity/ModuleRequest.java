package com.ap.greenpole.clientCompanyModule.entity;

import com.ap.greenpole.clientCompanyModule.utils.Const;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "tbl_request_approval")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("request_id")
    @Column(name = "request_id")
    private long requestId;

    @JsonProperty("requester_id")
    @Column(name = "requester_id")
    long requesterId;

    @JsonProperty("resource_id")
    @Column(name = "resource_id")
    long resourceId;

    @JsonProperty("approver_id")
    @Column(name = "approver_id")
    long approverId;

    int status;

    private String reason, modules;

    @JsonProperty("request_code")
    @Column(name = "request_code")
    String requestCode;

    @DateTimeFormat(pattern= Const.DATE_FORMATE)
    @JsonProperty("created_on")
    @Column(name = "created_on")
    private Date createdOn;

    @DateTimeFormat(pattern=Const.DATE_FORMATE)
    @JsonProperty("approved_on")
    @Column(name = "approved_on")
    Date approvedOn;

    @JsonProperty("old_record")
    @Column(name = "old_record")
    private String oldRecord;

    @JsonProperty("new_record")
    @Column(name = "new_record")
    private String newRecord;

    @JsonProperty("action_required")
    @Column(name = "action_required")
    private String actionRequired;

    public ModuleRequest(Long userId, String action, int status, String oldData, String holder){
        actionRequired = action;
        this.status = status;
        newRecord = holder;
        this.requesterId = userId;
        oldRecord = oldData;
    }

    public ModuleRequest(Long userId, String action, int status, String holder){
        actionRequired = action;
        this.status = status;
        newRecord = holder;
        this.requesterId = userId;

    }

}
