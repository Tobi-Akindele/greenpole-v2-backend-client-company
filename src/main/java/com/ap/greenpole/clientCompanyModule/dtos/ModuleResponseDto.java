package com.ap.greenpole.clientCompanyModule.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder(toBuilder = true)
public class ModuleResponseDto {

    @JsonProperty(value = "request_id")
    long requestId;

    @JsonProperty(value = "requester_id")
    long requesterId;

    @JsonProperty(value = "status")
    String status;

    @JsonProperty(value = "created_on")
    Date createdOn;

}
