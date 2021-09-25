package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreateClientCompanyResponseDto implements Serializable {

    @JsonProperty(value = "approval_request")
    ModuleResponseDto responseDto;

    @JsonProperty(value = "request_data")
    ClientCompanyRequestDto dto;
}
