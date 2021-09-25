package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class MappedModuleResponseDto implements Serializable {

    @JsonProperty("request_id")
    public long requestId;

    @JsonProperty("requester_id")
    public long requesterId;

    @JsonProperty("resource_id")
    public long resourceId;

    @JsonProperty("status")
    public String status;

    @JsonProperty("reason")
    public String reason;

    @JsonProperty("modules")
    public String modules;

    @JsonProperty("request_code")
    public String requestCode;

    @JsonProperty("created_on")
    public Date createdOn;

    @JsonProperty("record")
    public Object record;

    @JsonProperty("action_required")
    public String actionRequired;
}
