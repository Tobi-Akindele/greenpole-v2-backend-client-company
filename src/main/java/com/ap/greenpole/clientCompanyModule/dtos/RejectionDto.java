package com.ap.greenpole.clientCompanyModule.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder(toBuilder = true)
public class RejectionDto {

    @NotEmpty
    @JsonProperty(value = "reason")
    String reason;
}
